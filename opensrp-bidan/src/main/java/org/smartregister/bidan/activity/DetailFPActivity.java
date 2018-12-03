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
import org.smartregister.bidan.utils.Support;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.DetailsRepository;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.smartregister.util.StringUtil.humanize;
/**
 * Created by sid-tech on 11/30/17
 */

public class DetailFPActivity extends Activity {

//    private static final String TAG = DetailFPActivity.class.getName();
    public static CommonPersonObjectClient kiclient;
//    private SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
//    private View.OnClickListener bpmListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Context context = Context.getInstance();
        setContentView(R.layout.kb_detail_activity);


//        String DetailStart = timer.format(new Date());
//        Map<String, String> Detail = new HashMap<String, String>();
//        Detail.put("start", DetailStart);

//        FlurryAgent.logEvent("KB_detail_view", Detail, true);

        final ImageView kiview = (ImageView) findViewById(R.id.tv_mother_detail_profile_view);
        //header
//        TextView today = (TextView) findViewById(R.id.tv_detail_today);

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
        TextView td_diastolik = (TextView) findViewById(R.id.txt_td_diastolik);
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
        TextView highRiskPregnancyPIH = (TextView) findViewById(R.id.txt_hrp_PIH);
        TextView highRiskPregnancyProteinEnergyMalnutrition = (TextView) findViewById(R.id.txt_hrp_PEM);

        TextView txt_highRiskLabourTBRisk = (TextView) findViewById(R.id.txt_highRiskLabourTBRisk);
        TextView txt_HighRiskLabourSectionCesareaRecord = (TextView) findViewById(R.id.txt_HighRiskLabourSectionCesareaRecord);
        TextView txt_highRisklabourFetusNumber = (TextView) findViewById(R.id.txt_hrl_FetusNumber);
        TextView txt_highRiskLabourFetusSize = (TextView) findViewById(R.id.txt_hrl_FetusSize);
        TextView txt_lbl_highRiskLabourFetusMalpresentation = (TextView) findViewById(R.id.txt_hrl_FetusMalpresentation);
        TextView txt_highRiskPregnancyAnemia = (TextView) findViewById(R.id.txt_hrp_Anemia);
        TextView txt_highRiskPregnancyDiabetes = (TextView) findViewById(R.id.txt_hrp_Diabetes);
        TextView HighRiskPregnancyTooManyChildren = (TextView) findViewById(R.id.txt_HighRiskPregnancyTooManyChildren);
        TextView highRiskPostPartumSectioCaesaria = (TextView) findViewById(R.id.txt_hrpp_SC);
        TextView highRiskPostPartumForceps = (TextView) findViewById(R.id.txt_hrpp_Forceps);
        TextView highRiskPostPartumVacum = (TextView) findViewById(R.id.txt_hrpp_Vacum);
        TextView highRiskPostPartumPreEclampsiaEclampsia = (TextView) findViewById(R.id.txt_hrpp_PreEclampsia);
        TextView highRiskPostPartumMaternalSepsis = (TextView) findViewById(R.id.txt_hrpp_MaternalSepsis);
        TextView highRiskPostPartumInfection = (TextView) findViewById(R.id.txt_hrpp_Infection);
        TextView highRiskPostPartumHemorrhage = (TextView) findViewById(R.id.txt_hrpp_Hemorrhage);

        TextView highRiskPostPartumPIH = (TextView) findViewById(R.id.txt_hrpp_PIH);
        TextView highRiskPostPartumDistosia = (TextView) findViewById(R.id.txt_hrpp_Distosia);
        TextView txt_highRiskHIVAIDS = (TextView) findViewById(R.id.txt_highRiskHIVAIDS);


