package org.smartregister.bidan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.utils.BidanFormUtils;
import org.smartregister.bidan.utils.Support;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.DetailsRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.smartregister.util.StringUtil.humanize;
import static org.smartregister.util.StringUtil.humanizeAndDoUPPERCASE;

//import org.smartregister.bidan.lib.FlurryFacade;

/**
 * Created by Iq on 07/09/16.
 */
public class DetailANCActivity extends Activity {

    private static final String TAG = DetailANCActivity.class.getSimpleName();
    public static CommonPersonObjectClient ancClient;
    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    SimpleDateFormat bpm_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anc_detail_activity);

        String DetailStart = timer.format(new Date());
        Map<String, String> Detail = new HashMap<String, String>();
        Detail.put("start", DetailStart);
        //FlurryAgent.logEvent("ANC_detail_view",Detail, true );

        // FR
        Context context = Context.getInstance();


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


        TextView details = (TextView) findViewById(R.id.tv_child_detail_information);
        TextView risk = (TextView) findViewById(R.id.tv_detail_today);
        TextView plan = (TextView) findViewById(R.id.tv_detail_l);


        TextView mother_summary = (TextView) findViewById(R.id.tv_mother_summary);
        // birth plan
        TextView txt_b_edd = (TextView) findViewById(R.id.tv_b_edd);
        TextView txt_b_lastvisit = (TextView) findViewById(R.id.tv_b_lastvisit);
        TextView txt_b_location = (TextView) findViewById(R.id.tv_b_location);

        TextView txt_b_edd2 = (TextView) findViewById(R.id.tv_b_edd2);
        TextView txt_b_attend = (TextView) findViewById(R.id.tv_b_attend);
        TextView txt_b_place = (TextView) findViewById(R.id.tv_b_place);
        TextView txt_b_person = (TextView) findViewById(R.id.tv_b_person);
        TextView txt_b_transport = (TextView) findViewById(R.id.tv_b_transport);
        TextView txt_b_blood = (TextView) findViewById(R.id.tv_b_blood);
        TextView txt_b_houser = (TextView) findViewById(R.id.tv_b_houser);
        TextView txt_b_stock = (TextView) findViewById(R.id.tv_b_stock);


        //detail data
        TextView Keterangan_k1k4 = (TextView) findViewById(R.id.txt_Keterangan_k1k4);
        TextView ancDate = (TextView) findViewById(R.id.txt_ancDate);
        TextView tanggalHPHT = (TextView) findViewById(R.id.txt_tanggalHPHT);
        TextView usiaKlinis = (TextView) findViewById(R.id.txt_usiaKlinis);
        TextView trimesterKe = (TextView) findViewById(R.id.txt_trimesterKe);
        TextView kunjunganKe = (TextView) findViewById(R.id.txt_kunjunganKe);
        TextView ancKe = (TextView) findViewById(R.id.txt_ancKe);
        TextView bbKg = (TextView) findViewById(R.id.txt_bbKg);
        TextView tandaVitalTDSistolik = (TextView) findViewById(R.id.txt_tandaVitalTDSistolik);
        TextView tandaVitalTDDiastolik = (TextView) findViewById(R.id.txt_tandaVitalTDDiastolik);
        TextView hasilPemeriksaanLILA = (TextView) findViewById(R.id.txt_hasilPemeriksaanLILA);
        TextView statusGiziibu = (TextView) findViewById(R.id.txt_statusGiziibu);
        TextView tfu = (TextView) findViewById(R.id.txt_tfu);
        TextView refleksPatelaIbu = (TextView) findViewById(R.id.txt_refleksPatelaIbu);
        TextView djj = (TextView) findViewById(R.id.txt_djj);
        TextView kepalaJaninTerhadapPAP = (TextView) findViewById(R.id.txt_kepalaJaninTerhadapPAP);
        TextView taksiranBeratJanin = (TextView) findViewById(R.id.txt_taksiranBeratJanin);
        TextView persentasiJanin = (TextView) findViewById(R.id.txt_persentasiJanin);
        TextView jumlahJanin = (TextView) findViewById(R.id.txt_jumlahJanin);


        TextView statusImunisasitt = (TextView) findViewById(R.id.txt_statusImunisasitt);
        TextView pelayananfe = (TextView) findViewById(R.id.txt_pelayananfe);
        TextView komplikasidalamKehamilan = (TextView) findViewById(R.id.txt_komplikasidalamKehamilan);

        TextView integrasiProgrampmtctvct = (TextView) findViewById(R.id.txt_integrasiProgrampmtctvct);
        TextView integrasiProgrampmtctPeriksaDarah = (TextView) findViewById(R.id.txt_integrasiProgrampmtctPeriksaDarah);
        TextView integrasiProgrampmtctSerologi = (TextView) findViewById(R.id.txt_integrasiProgrampmtctSerologi);
        TextView integrasiProgrampmtctarvProfilaksis = (TextView) findViewById(R.id.txt_integrasiProgrampmtctarvProfilaksis);
        TextView integrasiProgramMalariaPeriksaDarah = (TextView) findViewById(R.id.txt_integrasiProgramMalariaPeriksaDarah);
        TextView integrasiProgramMalariaObat = (TextView) findViewById(R.id.txt_integrasiProgramMalariaObat);
        TextView integrasiProgramMalariaKelambuBerinsektisida = (TextView) findViewById(R.id.txt_integrasiProgramMalariaKelambuBerinsektisida);
        TextView integrasiProgramtbDahak = (TextView) findViewById(R.id.txt_integrasiProgramtbDahak);
        TextView integrasiProgramtbObat = (TextView) findViewById(R.id.txt_integrasiProgramtbObat);

        TextView laboratoriumPeriksaHbHasil = (TextView) findViewById(R.id.txt_laboratoriumPeriksaHbHasil);
        TextView laboratoriumPeriksaHbAnemia = (TextView) findViewById(R.id.txt_laboratoriumPeriksaHbAnemia);
        TextView laboratoriumProteinUria = (TextView) findViewById(R.id.txt_laboratoriumProteinUria);
        TextView laboratoriumGulaDarah = (TextView) findViewById(R.id.txt_laboratoriumGulaDarah);
        TextView laboratoriumThalasemia = (TextView) findViewById(R.id.txt_laboratoriumThalasemia);
        TextView laboratoriumSifilis = (TextView) findViewById(R.id.txt_laboratoriumSifilis);
        TextView laboratoriumHbsAg = (TextView) findViewById(R.id.txt_laboratoriumHbsAg);


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

        ImageView heart_bpm = (ImageView) findViewById(R.id.iv_icon_device);
        heart_bpm.setVisibility(View.VISIBLE);
