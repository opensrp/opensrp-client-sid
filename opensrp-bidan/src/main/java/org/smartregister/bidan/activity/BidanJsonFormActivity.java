package org.smartregister.bidan.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.fragment.PathJsonFormFragment;
import org.smartregister.bidan.repository.StockRepository;
import org.smartregister.immunization.repository.VaccineTypeRepository;

import java.util.ArrayList;
import java.util.Date;

import util.JsonFormUtils;

public class BidanJsonFormActivity extends JsonFormActivity {

    private int generatedId = -1;
    private MaterialEditText balancetextview;
    private PathJsonFormFragment pathJsonFormFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initializeFormFragment() {
        pathJsonFormFragment = PathJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, pathJsonFormFragment).commit();
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId);
        refreshCalculateLogic(key, value);

    }

    @Override
    public void onFormFinish() {
        super.onFormFinish();
    }

    private void refreshCalculateLogic(String key, String value) {

//        stockVialsenteredinAdjustmentForm(key, value);
    }

    private void initializeBalanceTextView(int currentBalance, int vialsUsed, MaterialEditText balanceTextView) {
        if (balanceTextView != null) {
            balanceTextView.setErrorColor(Color.BLACK);
            if (currentBalance != 0) {
                Typeface typeFace = Typeface.create(balanceTextView.getTypeface(), Typeface.ITALIC);
                balanceTextView.setAccentTypeface(typeFace);
                balanceTextView.setError(currentBalance + " child(ren) vaccinated today. Assuming " + vialsUsed + " vial(s) used.");
            } else {
                balanceTextView.setError("");
            }
        }
    }

    private String checkifmeasles(String vaccineName) {
        if (vaccineName.equalsIgnoreCase("M/MR")) {
            return "measles";
        }
        return vaccineName;
    }

    public boolean checkIfBalanceNegative() {
        boolean balancecheck = true;
        String balancestring = pathJsonFormFragment.getRelevantTextViewString("Balance");

        if (balancestring.contains("New balance") && StringUtils.isNumeric(balancestring)) {
            int balance = Integer.parseInt(balancestring.replace("New balance:", "").trim());
            if (balance < 0) {
                balancecheck = false;
            }
        }

        return balancecheck;
    }

    public boolean checkIfAtLeastOneServiceGiven() {
        JSONObject object = getStep("step1");
        try {
            if (object.getString("title").contains("Record out of catchment area service")) {
                JSONArray fields = object.getJSONArray("fields");
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject vaccineGroup = fields.getJSONObject(i);
                    if (vaccineGroup.has("key") && vaccineGroup.has("is_vaccine_group")) {
                        if (vaccineGroup.getBoolean("is_vaccine_group") && vaccineGroup.has("options")) {
                            JSONArray vaccineOptions = vaccineGroup.getJSONArray("options");
                            for (int j = 0; j < vaccineOptions.length(); j++) {
                                JSONObject vaccineOption = vaccineOptions.getJSONObject(j);
                                if (vaccineOption.has("value") && vaccineOption.getBoolean("value")) {
                                    return true;
                                }
                            }
                        }
                    } else if (vaccineGroup.has("key") && vaccineGroup.getString("key").equals("Weight_Kg") && vaccineGroup.has("value") && vaccineGroup.getString("value").length() > 0) {
                        return true;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
}

