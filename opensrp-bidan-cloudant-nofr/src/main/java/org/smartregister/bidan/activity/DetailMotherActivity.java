package org.smartregister.bidan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.utils.Support;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static org.smartregister.util.StringUtil.humanize;

//import org.smartregister.bidan.lib.FlurryFacade;

/**
 */
public class DetailMotherActivity extends Activity {

    //image retrieving
    private static final String TAG = DetailMotherActivity.class.getName();
    public static CommonPersonObjectClient motherClient;
    static String entityid;
    private static HashMap<String, String> hash;
    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss", Locale.US);
//    ImageView kiview;
    private boolean updateMode = false;

//    public static void setImagetoHolderFromUri(Activity activity, String file, ImageView view, int placeholder) {
//        view.setImageDrawable(activity.getResources().getDrawable(placeholder));
//        File externalFile = new File(file);
//        if (externalFile.exists()) {
//            Uri external = Uri.fromFile(externalFile);
//            view.setImageURI(external);
//        }
//
//    }

    // Main Profile
    @Bind(R.id.tv_mother_detail_profile_view) ImageView kiview;

    @Bind(R.id.tv_wife_name) TextView nama;
    @Bind(R.id.tv_nik) TextView nik;
    @Bind(R.id.tv_husband_name) TextView husband_name;
    @Bind(R.id.tv_dob) TextView dob;
    @Bind(R.id.tv_contact_phone_number) TextView phone;
    @Bind(R.id.tv_risk1) TextView risk1;
    @Bind(R.id.tv_risk2) TextView risk2;
    @Bind(R.id.tv_risk3) TextView risk3;
    @Bind(R.id.tv_risk4) TextView risk4;

    @Bind(R.id.tv_show_more) TextView show_risk;

    @Bind(R.id.tv_show_more_detail) TextView show_detail;

    //detail data
    @Bind(R.id.tv_village_name)TextView village;
    @Bind(R.id.txt_subvillage)TextView subvillage;
    @Bind(R.id.txt_age)TextView age;
    @Bind(R.id.txt_alamat)TextView alamat;
    @Bind(R.id.txt_edu)TextView education;
    @Bind(R.id.txt_agama)TextView religion;
    @Bind(R.id.txt_job)TextView job;
    @Bind(R.id.txt_gakin)TextView gakin;
    @Bind(R.id.txt_blood)TextView blood_type;
    @Bind(R.id.txt_asuransi)TextView asuransi;