//        ImageView device = (ImageView) findViewById(R.id.iv_icon_device);
//        heart_bpm.setOnClickListener(bpmListener);
        ImageButton back = (ImageButton) findViewById(R.id.btn_back_to_home);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DetailANCActivity.this, ANCSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
                String DetailEnd = timer.format(new Date());
                Map<String, String> Detail = new HashMap<>();
                Detail.put("end", DetailEnd);
                //FlurryAgent.logEvent("ANC_detail_view", Detail, true);
            }
        });

        DetailsRepository detailsRepository = Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(ancClient);

        Keterangan_k1k4.setText(String.format(": %s", humanize(ancClient.getDetails().get("Keterangan_k1k4") != null ? ancClient.getDetails().get("Keterangan_k1k4") : "-")));
        tanggalHPHT.setText(String.format(": %s", humanize(ancClient.getDetails().get("tanggalHPHT") != null ? ancClient.getDetails().get("tanggalHPHT") : "-")));
        usiaKlinis.setText(String.format(": %s", humanize(ancClient.getDetails().get("usiaKlinis") != null ? ancClient.getDetails().get("usiaKlinis") : "-")));
        trimesterKe.setText(String.format(": %s", humanize(ancClient.getDetails().get("trimesterKe") != null ? ancClient.getDetails().get("trimesterKe") : "-")));
        kunjunganKe.setText(String.format(": %s", humanize(ancClient.getDetails().get("kunjunganKe") != null ? ancClient.getDetails().get("kunjunganKe") : "-")));
        bbKg.setText(String.format(": %s", humanize(ancClient.getDetails().get("bbKg") != null ? ancClient.getDetails().get("bbKg") : "-")));
        tandaVitalTDSistolik.setText(String.format(": %s", humanize(ancClient.getDetails().get("tandaVitalTDSistolik") != null ? ancClient.getDetails().get("tandaVitalTDSistolik") : "-")));
        tandaVitalTDDiastolik.setText(String.format(": %s", humanize(ancClient.getDetails().get("tandaVitalTDDiastolik") != null ? ancClient.getDetails().get("tandaVitalTDDiastolik") : "-")));
        hasilPemeriksaanLILA.setText(String.format(": %s", humanize(ancClient.getDetails().get("hasilPemeriksaanLILA") != null ? ancClient.getDetails().get("hasilPemeriksaanLILA") : "-")));
        statusGiziibu.setText(String.format(": %s", humanize(ancClient.getDetails().get("statusGiziibu") != null ? ancClient.getDetails().get("statusGiziibu") : "-")));
        tfu.setText(String.format(": %s", humanize(ancClient.getDetails().get("tfu") != null ? ancClient.getDetails().get("tfu") : "-")));
        refleksPatelaIbu.setText(String.format(": %s", humanize(ancClient.getDetails().get("refleksPatelaIbu") != null ? ancClient.getDetails().get("refleksPatelaIbu") : "-")));
        djj.setText(String.format(": %s", humanize(ancClient.getDetails().get("djj") != null ? ancClient.getDetails().get("djj") : "-")));
        kepalaJaninTerhadapPAP.setText(String.format(": %s", humanize(ancClient.getDetails().get("kepalaJaninTerhadapPAP") != null ? ancClient.getDetails().get("kepalaJaninTerhadapPAP") : "-")));
        taksiranBeratJanin.setText(String.format(": %s", humanize(ancClient.getDetails().get("taksiranBeratJanin") != null ? ancClient.getDetails().get("taksiranBeratJanin") : "-")));
        persentasiJanin.setText(String.format(": %s", humanize(ancClient.getDetails().get("persentasiJanin") != null ? ancClient.getDetails().get("persentasiJanin") : "-")));
        jumlahJanin.setText(String.format(": %s", humanize(ancClient.getDetails().get("jumlahJanin") != null ? ancClient.getDetails().get("jumlahJanin") : "-")));


        statusImunisasitt.setText(String.format(": %s", humanizeAndDoUPPERCASE(ancClient.getDetails().get("statusImunisasitt") != null ? ancClient.getDetails().get("statusImunisasitt") : "-")));
        pelayananfe.setText(String.format(": %s", humanize(ancClient.getDetails().get("pelayananfe") != null ? ancClient.getDetails().get("pelayananfe") : "-")));
        komplikasidalamKehamilan.setText(String.format(": %s", humanize(ancClient.getDetails().get("komplikasidalamKehamilan") != null ? ancClient.getDetails().get("komplikasidalamKehamilan") : "-")));
        integrasiProgrampmtctvct.setText(String.format(": %s", humanize(ancClient.getDetails().get("integrasiProgrampmtctvct") != null ? ancClient.getDetails().get("integrasiProgrampmtctvct") : "-")));
        integrasiProgrampmtctPeriksaDarah.setText(String.format(": %s", humanize(ancClient.getDetails().get("integrasiProgrampmtctPeriksaDarah") != null ? ancClient.getDetails().get("integrasiProgrampmtctPeriksaDarah") : "-")));
        integrasiProgrampmtctSerologi.setText(String.format(": %s", humanize(ancClient.getDetails().get("integrasiProgrampmtctSerologi") != null ? ancClient.getDetails().get("integrasiProgrampmtctSerologi") : "-")));
        integrasiProgrampmtctarvProfilaksis.setText(String.format(": %s", humanize(ancClient.getDetails().get("integrasiProgrampmtctarvProfilaksis") != null ? ancClient.getDetails().get("integrasiProgrampmtctarvProfilaksis") : "-")));
        integrasiProgramMalariaPeriksaDarah.setText(String.format(": %s", humanize(ancClient.getDetails().get("integrasiProgramMalariaPeriksaDarah") != null ? ancClient.getDetails().get("integrasiProgramMalariaPeriksaDarah") : "-")));
        integrasiProgramMalariaObat.setText(String.format(": %s", humanize(ancClient.getDetails().get("integrasiProgramMalariaObat") != null ? ancClient.getDetails().get("integrasiProgramMalariaObat") : "-")));
        integrasiProgramMalariaKelambuBerinsektisida.setText(String.format(": %s", ancClient.getDetails().get("integrasiProgramMalariaKelambuBerinsektisida") != null ? ancClient.getDetails().get("integrasiProgramMalariaKelambuBerinsektisida") : "-"));
        integrasiProgramtbDahak.setText(String.format(": %s", humanize(ancClient.getDetails().get("integrasiProgramtbDahak") != null ? ancClient.getDetails().get("integrasiProgramtbDahak") : "-")));
        integrasiProgramtbObat.setText(String.format(": %s", humanize(ancClient.getDetails().get("integrasiProgramtbObat") != null ? ancClient.getDetails().get("integrasiProgramtbObat") : "-")));
        laboratoriumPeriksaHbHasil.setText(String.format(": %s", humanize(ancClient.getDetails().get("laboratoriumPeriksaHbHasil") != null ? ancClient.getDetails().get("laboratoriumPeriksaHbHasil") : "-")));
        laboratoriumPeriksaHbAnemia.setText(String.format(": %s", humanize(ancClient.getDetails().get("laboratoriumPeriksaHbAnemia") != null ? ancClient.getDetails().get("laboratoriumPeriksaHbAnemia") : "-")));
        laboratoriumProteinUria.setText(String.format(": %s", humanize(ancClient.getDetails().get("laboratoriumProteinUria") != null ? ancClient.getDetails().get("laboratoriumProteinUria") : "-")));
        laboratoriumGulaDarah.setText(String.format(": %s", humanize(ancClient.getDetails().get("laboratoriumGulaDarah") != null ? ancClient.getDetails().get("laboratoriumGulaDarah") : "-")));
        laboratoriumThalasemia.setText(String.format(": %s", humanize(ancClient.getDetails().get("laboratoriumThalasemia") != null ? ancClient.getDetails().get("laboratoriumThalasemia") : "-")));
        laboratoriumSifilis.setText(String.format(": %s", humanize(ancClient.getDetails().get("laboratoriumSifilis") != null ? ancClient.getDetails().get("laboratoriumSifilis") : "-")));
        laboratoriumHbsAg.setText(String.format(": %s", humanize(ancClient.getDetails().get("laboratoriumHbsAg") != null ? ancClient.getDetails().get("laboratoriumHbsAg") : "-")));

        //risk detail
        txt_lbl_highRiskLabourFetusMalpresentation.setText(humanize(ancClient.getDetails().get("highRiskLabourFetusMalpresentation") != null ? ancClient.getDetails().get("highRiskLabourFetusMalpresentation") : "-"));
        txt_highRisklabourFetusNumber.setText(humanize(ancClient.getDetails().get("highRisklabourFetusNumber") != null ? ancClient.getDetails().get("highRisklabourFetusNumber") : "-"));
        txt_highRiskLabourFetusSize.setText(humanize(ancClient.getDetails().get("highRiskLabourFetusSize") != null ? ancClient.getDetails().get("highRiskLabourFetusSize") : "-"));
        highRiskPregnancyProteinEnergyMalnutrition.setText(humanize(ancClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null ? ancClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") : "-"));
        highRiskPregnancyPIH.setText(humanize(ancClient.getDetails().get("highRiskPregnancyPIH") != null ? ancClient.getDetails().get("highRiskPregnancyPIH") : "-"));
        txt_highRiskPregnancyDiabetes.setText(humanize(ancClient.getDetails().get("highRiskPregnancyDiabetes") != null ? ancClient.getDetails().get("highRiskPregnancyDiabetes") : "-"));
        txt_highRiskPregnancyAnemia.setText(humanize(ancClient.getDetails().get("highRiskPregnancyAnemia") != null ? ancClient.getDetails().get("highRiskPregnancyAnemia") : "-"));

        highRiskPostPartumSectioCaesaria.setText(humanize(ancClient.getDetails().get("highRiskPostPartumSectioCaesaria") != null ? ancClient.getDetails().get("highRiskPostPartumSectioCaesaria") : "-"));
        highRiskPostPartumForceps.setText(humanize(ancClient.getDetails().get("highRiskPostPartumForceps") != null ? ancClient.getDetails().get("highRiskPostPartumForceps") : "-"));
        highRiskPostPartumVacum.setText(humanize(ancClient.getDetails().get("highRiskPostPartumVacum") != null ? ancClient.getDetails().get("highRiskPostPartumVacum") : "-"));
        highRiskPostPartumPreEclampsiaEclampsia.setText(humanize(ancClient.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") != null ? ancClient.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") : "-"));
        highRiskPostPartumMaternalSepsis.setText(humanize(ancClient.getDetails().get("highRiskPostPartumMaternalSepsis") != null ? ancClient.getDetails().get("highRiskPostPartumMaternalSepsis") : "-"));
        highRiskPostPartumInfection.setText(humanize(ancClient.getDetails().get("highRiskPostPartumInfection") != null ? ancClient.getDetails().get("highRiskPostPartumInfection") : "-"));
        highRiskPostPartumHemorrhage.setText(humanize(ancClient.getDetails().get("highRiskPostPartumHemorrhage") != null ? ancClient.getDetails().get("highRiskPostPartumHemorrhage") : "-"));
        highRiskPostPartumPIH.setText(humanize(ancClient.getDetails().get("highRiskPostPartumPIH") != null ? ancClient.getDetails().get("highRiskPostPartumPIH") : "-"));
        highRiskPostPartumDistosia.setText(humanize(ancClient.getDetails().get("highRiskPostPartumDistosia") != null ? ancClient.getDetails().get("highRiskPostPartumDistosia") : "-"));

        ancKe.setText(String.format(": %s", ancClient.getDetails().get("ancKe") != null ? ancClient.getDetails().get("ancKe") : "-"));

        ancDate.setText(String.format(": %s", ancClient.getDetails().get("ancDate") != null ? ancClient.getDetails().get("ancDate") : "-"));

        if (ancClient.getCaseId() != null) {//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(ancClient.getCaseId(), OpenSRPImageLoader.getStaticImageListener(kiview, R.mipmap.woman_placeholder, R.mipmap.woman_placeholder));
            Support.setImagetoHolderFromUri(this,
                    ancClient.getDetails().get("base_entity_id"),
                    kiview, R.mipmap.woman_placeholder);
        }

        nama.setText(String.format("%s%s", getResources().getString(R.string.name), ancClient.getColumnmaps().get("namalengkap") != null ? ancClient.getColumnmaps().get("namalengkap") : "-"));
        nik.setText(String.format("%s%s", getResources().getString(R.string.nik), ancClient.getDetails().get("nik") != null ? ancClient.getDetails().get("nik") : "-"));
        husband_name.setText(String.format("%s%s", getResources().getString(R.string.husband_name), ancClient.getColumnmaps().get("namaSuami") != null ? ancClient.getColumnmaps().get("namaSuami") : "-"));
        String tgl = ancClient.getDetails().get("tanggalLahir") != null ? ancClient.getDetails().get("tanggalLahir") : "-";
        String tgl_lahir = tgl.substring(0, tgl.indexOf("T"));
        dob.setText(String.format("%s%s", getResources().getString(R.string.dob), tgl_lahir));

        //   dob.setText(getResources().getString(R.string.dob)+ (ancClient.getDetails().get("tanggalLahir") != null ? ancClient.getDetails().get("tanggalLahir") : "-"));
        phone.setText(String.format("No HP: %s", ancClient.getDetails().get("NomorTelponHp") != null ? ancClient.getDetails().get("NomorTelponHp") : "-"));

        //risk
        if (ancClient.getDetails().get("highRiskPregnancyYoungMaternalAge") != null) {
            risk1.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyYoungMaternalAge), humanize(ancClient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if (ancClient.getDetails().get("highRiskPregnancyOldMaternalAge") != null) {
            risk1.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyOldMaternalAge), humanize(ancClient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if (ancClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null
                || ancClient.getDetails().get("HighRiskPregnancyAbortus") != null
                || ancClient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null
                ) {
            risk2.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyProteinEnergyMalnutrition), humanize(ancClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"))));
            risk3.setText(String.format("%s%s", getResources().getString(R.string.HighRiskPregnancyAbortus), humanize(ancClient.getDetails().get("HighRiskPregnancyAbortus"))));
            risk4.setText(String.format("%s%s", getResources().getString(R.string.HighRiskLabourSectionCesareaRecord), humanize(ancClient.getDetails().get("HighRiskLabourSectionCesareaRecord"))));

        }
        txt_highRiskLabourTBRisk.setText(humanize(ancClient.getDetails().get("highRiskLabourTBRisk") != null ? ancClient.getDetails().get("highRiskLabourTBRisk") : "-"));

        highRiskSTIBBVs.setText(humanize(ancClient.getDetails().get("highRiskSTIBBVs") != null ? ancClient.getDetails().get("highRiskSTIBBVs") : "-"));
        highRiskEctopicPregnancy.setText(humanize(ancClient.getDetails().get("highRiskEctopicPregnancy") != null ? ancClient.getDetails().get("highRiskEctopicPregnancy") : "-"));
        highRiskCardiovascularDiseaseRecord.setText(humanize(ancClient.getDetails().get("highRiskCardiovascularDiseaseRecord") != null ? ancClient.getDetails().get("highRiskCardiovascularDiseaseRecord") : "-"));
        highRiskDidneyDisorder.setText(humanize(ancClient.getDetails().get("highRiskDidneyDisorder") != null ? ancClient.getDetails().get("highRiskDidneyDisorder") : "-"));
        highRiskHeartDisorder.setText(humanize(ancClient.getDetails().get("highRiskHeartDisorder") != null ? ancClient.getDetails().get("highRiskHeartDisorder") : "-"));
        highRiskAsthma.setText(humanize(ancClient.getDetails().get("highRiskAsthma") != null ? ancClient.getDetails().get("highRiskAsthma") : "-"));
        highRiskTuberculosis.setText(humanize(ancClient.getDetails().get("highRiskTuberculosis") != null ? ancClient.getDetails().get("highRiskTuberculosis") : "-"));
        highRiskMalaria.setText(humanize(ancClient.getDetails().get("highRiskMalaria") != null ? ancClient.getDetails().get("highRiskMalaria") : "-"));

        txt_HighRiskLabourSectionCesareaRecord.setText(humanize(ancClient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null ? ancClient.getDetails().get("HighRiskLabourSectionCesareaRecord") : "-"));
        HighRiskPregnancyTooManyChildren.setText(humanize(ancClient.getDetails().get("HighRiskPregnancyTooManyChildren") != null ? ancClient.getDetails().get("HighRiskPregnancyTooManyChildren") : "-"));

        txt_highRiskHIVAIDS.setText(humanize(ancClient.getDetails().get("highRiskHIVAIDS") != null ? ancClient.getDetails().get("highRiskHIVAIDS") : "-"));

        show_risk.setVisibility(View.GONE);
        show_detail.setVisibility(View.GONE);

        //  TextView details = (TextView) findViewById(R.id.child_detail_information);
        //  TextView risk = (TextView) findViewById(R.id.detail_today);
        //  TextView plan = (TextView) findViewById(R.id.detail_l);
        // birth plan
        txt_b_edd.setText(humanize(ancClient.getDetails().get("htp") != null ? ancClient.getDetails().get("htp") : "-"));
        txt_b_lastvisit.setText(humanize(ancClient.getDetails().get("tanggalKunjunganRencanaPersalinan") != null ? ancClient.getDetails().get("tanggalKunjunganRencanaPersalinan") : "-"));
        txt_b_location.setText(humanize(ancClient.getDetails().get("lokasiPeriksa") != null ? ancClient.getDetails().get("lokasiPeriksa") : "-"));

        txt_b_edd2.setText(humanize(ancClient.getDetails().get("htp") != null ? ancClient.getDetails().get("htp") : "-"));
        txt_b_attend.setText(humanize(ancClient.getDetails().get("rencanaPenolongPersalinan") != null ? ancClient.getDetails().get("rencanaPenolongPersalinan") : "-"));
        txt_b_place.setText(humanize(ancClient.getDetails().get("tempatRencanaPersalinan") != null ? ancClient.getDetails().get("tempatRencanaPersalinan") : "-"));
        txt_b_person.setText(humanize(ancClient.getDetails().get("rencanaPendampingPersalinan") != null ? ancClient.getDetails().get("rencanaPendampingPersalinan") : "-"));
        txt_b_transport.setText(humanize(ancClient.getDetails().get("transportasi") != null ? ancClient.getDetails().get("transportasi") : "-"));
        txt_b_blood.setText(humanize(ancClient.getDetails().get("pendonor") != null ? ancClient.getDetails().get("pendonor") : "-"));
        txt_b_houser.setText(humanize(ancClient.getDetails().get("kondisiRumah") != null ? ancClient.getDetails().get("kondisiRumah") : "-"));
        txt_b_stock.setText(humanize(ancClient.getDetails().get("persediaanPerlengkapanPersalinan") != null ? ancClient.getDetails().get("persediaanPerlengkapanPersalinan") : "-"));


        risk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FlurryFacade.logEvent("click_risk_detail");
                findViewById(R.id.id1).setVisibility(View.GONE);
                findViewById(R.id.id2).setVisibility(View.VISIBLE);
                findViewById(R.id.id3).setVisibility(View.VISIBLE);
                //  findViewById(R.id.show_more_detail).setVisibility(View.VISIBLE);
                // findViewById(R.id.show_more).setVisibility(View.GONE);
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(View.VISIBLE);
                findViewById(R.id.id2).setVisibility(View.GONE);
                findViewById(R.id.id3).setVisibility(View.VISIBLE);
                //  findViewById(R.id.show_more).setVisibility(View.VISIBLE);
                //  findViewById(R.id.show_more_detail).setVisibility(View.GONE);
            }
        });

        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(View.GONE);
                findViewById(R.id.id2).setVisibility(View.GONE);
                findViewById(R.id.id3).setVisibility(View.VISIBLE);
                // mother_summary.setText("Birth Plan Summary");
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, ANCSmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }

}
