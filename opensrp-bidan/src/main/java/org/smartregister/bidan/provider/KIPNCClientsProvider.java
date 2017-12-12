package org.smartregister.bidan.provider;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.bidan.R;
import org.smartregister.bidan.utils.Support;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.smartregister.util.StringUtil.humanize;
import static org.smartregister.util.StringUtil.humanizeAndDoUPPERCASE;

/**
 * Created by sid-tech on 11/30/17.
 */

public class KIPNCClientsProvider extends BaseClientsProvider {

    private final Context context;
    private final View.OnClickListener onClickListener;
    private final OpenSRPImageLoader mImageLoader;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    protected CommonPersonObjectController controller;
    AlertService alertService;
    @Bind(R.id.profile_info_layout)
    LinearLayout profilelayout;
    @Bind(R.id.tv_wife_name)
    TextView wife_name;
    @Bind(R.id.tv_husband_name)
    TextView husband_name;
    @Bind(R.id.tv_village_name)
    TextView village_name;
    @Bind(R.id.tv_wife_age)
    TextView wife_age;
    @Bind(R.id.pnc_id)
    TextView pnc_id;
    // Badge Flag
    @Bind(R.id.iv_hr_badge)
    ImageView hr_badge;
    // unique_id = (TextView)@Bind(R.id.unique_id);
    @Bind(R.id.iv_hrl_badge)
    ImageView img_hrl_badge;
    @Bind(R.id.iv_bpl_badge)
    ImageView bpl_badge;
    @Bind(R.id.iv_hrp_badge)
    ImageView hrp_badge;
    @Bind(R.id.iv_hrpp_badge)
    ImageView hrpp_badge;
    @Bind(R.id.tv_dok_tanggal_bersalin)
    TextView tanggal_bersalin;
    @Bind(R.id.tv_tempat_persalinan)
    TextView tempat_persalinan;
    @Bind(R.id.tv_tipe)
    TextView dok_tipe;
    @Bind(R.id.tv_komplikasi)
    TextView komplikasi;
    @Bind(R.id.tv_tgl_kunjungan_pnc)
    TextView tanggal_kunjungan;
    @Bind(R.id.tv_kf)
    TextView KF;
    @Bind(R.id.tv_vit_a)
    TextView vit_a;
    @Bind(R.id.tv_td_sistolik)
    TextView td_sistolik;
    @Bind(R.id.tv_td_diastolik)
    TextView td_diastolik;
    @Bind(R.id.tv_td_suhu)
    TextView td_suhu;
    //  txt_kondisi_ibu txt_KF txt_vit_a
    @Bind(R.id.iv_mother_photo)
    ImageView profilepic;
    @Bind(R.id.ib_btn_edit)
    ImageButton follow_up;
    private Drawable iconPencilDrawable;

    public KIPNCClientsProvider(Context context, View.OnClickListener onClickListener, AlertService alertService) {

        super(context);
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(R.color.text_black);

        mImageLoader = DrishtiApplication.getCachedImageLoaderInstance();

    }

    public void getView(SmartRegisterClient smartRegisterClient, View convertView) {

        try {
            ButterKnife.bind(this, convertView);
        } catch (Exception e) {
            e.getCause().printStackTrace();
        }

        // ========================================================================================
        // Load Value from Local DB
        // ========================================================================================
        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        AllCommonsRepository allancRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_pnc");
        CommonPersonObject pncobject = allancRepository.findByCaseID(pc.entityId());

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);
        Map<String, String> details = detailsRepository.getAllDetailsForClient(pc.entityId());
        details.putAll(pncobject.getColumnmaps());

        System.out.println("client : " + pc.getColumnmaps().toString());
        System.out.println("event : " + pc.getDetails().toString());

