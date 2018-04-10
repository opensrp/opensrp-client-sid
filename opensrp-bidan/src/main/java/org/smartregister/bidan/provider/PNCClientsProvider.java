package org.smartregister.bidan.provider;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.LoginActivity;
import org.smartregister.bidan.utils.Support;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.smartregister.util.StringUtil.humanize;
import static org.smartregister.util.StringUtil.humanizeAndDoUPPERCASE;

/**
 * Created by sid-tech on 11/30/17
 */

public class PNCClientsProvider extends BaseClientsProvider {

    private final String TAG = PNCClientsProvider.class.getSimpleName();
    private final Context mContext;
    private final View.OnClickListener onClickListener;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    protected CommonPersonObjectController controller;

//    @Bind(R.id.profile_info_layout)
//    private LinearLayout profilelayout;
//    @Bind(R.id.tv_wife_name)
//    private TextView wife_name;
//    @Bind(R.id.tv_husband_name)
//    private TextView husband_name;
//    @Bind(R.id.tv_village_name)
//    private TextView village_name;
//    @Bind(R.id.tv_wife_age)
//    private TextView wife_age;
//    @Bind(R.id.pnc_id)
//    private TextView pnc_id;
//    // unique_id = (TextView)@Bind(R.id.unique_id);
//    @Bind(R.id.tv_dok_tanggal_bersalin)
//    private TextView tanggal_bersalin;
//    @Bind(R.id.tv_tempat_persalinan)
//    private TextView tempat_persalinan;
//    @Bind(R.id.tv_tipe)
//    private TextView dok_tipe;
//    @Bind(R.id.tv_komplikasi)
//    private TextView komplikasi;
//    @Bind(R.id.tv_tgl_kunjungan_pnc)
//    private TextView tanggal_kunjungan;
//    @Bind(R.id.tv_kf)
//    private TextView KF;
//    @Bind(R.id.tv_vit_a)
//    private TextView vit_a;
//    @Bind(R.id.tv_td_sistolik)
//    private TextView td_sistolik;
//    @Bind(R.id.tv_td_diastolik)
//    private TextView td_diastolik;
//    @Bind(R.id.tv_td_suhu)
//    private TextView td_suhu;
//    @Bind(R.id.iv_mother_photo)
//    private ImageView profilepic;
//    @Bind(R.id.ib_btn_edit)
//    private ImageButton follow_up;
////    private Drawable iconPencilDrawable;
//
//    // Badge Flag
//    @Bind(R.id.iv_hr_badge)
//    private ImageView hr_badge;
//    @Bind(R.id.iv_hrl_badge)
//    private ImageView img_hrl_badge;
//    @Bind(R.id.iv_hrp_badge)
//    private ImageView hrp_badge;

//    @Bind(R.id.iv_bpl_badge)
//    private ImageView bpl_badge;
//    @Bind(R.id.iv_hrpp_badge)
//    private ImageView hrpp_badge;
//    private AlertService alertService;

    //  txt_kondisi_ibu txt_KF txt_vit_a


