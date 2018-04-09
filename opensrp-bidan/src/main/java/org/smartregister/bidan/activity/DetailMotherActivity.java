package org.smartregister.bidan.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.bidan.R;
import org.smartregister.bidan.utils.CameraPreviewActivity;
import org.smartregister.bidan.utils.Support;
import org.smartregister.bidan.utils.Tools;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.smartregister.util.StringUtil.humanize;


/**
 * Created by SID
 */
public class DetailMotherActivity extends Activity {

    //image retrieving
    private static final String TAG = DetailMotherActivity.class.getName();
    public static CommonPersonObjectClient motherClient;
    static String entityid;
    // Main Profile
    @Bind(R.id.tv_mother_detail_profile_view)
    private ImageView kiview;
    @Bind(R.id.tv_wife_name)
    private TextView nama;
    @Bind(R.id.tv_nik)
    private TextView nik;
    @Bind(R.id.tv_husband_name)
    private TextView husband_name;
    @Bind(R.id.tv_dob)
    private TextView dob;
    @Bind(R.id.tv_contact_phone_number)
    private TextView phone;
    @Bind(R.id.tv_risk1)
    private TextView risk1;
    @Bind(R.id.tv_risk2)
    private TextView risk2;
    @Bind(R.id.tv_risk3)
    private TextView risk3;
    @Bind(R.id.tv_risk4)
    private TextView risk4;
    @Bind(R.id.tv_show_more)
    private TextView show_risk;
    @Bind(R.id.tv_show_more_detail)
    private TextView show_detail;
    //detail data
    @Bind(R.id.tv_village_name)
    private TextView village;
    @Bind(R.id.txt_subvillage)
    private TextView subvillage;
    @Bind(R.id.txt_age)
    private TextView age;
    @Bind(R.id.txt_alamat)
    private TextView alamat;
    @Bind(R.id.txt_edu)
    private TextView education;
    @Bind(R.id.txt_agama)
    private TextView religion;
    @Bind(R.id.txt_job)
    private TextView job;
    @Bind(R.id.txt_gakin)
    private TextView gakin;
    @Bind(R.id.txt_blood)
    private TextView blood_type;
    @Bind(R.id.txt_asuransi)
    private TextView asuransi;
    //detail RISK
    @Bind(R.id.txt_highRiskSTIBBVs)
    private TextView highRiskSTIBBVs;
    @Bind(R.id.txt_highRiskEctopicPregnancy)
    private TextView highRiskEctopicPregnancy;
    @Bind(R.id.txt_highRiskCardiovascularDiseaseRecord)
    private TextView highRiskCardiovascularDiseaseRecord;
    @Bind(R.id.txt_highRiskDidneyDisorder)
    private TextView highRiskDidneyDisorder;
    @Bind(R.id.txt_highRiskHeartDisorder)
    private TextView highRiskHeartDisorder;
    @Bind(R.id.txt_highRiskAsthma)
    private TextView highRiskAsthma;
    @Bind(R.id.txt_highRiskTuberculosis)
    private TextView highRiskTuberculosis;
    @Bind(R.id.txt_highRiskMalaria)
    private TextView highRiskMalaria;
    @Bind(R.id.txt_hrp_PIH)
    private TextView highRiskPregnancyPIH;
    @Bind(R.id.txt_hrp_PEM)
    private TextView highRiskPregnancyProteinEnergyMalnutrition;
    @Bind(R.id.txt_highRiskLabourTBRisk)
    private TextView txt_highRiskLabourTBRisk;
    @Bind(R.id.txt_HighRiskLabourSectionCesareaRecord)
    private TextView txt_HighRiskLabourSectionCesareaRecord;
    @Bind(R.id.txt_hrl_FetusNumber)
    private TextView txt_highRisklabourFetusNumber;
    @Bind(R.id.txt_hrl_FetusSize)
    private TextView txt_highRiskLabourFetusSize;
    @Bind(R.id.txt_hrl_FetusMalpresentation)
    private TextView txt_lbl_highRiskLabourFetusMalpresentation;
    @Bind(R.id.txt_hrp_Anemia)
    private TextView txt_highRiskPregnancyAnemia;
    @Bind(R.id.txt_hrp_Diabetes)
    private TextView txt_highRiskPregnancyDiabetes;
    @Bind(R.id.txt_HighRiskPregnancyTooManyChildren)
    private TextView HighRiskPregnancyTooManyChildren;
    @Bind(R.id.txt_hrpp_SC)
    private TextView highRiskPostPartumSectioCaesaria;
    @Bind(R.id.txt_hrpp_Forceps)
    private TextView highRiskPostPartumForceps;
    @Bind(R.id.txt_hrpp_Vacum)
    private TextView highRiskPostPartumVacum;
    @Bind(R.id.txt_hrpp_PreEclampsia)
    private TextView highRiskPostPartumPreEclampsiaEclampsia;
    @Bind(R.id.txt_hrpp_MaternalSepsis)
    private TextView highRiskPostPartumMaternalSepsis;
    @Bind(R.id.txt_hrpp_Infection)
    private TextView highRiskPostPartumInfection;
    @Bind(R.id.txt_hrpp_Hemorrhage)
    private TextView highRiskPostPartumHemorrhage;
    @Bind(R.id.txt_hrpp_PIH)
    private TextView highRiskPostPartumPIH;
    @Bind(R.id.txt_hrpp_Distosia)
    private TextView highRiskPostPartumDistosia;
    @Bind(R.id.txt_highRiskHIVAIDS)
    private TextView txt_highRiskHIVAIDS;
    @Bind(R.id.btn_back_to_home)
    private ImageButton back;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ki_detail_activity);

        ButterKnife.bind(this);

        userId = motherClient.getDetails().get("base_entity_id");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DetailMotherActivity.this, KISmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(motherClient);

        Support.setImagetoHolderFromUri(this, getObjValue(motherClient, "base_entity_id"), kiview, R.mipmap.woman_placeholder);