    //detail RISK
    @Bind(R.id.txt_highRiskSTIBBVs) TextView highRiskSTIBBVs;
    @Bind(R.id.txt_highRiskEctopicPregnancy) TextView highRiskEctopicPregnancy;
    @Bind(R.id.txt_highRiskCardiovascularDiseaseRecord) TextView highRiskCardiovascularDiseaseRecord;
    @Bind(R.id.txt_highRiskDidneyDisorder) TextView highRiskDidneyDisorder;
    @Bind(R.id.txt_highRiskHeartDisorder) TextView highRiskHeartDisorder;
    @Bind(R.id.txt_highRiskAsthma) TextView highRiskAsthma;
    @Bind(R.id.txt_highRiskTuberculosis) TextView highRiskTuberculosis;
    @Bind(R.id.txt_highRiskMalaria) TextView highRiskMalaria;
    @Bind(R.id.txt_highRiskPregnancyPIH) TextView highRiskPregnancyPIH;
    @Bind(R.id.txt_highRiskPregnancyProteinEnergyMalnutrition) TextView highRiskPregnancyProteinEnergyMalnutrition;
    @Bind(R.id.txt_highRiskLabourTBRisk) TextView txt_highRiskLabourTBRisk;
    @Bind(R.id.txt_HighRiskLabourSectionCesareaRecord) TextView txt_HighRiskLabourSectionCesareaRecord;
    @Bind(R.id.txt_highRisklabourFetusNumber) TextView txt_highRisklabourFetusNumber;
    @Bind(R.id.txt_highRiskLabourFetusSize) TextView txt_highRiskLabourFetusSize;
    @Bind(R.id.txt_lbl_highRiskLabourFetusMalpresentation) TextView txt_lbl_highRiskLabourFetusMalpresentation;
    @Bind(R.id.txt_highRiskPregnancyAnemia) TextView txt_highRiskPregnancyAnemia;
    @Bind(R.id.txt_highRiskPregnancyDiabetes) TextView txt_highRiskPregnancyDiabetes;
    @Bind(R.id.txt_HighRiskPregnancyTooManyChildren) TextView HighRiskPregnancyTooManyChildren;
    @Bind(R.id.txt_highRiskPostPartumSectioCaesaria) TextView highRiskPostPartumSectioCaesaria;
    @Bind(R.id.txt_highRiskPostPartumForceps) TextView highRiskPostPartumForceps;
    @Bind(R.id.txt_highRiskPostPartumVacum) TextView highRiskPostPartumVacum;
    @Bind(R.id.txt_highRiskPostPartumPreEclampsiaEclampsia) TextView highRiskPostPartumPreEclampsiaEclampsia;
    @Bind(R.id.txt_highRiskPostPartumMaternalSepsis) TextView highRiskPostPartumMaternalSepsis;
    @Bind(R.id.txt_highRiskPostPartumInfection) TextView highRiskPostPartumInfection;
    @Bind(R.id.txt_highRiskPostPartumHemorrhage) TextView highRiskPostPartumHemorrhage;
    @Bind(R.id.txt_highRiskPostPartumPIH) TextView highRiskPostPartumPIH;
    @Bind(R.id.txt_highRiskPostPartumDistosia) TextView highRiskPostPartumDistosia;
    @Bind(R.id.txt_highRiskHIVAIDS)TextView txt_highRiskHIVAIDS;

    @Bind(org.smartregister.R.id.btn_back_to_home) ImageButton back;

