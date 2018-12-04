package org.smartregister.bidan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.repository.EventRepository;
import org.smartregister.bidan.utils.Support;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.smartregister.util.StringUtil.humanize;

//import org.smartregister.bidan.lib.FlurryFacade;

/**
 * Created by Iq on 07/09/16
 */
public class DetailANCActivity extends Activity {

    private static final String TAG = DetailANCActivity.class.getSimpleName();
    public static CommonPersonObjectClient ancClient;
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
//    @Bind(R.id.tv_child_detail_information)
//    private TextView details;
//    @Bind(R.id.tv_detail_today)
//    private TextView risk;
//    @Bind(R.id.tv_detail_l)
//    private TextView plan;
//    // birth plan
////    @Bind(R.id.tv_mother_summary)
////    TextView mother_summary;
//    @Bind(R.id.tv_b_edd)
//    private TextView txt_b_edd;
//    @Bind(R.id.tv_b_lastvisit)
//    private TextView txt_b_lastvisit;
//    @Bind(R.id.tv_b_location)
//    private TextView txt_b_location;
//    @Bind(R.id.tv_b_edd2)
//    private TextView txt_b_edd2;
//    @Bind(R.id.tv_b_attend)
//    private TextView txt_b_attend;
//    @Bind(R.id.tv_b_place)
//    private TextView txt_b_place;
//    @Bind(R.id.tv_b_person)
//    private TextView txt_b_person;
//    @Bind(R.id.tv_b_transport)
//    private TextView txt_b_transport;
//    @Bind(R.id.tv_b_blood)
//    private TextView txt_b_blood;
//    @Bind(R.id.tv_b_houser)
//    private TextView txt_b_houser;
//    @Bind(R.id.tv_b_stock)
//    private TextView txt_b_stock;
//    //detail data
////    @Bind(R.id.txt_Keterangan_k1k4)
////    private TextView Keterangan_k1k4;
//    @Bind(R.id.txt_ancDate)
//    private TextView ancDate;
//    @Bind(R.id.txt_tanggalHPHT)
//    private TextView tanggalHPHT;
//    @Bind(R.id.txt_usiaKlinis_anc)
//    private TextView usiaKlinis;
//    @Bind(R.id.txt_trimesterKe)
//    private TextView trimesterKe;
//    @Bind(R.id.txt_kunjunganKe)
//    private TextView kunjunganKe;
//    @Bind(R.id.txt_ancKe)
//    private TextView ancKe;
//    @Bind(R.id.txt_bbKg)
//    private TextView bbKg;
//    @Bind(R.id.txt_tandaVitalTDSistolik)
//    private TextView tandaVitalTDSistolik;
//    @Bind(R.id.txt_tandaVitalTDDiastolik)
//    private TextView tandaVitalTDDiastolik;
//    @Bind(R.id.txt_hasilPemeriksaanLILA)
//    private TextView hasilPemeriksaanLILA;
//    @Bind(R.id.txt_statusGiziibu)
//    private TextView statusGiziibu;
//    @Bind(R.id.txt_tfu)
//    private TextView tfu;
//    @Bind(R.id.txt_refleksPatelaIbu)
//    private TextView refleksPatelaIbu;
//    @Bind(R.id.txt_djj)
//    private TextView djj;
//    @Bind(R.id.txt_kepalaJaninTerhadapPAP)
//    private TextView kepalaJaninTerhadapPAP;
//    @Bind(R.id.txt_taksiranBeratJanin)
//    private TextView taksiranBeratJanin;
//    @Bind(R.id.txt_persentasiJanin)
//    private TextView persentasiJanin;
//    @Bind(R.id.txt_jumlahJanin)
//    private TextView jumlahJanin;
//    @Bind(R.id.txt_statusImunisasiTT)
//    private TextView statusImunisasitt;
//    @Bind(R.id.txt_pelayananfe)
//    private TextView pelayananfe;
//    @Bind(R.id.txt_komplikasiKehamilan)
//    private TextView komplikasidalamKehamilan;
//    @Bind(R.id.txt_integrasiProgrampmtctvct)
//    private TextView integrasiProgrampmtctvct;
//    @Bind(R.id.txt_integrasiProgrampmtctPeriksaDarah)
//    private TextView integrasiProgrampmtctPeriksaDarah;
//    @Bind(R.id.txt_integrasiProgrampmtctSerologi)
//    private TextView integrasiProgrampmtctSerologi;
//    @Bind(R.id.txt_integrasiProgrampmtctarvProfilaksis)
//    private TextView integrasiProgrampmtctarvProfilaksis;
//    @Bind(R.id.txt_integrasiProgramMalariaPeriksaDarah)
//    private TextView integrasiProgramMalariaPeriksaDarah;
//    @Bind(R.id.txt_integrasiProgramMalariaObat)
//    private TextView integrasiProgramMalariaObat;
//    @Bind(R.id.txt_integrasiProgramMalariaKelambuBerinsektisida)
//    private TextView integrasiProgramMalariaKelambuBerinsektisida;
//    @Bind(R.id.txt_integrasiProgramtbDahak)
//    private TextView integrasiProgramtbDahak;
//    @Bind(R.id.txt_integrasiProgramtbObat)
//    private TextView integrasiProgramtbObat;
//    @Bind(R.id.txt_laboratoriumPeriksaHbHasil)
//    private TextView laboratoriumPeriksaHbHasil;
//    @Bind(R.id.txt_laboratoriumPeriksaHbAnemia)
//    private TextView laboratoriumPeriksaHbAnemia;
//    @Bind(R.id.txt_laboratoriumProteinUria)
//    private TextView laboratoriumProteinUria;
//    @Bind(R.id.txt_laboratoriumGulaDarah)
//    private TextView laboratoriumGulaDarah;
//    @Bind(R.id.txt_laboratoriumThalasemia)
//    private TextView laboratoriumThalasemia;
//    @Bind(R.id.txt_laboratoriumSifilis)
//    private TextView laboratoriumSifilis;
//    @Bind(R.id.txt_laboratoriumHbsAg)
//    private TextView laboratoriumHbsAg;
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
//
//    @Bind(R.id.txt_hrpp_PIH)
//    private TextView highRiskPostPartumPIH;
//    @Bind(R.id.txt_hrpp_Distosia)
//    private TextView highRiskPostPartumDistosia;
//    @Bind(R.id.txt_highRiskHIVAIDS)
//    private TextView txt_highRiskHIVAIDS;
//
//    @Bind(R.id.iv_icon_device)
//    private ImageView heart_bpm;
//    @Bind(R.id.btn_back_to_home)
//    private ImageButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anc_detail_activity);

//        ButterKnife.bind(this);
        findViewById(R.id.iv_icon_device).setVisibility(VISIBLE);

