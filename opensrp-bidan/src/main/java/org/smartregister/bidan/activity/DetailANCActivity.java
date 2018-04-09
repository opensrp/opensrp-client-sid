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

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.smartregister.util.StringUtil.humanize;
import static org.smartregister.util.StringUtil.humanizeAndDoUPPERCASE;

//import org.smartregister.bidan.lib.FlurryFacade;

/**
 * Created by Iq on 07/09/16
 */
public class DetailANCActivity extends Activity {

    private static final String TAG = DetailANCActivity.class.getSimpleName();
    public static CommonPersonObjectClient ancClient;
    //    private SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
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


    @Bind(R.id.tv_child_detail_information)
    private TextView details;
    @Bind(R.id.tv_detail_today)
    private TextView risk;
    @Bind(R.id.tv_detail_l)
    private TextView plan;

    // birth plan
//    @Bind(R.id.tv_mother_summary)
//    TextView mother_summary;

    @Bind(R.id.tv_b_edd)
    private TextView txt_b_edd;
    @Bind(R.id.tv_b_lastvisit)
    private TextView txt_b_lastvisit;
    @Bind(R.id.tv_b_location)
    private TextView txt_b_location;

    @Bind(R.id.tv_b_edd2)
    private TextView txt_b_edd2;
    @Bind(R.id.tv_b_attend)
    private TextView txt_b_attend;
    @Bind(R.id.tv_b_place)
    private TextView txt_b_place;
    @Bind(R.id.tv_b_person)
    private TextView txt_b_person;
    @Bind(R.id.tv_b_transport)
    private TextView txt_b_transport;
    @Bind(R.id.tv_b_blood)
    private TextView txt_b_blood;
    @Bind(R.id.tv_b_houser)
    private TextView txt_b_houser;
    @Bind(R.id.tv_b_stock)
    private TextView txt_b_stock;


    //detail data
    @Bind(R.id.txt_Keterangan_k1k4)
    private TextView Keterangan_k1k4;
    @Bind(R.id.txt_ancDate)
    private TextView ancDate;
    @Bind(R.id.txt_tanggalHPHT)
    private TextView tanggalHPHT;
    @Bind(R.id.txt_usiaKlinis)
    private TextView usiaKlinis;
    @Bind(R.id.txt_trimesterKe)
    private TextView trimesterKe;
    @Bind(R.id.txt_kunjunganKe)
    private TextView kunjunganKe;
    @Bind(R.id.txt_ancKe)
    private TextView ancKe;
    @Bind(R.id.txt_bbKg)
    private TextView bbKg;
    @Bind(R.id.txt_tandaVitalTDSistolik)
    private TextView tandaVitalTDSistolik;
    @Bind(R.id.txt_tandaVitalTDDiastolik)
    private TextView tandaVitalTDDiastolik;
    @Bind(R.id.txt_hasilPemeriksaanLILA)
    private TextView hasilPemeriksaanLILA;
    @Bind(R.id.txt_statusGiziibu)
    private TextView statusGiziibu;
    @Bind(R.id.txt_tfu)
    private TextView tfu;
    @Bind(R.id.txt_refleksPatelaIbu)
    private TextView refleksPatelaIbu;
    @Bind(R.id.txt_djj)
    private TextView djj;
    @Bind(R.id.txt_kepalaJaninTerhadapPAP)
    private TextView kepalaJaninTerhadapPAP;
    @Bind(R.id.txt_taksiranBeratJanin)
    private TextView taksiranBeratJanin;
    @Bind(R.id.txt_persentasiJanin)
    private TextView persentasiJanin;
    @Bind(R.id.txt_jumlahJanin)
    private TextView jumlahJanin;


    @Bind(R.id.txt_statusImunisasitt)
    private TextView statusImunisasitt;
    @Bind(R.id.txt_pelayananfe)
    private TextView pelayananfe;
    @Bind(R.id.txt_komplikasidalamKehamilan)
    private TextView komplikasidalamKehamilan;

    @Bind(R.id.txt_integrasiProgrampmtctvct)
    private TextView integrasiProgrampmtctvct;
    @Bind(R.id.txt_integrasiProgrampmtctPeriksaDarah)
    private TextView integrasiProgrampmtctPeriksaDarah;
    @Bind(R.id.txt_integrasiProgrampmtctSerologi)
    private TextView integrasiProgrampmtctSerologi;
    @Bind(R.id.txt_integrasiProgrampmtctarvProfilaksis)
    private TextView integrasiProgrampmtctarvProfilaksis;
    @Bind(R.id.txt_integrasiProgramMalariaPeriksaDarah)
    private TextView integrasiProgramMalariaPeriksaDarah;
    @Bind(R.id.txt_integrasiProgramMalariaObat)
    private TextView integrasiProgramMalariaObat;
    @Bind(R.id.txt_integrasiProgramMalariaKelambuBerinsektisida)
    private TextView integrasiProgramMalariaKelambuBerinsektisida;
    @Bind(R.id.txt_integrasiProgramtbDahak)
    private TextView integrasiProgramtbDahak;
    @Bind(R.id.txt_integrasiProgramtbObat)
    private TextView integrasiProgramtbObat;

    @Bind(R.id.txt_laboratoriumPeriksaHbHasil)
    private TextView laboratoriumPeriksaHbHasil;
    @Bind(R.id.txt_laboratoriumPeriksaHbAnemia)
    private TextView laboratoriumPeriksaHbAnemia;
    @Bind(R.id.txt_laboratoriumProteinUria)
    private TextView laboratoriumProteinUria;
    @Bind(R.id.txt_laboratoriumGulaDarah)
    private TextView laboratoriumGulaDarah;
    @Bind(R.id.txt_laboratoriumThalasemia)
    private TextView laboratoriumThalasemia;
    @Bind(R.id.txt_laboratoriumSifilis)
    private TextView laboratoriumSifilis;
    @Bind(R.id.txt_laboratoriumHbsAg)
    private TextView laboratoriumHbsAg;


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

    @Bind(R.id.iv_icon_device)
    private ImageView heart_bpm;
    @Bind(R.id.btn_back_to_home)
    private ImageButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anc_detail_activity);

        ButterKnife.bind(this);
        heart_bpm.setVisibility(View.VISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
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

        if (ancClient.getCaseId() != null) {
            Support.setImagetoHolderFromUri(this, ancClient.getDetails().get("base_entity_id"), kiview, R.mipmap.woman_placeholder);
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
        Log.d(TAG, "onBackPressed: ");
        finish();
        startActivity(new Intent(this, ANCSmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }

}