    @OnClick(R.id.btn_back_to_home) void sayHello() {
//        ButterKnife.apply(headerViews, ALPHA_FADE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.ki_detail_activity);

        String DetailStart = timer.format(new Date());
        Map<String, String> Detail = new HashMap<>();
        Detail.put("start", DetailStart);
//        FlurryAgent.logEvent("KI_detail_view",Detail, true );

        kiview = (ImageView) findViewById(R.id.tv_mother_detail_profile_view);

        //profile
//        TextView nama = (TextView) findViewById(R.id.txt_wife_name);
//        TextView nik = (TextView) findViewById(R.id.txt_nik);
//        TextView husband_name = (TextView) findViewById(R.id.txt_husband_name);
//        TextView dob = (TextView) findViewById(R.id.txt_dob);
//        TextView phone = (TextView) findViewById(R.id.txt_contact_phone_number);
//        TextView risk1 = (TextView) findViewById(R.id.txt_risk1);
//        TextView risk2 = (TextView) findViewById(R.id.txt_risk2);
//        TextView risk3 = (TextView) findViewById(R.id.txt_risk3);
//        TextView risk4 = (TextView) findViewById(R.id.txt_risk4);

//        final TextView show_risk = (TextView) findViewById(R.id.show_more);
//        final TextView show_detail = (TextView) findViewById(R.id.show_more_detail);

        //detail data
//        TextView village = (TextView) findViewById(R.id.txt_village_name);
//        TextView subvillage = (TextView) findViewById(R.id.txt_subvillage);
//        TextView age = (TextView) findViewById(R.id.txt_age);
//        TextView alamat = (TextView) findViewById(R.id.txt_alamat);
//        TextView education = (TextView) findViewById(R.id.txt_edu);
//        TextView religion = (TextView) findViewById(R.id.txt_agama);
//        TextView job = (TextView) findViewById(R.id.txt_job);
//        TextView gakin = (TextView) findViewById(R.id.txt_gakin);
//        TextView blood_type = (TextView) findViewById(R.id.txt_blood);
//        TextView asuransi = (TextView) findViewById(R.id.txt_asuransi);

        //detail RISK
//        TextView highRiskSTIBBVs = (TextView) findViewById(R.id.txt_highRiskSTIBBVs);
//        TextView highRiskEctopicPregnancy = (TextView) findViewById(R.id.txt_highRiskEctopicPregnancy);
//        TextView highRiskCardiovascularDiseaseRecord = (TextView) findViewById(R.id.txt_highRiskCardiovascularDiseaseRecord);
//        TextView highRiskDidneyDisorder = (TextView) findViewById(R.id.txt_highRiskDidneyDisorder);
//        TextView highRiskHeartDisorder = (TextView) findViewById(R.id.txt_highRiskHeartDisorder);
//        TextView highRiskAsthma = (TextView) findViewById(R.id.txt_highRiskAsthma);
//        TextView highRiskTuberculosis = (TextView) findViewById(R.id.txt_highRiskTuberculosis);
//        TextView highRiskMalaria = (TextView) findViewById(R.id.txt_highRiskMalaria);
//        TextView highRiskPregnancyPIH = (TextView) findViewById(R.id.txt_highRiskPregnancyPIH);
//        TextView highRiskPregnancyProteinEnergyMalnutrition = (TextView) findViewById(R.id.txt_highRiskPregnancyProteinEnergyMalnutrition);
//        TextView txt_highRiskLabourTBRisk = (TextView) findViewById(R.id.txt_highRiskLabourTBRisk);
//        TextView txt_HighRiskLabourSectionCesareaRecord = (TextView) findViewById(R.id.txt_HighRiskLabourSectionCesareaRecord);
//        TextView txt_highRisklabourFetusNumber = (TextView) findViewById(R.id.txt_highRisklabourFetusNumber);
//        TextView txt_highRiskLabourFetusSize = (TextView) findViewById(R.id.txt_highRiskLabourFetusSize);
//        TextView txt_lbl_highRiskLabourFetusMalpresentation = (TextView) findViewById(R.id.txt_lbl_highRiskLabourFetusMalpresentation);
//        TextView txt_highRiskPregnancyAnemia = (TextView) findViewById(R.id.txt_highRiskPregnancyAnemia);
//        TextView txt_highRiskPregnancyDiabetes = (TextView) findViewById(R.id.txt_highRiskPregnancyDiabetes);
//        TextView HighRiskPregnancyTooManyChildren = (TextView) findViewById(R.id.txt_HighRiskPregnancyTooManyChildren);
//        TextView highRiskPostPartumSectioCaesaria = (TextView) findViewById(R.id.txt_highRiskPostPartumSectioCaesaria);
//        TextView highRiskPostPartumForceps = (TextView) findViewById(R.id.txt_highRiskPostPartumForceps);
//        TextView highRiskPostPartumVacum = (TextView) findViewById(R.id.txt_highRiskPostPartumVacum);
//        TextView highRiskPostPartumPreEclampsiaEclampsia = (TextView) findViewById(R.id.txt_highRiskPostPartumPreEclampsiaEclampsia);
//        TextView highRiskPostPartumMaternalSepsis = (TextView) findViewById(R.id.txt_highRiskPostPartumMaternalSepsis);
//        TextView highRiskPostPartumInfection = (TextView) findViewById(R.id.txt_highRiskPostPartumInfection);
//        TextView highRiskPostPartumHemorrhage = (TextView) findViewById(R.id.txt_highRiskPostPartumHemorrhage);
//        TextView highRiskPostPartumPIH = (TextView) findViewById(R.id.txt_highRiskPostPartumPIH);
//        TextView highRiskPostPartumDistosia = (TextView) findViewById(R.id.txt_highRiskPostPartumDistosia);
//        TextView txt_highRiskHIVAIDS = (TextView) findViewById(R.id.txt_highRiskHIVAIDS);
//
//        ImageButton back = (ImageButton) findViewById(org.smartregister.R.id.btn_back_to_home);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DetailMotherActivity.this, NativeKIbuSmartRegisterActivity.class));
                overridePendingTransition(0, 0);

                String DetailEnd = timer.format(new Date());
                Map<String, String> Detail = new HashMap<>();
                Detail.put("end", DetailEnd);
//                FlurryAgent.logEvent("KI_detail_view", Detail, true);
            }
        });

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(motherClient);

        //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//        DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(motherClient.getCaseId(), OpenSRPImageLoader.getStaticImageListener(kiview, R.mipmap.woman_placeholder, R.mipmap.woman_placeholder));

        Support.setImagetoHolderFromUri(this,
                DrishtiApplication.getAppDir() + File.separator + motherClient.getDetails().get("base_entity_id") + ".JPEG",
                kiview, R.mipmap.woman_placeholder);

        nama.setText(String.format("%s%s", getResources().getString(R.string.name), motherClient.getColumnmaps().get("namalengkap") != null ? motherClient.getColumnmaps().get("namalengkap") : "-"));
        nik.setText(String.format("%s%s", getResources().getString(R.string.nik), motherClient.getDetails().get("nik") != null ? motherClient.getDetails().get("nik") : "-"));
        husband_name.setText(String.format("%s%s", getResources().getString(R.string.husband_name), motherClient.getColumnmaps().get("namaSuami") != null ? motherClient.getColumnmaps().get("namaSuami") : "-"));
        String tgl = motherClient.getDetails().get("tanggalLahir") != null ? motherClient.getDetails().get("tanggalLahir") : "-";

        String tgl_lahir = "null";
        if(tgl != null && !tgl.isEmpty()) {
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
        age.setText(String.format(": %s", humanize(motherClient.getColumnmaps().get("umur") != null ? motherClient.getColumnmaps().get("umur") : "-")));
        alamat.setText(String.format(": %s", humanize(motherClient.getDetails().get("address3") != null ? motherClient.getDetails().get("address3") : "-")));
        education.setText(String.format(": %s", humanize(motherClient.getDetails().get("pendidikan") != null ? motherClient.getDetails().get("pendidikan") : "-")));
        religion.setText(String.format(": %s", humanize(motherClient.getDetails().get("agama") != null ? motherClient.getDetails().get("agama") : "-")));
        job.setText(String.format(": %s", humanize(motherClient.getDetails().get("pekerjaan") != null ? motherClient.getDetails().get("pekerjaan") : "-")));
        gakin.setText(String.format(": %s", humanize(motherClient.getDetails().get("gakinTidak") != null ? motherClient.getDetails().get("gakinTidak") : "-")));
        blood_type.setText(String.format(": %s", humanize(motherClient.getDetails().get("golonganDarah") != null ? motherClient.getDetails().get("golonganDarah") : "-")));
        asuransi.setText(String.format(": %s", humanize(motherClient.getDetails().get("asuransiJiwa") != null ? motherClient.getDetails().get("asuransiJiwa") : "-")));

        //risk detail
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

//        hash = Tools.retrieveHash(context.applicationContext());

        kiview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FlurryFacade.logEvent("taking_mother_pictures_on_kohort_ibu_detail_view");
                entityid = motherClient.entityId();

//                if(hash.containsValue(entityid)){
//                    updateMode = true;
//                }
//
//                Intent takePictureIntent = new Intent(DetailMotherActivity.this, SmartShutterActivity.class);
//                takePictureIntent.putExtra("org.sid.sidface.SmartShutterActivity.updated", updateMode);
//                takePictureIntent.putExtra("IdentifyPerson", false);
//                takePictureIntent.putExtra("org.sid.sidface.ImageConfirmation.id", entityid);
//                takePictureIntent.putExtra("org.sid.sidface.ImageConfirmation.origin", TAG); // send Class Name
//                startActivityForResult(takePictureIntent, 2);

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, NativeKIbuSmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        refresh
        Log.e(TAG, "onActivityResult: refresh");
        finish();
        startActivity(getIntent());

    }
}
