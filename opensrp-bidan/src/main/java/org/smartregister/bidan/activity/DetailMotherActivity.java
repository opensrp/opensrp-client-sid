package org.smartregister.bidan.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.utils.CameraPreviewActivity;
import org.smartregister.bidan.utils.Support;
import org.smartregister.bidan.utils.Tools;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.smartregister.util.StringUtil.humanize;

//import butterknife.ButterKnife;
/**
 * Created by SID
 */
public class DetailMotherActivity extends Activity {

    //image retrieving
    private static final String TAG = DetailMotherActivity.class.getName();
    public static CommonPersonObjectClient motherClient;
//    static String entityid;
    // Main Profile
//    @Bind(R.id.tv_mother_detail_profile_view)
//    private ImageView kiview;
//    @Bind(R.id.tv_wife_name)
//    private TextView nama;
//    @Bind(R.id.tv_nik)
//    private TextView nik;
//    @Bind(R.id.tv_husband_name)
//    private TextView husband_name;
//    @Bind(R.id.tv_dob)
//    private TextView dob;
//    @Bind(R.id.tv_contact_phone_number)
//    private TextView phone;
//    @Bind(R.id.tv_risk1)
//    private TextView risk1;
//    @Bind(R.id.tv_risk2)
//    private TextView risk2;
//    @Bind(R.id.tv_risk3)
//    private TextView risk3;
//    @Bind(R.id.tv_risk4)
//    private TextView risk4;
//    @Bind(R.id.tv_show_more)
//    private TextView show_risk;
//    @Bind(R.id.tv_show_more_detail)
//    private TextView show_detail;
//    //detail data
//    @Bind(R.id.tv_village_name)
//    private TextView village;
//    @Bind(R.id.txt_subvillage)
//    private TextView subvillage;
//    @Bind(R.id.txt_age)
//    private TextView age;
//    @Bind(R.id.txt_alamat)
//    private TextView alamat;
//    @Bind(R.id.txt_edu)
//    private TextView education;
//    @Bind(R.id.txt_agama)
//    private TextView religion;
//    @Bind(R.id.txt_job)
//    private TextView job;
//    @Bind(R.id.txt_gakin)
//    private TextView gakin;
//    @Bind(R.id.txt_blood)
//    private TextView blood_type;
//    @Bind(R.id.txt_asuransi)
//    private TextView asuransi;
//    //detail RISK
//    @Bind(R.id.txt_highRiskSTIBBVs)
//    private TextView highRiskSTIBBVs;
//    @Bind(R.id.txt_highRiskEctopicPregnancy)
//    private TextView highRiskEctopicPregnancy;
//    @Bind(R.id.txt_highRiskCardiovascularDiseaseRecord)
//    private TextView highRiskCardiovascularDiseaseRecord;
//    @Bind(R.id.txt_highRiskDidneyDisorder)
//    private TextView highRiskDidneyDisorder;
//    @Bind(R.id.txt_highRiskHeartDisorder)
//    private TextView highRiskHeartDisorder;
//    @Bind(R.id.txt_highRiskAsthma)
//    private TextView highRiskAsthma;
//    @Bind(R.id.txt_highRiskTuberculosis)
//    private TextView highRiskTuberculosis;
//    @Bind(R.id.txt_highRiskMalaria)
//    private TextView highRiskMalaria;
//    @Bind(R.id.txt_hrp_PIH)
//    private TextView highRiskPregnancyPIH;
//    @Bind(R.id.txt_hrp_PEM)
//    private TextView highRiskPregnancyProteinEnergyMalnutrition;
//    @Bind(R.id.txt_highRiskLabourTBRisk)
//    private TextView txt_highRiskLabourTBRisk;
//    @Bind(R.id.txt_HighRiskLabourSectionCesareaRecord)
//    private TextView txt_HighRiskLabourSectionCesareaRecord;
//    @Bind(R.id.txt_hrl_FetusNumber)
//    private TextView txt_highRisklabourFetusNumber;
//    @Bind(R.id.txt_hrl_FetusSize)
//    private TextView txt_highRiskLabourFetusSize;
//    @Bind(R.id.txt_hrl_FetusMalpresentation)
//    private TextView txt_lbl_highRiskLabourFetusMalpresentation;
//    @Bind(R.id.txt_hrp_Anemia)
//    private TextView txt_highRiskPregnancyAnemia;
//    @Bind(R.id.txt_hrp_Diabetes)
//    private TextView txt_highRiskPregnancyDiabetes;
//    @Bind(R.id.txt_HighRiskPregnancyTooManyChildren)
//    private TextView HighRiskPregnancyTooManyChildren;
//    @Bind(R.id.txt_hrpp_SC)
//    private TextView highRiskPostPartumSectioCaesaria;
//    @Bind(R.id.txt_hrpp_Forceps)
//    private TextView highRiskPostPartumForceps;
//    @Bind(R.id.txt_hrpp_Vacum)
//    private TextView highRiskPostPartumVacum;
//    @Bind(R.id.txt_hrpp_PreEclampsia)
//    private TextView highRiskPostPartumPreEclampsiaEclampsia;
//    @Bind(R.id.txt_hrpp_MaternalSepsis)
//    private TextView highRiskPostPartumMaternalSepsis;
//    @Bind(R.id.txt_hrpp_Infection)
//    private TextView highRiskPostPartumInfection;
//    @Bind(R.id.txt_hrpp_Hemorrhage)
//    private TextView highRiskPostPartumHemorrhage;
//    @Bind(R.id.txt_hrpp_PIH)
//    private TextView highRiskPostPartumPIH;
//    @Bind(R.id.txt_hrpp_Distosia)
//    private TextView highRiskPostPartumDistosia;
//    @Bind(R.id.txt_highRiskHIVAIDS)
//    private TextView txt_highRiskHIVAIDS;
//    @Bind(R.id.btn_back_to_home)
//    private ImageButton back;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ki_detail_activity);

//        ButterKnife.bind(this);
        userId = motherClient.getDetails().get("base_entity_id");