        // ========================================================================================
        // Set Value
        // ========================================================================================
        profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.woman_placeholder));
        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);
        profilelayout.setOnClickListener(onClickListener);
        profilelayout.setTag(smartRegisterClient);

        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        follow_up.setImageDrawable(iconPencilDrawable);
        follow_up.setOnClickListener(onClickListener);
        //set image

        if (pc.getDetails() != null) {
            pc.getDetails().putAll(details);
        } else {
            pc.setDetails(details);
        }

        tanggal_bersalin.setText(humanize(pc.getDetails().get("tanggalKalaIAktif") != null ? pc.getDetails().get("tanggalKalaIAktif") : ""));
        String tempat = pc.getDetails().get("tempatBersalin") != null ? pc.getDetails().get("tempatBersalin") : "";
        tempat_persalinan.setText(tempat.equals("podok_bersalin_desa") ? "POLINDES" : tempat.equals("pusat_kesehatan_masyarakat_pembantu") ? "Puskesmas pembantu" : tempat.equals("pusat_kesehatan_masyarakat") ? "Puskesmas" : humanize(tempat));
        dok_tipe.setText(humanize(pc.getDetails().get("caraPersalinanIbu") != null ? pc.getDetails().get("caraPersalinanIbu") : ""));
        komplikasi.setText(humanize(pc.getDetails().get("komplikasi") != null ? translateComplication(pc.getDetails().get("komplikasi")) : ""));

        String date = pc.getDetails().get("PNCDate") != null ? pc.getDetails().get("PNCDate") : "";
        String str_vit_a = pc.getDetails().get("pelayananfe") != null ? pc.getDetails().get("pelayananfe") : "";
        tanggal_kunjungan.setText(String.format("%s %s", context.getString(R.string.str_pnc_delivery_date), date));

        vit_a.setText(String.format("%s %s", context.getString(R.string.fe), yesNo(str_vit_a)));
        td_suhu.setText(humanize(pc.getDetails().get("tandaVitalSuhu") != null ? pc.getDetails().get("tandaVitalSuhu") : ""));
        td_sistolik.setText(humanize(pc.getDetails().get("tandaVitalTDSistolik") != null ? pc.getDetails().get("tandaVitalTDSistolik") : ""));
        td_diastolik.setText(humanize(pc.getDetails().get("tandaVitalTDDiastolik") != null ? pc.getDetails().get("tandaVitalTDDiastolik") : ""));
        hr_badge.setVisibility(View.INVISIBLE);
        hrp_badge.setVisibility(View.INVISIBLE);
        img_hrl_badge.setVisibility(View.INVISIBLE);

        //Risk flag
        risk(pc.getDetails().get("highRiskSTIBBVs"), pc.getDetails().get("highRiskEctopicPregnancy"), pc.getDetails().get("highRiskCardiovascularDiseaseRecord"),
                pc.getDetails().get("highRiskDidneyDisorder"), pc.getDetails().get("highRiskHeartDisorder"), pc.getDetails().get("highRiskAsthma"),
                pc.getDetails().get("highRiskTuberculosis"), pc.getDetails().get("highRiskMalaria"), pc.getDetails().get("highRiskPregnancyYoungMaternalAge"),
                pc.getDetails().get("highRiskPregnancyOldMaternalAge"), hr_badge);

        risk(pc.getDetails().get("highRiskPregnancyPIH"), pc.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"),
                pc.getDetails().get("HighRiskPregnancyTooManyChildren"),
                pc.getDetails().get("highRiskPregnancyDiabetes"), pc.getDetails().get("highRiskPregnancyAnemia"), null, null, null, null, null, hrp_badge);

        risk(pc.getDetails().get("highRiskLabourFetusMalpresentation"), pc.getDetails().get("highRiskLabourFetusSize"),
                pc.getDetails().get("highRisklabourFetusNumber"), pc.getDetails().get("HighRiskLabourSectionCesareaRecord"),
                pc.getDetails().get("highRiskLabourTBRisk"), null, null, null, null, null, img_hrl_badge);

        String kf_ke = pc.getDetails().get("hariKeKF") != null ? pc.getDetails().get("hariKeKF") : "";
        KF.setText(String.format("%s %s", context.getString(R.string.hari_ke_kf), humanizeAndDoUPPERCASE(kf_ke)));
        wife_name.setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "");
        husband_name.setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "");
        village_name.setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
        wife_age.setText(pc.getDetails().get("umur") != null ? pc.getDetails().get("umur") : "");
        pnc_id.setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");

        // Photo
        profilepic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
        Support.setImagetoHolderFromUri((Activity) context, pc.getDetails().get("base_entity_id"), profilepic, R.mipmap.woman_placeholder);

        convertView.setLayoutParams(clientViewLayoutParams);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {
        getView(smartRegisterClient, view);
    }

    private String yesNo(String text) {
        return context.getString(text.toLowerCase().contains("y") ? R.string.mcareyes_button_label : R.string.mcareno_button_label);
    }

    private String translateComplication(String text) {
        return text.toLowerCase().contains("dak_ada_kompli") ? context.getString(R.string.no_complication) : text;
    }

    public void risk(String risk1, String risk2, String risk3, String risk4, String risk5, String risk6, String risk7, String risk8, String risk9, String risk10, ImageView riskview) {
        if (risk1 != null && risk1.equals("yes")
                || risk2 != null && risk2.equals("yes")
                || risk3 != null && risk3.equals("yes")
                || risk4 != null && risk4.equals("yes")
                || risk5 != null && risk5.equals("yes")
                || risk6 != null && risk6.equals("yes")
                || risk7 != null && risk7.equals("yes")
                || risk8 != null && risk8.equals("yes")
                || risk9 != null && risk9.equals("yes")
                || risk10 != null && risk10.equals("yes")) {

            riskview.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        View view = inflater().inflate(R.layout.smart_register_ki_pnc_client, null);
        return view;
    }

}
