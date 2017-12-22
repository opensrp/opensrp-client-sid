package org.smartregister.bidan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.facialrecognition.activities.OpenCameraActivity;
import org.smartregister.facialrecognition.util.BitmapUtil;
import org.smartregister.facialrecognition.utils.Tools;
import org.smartregister.repository.DetailsRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.smartregister.bidan.utils.Support.setImagetoHolderFromUri;
import static org.smartregister.util.StringUtil.humanize;

//import org.smartregister.bidan.activity.v1.NativeKBSmartRegisterActivity;

/**
 * Created by sid-tech on 11/30/17.
 */

public class DetailFPActivity extends Activity {

    private static final String TAG = DetailFPActivity.class.getName();
    public static CommonPersonObjectClient fpClient;
    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.kb_detail_activity);


        String DetailStart = timer.format(new Date());
        Map<String, String> Detail = new HashMap<String, String>();
        Detail.put("start", DetailStart);

//        FlurryAgent.logEvent("KB_detail_view", Detail, true);

        final ImageView kiview = (ImageView) findViewById(R.id.tv_mother_detail_profile_view);
        //header
        TextView today = (TextView) findViewById(R.id.tv_detail_today);

        //profile
        TextView nama = (TextView) findViewById(R.id.tv_wife_name);
        TextView nik = (TextView) findViewById(R.id.tv_nik);
        TextView husband_name = (TextView) findViewById(R.id.tv_husband_name);
        TextView dob = (TextView) findViewById(R.id.tv_dob);
        TextView phone = (TextView) findViewById(R.id.tv_contact_phone_number);
        TextView risk1 = (TextView) findViewById(R.id.tv_risk1);
        TextView risk2 = (TextView) findViewById(R.id.tv_risk2);
        TextView risk3 = (TextView) findViewById(R.id.tv_risk3);
        TextView risk4 = (TextView) findViewById(R.id.tv_risk4);

        final TextView show_risk = (TextView) findViewById(R.id.tv_show_more);
        final TextView show_detail = (TextView) findViewById(R.id.tv_show_more_detail);

        //detail data
        TextView village = (TextView) findViewById(R.id.tv_village_name);
        TextView subvillage = (TextView) findViewById(R.id.txt_subvillage);
        TextView age = (TextView) findViewById(R.id.txt_age);
        TextView alamat = (TextView) findViewById(R.id.txt_alamat);
        TextView education = (TextView) findViewById(R.id.txt_edu);
        TextView religion = (TextView) findViewById(R.id.txt_agama);
        TextView job = (TextView) findViewById(R.id.txt_job);
        TextView gakin = (TextView) findViewById(R.id.txt_gakin);
        TextView blood_type = (TextView) findViewById(R.id.txt_blood);
        TextView asuransi = (TextView) findViewById(R.id.txt_asuransi);

        TextView jenisKontrasepsi = (TextView) findViewById(R.id.txt_jenisKontrasepsi);
        TextView td_diastolik = (TextView) findViewById(R.id.tv_td_diastolik);
        TextView tdSistolik = (TextView) findViewById(R.id.txt_tdSistolik);
        TextView alkilila = (TextView) findViewById(R.id.txt_alkilila);
        TextView alkiPenyakitIms = (TextView) findViewById(R.id.txt_alkiPenyakitIms);
        TextView keteranganTentangPesertaKB = (TextView) findViewById(R.id.txt_keteranganTentangPesertaKB);
        TextView keteranganTentangPesertaKB2 = (TextView) findViewById(R.id.txt_keteranganTentangPesertaKB2);
        TextView alkiPenyakitKronis = (TextView) findViewById(R.id.txt_alkiPenyakitKronis);
        TextView alkihb = (TextView) findViewById(R.id.txt_alkihb);
        TextView keteranganGantiCara = (TextView) findViewById(R.id.txt_keteranganGantiCara);

        //detail RISK
        TextView highRiskSTIBBVs = (TextView) findViewById(R.id.txt_highRiskSTIBBVs);
        TextView highRiskEctopicPregnancy = (TextView) findViewById(R.id.txt_highRiskEctopicPregnancy);
        TextView highRiskCardiovascularDiseaseRecord = (TextView) findViewById(R.id.txt_highRiskCardiovascularDiseaseRecord);
        TextView highRiskDidneyDisorder = (TextView) findViewById(R.id.txt_highRiskDidneyDisorder);
        TextView highRiskHeartDisorder = (TextView) findViewById(R.id.txt_highRiskHeartDisorder);
        TextView highRiskAsthma = (TextView) findViewById(R.id.txt_highRiskAsthma);
        TextView highRiskTuberculosis = (TextView) findViewById(R.id.txt_highRiskTuberculosis);
        TextView highRiskMalaria = (TextView) findViewById(R.id.txt_highRiskMalaria);
        TextView highRiskPregnancyPIH = (TextView) findViewById(R.id.txt_highRiskPregnancyPIH);
        TextView highRiskPregnancyProteinEnergyMalnutrition = (TextView) findViewById(R.id.txt_highRiskPregnancyProteinEnergyMalnutrition);

        TextView txt_highRiskLabourTBRisk = (TextView) findViewById(R.id.txt_highRiskLabourTBRisk);
        TextView txt_HighRiskLabourSectionCesareaRecord = (TextView) findViewById(R.id.txt_HighRiskLabourSectionCesareaRecord);
        TextView txt_highRisklabourFetusNumber = (TextView) findViewById(R.id.txt_highRisklabourFetusNumber);
        TextView txt_highRiskLabourFetusSize = (TextView) findViewById(R.id.txt_highRiskLabourFetusSize);
        TextView txt_lbl_highRiskLabourFetusMalpresentation = (TextView) findViewById(R.id.txt_lbl_highRiskLabourFetusMalpresentation);
        TextView txt_highRiskPregnancyAnemia = (TextView) findViewById(R.id.txt_highRiskPregnancyAnemia);
        TextView txt_highRiskPregnancyDiabetes = (TextView) findViewById(R.id.txt_highRiskPregnancyDiabetes);
        TextView HighRiskPregnancyTooManyChildren = (TextView) findViewById(R.id.txt_HighRiskPregnancyTooManyChildren);
        TextView highRiskPostPartumSectioCaesaria = (TextView) findViewById(R.id.txt_highRiskPostPartumSectioCaesaria);
        TextView highRiskPostPartumForceps = (TextView) findViewById(R.id.txt_highRiskPostPartumForceps);
        TextView highRiskPostPartumVacum = (TextView) findViewById(R.id.txt_highRiskPostPartumVacum);
        TextView highRiskPostPartumPreEclampsiaEclampsia = (TextView) findViewById(R.id.txt_highRiskPostPartumPreEclampsiaEclampsia);
        TextView highRiskPostPartumMaternalSepsis = (TextView) findViewById(R.id.txt_highRiskPostPartumMaternalSepsis);
        TextView highRiskPostPartumInfection = (TextView) findViewById(R.id.txt_highRiskPostPartumInfection);
        TextView highRiskPostPartumHemorrhage = (TextView) findViewById(R.id.txt_highRiskPostPartumHemorrhage);

        TextView highRiskPostPartumPIH = (TextView) findViewById(R.id.txt_highRiskPostPartumPIH);
        TextView highRiskPostPartumDistosia = (TextView) findViewById(R.id.txt_highRiskPostPartumDistosia);
        TextView txt_highRiskHIVAIDS = (TextView) findViewById(R.id.txt_highRiskHIVAIDS);


        ImageButton back = (ImageButton) findViewById(R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DetailFPActivity.this, NativeKIFPSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
                String DetailEnd = timer.format(new Date());
                Map<String, String> Detail = new HashMap<>();
                Detail.put("end", DetailEnd);
//                FlurryAgent.logEvent("KB_detail_view", Detail, true);
            }
        });

        DetailsRepository detailsRepository = Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(fpClient);

        if (fpClient.getCaseId() != null) {//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(fpClient.getCaseId(), OpenSRPImageLoader.getStaticImageListener(kiview, R.mipmap.woman_placeholder, R.mipmap.woman_placeholder));
            setImagetoHolderFromUri(this, fpClient.getDetails().get("base_entity_id"), kiview, R.mipmap.woman_placeholder);
        }


        nama.setText(String.format("%s%s", getResources().getString(R.string.name), fpClient.getColumnmaps().get("namalengkap") != null ? fpClient.getColumnmaps().get("namalengkap") : "-"));
        nik.setText(String.format("%s%s", getResources().getString(R.string.nik), fpClient.getDetails().get("nik") != null ? fpClient.getDetails().get("nik") : "-"));
        husband_name.setText(String.format("%s%s", getResources().getString(R.string.husband_name), fpClient.getColumnmaps().get("namaSuami") != null ? fpClient.getColumnmaps().get("namaSuami") : "-"));
        String tgl = fpClient.getDetails().get("tanggalLahir") != null ? fpClient.getDetails().get("tanggalLahir") : "-";
        String tgl_lahir = null;
        if (tgl != null && !tgl.isEmpty()) {
            tgl_lahir = tgl.substring(0, tgl.indexOf("T"));
        }

        dob.setText(String.format("%s%s", getResources().getString(R.string.dob), tgl_lahir));
        phone.setText(String.format("No HP: %s", fpClient.getDetails().get("NomorTelponHp") != null ? fpClient.getDetails().get("NomorTelponHp") : "-"));

        //risk
        if (fpClient.getDetails().get("highRiskPregnancyYoungMaternalAge") != null) {
            risk1.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyYoungMaternalAge), humanize(fpClient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if (fpClient.getDetails().get("highRiskPregnancyOldMaternalAge") != null) {
            risk1.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyOldMaternalAge), humanize(fpClient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if (fpClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null
                || fpClient.getDetails().get("HighRiskPregnancyAbortus") != null
                || fpClient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null
                ) {
            risk2.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyProteinEnergyMalnutrition), humanize(fpClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"))));
            risk3.setText(String.format("%s%s", getResources().getString(R.string.HighRiskPregnancyAbortus), humanize(fpClient.getDetails().get("HighRiskPregnancyAbortus"))));
            risk4.setText(String.format("%s%s", getResources().getString(R.string.HighRiskLabourSectionCesareaRecord), humanize(fpClient.getDetails().get("HighRiskLabourSectionCesareaRecord"))));

        }

        show_risk.setText(getResources().getString(R.string.show_more_button));
        show_detail.setText(getResources().getString(R.string.show_less_button));

        village.setText(String.format(": %s", humanize(fpClient.getDetails().get("cityVillage") != null ? fpClient.getDetails().get("cityVillage") : "-")));
        subvillage.setText(String.format(": %s", humanize(fpClient.getDetails().get("address1") != null ? fpClient.getDetails().get("address1") : "-")));
        age.setText(String.format(": %s", humanize(fpClient.getColumnmaps().get("umur") != null ? fpClient.getColumnmaps().get("umur") : "-")));
        alamat.setText(String.format(": %s", humanize(fpClient.getDetails().get("address3") != null ? fpClient.getDetails().get("address3") : "-")));
        education.setText(String.format(": %s", humanize(fpClient.getDetails().get("pendidikan") != null ? fpClient.getDetails().get("pendidikan") : "-")));
        religion.setText(String.format(": %s", humanize(fpClient.getDetails().get("agama") != null ? fpClient.getDetails().get("agama") : "-")));
        job.setText(String.format(": %s", humanize(fpClient.getDetails().get("pekerjaan") != null ? fpClient.getDetails().get("pekerjaan") : "-")));
        gakin.setText(String.format(": %s", humanize(fpClient.getDetails().get("gakinTidak") != null ? fpClient.getDetails().get("gakinTidak") : "-")));
        blood_type.setText(String.format(": %s", humanize(fpClient.getDetails().get("golonganDarah") != null ? fpClient.getDetails().get("golonganDarah") : "-")));
        asuransi.setText(String.format(": %s", humanize(fpClient.getDetails().get("jamkesmas") != null ? fpClient.getDetails().get("jamkesmas") : "-")));

        jenisKontrasepsi.setText(String.format(": %s", humanize(fpClient.getDetails().get("jenisKontrasepsi") != null ? fpClient.getDetails().get("jenisKontrasepsi") : "-")));
        alkihb.setText(String.format(": %s", humanize(fpClient.getDetails().get("alkihb") != null ? fpClient.getDetails().get("alkihb") : "-")));
        tdSistolik.setText(String.format(": %s", humanize(fpClient.getDetails().get("tdDiastolik") != null ? fpClient.getDetails().get("tdDiastolik") : "-")));
        td_diastolik.setText(String.format(": %s", humanize(fpClient.getDetails().get("tdDiastolik") != null ? fpClient.getDetails().get("tdDiastolik") : "-")));
        alkilila.setText(String.format(": %s", humanize(fpClient.getDetails().get("alkilila") != null ? fpClient.getDetails().get("alkilila") : "-")));
        alkiPenyakitIms.setText(String.format(": %s", humanize(fpClient.getDetails().get("alkiPenyakitIms") != null ? fpClient.getDetails().get("alkiPenyakitIms") : "-")));
        keteranganTentangPesertaKB.setText(String.format(": %s", humanize(fpClient.getDetails().get("keteranganTentangPesertaKB") != null ? fpClient.getDetails().get("keteranganTentangPesertaKB") : "-")));
        keteranganTentangPesertaKB2.setText(String.format(": %s", humanize(fpClient.getDetails().get("keterangantentangPesertaKB2") != null ? fpClient.getDetails().get("keterangantentangPesertaKB2") : "-")));
        alkiPenyakitKronis.setText(String.format(": %s", humanize(fpClient.getDetails().get("alkiPenyakitKronis") != null ? fpClient.getDetails().get("alkiPenyakitKronis") : "-")));
        keteranganGantiCara.setText(String.format(": %s", humanize(fpClient.getDetails().get("keteranganGantiCara") != null ? fpClient.getDetails().get("keteranganGantiCara") : "-")));

//      risk detail
        highRiskSTIBBVs.setText(humanize(fpClient.getDetails().get("highRiskSTIBBVs") != null ? fpClient.getDetails().get("highRiskSTIBBVs") : "-"));
        highRiskEctopicPregnancy.setText(humanize(fpClient.getDetails().get("highRiskEctopicPregnancy") != null ? fpClient.getDetails().get("highRiskEctopicPregnancy") : "-"));
        highRiskCardiovascularDiseaseRecord.setText(humanize(fpClient.getDetails().get("highRiskCardiovascularDiseaseRecord") != null ? fpClient.getDetails().get("highRiskCardiovascularDiseaseRecord") : "-"));
        highRiskDidneyDisorder.setText(humanize(fpClient.getDetails().get("highRiskDidneyDisorder") != null ? fpClient.getDetails().get("highRiskDidneyDisorder") : "-"));
        highRiskHeartDisorder.setText(humanize(fpClient.getDetails().get("highRiskHeartDisorder") != null ? fpClient.getDetails().get("highRiskHeartDisorder") : "-"));
        highRiskAsthma.setText(humanize(fpClient.getDetails().get("highRiskAsthma") != null ? fpClient.getDetails().get("highRiskAsthma") : "-"));
        highRiskTuberculosis.setText(humanize(fpClient.getDetails().get("highRiskTuberculosis") != null ? fpClient.getDetails().get("highRiskTuberculosis") : "-"));
        highRiskMalaria.setText(humanize(fpClient.getDetails().get("highRiskMalaria") != null ? fpClient.getDetails().get("highRiskMalaria") : "-"));

        txt_HighRiskLabourSectionCesareaRecord.setText(humanize(fpClient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null ? fpClient.getDetails().get("HighRiskLabourSectionCesareaRecord") : "-"));
        HighRiskPregnancyTooManyChildren.setText(humanize(fpClient.getDetails().get("HighRiskPregnancyTooManyChildren") != null ? fpClient.getDetails().get("HighRiskPregnancyTooManyChildren") : "-"));

        txt_highRiskHIVAIDS.setText(humanize(fpClient.getDetails().get("highRiskHIVAIDS") != null ? fpClient.getDetails().get("highRiskHIVAIDS") : "-"));

        AllCommonsRepository iburep = Context.getInstance().allCommonsRepositoryobjects("ibu");
        if (fpClient.getColumnmaps().get("ibu.id") != null) {
            final CommonPersonObject ibuparent = iburep.findByCaseID(fpClient.getColumnmaps().get("ibu.id"));

            txt_lbl_highRiskLabourFetusMalpresentation.setText(humanize(ibuparent.getDetails().get("highRiskLabourFetusMalpresentation") != null ? ibuparent.getDetails().get("highRiskLabourFetusMalpresentation") : "-"));
            txt_highRisklabourFetusNumber.setText(humanize(ibuparent.getDetails().get("highRisklabourFetusNumber") != null ? ibuparent.getDetails().get("highRisklabourFetusNumber") : "-"));
            txt_highRiskLabourFetusSize.setText(humanize(ibuparent.getDetails().get("highRiskLabourFetusSize") != null ? ibuparent.getDetails().get("highRiskLabourFetusSize") : "-"));
            txt_highRiskLabourTBRisk.setText(humanize(ibuparent.getDetails().get("highRiskLabourTBRisk") != null ? ibuparent.getDetails().get("highRiskLabourTBRisk") : "-"));
            highRiskPregnancyProteinEnergyMalnutrition.setText(humanize(ibuparent.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null ? ibuparent.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") : "-"));
            highRiskPregnancyPIH.setText(humanize(ibuparent.getDetails().get("highRiskPregnancyPIH") != null ? ibuparent.getDetails().get("highRiskPregnancyPIH") : "-"));
            txt_highRiskPregnancyDiabetes.setText(humanize(ibuparent.getDetails().get("highRiskPregnancyDiabetes") != null ? ibuparent.getDetails().get("highRiskPregnancyDiabetes") : "-"));
            txt_highRiskPregnancyAnemia.setText(humanize(ibuparent.getDetails().get("highRiskPregnancyAnemia") != null ? ibuparent.getDetails().get("highRiskPregnancyAnemia") : "-"));

            highRiskPostPartumSectioCaesaria.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumSectioCaesaria") != null ? ibuparent.getDetails().get("highRiskPostPartumSectioCaesaria") : "-"));
            highRiskPostPartumForceps.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumForceps") != null ? ibuparent.getDetails().get("highRiskPostPartumForceps") : "-"));
            highRiskPostPartumVacum.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumVacum") != null ? ibuparent.getDetails().get("highRiskPostPartumVacum") : "-"));
            highRiskPostPartumPreEclampsiaEclampsia.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") != null ? ibuparent.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") : "-"));
            highRiskPostPartumMaternalSepsis.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumMaternalSepsis") != null ? ibuparent.getDetails().get("highRiskPostPartumMaternalSepsis") : "-"));
            highRiskPostPartumInfection.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumInfection") != null ? ibuparent.getDetails().get("highRiskPostPartumInfection") : "-"));
            highRiskPostPartumHemorrhage.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumHemorrhage") != null ? ibuparent.getDetails().get("highRiskPostPartumHemorrhage") : "-"));
            highRiskPostPartumPIH.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumPIH") != null ? ibuparent.getDetails().get("highRiskPostPartumPIH") : "-"));
            highRiskPostPartumDistosia.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumDistosia") != null ? ibuparent.getDetails().get("highRiskPostPartumDistosia") : "-"));
        }

        show_risk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FlurryFacade.logEvent("click_risk_detail");
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

        // Enable FR
        BitmapUtil.enableFR(context, DetailFPActivity.this, fpClient, kiview) ;


    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, NativeKIFPSmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }
}