        findViewById(R.id.tv_show_more).setVisibility(GONE);
        findViewById(R.id.tv_show_more_detail).setVisibility(GONE);

        findViewById(R.id.btn_back_to_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DetailANCActivity.this, ANCSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
//                String DetailEnd = timer.format(new Date());
//                Map<String, String> Detail = new HashMap<>();
//                Detail.put("end", DetailEnd);
                //FlurryAgent.logEvent("ANC_detail_view", Detail, true);
            }
        });

        DetailsRepository detailsRepository = Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(ancClient);

        if (ancClient.getCaseId() != null) {
            Support.setImagetoHolderFromUri(this, ancClient.getDetails().get("base_entity_id"), (ImageView) findViewById(R.id.tv_mother_detail_profile_view), R.mipmap.woman_placeholder);
        }

        ((TextView) findViewById(R.id.tv_wife_name)).setText(String.format("%s%s", getResources().getString(R.string.name), ancClient.getColumnmaps().get("namalengkap") != null ? ancClient.getColumnmaps().get("namalengkap") : "-"));
        ((TextView) findViewById(R.id.tv_nik)).setText(String.format("%s%s", getResources().getString(R.string.nik), ancClient.getDetails().get("nik") != null ? ancClient.getDetails().get("nik") : "-"));
        ((TextView) findViewById(R.id.tv_husband_name)).setText(String.format("%s%s", getResources().getString(R.string.husband_name), ancClient.getColumnmaps().get("namaSuami") != null ? ancClient.getColumnmaps().get("namaSuami") : "-"));

        String tgl = ancClient.getDetails().get("tanggalLahir") != null ? ancClient.getDetails().get("tanggalLahir") : "-";
        String tgl_lahir = tgl.substring(0, tgl.indexOf("T"));
        ((TextView) findViewById(R.id.tv_dob)).setText(String.format("%s%s", getResources().getString(R.string.dob), tgl_lahir));
        //   dob.setText(getResources().getString(R.string.dob)+ (ancClient.getDetails().get("tanggalLahir") != null ? ancClient.getDetails().get("tanggalLahir") : "-"));
        ((TextView) findViewById(R.id.tv_contact_phone_number)).setText(String.format("No HP: %s", ancClient.getDetails().get("NomorTelponHp") != null ? ancClient.getDetails().get("NomorTelponHp") : "-"));

        ((TextView) findViewById(R.id.txt_keterangan_k1k4)).setText(getStrValue("KeteranganK1k4Who")=="Ya"?String.format(": %s",getResources().getString(R.string.standart)):String.format(": %s",getResources().getString(R.string.non_standart)));
        ((TextView) findViewById(R.id.txt_tanggalHPHT)).setText(getStrValue("tanggalHPHT"));
        ((TextView) findViewById(R.id.txt_usiaKlinis_anc)).setText(getStrValue("usiaKlinis"));
        ((TextView) findViewById(R.id.txt_trimesterKe)).setText(getStrValue("trimesterKe"));
        ((TextView) findViewById(R.id.txt_kunjunganKe)).setText(getStrValue("kunjunganKe"));
        ((TextView) findViewById(R.id.txt_bbKg)).setText(getStrValue("bbKg"));
        ((TextView) findViewById(R.id.txt_tandaVitalTDSistolik)).setText(getStrValue("tandaVitalTDSistolik"));
        ((TextView) findViewById(R.id.txt_tandaVitalTDDiastolik)).setText(getStrValue("tandaVitalTDDiastolik"));
        ((TextView) findViewById(R.id.txt_hasilPemeriksaanLILA)).setText(getStrValue("hasilPemeriksaanLILA"));
        ((TextView) findViewById(R.id.txt_statusGiziibu)).setText(getStrValue("statusGiziibu"));
        ((TextView) findViewById(R.id.txt_tfu)).setText(getStrValue("tfu"));
        ((TextView) findViewById(R.id.txt_refleksPatelaIbu)).setText(getStrValue("refleksPatelaIbu"));
        ((TextView) findViewById(R.id.txt_djj)).setText(getStrValue("djj"));
        ((TextView) findViewById(R.id.txt_kepalaJaninTerhadapPAP)).setText(getStrValue("kepalaJaninTerhadapPAP"));
        ((TextView) findViewById(R.id.txt_taksiranBeratJanin)).setText(getStrValue("taksiranBeratJanin"));
        ((TextView) findViewById(R.id.txt_persentasiJanin)).setText(getStrValue("persentasiJanin"));
        ((TextView) findViewById(R.id.txt_jumlahJanin)).setText(getStrValue("jumlahJanin"));

        ((TextView) findViewById(R.id.txt_statusImunisasiTT)).setText(getStrValue("statusImunisasitt"));
        ((TextView) findViewById(R.id.txt_pelayananfe)).setText(getStrValue("pelayananFe"));
        ((TextView) findViewById(R.id.txt_komplikasiKehamilan)).setText(getStrValue("komplikasidalamKehamilan"));
        ((TextView) findViewById(R.id.txt_integrasiProgrampmtctvct)).setText(getStrValue("integrasiProgrampmtctvct"));
        ((TextView) findViewById(R.id.txt_integrasiProgrampmtctPeriksaDarah)).setText(getStrValue("integrasiProgrampmtctPeriksaDarah"));
        ((TextView) findViewById(R.id.txt_integrasiProgrampmtctSerologi)).setText(getStrValue("integrasiProgrampmtctSerologi"));
        ((TextView) findViewById(R.id.txt_integrasiProgrampmtctarvProfilaksis)).setText(getStrValue("integrasiProgrampmtctarvProfilaksis"));
        ((TextView) findViewById(R.id.txt_integrasiProgramMalariaPeriksaDarah)).setText(getStrValue("integrasiProgramMalariaPeriksaDarah"));
        ((TextView) findViewById(R.id.txt_integrasiProgramMalariaObat)).setText(getStrValue("integrasiProgramMalariaObat"));
        ((TextView) findViewById(R.id.txt_integrasiProgramMalariaKelambuBerinsektisida)).setText(getStrValue("integrasiProgramMalariaKelambuBerinsektisida"));
        ((TextView) findViewById(R.id.txt_integrasiProgramtbDahak)).setText(getStrValue("integrasiProgramtbDahak"));
        ((TextView) findViewById(R.id.txt_integrasiProgramtbObat)).setText(getStrValue("integrasiProgramtbObat"));
        ((TextView) findViewById(R.id.txt_laboratoriumPeriksaHbHasil)).setText(getStrValue("laboratoriumPeriksaHbHasil"));
        ((TextView) findViewById(R.id.txt_laboratoriumPeriksaHbAnemia)).setText(getStrValue("laboratoriumPeriksaHbAnemia"));
        ((TextView) findViewById(R.id.txt_laboratoriumProteinUria)).setText(getStrValue("laboratoriumProteinUria"));
        ((TextView) findViewById(R.id.txt_laboratoriumGulaDarah)).setText(getStrValue("laboratoriumGulaDarah"));
        ((TextView) findViewById(R.id.txt_laboratoriumThalasemia)).setText(getStrValue("laboratoriumThalasemia"));
        ((TextView) findViewById(R.id.txt_laboratoriumSifilis)).setText(getStrValue("laboratoriumSifilis"));
        ((TextView) findViewById(R.id.txt_laboratoriumHbsAg)).setText(getStrValue("laboratoriumHbsAg"));
        // Risk detail
        ((TextView) findViewById(R.id.txt_hrl_FetusMalpresentation)).setText(getStrValue("highRiskLabourFetusMalpresentation"));
        ((TextView) findViewById(R.id.txt_hrl_FetusNumber)).setText(getStrValue("highRisklabourFetusNumber"));
        ((TextView) findViewById(R.id.txt_hrl_FetusSize)).setText(getStrValue("highRiskLabourFetusSize"));
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

        ((TextView) findViewById(R.id.txt_ancKe)).setText(getStrValue("ancKe"));

        ((TextView) findViewById(R.id.txt_ancDate)).setText(getStrValue("ancDate"));

        //risk
        if (ancClient.getDetails().get("highRiskPregnancyYoungMaternalAge") != null) {
            ((TextView) findViewById(R.id.tv_risk1)).setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyYoungMaternalAge), humanize(ancClient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if (ancClient.getDetails().get("highRiskPregnancyOldMaternalAge") != null) {
            ((TextView) findViewById(R.id.tv_risk1)).setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyOldMaternalAge), humanize(ancClient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if (ancClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null
                || ancClient.getDetails().get("HighRiskPregnancyAbortus") != null
                || ancClient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null
                ) {
            ((TextView) findViewById(R.id.tv_risk2)).setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyProteinEnergyMalnutrition), humanize(ancClient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"))));
            ((TextView) findViewById(R.id.tv_risk3)).setText(String.format("%s%s", getResources().getString(R.string.HighRiskPregnancyAbortus), humanize(ancClient.getDetails().get("HighRiskPregnancyAbortus"))));
            ((TextView) findViewById(R.id.tv_risk4)).setText(String.format("%s%s", getResources().getString(R.string.HighRiskLabourSectionCesareaRecord), humanize(ancClient.getDetails().get("HighRiskLabourSectionCesareaRecord"))));
        }
        ((TextView) findViewById(R.id.txt_highRiskLabourTBRisk)).setText(getStrValue("highRiskLabourTBRisk"));

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


        //  TextView details = (TextView) findViewById(R.id.child_detail_information));
        //  TextView risk = (TextView) findViewById(R.id.detail_today));
        //  TextView plan = (TextView) findViewById(R.id.detail_l));
        // birth plan
        ((TextView) findViewById(R.id.tv_b_edd)).setText(getStrValue("htp"));
        ((TextView) findViewById(R.id.tv_b_lastvisit)).setText(getStrValue("tanggalKunjunganRencanaPersalinan"));
        ((TextView) findViewById(R.id.tv_b_location)).setText(getStrValue("lokasiPeriksa"));

        ((TextView) findViewById(R.id.tv_b_edd2)).setText(getStrValue("htp"));
        ((TextView) findViewById(R.id.tv_b_attend)).setText(getStrValue("rencanaPenolongPersalinan"));
        ((TextView) findViewById(R.id.tv_b_place)).setText(getStrValue("tempatRencanaPersalinan"));
        ((TextView) findViewById(R.id.tv_b_person)).setText(getStrValue("rencanaPendampingPersalinan"));
        ((TextView) findViewById(R.id.tv_b_transport)).setText(getStrValue("transportasi"));
        ((TextView) findViewById(R.id.tv_b_blood)).setText(getStrValue("pendonor"));
        ((TextView) findViewById(R.id.tv_b_houser)).setText(getStrValue("kondisiRumah"));
        ((TextView) findViewById(R.id.tv_b_stock)).setText(getStrValue("persediaanPerlengkapanPersalinan"));


        findViewById(R.id.tv_detail_today).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FlurryFacade.logEvent("click_risk_detail");
                findViewById(R.id.id1).setVisibility(GONE);
                findViewById(R.id.id2).setVisibility(VISIBLE);
                findViewById(R.id.id3).setVisibility(GONE);
                findViewById(R.id.id4).setVisibility(GONE);
                //  findViewById(R.id.show_more_detail).setVisibility(VISIBLE);
                // findViewById(R.id.show_more).setVisibility(GONE);
            }
        });

        findViewById(R.id.tv_anc_detail_information).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(VISIBLE);
                findViewById(R.id.id2).setVisibility(GONE);
                findViewById(R.id.id3).setVisibility(GONE);
                findViewById(R.id.id4).setVisibility(GONE);
                //  findViewById(R.id.show_more).setVisibility(VISIBLE);
                //  findViewById(R.id.show_more_detail).setVisibility(GONE);
            }
        });

        findViewById(R.id.tv_detail_l).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(GONE);
                findViewById(R.id.id2).setVisibility(GONE);
                findViewById(R.id.id3).setVisibility(VISIBLE);
                findViewById(R.id.id4).setVisibility(GONE);
                // mother_summary.setText("Birth Plan Summary");
            }
        });

        findViewById(R.id.tv_detail_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(GONE);
                findViewById(R.id.id2).setVisibility(GONE);
                findViewById(R.id.id3).setVisibility(GONE);
                findViewById(R.id.id4).setVisibility(VISIBLE);
                // mother_summary.setText("Birth Plan Summary");
            }
        });

        final List<JSONObject> ancEvents = EventRepository.getANCByBaseEntityId(ancClient.entityId());

        final LinearLayout history_button = findViewById(R.id.visit_history_button);
        int i = 1;
        for (final JSONObject ancEvent:ancEvents) {
            final CustomFontTextView btn = (CustomFontTextView) getLayoutInflater().inflate(R.layout.visit_button,null);
            btn.setText(getResources().getString(R.string.visit_number)+i);
            btn.setWidth(500);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View history_view = findViewById(R.id.visit_history_detail);
                    history_view.setVisibility(VISIBLE);
                    try {
                        String date = ancEvent.getString("ancDate");
                        ((TextView)history_view.findViewById(R.id.txt_ancDate)).setText(date);
                        ((TextView)history_view.findViewById(R.id.txt_ancKe)).setText(ancEvent.has("ancKe")?ancEvent.getString("ancKe"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_keterangan_k1k4)).setText(ancEvent.has("ancKe")?ancEvent.getString("KeteranganK1k4Who"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_kunjunganKe)).setText(ancEvent.has("kunjunganKe")?ancEvent.getString("kunjunganKe"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_lokasiPeriksa)).setText(ancEvent.has("lokasiPeriksa")?ancEvent.getString("lokasiPeriksa").equals("Lainnya")?ancEvent.getString("lokasiPeriksaOther"):ancEvent.getString("lokasiPeriksa"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_usiaKlinis_anc)).setText(ancEvent.has("usiaKlinis")?ancEvent.getString("usiaKlinis"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_trimesterKe)).setText(ancEvent.has("trimesterKe")?ancEvent.getString("trimesterKe"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_bbKg)).setText(ancEvent.has("bbKg")?ancEvent.getString("bbKg"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_tandaVitalTDSistolik)).setText(ancEvent.has("tandaVitalTDSistolik")?ancEvent.getString("tandaVitalTDSistolik"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_tandaVitalTDDiastolik)).setText(ancEvent.has("tandaVitalTDDiastolik")?ancEvent.getString("tandaVitalTDDiastolik"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_hasilPemeriksaanLILA)).setText(ancEvent.has("hasilPemeriksaanLILA")?ancEvent.getString("hasilPemeriksaanLILA"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_statusGiziibu)).setText(ancEvent.has("statusGiziibu")?ancEvent.getString("statusGiziibu"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_tfu)).setText(ancEvent.has("tfu")?ancEvent.getString("tfu"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_refleksPatelaIbu)).setText(ancEvent.has("refleksPatelaIbu")?ancEvent.getString("refleksPatelaIbu"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_djj)).setText(ancEvent.has("djj")?ancEvent.getString("djj"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_kepalaJaninTerhadapPAP)).setText(ancEvent.has("kepalaJaninTerhadapPAP")?ancEvent.getString("kepalaJaninTerhadapPAP"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_persentasiJanin)).setText(ancEvent.has("persentasiJanin")?ancEvent.getString("persentasiJanin"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_taksiranBeratJanin)).setText(ancEvent.has("taksiranBeratJanin")?ancEvent.getString("taksiranBeratJanin"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_jumlahJanin)).setText(ancEvent.has("jumlahJanin")?ancEvent.getString("jumlahJanin"):"-");

                        ((TextView)history_view.findViewById(R.id.txt_statusImunisasiTT)).setText(ancEvent.has("statusImunisasitt")?ancEvent.getString("statusImunisasitt"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_pelayananfe)).setText(ancEvent.has("pelayananfe")?ancEvent.getString("pelayananfe"):"-");
                        ((TextView)history_view.findViewById(R.id.txt_komplikasiKehamilan)).setText(ancEvent.has("komplikasidalamKehamilan")?ancEvent.getString("komplikasidalamKehamilan"):"-");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            history_button.addView(btn);
            i++;
        }

    }

    private String getStrValue(String str) {
        return String.format(": %s", humanize(ancClient.getDetails().get(str) != null ? ancClient.getDetails().get(str) : "-"));
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        finish();
        startActivity(new Intent(this, ANCSmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }

}