        findViewById(R.id.btn_back_to_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DetailsRepository detailsRepository = Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(motherClient);

        Support.setImagetoHolderFromUri(this, getObjValue(motherClient, "base_entity_id"), ((ImageView) findViewById(R.id.tv_mother_detail_profile_view)), R.mipmap.woman_placeholder);
//        motherClient.getDetails().get("base_entity_id"), kiview, R.mipmap.woman_placeholder);

        ((TextView) findViewById(R.id.tv_wife_name)).setText(String.format("%s%s", getResources().getString(R.string.name), motherClient.getColumnmaps().get("namalengkap") != null ? motherClient.getColumnmaps().get("namalengkap") : "-"));
        ((TextView) findViewById(R.id.tv_nik)).setText(String.format("%s%s", getResources().getString(R.string.nik), motherClient.getDetails().get("nik") != null ? motherClient.getDetails().get("nik") : "-"));
        ((TextView) findViewById(R.id.tv_husband_name)).setText(String.format("%s%s", getResources().getString(R.string.husband_name), motherClient.getColumnmaps().get("namaSuami") != null ? motherClient.getColumnmaps().get("namaSuami") : "-"));
        String tgl = motherClient.getDetails().get("tanggalLahir") != null ? motherClient.getDetails().get("tanggalLahir") : "-";

        String tgl_lahir = "null";
        if (tgl != null && !tgl.isEmpty()) {
            tgl_lahir = tgl.substring(0, tgl.indexOf("T"));
        }

        ((TextView) findViewById(R.id.tv_dob)).setText(String.format("%s%s", getResources().getString(R.string.dob), tgl_lahir));
        ((TextView) findViewById(R.id.tv_contact_phone_number)).setText(String.format("No HP: %s", motherClient.getDetails().get("NomorTelponHp") != null ? motherClient.getDetails().get("NomorTelponHp") : "-"));

        //risk
        if (motherClient.getDetails().get("highRiskPregnancyYoungMaternalAge") != null) {
            ((TextView) findViewById(R.id.tv_risk1)).setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyYoungMaternalAge), humanize(motherClient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if (motherClient.getDetails().get("highRiskPregnancyOldMaternalAge") != null) {
            ((TextView) findViewById(R.id.tv_risk1)).setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyOldMaternalAge), humanize(motherClient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }

        if (motherClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null
                || motherClient.getDetails().get("HighRiskPregnancyAbortus") != null
                || motherClient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null
                ) {
            ((TextView) findViewById(R.id.tv_risk2)).setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyProteinEnergyMalnutrition), humanize(motherClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"))));
            ((TextView) findViewById(R.id.tv_risk3)).setText(String.format("%s%s", getResources().getString(R.string.HighRiskPregnancyAbortus), humanize(motherClient.getDetails().get("HighRiskPregnancyAbortus"))));
            ((TextView) findViewById(R.id.tv_risk4)).setText(String.format("%s%s", getResources().getString(R.string.HighRiskLabourSectionCesareaRecord), humanize(motherClient.getDetails().get("HighRiskLabourSectionCesareaRecord"))));
        }

        ((TextView) findViewById(R.id.tv_show_more)).setText(getResources().getString(R.string.show_more_button));
        ((TextView) findViewById(R.id.tv_show_more_detail)).setText(getResources().getString(R.string.show_less_button));

        // Bio Detail
        ((TextView) findViewById(R.id.tv_village_name)).setText(getStrValue("cityVillage"));
        ((TextView) findViewById(R.id.txt_subvillage)).setText(getStrValue("address1"));
        ((TextView) findViewById(R.id.txt_alamat)).setText(getStrValue("address3"));
        ((TextView) findViewById(R.id.txt_age)).setText(getStrValue("umur"));
        ((TextView) findViewById(R.id.txt_edu)).setText(getStrValue("pendidikan"));
        ((TextView) findViewById(R.id.txt_agama)).setText(getStrValue("agama"));
        ((TextView) findViewById(R.id.txt_job)).setText(getStrValue("pekerjaan"));
        ((TextView) findViewById(R.id.txt_gakin)).setText(getStrValue("gakinTidak"));
        ((TextView) findViewById(R.id.txt_blood)).setText(getStrValue("golonganDarah"));
        ((TextView) findViewById(R.id.txt_asuransi)).setText(getStrValue("asuransiJiwa"));
        // ========================================================================================|
        // Risks Detail                                                                            |
        // ========================================================================================|
        ((TextView) findViewById(R.id.txt_highRiskSTIBBVs)).setText(getStrValue("highRiskSTIBBVs"));
        ((TextView) findViewById(R.id.txt_highRiskEctopicPregnancy)).setText(getStrValue("highRiskEctopicPregnancy"));
        ((TextView) findViewById(R.id.txt_highRiskCardiovascularDiseaseRecord)).setText(getStrValue("highRiskCardiovascularDiseaseRecord"));
        ((TextView) findViewById(R.id.txt_highRiskDidneyDisorder)).setText(getStrValue("highRiskDidneyDisorder"));
        ((TextView) findViewById(R.id.txt_highRiskHeartDisorder)).setText(getStrValue("highRiskHeartDisorder"));
        ((TextView) findViewById(R.id.txt_highRiskAsthma)).setText(getStrValue("highRiskAsthma"));
        ((TextView) findViewById(R.id.txt_highRiskTuberculosis)).setText(getStrValue("highRiskTuberculosis"));
        ((TextView) findViewById(R.id.txt_highRiskMalaria)).setText(getStrValue("highRiskMalaria"));
        ((TextView) findViewById(R.id.txt_HighRiskLabourSectionCesareaRecord)).setText(getStrValue("HighRiskLabourSectionCesareaRecord"));
        ((TextView) findViewById(R.id.txt_HighRiskPregnancyTooManyChildren)).setText(getStrValue("HighRiskPregnancyTooManyChildren"));
        ((TextView) findViewById(R.id.txt_highRiskHIVAIDS)).setText(getStrValue("highRiskHIVAIDS"));

        ((TextView) findViewById(R.id.txt_hrl_FetusMalpresentation)).setText(getStrValue("highRiskLabourFetusMalpresentation"));
        ((TextView) findViewById(R.id.txt_hrl_FetusNumber)).setText(getStrValue("highRisklabourFetusNumber"));
        ((TextView) findViewById(R.id.txt_hrl_FetusSize)).setText(getStrValue("highRiskLabourFetusSize"));
        ((TextView) findViewById(R.id.txt_highRiskLabourTBRisk)).setText(getStrValue("highRiskLabourTBRisk"));
        ((TextView) findViewById(R.id.txt_hrp_PEM)).setText(getStrValue("highRiskPregnancyProteinEnergyMalnutrition"));
        ((TextView) findViewById(R.id.txt_hrp_PIH)).setText(getStrValue("highRiskPregnancyPIH"));
        ((TextView) findViewById(R.id.txt_hrp_Diabetes)).setText(getStrValue("highRiskPregnancyDiabetes"));
        ((TextView) findViewById(R.id.txt_hrp_Anemia)).setText(getStrValue("highRiskPregnancyAnemia"));
        ((TextView) findViewById(R.id.txt_hrpp_SC)).setText(getStrValue("highRiskPostPartumSectioCaesaria"));
        ((TextView) findViewById(R.id.txt_hrpp_Forceps)).setText(getStrValue("highRiskPostPartumForceps"));
        ((TextView) findViewById(R.id.txt_hrpp_Vacum)).setText(getStrValue("highRiskPostPartumVacum"));
        ((TextView) findViewById(R.id.txt_hrpp_PreEclampsia)).setText(getStrValue("highRiskPostPartumPreEclampsiaEclampsia"));
        ((TextView) findViewById(R.id.txt_hrpp_MaternalSepsis)).setText(getStrValue("highRiskPostPartumMaternalSepsis"));
        ((TextView) findViewById(R.id.txt_hrpp_Infection)).setText(getStrValue("highRiskPostPartumInfection"));
        ((TextView) findViewById(R.id.txt_hrpp_Hemorrhage)).setText(getStrValue("highRiskPostPartumHemorrhage"));
        ((TextView) findViewById(R.id.txt_hrpp_PIH)).setText(getStrValue("highRiskPostPartumPIH"));
        ((TextView) findViewById(R.id.txt_hrpp_Distosia)).setText(getStrValue("highRiskPostPartumDistosia"));

        findViewById(R.id.tv_show_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FlurryFacade.logEvent("click_risk_detail");
                findViewById(R.id.id1).setVisibility(GONE);
                findViewById(R.id.id2).setVisibility(VISIBLE);
                findViewById(R.id.tv_show_more_detail).setVisibility(VISIBLE);
                findViewById(R.id.tv_show_more).setVisibility(GONE);
            }
        });