        ImageButton back = (ImageButton) findViewById(R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DetailFPActivity.this, FPSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
//                String DetailEnd = timer.format(new Date());
//                Map<String, String> Detail = new HashMap<>();
//                Detail.put("end", DetailEnd);
//                FlurryAgent.logEvent("KB_detail_view", Detail, true);
            }
        });

        DetailsRepository detailsRepository = Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(kiclient);

        if (kiclient.getCaseId() != null) {//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(kiclient.getCaseId(), OpenSRPImageLoader.getStaticImageListener(kiview, R.mipmap.woman_placeholder, R.mipmap.woman_placeholder));
            Support.setImagetoHolderFromUri(this,
                    kiclient.getDetails().get("base_entity_id"),
                    kiview, R.mipmap.woman_placeholder);
        }


        nama.setText(String.format("%s%s", getResources().getString(R.string.name), kiclient.getColumnmaps().get("namalengkap") != null ? kiclient.getColumnmaps().get("namalengkap") : "-"));
        nik.setText(String.format("%s%s", getResources().getString(R.string.nik), kiclient.getDetails().get("nik") != null ? kiclient.getDetails().get("nik") : "-"));
        husband_name.setText(String.format("%s%s", getResources().getString(R.string.husband_name), kiclient.getColumnmaps().get("namaSuami") != null ? kiclient.getColumnmaps().get("namaSuami") : "-"));
        String tgl = kiclient.getDetails().get("tanggalLahir") != null ? kiclient.getDetails().get("tanggalLahir") : "-";
        String tgl_lahir = null;
        if (tgl != null && !tgl.isEmpty()) {
            tgl_lahir = tgl.substring(0, tgl.indexOf("T"));
        }

        dob.setText(String.format("%s%s", getResources().getString(R.string.dob), tgl_lahir));
        phone.setText(String.format("No HP: %s", kiclient.getDetails().get("NomorTelponHp") != null ? kiclient.getDetails().get("NomorTelponHp") : "-"));

        //risk
        if (kiclient.getDetails().get("highRiskPregnancyYoungMaternalAge") != null) {
            risk1.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyYoungMaternalAge), humanize(kiclient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if (kiclient.getDetails().get("highRiskPregnancyOldMaternalAge") != null) {
            risk1.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyOldMaternalAge), humanize(kiclient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if (kiclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null
                || kiclient.getDetails().get("HighRiskPregnancyAbortus") != null
                || kiclient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null
                ) {
            risk2.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyProteinEnergyMalnutrition), humanize(kiclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"))));
            risk3.setText(String.format("%s%s", getResources().getString(R.string.HighRiskPregnancyAbortus), humanize(kiclient.getDetails().get("HighRiskPregnancyAbortus"))));
            risk4.setText(String.format("%s%s", getResources().getString(R.string.HighRiskLabourSectionCesareaRecord), humanize(kiclient.getDetails().get("HighRiskLabourSectionCesareaRecord"))));

        }

        show_risk.setText(getResources().getString(R.string.show_more_button));
        show_detail.setText(getResources().getString(R.string.show_less_button));

        village.setText(String.format(": %s", humanize(kiclient.getDetails().get("cityVillage") != null ? kiclient.getDetails().get("cityVillage") : "-")));
        subvillage.setText(String.format(": %s", humanize(kiclient.getDetails().get("address1") != null ? kiclient.getDetails().get("address1") : "-")));
        age.setText(String.format(": %s", humanize(kiclient.getColumnmaps().get("umur") != null ? kiclient.getColumnmaps().get("umur") : "-")));
        alamat.setText(String.format(": %s", humanize(kiclient.getDetails().get("address3") != null ? kiclient.getDetails().get("address3") : "-")));
        education.setText(String.format(": %s", humanize(kiclient.getDetails().get("pendidikan") != null ? kiclient.getDetails().get("pendidikan") : "-")));
        religion.setText(String.format(": %s", humanize(kiclient.getDetails().get("agama") != null ? kiclient.getDetails().get("agama") : "-")));
        job.setText(String.format(": %s", humanize(kiclient.getDetails().get("pekerjaan") != null ? kiclient.getDetails().get("pekerjaan") : "-")));
        gakin.setText(String.format(": %s", humanize(kiclient.getDetails().get("gakinTidak") != null ? kiclient.getDetails().get("gakinTidak") : "-")));
        blood_type.setText(String.format(": %s", humanize(kiclient.getDetails().get("golonganDarah") != null ? kiclient.getDetails().get("golonganDarah") : "-")));
        asuransi.setText(String.format(": %s", humanize(kiclient.getDetails().get("asuransiJiwa") != null ? kiclient.getDetails().get("asuransiJiwa") : "-")));

        jenisKontrasepsi.setText(String.format(": %s", humanize(kiclient.getDetails().get("jenisKontrasepsi") != null ? kiclient.getDetails().get("jenisKontrasepsi") : "-")));
        alkihb.setText(String.format(": %s", humanize(kiclient.getDetails().get("alkihb") != null ? kiclient.getDetails().get("alkihb") : "-")));
        tdSistolik.setText(String.format(": %s", humanize(kiclient.getDetails().get("tdSistolik") != null ? kiclient.getDetails().get("tdSistolik") : "-")));
        td_diastolik.setText(String.format(": %s", humanize(kiclient.getDetails().get("tdDiastolik") != null ? kiclient.getDetails().get("tdDiastolik") : "-")));
        alkilila.setText(String.format(": %s", humanize(kiclient.getDetails().get("alkilila") != null ? kiclient.getDetails().get("alkilila") : "-")));
        alkiPenyakitIms.setText(String.format(": %s", humanize(kiclient.getDetails().get("alkiPenyakitIms") != null ? kiclient.getDetails().get("alkiPenyakitIms") : "-")));
        keteranganTentangPesertaKB.setText(String.format(": %s", humanize(kiclient.getDetails().get("keteranganTentangPesertaKB") != null ? kiclient.getDetails().get("keteranganTentangPesertaKB") : "-")));
        keteranganTentangPesertaKB2.setText(String.format(": %s", humanize(kiclient.getDetails().get("keterangantentangPesertaKB2") != null ? kiclient.getDetails().get("keterangantentangPesertaKB2") : "-")));
        alkiPenyakitKronis.setText(String.format(": %s", humanize(kiclient.getDetails().get("alkiPenyakitKronis") != null ? kiclient.getDetails().get("alkiPenyakitKronis") : "-")));
        keteranganGantiCara.setText(String.format(": %s", humanize(kiclient.getDetails().get("keteranganGantiCara") != null ? kiclient.getDetails().get("keteranganGantiCara") : "-")));

//      risk detail
        highRiskSTIBBVs.setText(humanize(kiclient.getDetails().get("highRiskSTIBBVs") != null ? kiclient.getDetails().get("highRiskSTIBBVs") : "-"));
        highRiskEctopicPregnancy.setText(humanize(kiclient.getDetails().get("highRiskEctopicPregnancy") != null ? kiclient.getDetails().get("highRiskEctopicPregnancy") : "-"));
        highRiskCardiovascularDiseaseRecord.setText(humanize(kiclient.getDetails().get("highRiskCardiovascularDiseaseRecord") != null ? kiclient.getDetails().get("highRiskCardiovascularDiseaseRecord") : "-"));
        highRiskDidneyDisorder.setText(humanize(kiclient.getDetails().get("highRiskDidneyDisorder") != null ? kiclient.getDetails().get("highRiskDidneyDisorder") : "-"));
        highRiskHeartDisorder.setText(humanize(kiclient.getDetails().get("highRiskHeartDisorder") != null ? kiclient.getDetails().get("highRiskHeartDisorder") : "-"));
        highRiskAsthma.setText(humanize(kiclient.getDetails().get("highRiskAsthma") != null ? kiclient.getDetails().get("highRiskAsthma") : "-"));
        highRiskTuberculosis.setText(humanize(kiclient.getDetails().get("highRiskTuberculosis") != null ? kiclient.getDetails().get("highRiskTuberculosis") : "-"));
        highRiskMalaria.setText(humanize(kiclient.getDetails().get("highRiskMalaria") != null ? kiclient.getDetails().get("highRiskMalaria") : "-"));

        txt_HighRiskLabourSectionCesareaRecord.setText(humanize(kiclient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null ? kiclient.getDetails().get("HighRiskLabourSectionCesareaRecord") : "-"));
        HighRiskPregnancyTooManyChildren.setText(humanize(kiclient.getDetails().get("HighRiskPregnancyTooManyChildren") != null ? kiclient.getDetails().get("HighRiskPregnancyTooManyChildren") : "-"));

        txt_highRiskHIVAIDS.setText(humanize(kiclient.getDetails().get("highRiskHIVAIDS") != null ? kiclient.getDetails().get("highRiskHIVAIDS") : "-"));

        AllCommonsRepository iburep = Context.getInstance().allCommonsRepositoryobjects("ibu");
        if (kiclient.getColumnmaps().get("ibu.id") != null) {
            final CommonPersonObject ibuparent = iburep.findByCaseID(kiclient.getColumnmaps().get("ibu.id"));

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
                findViewById(R.id.id1).setVisibility(GONE);
                findViewById(R.id.id2).setVisibility(VISIBLE);
                findViewById(R.id.tv_show_more_detail).setVisibility(VISIBLE);
                findViewById(R.id.tv_show_more).setVisibility(GONE);
            }
        });

        show_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(VISIBLE);
                findViewById(R.id.id2).setVisibility(GONE);
                findViewById(R.id.tv_show_more).setVisibility(VISIBLE);
                findViewById(R.id.tv_show_more_detail).setVisibility(GONE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, FPSmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }
}
