package org.smartregister.bidan.helper.view;

import android.content.Context;
import android.text.style.ForegroundColorSpan;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.bidan.R;
import org.smartregister.bidan.util.Constants;

import java.util.Map;

import util.BidanConstants;
import util.BidanSpannableStringBuilder;

/**
 * Created by ndegwamartin on 15/01/2018.
 */

public class TestResultsStringBuilderHelper {
    private Context context;

    TestResultsStringBuilderHelper(Context context) {
        this.context = context;
    }

    public BidanSpannableStringBuilder getSmearResultStringBuilder(Map<String, String> testResults, BidanSpannableStringBuilder stringBuilder) {
        ForegroundColorSpan redForegroundColorSpan = getRedForegroundColorSpan();
        stringBuilder.append("Smear ");
        switch (testResults.get(BidanConstants.RESULT.TEST_RESULT)) {
            case "one_plus":
                stringBuilder.append("1+", redForegroundColorSpan);
                break;
            case "two_plus":
                stringBuilder.append("2+", redForegroundColorSpan);
                break;
            case "three_plus":
                stringBuilder.append("3+", redForegroundColorSpan);
                break;
            case "scanty":
                stringBuilder.append("Scanty", redForegroundColorSpan);
                break;
            case "negative":
                stringBuilder.append("Negative", getBlackForegroundColorSpan());
                break;
            default:
                stringBuilder.append(WordUtils.capitalize(testResults.get(BidanConstants.RESULT.TEST_RESULT).substring(0, 2)), redForegroundColorSpan);
                break;
        }
        stringBuilder.append("\n");
        return stringBuilder;
    }


    public BidanSpannableStringBuilder getCultureResultStringBuilder(Map<String, String> testResults, BidanSpannableStringBuilder stringBuilder) {
        ForegroundColorSpan colorSpan = testResults.get(BidanConstants.RESULT.CULTURE_RESULT).equals(Constants.RESULT.POSITIVE) ? getRedForegroundColorSpan() : getBlackForegroundColorSpan();
        stringBuilder.append("Culture ");
        stringBuilder.append(WordUtils.capitalizeFully(testResults.get(BidanConstants.RESULT.CULTURE_RESULT).substring(0, 3)), colorSpan);
        stringBuilder.append("\n");
        return stringBuilder;
    }


    public BidanSpannableStringBuilder getXRayResultStringBuilder(Map<String, String> testResults, BidanSpannableStringBuilder stringBuilder) {
        ForegroundColorSpan blackForegroundColorSpan = getBlackForegroundColorSpan();
        stringBuilder.append("Chest X-Ray ");
        if (testResults.get(BidanConstants.RESULT.XRAY_RESULT).equals("indicative")) {
            stringBuilder.append("Indicative", getRedForegroundColorSpan());
        } else {
            stringBuilder.append("Not Indicative", blackForegroundColorSpan);
        }
        stringBuilder.append("\n");
        return stringBuilder;
    }

    private ForegroundColorSpan getRedForegroundColorSpan() {
        ForegroundColorSpan redForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(R.color.test_result_positive_red));
        return redForegroundColorSpan;
    }

    private ForegroundColorSpan getBlackForegroundColorSpan() {
        ForegroundColorSpan blackForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(R.color.test_result_negative_black));
        return blackForegroundColorSpan;
    }

    public BidanSpannableStringBuilder getXpertResultStringBuilder(Map<String, String> testResults, BidanSpannableStringBuilder stringBuilder, boolean withOtherResults) {
        stringBuilder.append("Gene Xpert ");
        ForegroundColorSpan blackForegroundColorSpan = getBlackForegroundColorSpan();
        stringBuilder.append(withOtherResults ? "Xpe " : "MTB ");
        stringBuilder.append(processXpertResult(testResults.get(BidanConstants.RESULT.MTB_RESULT)), getColorSpan(testResults.get(BidanConstants.RESULT.MTB_RESULT).equals(Constants.TEST_RESULT.XPERT.DETECTED)));
        if (testResults.containsKey(BidanConstants.RESULT.ERROR_CODE)) {
            stringBuilder.append(" ");
            stringBuilder.append(testResults.get(Constants.RESULT.ERROR_CODE), blackForegroundColorSpan);
        } else if (testResults.get(BidanConstants.RESULT.MTB_RESULT).equals(Constants.TEST_RESULT.XPERT.DETECTED) && testResults.get(BidanConstants.RESULT.RIF_RESULT) != null) {
            stringBuilder.append(withOtherResults ? "/ " : " / RIF ");
            stringBuilder.append(processXpertResult(testResults.get(BidanConstants.RESULT.RIF_RESULT)), getColorSpan(testResults.get(BidanConstants.RESULT.RIF_RESULT).equals(Constants.TEST_RESULT.XPERT.DETECTED)));
        }
        stringBuilder.append("\n");
        return stringBuilder;
    }

    public String processXpertResult(String result) {

        if (result == null)
            return "-ve";
        switch (result) {
            case Constants.TEST_RESULT.XPERT.DETECTED:
                return "+ve";
            case Constants.TEST_RESULT.XPERT.NOT_DETECTED:
                return "-ve";
            case Constants.TEST_RESULT.XPERT.INDETERMINATE:
                return "?";
            case Constants.TEST_RESULT.XPERT.ERROR:
                return "err";
            case Constants.TEST_RESULT.XPERT.NO_RESULT:
                return "No result";
            default:
                return result;
        }
    }

    private ForegroundColorSpan getColorSpan(boolean isPositive) {
        return isPositive ? getRedForegroundColorSpan() : getBlackForegroundColorSpan();
    }
}