//        motherClient.getDetails().get("base_entity_id"), kiview, R.mipmap.woman_placeholder);

        nama.setText(String.format("%s%s", getResources().getString(R.string.name), motherClient.getColumnmaps().get("namalengkap") != null ? motherClient.getColumnmaps().get("namalengkap") : "-"));
        nik.setText(String.format("%s%s", getResources().getString(R.string.nik), motherClient.getDetails().get("nik") != null ? motherClient.getDetails().get("nik") : "-"));
        husband_name.setText(String.format("%s%s", getResources().getString(R.string.husband_name), motherClient.getColumnmaps().get("namaSuami") != null ? motherClient.getColumnmaps().get("namaSuami") : "-"));
        String tgl = motherClient.getDetails().get("tanggalLahir") != null ? motherClient.getDetails().get("tanggalLahir") : "-";

        String tgl_lahir = "null";
        if (tgl != null && !tgl.isEmpty()) {
            tgl_lahir = tgl.substring(0, tgl.indexOf("T"));
        }

        dob.setText(String.format("%s%s", getResources().getString(R.string.dob), tgl_lahir));
        phone.setText(String.format("No HP: %s", motherClient.getDetails().get("NomorTelponHp") != null ? motherClient.getDetails().get("NomorTelponHp") : "-"));

        //risk
        if (motherClient.getDetails().get("highRiskPregnancyYoungMaternalAge") != null) {
            risk1.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyYoungMaternalAge), humanize(motherClient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if (motherClient.getDetails().get("highRiskPregnancyOldMaternalAge") != null) {
            risk1.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyOldMaternalAge), humanize(motherClient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if (motherClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null
                || motherClient.getDetails().get("HighRiskPregnancyAbortus") != null
                || motherClient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null
                ) {
            risk2.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyProteinEnergyMalnutrition), humanize(motherClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"))));
            risk3.setText(String.format("%s%s", getResources().getString(R.string.HighRiskPregnancyAbortus), humanize(motherClient.getDetails().get("HighRiskPregnancyAbortus"))));
            risk4.setText(String.format("%s%s", getResources().getString(R.string.HighRiskLabourSectionCesareaRecord), humanize(motherClient.getDetails().get("HighRiskLabourSectionCesareaRecord"))));
        }

        show_risk.setText(getResources().getString(R.string.show_more_button));
        show_detail.setText(getResources().getString(R.string.show_less_button));

        //detail
        village.setText(String.format(": %s", humanize(motherClient.getDetails().get("cityVillage") != null ? motherClient.getDetails().get("cityVillage") : "-")));
        subvillage.setText(String.format(": %s", humanize(motherClient.getDetails().get("address1") != null ? motherClient.getDetails().get("address1") : "-")));
        alamat.setText(String.format(": %s", humanize(motherClient.getDetails().get("address3") != null ? motherClient.getDetails().get("address3") : "-")));
        age.setText(String.format(": %s", humanize(motherClient.getColumnmaps().get("umur") != null ? motherClient.getColumnmaps().get("umur") : "-")));
        education.setText(String.format(": %s", humanize(motherClient.getDetails().get("pendidikan") != null ? motherClient.getDetails().get("pendidikan") : "-")));
        religion.setText(String.format(": %s", humanize(motherClient.getDetails().get("agama") != null ? motherClient.getDetails().get("agama") : "-")));
        job.setText(String.format(": %s", humanize(motherClient.getDetails().get("pekerjaan") != null ? motherClient.getDetails().get("pekerjaan") : "-")));
        gakin.setText(String.format(": %s", humanize(motherClient.getDetails().get("gakinTidak") != null ? motherClient.getDetails().get("gakinTidak") : "-")));
        blood_type.setText(String.format(": %s", humanize(motherClient.getDetails().get("golonganDarah") != null ? motherClient.getDetails().get("golonganDarah") : "-")));
        asuransi.setText(String.format(": %s", humanize(motherClient.getDetails().get("asuransiJiwa") != null ? motherClient.getDetails().get("asuransiJiwa") : "-")));

        // ========================================================================================|
        // Risks Detail                                                                            |
        // ========================================================================================|
        highRiskSTIBBVs.setText(humanize(motherClient.getDetails().get("highRiskSTIBBVs") != null ? motherClient.getDetails().get("highRiskSTIBBVs") : "-"));
        highRiskEctopicPregnancy.setText(humanize(motherClient.getDetails().get("highRiskEctopicPregnancy") != null ? motherClient.getDetails().get("highRiskEctopicPregnancy") : "-"));
        highRiskCardiovascularDiseaseRecord.setText(humanize(motherClient.getDetails().get("highRiskCardiovascularDiseaseRecord") != null ? motherClient.getDetails().get("highRiskCardiovascularDiseaseRecord") : "-"));
        highRiskDidneyDisorder.setText(humanize(motherClient.getDetails().get("highRiskDidneyDisorder") != null ? motherClient.getDetails().get("highRiskDidneyDisorder") : "-"));
        highRiskHeartDisorder.setText(humanize(motherClient.getDetails().get("highRiskHeartDisorder") != null ? motherClient.getDetails().get("highRiskHeartDisorder") : "-"));
        highRiskAsthma.setText(humanize(motherClient.getDetails().get("highRiskAsthma") != null ? motherClient.getDetails().get("highRiskAsthma") : "-"));
        highRiskTuberculosis.setText(humanize(motherClient.getDetails().get("highRiskTuberculosis") != null ? motherClient.getDetails().get("highRiskTuberculosis") : "-"));
        highRiskMalaria.setText(humanize(motherClient.getDetails().get("highRiskMalaria") != null ? motherClient.getDetails().get("highRiskMalaria") : "-"));
        txt_HighRiskLabourSectionCesareaRecord.setText(humanize(motherClient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null ? motherClient.getDetails().get("HighRiskLabourSectionCesareaRecord") : "-"));
        HighRiskPregnancyTooManyChildren.setText(humanize(motherClient.getDetails().get("HighRiskPregnancyTooManyChildren") != null ? motherClient.getDetails().get("HighRiskPregnancyTooManyChildren") : "-"));
        txt_highRiskHIVAIDS.setText(humanize(motherClient.getDetails().get("highRiskHIVAIDS") != null ? motherClient.getDetails().get("highRiskHIVAIDS") : "-"));


        txt_lbl_highRiskLabourFetusMalpresentation.setText(humanize(motherClient.getDetails().get("highRiskLabourFetusMalpresentation") != null ? motherClient.getDetails().get("highRiskLabourFetusMalpresentation") : "-"));
        txt_highRisklabourFetusNumber.setText(humanize(motherClient.getDetails().get("highRisklabourFetusNumber") != null ? motherClient.getDetails().get("highRisklabourFetusNumber") : "-"));
        txt_highRiskLabourFetusSize.setText(humanize(motherClient.getDetails().get("highRiskLabourFetusSize") != null ? motherClient.getDetails().get("highRiskLabourFetusSize") : "-"));
        txt_highRiskLabourTBRisk.setText(humanize(motherClient.getDetails().get("highRiskLabourTBRisk") != null ? motherClient.getDetails().get("highRiskLabourTBRisk") : "-"));
        highRiskPregnancyProteinEnergyMalnutrition.setText(humanize(motherClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null ? motherClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") : "-"));
        highRiskPregnancyPIH.setText(humanize(motherClient.getDetails().get("highRiskPregnancyPIH") != null ? motherClient.getDetails().get("highRiskPregnancyPIH") : "-"));
        txt_highRiskPregnancyDiabetes.setText(humanize(motherClient.getDetails().get("highRiskPregnancyDiabetes") != null ? motherClient.getDetails().get("highRiskPregnancyDiabetes") : "-"));
        txt_highRiskPregnancyAnemia.setText(humanize(motherClient.getDetails().get("highRiskPregnancyAnemia") != null ? motherClient.getDetails().get("highRiskPregnancyAnemia") : "-"));
        highRiskPostPartumSectioCaesaria.setText(humanize(motherClient.getDetails().get("highRiskPostPartumSectioCaesaria") != null ? motherClient.getDetails().get("highRiskPostPartumSectioCaesaria") : "-"));
        highRiskPostPartumForceps.setText(humanize(motherClient.getDetails().get("highRiskPostPartumForceps") != null ? motherClient.getDetails().get("highRiskPostPartumForceps") : "-"));
        highRiskPostPartumVacum.setText(humanize(motherClient.getDetails().get("highRiskPostPartumVacum") != null ? motherClient.getDetails().get("highRiskPostPartumVacum") : "-"));
        highRiskPostPartumPreEclampsiaEclampsia.setText(humanize(motherClient.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") != null ? motherClient.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") : "-"));
        highRiskPostPartumMaternalSepsis.setText(humanize(motherClient.getDetails().get("highRiskPostPartumMaternalSepsis") != null ? motherClient.getDetails().get("highRiskPostPartumMaternalSepsis") : "-"));
        highRiskPostPartumInfection.setText(humanize(motherClient.getDetails().get("highRiskPostPartumInfection") != null ? motherClient.getDetails().get("highRiskPostPartumInfection") : "-"));
        highRiskPostPartumHemorrhage.setText(humanize(motherClient.getDetails().get("highRiskPostPartumHemorrhage") != null ? motherClient.getDetails().get("highRiskPostPartumHemorrhage") : "-"));
        highRiskPostPartumPIH.setText(humanize(motherClient.getDetails().get("highRiskPostPartumPIH") != null ? motherClient.getDetails().get("highRiskPostPartumPIH") : "-"));
        highRiskPostPartumDistosia.setText(humanize(motherClient.getDetails().get("highRiskPostPartumDistosia") != null ? motherClient.getDetails().get("highRiskPostPartumDistosia") : "-"));

        show_risk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FlurryFacade.logEvent("click_risk_detail");
                findViewById(R.id.id1).setVisibility(View.GONE);
                findViewById(R.id.id2).setVisibility(View.VISIBLE);
                findViewById(R.id.tv_show_more_detail).setVisibility(View.VISIBLE);
                findViewById(R.id.tv_show_more).setVisibility(View.GONE);
            }
        });

        show_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(View.VISIBLE);
                findViewById(R.id.id2).setVisibility(View.GONE);
                findViewById(R.id.tv_show_more).setVisibility(View.VISIBLE);
                findViewById(R.id.tv_show_more_detail).setVisibility(View.GONE);
            }
        });

        kiview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entityid = motherClient.entityId();

                Intent intent = new Intent(DetailMotherActivity.this, CameraPreviewActivity.class);
                intent.putExtra(CameraPreviewActivity.REQUEST_TYPE, 201);
                startActivityForResult(intent, 201);

            }
        });

    }

    private String getObjValue(CommonPersonObjectClient motherClient, String base_entity_id) {

        return motherClient.getDetails().get(base_entity_id);

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, KISmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 201) {
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

            finish();
        }

        finish();
        startActivity(getIntent());

    }
}
