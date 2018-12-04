package org.smartregister.gizi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.jjoe64.graphview.GraphView;

import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.gizi.R;
import org.smartregister.repository.DetailsRepository;

import util.formula.Support;
import util.growthchart.GraphConstant;
import util.growthchart.GrowthChartGenerator;
import util.zscore.ZScoreSystemCalculation;

/**
 * Created by Null on 2016-12-06.
 */
public class GiziZScoreChartActivity extends Activity {

    public static CommonPersonObjectClient client;
    private GrowthChartGenerator generator;
    private ZScoreSystemCalculation calc;
    private TextView detailActivity;
    private ImageButton back;
    private TextView lfaActivity;
    private String historyUmur;
    private String historyBerat;
    private String historyTinggi;
    private String historyTinggiUmurHari;
    private GraphView zScoreGraph;
    private CheckBox wfaCheckBox;
    private CheckBox hfaCheckBox;
    private CheckBox wfhCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = Context.getInstance();
        calc = new ZScoreSystemCalculation();
        setContentView(R.layout.gizi_z_score_activity);
        FlurryAgent.logEvent("ZScore_chart_view");

        if (client == null) {
            DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
            detailsRepository.updateDetails(client);
        }

        // initializing global variable
        initializeGlobalVariable();

        // Initializing layout component
        // configure nav bar option
        detailActivity = (TextView) findViewById(R.id.chart_navbar_details);
        back = (ImageButton) findViewById(R.id.btn_back_to_home);
        lfaActivity = (TextView) findViewById(R.id.chart_navbar_growth_chart);
        wfaCheckBox = (CheckBox) findViewById(R.id.wfaCheckBox);
        hfaCheckBox = (CheckBox) findViewById(R.id.hfaCheckBox);
        wfhCheckBox = (CheckBox) findViewById(R.id.wflCheckBox);
        zScoreGraph = (GraphView) findViewById(R.id.z_score_chart);
        initializeActionCheckBox();
        initializeActionNavBar();
        refreshGraph();
    }

    private void refreshGraph() {
        String[] data = initializeZScoreSeries();
        String seriesAxis = data[0];
        String seriesData = data[1];

        generator = new GrowthChartGenerator(zScoreGraph, GraphConstant.Z_SCORE_CHART,
                client.getDetails().get("tanggalLahirAnak"),
                client.getDetails().get("gender"),
                seriesAxis, seriesData
        );
    }

    private void initializeGlobalVariable() {
        ////System.out.println("data z score client = "+client.getDetails().toString());
        historyUmur = split(Support.fixHistory(client.getDetails().get("history_berat")))[0];
//        String historyUmurHari = client.getDetails().get("history_umur_tinggi");
        historyBerat = split(Support.fixHistory(client.getDetails().get("history_berat")))[1];
        historyTinggi = cleanBlankValueOf(Support.fixHistory(client.getDetails().get("history_tinggi")));
        historyTinggiUmurHari = cleanBlankValueOf(client.getDetails().get("history_tinggi_umur_hari"));

    }

    private void initializeActionCheckBox() {
        wfaCheckBox.setChecked(true);
        hfaCheckBox.setChecked(true);
        wfhCheckBox.setChecked(true);

        wfaCheckBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked)
                            generator.putSeriesOfIndex(0);
                        else
                            generator.removeSeriesOfIndex(0);

                    }
                }
        );
        hfaCheckBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked)
                            generator.putSeriesOfIndex(1);
                        else
                            generator.removeSeriesOfIndex(1);
                    }
                }
        );
        wfhCheckBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked)
                            generator.putSeriesOfIndex(2);
                        else
                            generator.removeSeriesOfIndex(2);
                    }
                }
        );
    }

    public void initializeActionNavBar() {
        detailActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                GiziDetailActivity.childclient = client;
                startActivity(new Intent(GiziZScoreChartActivity.this, GiziDetailActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        lfaActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                GiziGrowthChartActivity.client = client;
                startActivity(new Intent(GiziZScoreChartActivity.this, GiziGrowthChartActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(GiziZScoreChartActivity.this, GiziSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }

    private String[] initializeZScoreSeries() {
        boolean wfaChecked = true;
        String axis1 = wfaChecked ? createWFAAxis() : "";
        String data1 = wfaChecked ? createWFASeries() : "";
        String axis2 = "", data2 = "";

        boolean hfaChecked = true;
        if (hfaChecked) {
            String tempAxis2 = createHFAAxis();
            if (!tempAxis2.equals("")) {
                axis2 = tempAxis2.split(",").length > 0 ? Integer.toString(Integer.parseInt(tempAxis2.split(",")[0]) / 30) : "";
                for (int i = 1; i < tempAxis2.split(",").length; i++) {
                    axis2 = axis2 + "," + Integer.toString(Integer.parseInt(tempAxis2.split(",")[i]) / 30);
                }
                data2 = createHFASeries();
            }
        }

        boolean wfhChecked = true;
        String axis3 = wfhChecked ? createWFHAxis() : "";
        String data3 = wfhChecked ? createWFHSeries() : "";

        ////System.out.println("data 1 = "+axis1+"@"+data1);
        ////System.out.println("data 2 = "+axis2+"@"+data2);
        ////System.out.println("data 3 = "+axis3+"@"+data3);

        return new String[]{axis1 + "@" + axis2 + "@" + axis3, data1 + "@" + data2 + "@" + data3};
    }

    //CREATING AXIS AND SERIES DATA
    private String createWFAAxis() {
        String seriesAxis = "";
        String[] temp = buildDayAgeArray(historyUmur/*, historyUmurHari*/).split(",");
        seriesAxis = temp[0].equals("") ? "" : "" + (Integer.parseInt(temp[0]) / 30);
        for (int i = 1; i < temp.length; i++) {
            if (Integer.parseInt(temp[i]) < 0)
                continue;
            seriesAxis = seriesAxis + "," + (Integer.parseInt(temp[i]) / 30);
        }
        return seriesAxis;
    }

    private String createWFASeries() {
        if (historyBerat == null)
            return "";

        String[] dayAge = buildDayAgeArray(historyUmur/*,historyUmurHari*/).split(",");
        String[] weight = historyBerat.split(",");
        boolean isMale = !client.getDetails().get("gender").toLowerCase().contains("em");
        String wfa = "";
        int ageLength = dayAge.length;

        if (ageLength == 1 && dayAge[0].equals(""))
            return dayAge[0];
        for (int i = 0; i < ageLength; i++) {
            ////System.out.println("age on day : "+dayAge[i]);
        }

        for (int i = 0; i < ageLength; i++) {
            if (Integer.parseInt(dayAge[i]) < 0)
                continue;
            if (i > 0)
                wfa = wfa + ",";
            wfa = wfa + calc.countWFA(isMale, Integer.parseInt(dayAge[i]), Double.parseDouble(weight[i + 1]));
        }
        return wfa;
    }

    private String createHFAAxis() {
        if (historyTinggi == null)
            return "";
        String[] historyUmur = historyTinggi.split(",");
        String[] historyUmurHari = historyTinggiUmurHari != null
                ? historyTinggiUmurHari.split(",")
                : new String[]{""};

        String tempUmur = historyUmur.length > 1 ? historyUmur[0].split(":")[0] : "";
        String tempUmurHari = historyUmurHari.length > 1 ? historyUmurHari[0].split(":")[0] : "";

        for (int i = 1; i < historyUmur.length; i++) {
            tempUmur = tempUmur + "," + historyUmur[i].split(":")[0];
            if (historyUmurHari.length > i)
                tempUmurHari = tempUmurHari + "," + historyUmurHari[i].split(":")[0];
        }
//        return buildDayAgeArray(tempUmur,tempUmurHari);
        return buildDayAgeArray(tempUmur);
    }

    private String createHFASeries() {
        String[] historyUmur = createHFAAxis().split(",");
        if (historyUmur.length < 1 || historyUmur[0].equals(""))
            return "";
        String[] temp = historyTinggi.split(",");
        boolean isMale = !client.getDetails().get("gender").toLowerCase().contains("em");


        String result = "";
        for (int i = 0; i < historyUmur.length; i++) {
            if (i > 0)
                result = result + ",";
            result = result + Double.toString(calc.countHFA(isMale, Integer.parseInt(historyUmur[i]), Double.parseDouble(temp[i + 1].split(":")[1])));
        }
        //////System.out.println("hfa result = "+result);
        return result;
    }

    private String createWFHAxis() {
        String axis = createHFAAxis();
        if (axis.equals(""))
            return "";
        String result = "";

        for (int i = 0; i < axis.split(",").length; i++) {
            result = result + "," + Integer.toString(Integer.parseInt(axis.split(",")[i]) / 30);
        }
        return result.substring(1, result.length());
    }

    private String createWFHSeries() {
        String result = "";
        String uT = createWFHAxis();
        String u = historyUmur;
        ////System.out.println("u = "+u);
        String b = historyBerat;
        ////System.out.println("b = "+b);
        String t = historyTinggi;
        ////System.out.println("t = "+t);
        if (u == null || uT.equals("") || t == null)
            return "";
        String[] umurTinggi = uT.split(",");
        String[] umur = u.split(",");
        String[] berat = b.split(",");
        String[] tinggi = t.split(",");

        boolean isMale = !client.getDetails().get("gender").toLowerCase().contains("em");
        int j = 1;
        for (int i = 0; i < umurTinggi.length; i++) {
            for (; j < umur.length; j++) {
                if (umurTinggi[i].equals(Integer.toString(Integer.parseInt(umur[j]) / 30))) {
                    ////System.out.println("berat = "+berat[j]);
                    ////System.out.println("tinggi = "+ tinggi[i].split(":")[1]);
                    result = result + "," + Double.toString(Integer.parseInt(umurTinggi[i]) < 24
                            ? calc.countWFL(isMale, Double.parseDouble(berat[j]), Double.parseDouble(tinggi[i + 1].split(":")[1]))
                            : calc.countWFH(isMale, Double.parseDouble(berat[j]), Double.parseDouble(tinggi[i + 1].split(":")[1])));
                    break;
                }
            }
        }
        ////System.out.println("z-score = "+result);
        return result.length() > 1 ? result.substring(1, result.length()) : "";
    }

    private String buildDayAgeArray(String age) {
        if (age == null)
            return "";
        else if (age.equals(""))
            return "";

        String result = "";
        String[] huhLength = age.split(",");
        for (int i = 1; i < huhLength.length; i++) {
            result = result + "," + huhLength[i];
        }
        return result.length() < 1 ? "" : result.substring(1, result.length());
    }

    public String cleanBlankValueOf(String string) {
        if (string == null)
            return null;
        String[] tempArray = string.split(",");
        String tempString = "";
        for (int i = 0; i < tempArray.length; i++) {
            if (tempArray[i].charAt(tempArray[i].length() - 1) == ':')
                continue;
            tempString = tempString + "~" + tempArray[i]
                    + (tempArray[i].substring(tempArray[i].length() - 1).equalsIgnoreCase(":") ? "0" : "")
                    + "~";
        }
        return tempString.substring(1, tempString.length() - 1).replaceAll("~~", ",");
    }

    private String[] split(String data) {
        if (data == null)
            return new String[]{"0", "0"};
        if (!data.contains(":"))
            return new String[]{"0", "0"};
        String[] temp = data.split(",");
        String[] result = {"", ""};
        for (int i = 0; i < temp.length; i++) {
            result[0] = result[0] + "," + temp[i].split(":")[0];
            result[1] = result[1] + "," + temp[i].split(":")[1];
        }
        result[0] = result[0].substring(1, result[0].length());
        result[1] = result[1].substring(1, result[1].length());
        return result;
    }

}