    public PNCClientsProvider(Context context, View.OnClickListener onClickListener, AlertService alertService) {

        super(context);
        this.onClickListener = onClickListener;
        this.mContext = context;
//        this.alertService = alertService;
        Log.e(TAG, "PNCClientsProvider: "+ alertService );

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.list_item_height));
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        int txtColorBlack = context.getResources().getColor(R.color.text_black);
//        OpenSRPImageLoader mImageLoader = DrishtiApplication.getCachedImageLoaderInstance();

    }

    public void getView(SmartRegisterClient smartRegisterClient, View convertView) {

//        try {
//            ButterKnife.bind(this, convertView);
//        } catch (Exception e) {
//            e.getCause().printStackTrace();
//        }

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

        Log.e(TAG, "getView:client : " + pc.getColumnmaps().toString());
        Log.e(TAG, "getView:event  : " + pc.getDetails().toString());

        // ========================================================================================
        // Set Value
        // ========================================================================================
        final ImageView profilePic = (ImageView) convertView.findViewById(R.id.iv_mother_photo);
        profilePic.setImageResource(R.mipmap.woman_placeholder);
        ((TextView) convertView.findViewById(R.id.tv_wife_name)).setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "");
        ((TextView) convertView.findViewById(R.id.tv_husband_name)).setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "");
        ((TextView) convertView.findViewById(R.id.tv_village_name)).setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
        ((TextView) convertView.findViewById(R.id.tv_wife_age)).setText(pc.getDetails().get("umur") != null ? pc.getDetails().get("umur") : "");
        ((TextView) convertView.findViewById(R.id.pnc_id)).setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");

        // Photo
        profilePic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
        Support.setImagetoHolderFromUri((Activity) mContext, pc.getDetails().get("base_entity_id"), profilePic, R.mipmap.woman_placeholder);

        ImageButton profilelayout = (ImageButton) convertView.findViewById(R.id.profile_info_layout);
        profilelayout.setOnClickListener(onClickListener);
        profilelayout.setTag(smartRegisterClient);

        ImageButton follow_up = (ImageButton) convertView.findViewById(R.id.btn_edit);
        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);
        follow_up.setImageResource(R.drawable.ic_pencil);
        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);

        if (pc.getDetails() != null) {
            pc.getDetails().putAll(details);
        } else {
            pc.setDetails(details);
        }

        ((TextView) convertView.findViewById(R.id.tv_dok_tanggal_bersalin)).setText(humanize(pc.getDetails().get("tanggalKalaIAktif") != null ? pc.getDetails().get("tanggalKalaIAktif") : ""));
        String tempat = pc.getDetails().get("tempatBersalin") != null ? pc.getDetails().get("tempatBersalin") : "";
        ((TextView) convertView.findViewById(R.id.tv_tempat_persalinan)).setText("podok_bersalin_desa".equals(tempat) ? "POLINDES" : "pusat_kesehatan_masyarakat_pembantu".equals(tempat) ? "Puskesmas pembantu" : "pusat_kesehatan_masyarakat".equals(tempat) ? "Puskesmas" : humanize(tempat));
        ((TextView) convertView.findViewById(R.id.dok_tipe)).setText(humanize(pc.getDetails().get("caraPersalinanIbu") != null ? pc.getDetails().get("caraPersalinanIbu") : ""));
        ((TextView) convertView.findViewById(R.id.tv_komplikasi)).setText(humanize(pc.getDetails().get("komplikasi") != null ? translateComplication(pc.getDetails().get("komplikasi")) : ""));

        String date = pc.getDetails().get("PNCDate") != null ? pc.getDetails().get("PNCDate") : "";
        String str_vit_a = pc.getDetails().get("pelayananfe") != null ? pc.getDetails().get("pelayananfe") : "";
        ((TextView) convertView.findViewById(R.id.tv_tgl_kunjungan_pnc)).setText(String.format("%s %s", mContext.getString(R.string.str_pnc_delivery_date), date));
        ((TextView) convertView.findViewById(R.id.tv_vit_a)).setText(String.format("%s %s", mContext.getString(R.string.fe), yesNo(str_vit_a)));
        ((TextView) convertView.findViewById(R.id.tv_td_suhu)).setText(humanize(pc.getDetails().get("tandaVitalSuhu") != null ? pc.getDetails().get("tandaVitalSuhu") : ""));
        ((TextView) convertView.findViewById(R.id.tv_td_sistolik)).setText(humanize(pc.getDetails().get("tandaVitalTDSistolik") != null ? pc.getDetails().get("tandaVitalTDSistolik") : ""));
        ((TextView) convertView.findViewById(R.id.tv_td_diastolik)).setText(humanize(pc.getDetails().get("tandaVitalTDDiastolik") != null ? pc.getDetails().get("tandaVitalTDDiastolik") : ""));

        convertView.findViewById(R.id.iv_hr_badge).setVisibility(View.INVISIBLE);
        convertView.findViewById(R.id.iv_hrp_badge).setVisibility(View.INVISIBLE);
        convertView.findViewById(R.id.iv_hrl_badge).setVisibility(View.INVISIBLE);

        //Risk flag
        if ("yes".matches(pc.getDetails().get("highRiskSTIBBVs")+ "||" + pc.getDetails().get("highRiskEctopicPregnancy")+ "||" +pc.getDetails().get("highRiskCardiovascularDiseaseRecord") + "||" +
                pc.getDetails().get("highRiskDidneyDisorder")+ "||" +pc.getDetails().get("highRiskHeartDisorder")+ "||" +pc.getDetails().get("highRiskAsthma") + "||" +
                pc.getDetails().get("highRiskTuberculosis")+ "||" +pc.getDetails().get("highRiskMalaria")+ "||" +pc.getDetails().get("highRiskPregnancyYoungMaternalAge") + "||" +
                pc.getDetails().get("highRiskPregnancyOldMaternalAge")))
            convertView.findViewById(R.id.iv_hr_badge).setVisibility(View.VISIBLE);

        if ("yes".matches(pc.getDetails().get("highRiskPregnancyPIH")+ "||" +pc.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") +"||"+
                pc.getDetails().get("HighRiskPregnancyTooManyChildren") + "||" +
                pc.getDetails().get("highRiskPregnancyDiabetes")+ "||" +pc.getDetails().get("highRiskPregnancyAnemia")))
            convertView.findViewById(R.id.iv_hrp_badge).setVisibility(View.VISIBLE);

        if ("yes".matches(pc.getDetails().get("highRiskLabourFetusMalpresentation")+ "||" +pc.getDetails().get("highRiskLabourFetusSize")+"||"+
                pc.getDetails().get("highRisklabourFetusNumber")+ "||" +pc.getDetails().get("HighRiskLabourSectionCesareaRecord")+"||"+
                pc.getDetails().get("highRiskLabourTBRisk")))
            convertView.findViewById(R.id.iv_hrl_badge).setVisibility(View.VISIBLE);

        String kf_ke = pc.getDetails().get("hariKeKF") != null ? pc.getDetails().get("hariKeKF") : "";
        ((TextView) convertView.findViewById(R.id.tv_kf)).setText(String.format("%s %s", mContext.getString(R.string.hari_ke_kf), humanizeAndDoUPPERCASE(kf_ke)));

        convertView.setLayoutParams(clientViewLayoutParams);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {
        LoginActivity.setLanguage();
        getView(smartRegisterClient, view);
    }

    private String yesNo(String text) {
        return mContext.getString(text.toLowerCase().contains("y") ? R.string.mcareyes_button_label : R.string.mcareno_button_label);
    }

    private String translateComplication(String text) {
        return text.toLowerCase().contains("dak_ada_kompli") ? mContext.getString(R.string.no_complication) : text;
    }

//    public void risk(String risk1, String risk2, String risk3, String risk4, String risk5, String risk6, String risk7, String risk8, String risk9, String risk10, ImageView riskview) {
//        if (risk1 != null && risk1.equals("yes")
//                || risk2 != null && risk2.equals("yes")
//                || risk3 != null && risk3.equals("yes")
//                || risk4 != null && risk4.equals("yes")
//                || risk5 != null && risk5.equals("yes")
//                || risk6 != null && risk6.equals("yes")
//                || risk7 != null && risk7.equals("yes")
//                || risk8 != null && risk8.equals("yes")
//                || risk9 != null && risk9.equals("yes")
//                || risk10 != null && risk10.equals("yes")) {
//
//            riskview.setVisibility(View.VISIBLE);
//        }
//
//    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(R.layout.smart_register_ki_pnc_client, null);
    }

}