        findViewById(R.id.tv_show_more_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(VISIBLE);
                findViewById(R.id.id2).setVisibility(GONE);
                findViewById(R.id.tv_show_more).setVisibility(VISIBLE);
                findViewById(R.id.tv_show_more_detail).setVisibility(GONE);
            }
        });

        findViewById(R.id.tv_mother_detail_profile_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                entityid = motherClient.entityId();
                Intent intent = new Intent(DetailMotherActivity.this, CameraPreviewActivity.class);
                intent.putExtra(CameraPreviewActivity.REQUEST_TYPE, 201);
                startActivityForResult(intent, 201);

            }
        });

    }

    private String getObjValue(CommonPersonObjectClient motherClient, String base_entity_id) {

        return motherClient.getDetails().get(base_entity_id);

    }

    private String getStrValue(String str) {
        return String.format(": %s", humanize(motherClient.getDetails().get(str) != null ? motherClient.getDetails().get(str) : "-"));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult: onActivityResult()");
        if (requestCode == 201 && resultCode==-1) {
            Log.d(TAG, "onActivityResult: Process Photo");
            StringBuilder path = new StringBuilder();
            path.append(DrishtiApplication.getAppDir());

            File file = new File(path.toString());
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdir();
            }
            if (file.canWrite()) {
                path.append(File.separator).append(userId).append(".jpg");
                Tools.saveFile(Tools.scaleDown((Bitmap) intent.getExtras().get("data"), 400.0f, false), path.toString());
            }

        }
        Log.d(TAG, "onActivityResult: getIntent() = "+getIntent().toString());
        finish();
        startActivity(getIntent());

    }
}
