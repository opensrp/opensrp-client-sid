package org.smartregister.bidan.provider;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.LoginActivity;
import org.smartregister.bidan.utils.Support;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.domain.Alert;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.Context;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class KBClientsProvider extends BaseClientsProvider {

    private static final String TAG = KBClientsProvider.class.getName();

    private final View.OnClickListener onClickListener;
    private final android.content.Context mContext;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    protected CommonPersonObjectController controller;
    private AlertService alertService;

    // layout/smart_register_kb_client.xml
//    @Bind(R.id.tv_no_ibu_kb)
//    private TextView no_ibu;
//
//    // layout/smart_register_client_obsetri_layout
//    @Bind(R.id.tv_gravida)
//    private TextView gravida;
//    @Bind(R.id.tv_parity)
//    private TextView parity;
//    @Bind(R.id.tv_number_of_abortus)
//    private TextView number_of_abortus;
//    @Bind(R.id.tv_number_of_alive)
//    private TextView number_of_alive;
//
//    // Profile KI
//    // layout/smart_register_client_profile_ki
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
//
//    @Bind(R.id.tv_kb_method)
//    private TextView kb_method;
//    @Bind(R.id.tv_kb_mulai)
//    private TextView kb_mulai;
//    @Bind(R.id.tv_risk_HB)
//    private TextView risk_HB;
//    @Bind(R.id.tv_risk_LILA)
//    private TextView LILA;
//    @Bind(R.id.tv_risk_PenyakitKronis)
//    private TextView risk_PenyakitKronis;
//    @Bind(R.id.tv_risk_IMS)
//    private TextView risk_IMS;
//    @Bind(R.id.tv_follow_due)
//    private TextView follow_up_due;
//    @Bind(R.id.follow_layout)
//    private LinearLayout follow_layout;
//    @Bind(R.id.tv_follow_status)
//    private TextView follow_status;
//    @Bind(R.id.tv_follow_up_due)
//    private TextView follow_due;
//    @Bind(R.id.iv_mother_photo)
//    private ImageView profilepic;
//    @Bind(R.id.ib_btn_edit)
//    private ImageButton follow_up;
//    private Drawable iconPencilDrawable;
//
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


    public KBClientsProvider(android.content.Context context, View.OnClickListener onClickListener, AlertService alertService) {
        super(context);
        this.onClickListener = onClickListener;
        this.mContext = context;
        this.alertService = alertService;
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.list_item_height));
    }

    private void getView(SmartRegisterClient smartRegisterClient, View convertView) {
//        try {
//
//            ButterKnife.bind(this, convertView);
//
//        } catch (Exception e) {
//            e.getCause().printStackTrace();
//            Log.e(TAG, "getView: " + e.getMessage());
//        }

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        DetailsRepository detailsRepository = Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);

//        System.out.println("client : " + pc.getColumnmaps().toString());
//        System.out.println("event : " + pc.getDetails().toString());

        Log.e(TAG, "getView: " + pc.getDetails().toString());
        Log.e(TAG, "getView: " + pc.getColumnmaps().toString());

        // ========================================================================================
        // Set Value
        // ========================================================================================

        LinearLayout profilelayout = (LinearLayout) convertView.findViewById(R.id.profile_info_layout);
        profilelayout.setOnClickListener(onClickListener);
        profilelayout.setTag(smartRegisterClient);

        final ImageView profilePic = (ImageView) convertView.findViewById(R.id.iv_mother_photo);
        profilePic.setImageResource(R.mipmap.woman_placeholder);
        ((TextView) convertView.findViewById(R.id.tv_wife_name)).setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "");
        ((TextView) convertView.findViewById(R.id.tv_husband_name)).setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "");
        ((TextView) convertView.findViewById(R.id.tv_village_name)).setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
        ((TextView) convertView.findViewById(R.id.tv_wife_age)).setText(pc.getDetails().get("umur") != null ? pc.getDetails().get("umur") : "");
        ((TextView) convertView.findViewById(R.id.pnc_id)).setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");

        profilelayout.setOnClickListener(onClickListener);
        profilelayout.setTag(smartRegisterClient);

        ImageButton follow_up = (ImageButton) convertView.findViewById(R.id.btn_edit);
        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);
        follow_up.setImageResource(R.drawable.ic_pencil);
        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);

//        hr_badge.setVisibility(View.INVISIBLE);
        ImageView hr_badge = (ImageView) convertView.findViewById(R.id.iv_hr_badge);

        //Risk flag
        if ("yes".matches(pc.getDetails().get("highRiskSTIBBVs")+"||"+ pc.getDetails().get("highRiskEctopicPregnancy")+"||"+ pc.getDetails().get("highRiskCardiovascularDiseaseRecord")+"||"+
                pc.getDetails().get("highRiskDidneyDisorder")+"||"+ pc.getDetails().get("highRiskHeartDisorder")+"||"+ pc.getDetails().get("highRiskAsthma")+"||"+
                pc.getDetails().get("highRiskTuberculosis")+"||"+ pc.getDetails().get("highRiskMalaria")+"||"+ pc.getDetails().get("highRiskPregnancyYoungMaternalAge")+"||"+
                pc.getDetails().get("highRiskPregnancyOldMaternalAge"))) hr_badge.setVisibility(View.VISIBLE);

        profilePic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
        Support.setImagetoHolderFromUri((Activity) mContext, pc.getDetails().get("base_entity_id"), profilePic, R.mipmap.woman_placeholder);

//        wife_name.setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "null");
//        husband_name.setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "null");
//        village_name.setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
//        wife_age.setText(pc.getColumnmaps().get("umur") != null ? pc.getColumnmaps().get("umur") : "");
//        no_ibu.setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");

//        ((TextView) convertView.findViewById(R.id.unique_id)).setText(pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) != null ? pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) : "");
        ((TextView) convertView.findViewById(R.id.tv_gravida)).setText(pc.getDetails().get("gravida") != null ? pc.getDetails().get("gravida") : "-");
        ((TextView) convertView.findViewById(R.id.tv_parity)).setText(pc.getDetails().get("partus") != null ? pc.getDetails().get("partus") : "-");
        ((TextView) convertView.findViewById(R.id.tv_number_of_abortus)).setText(pc.getDetails().get("abortus") != null ? pc.getDetails().get("abortus") : "-");
        ((TextView) convertView.findViewById(R.id.tv_number_of_alive)).setText(pc.getDetails().get("hidup") != null ? pc.getDetails().get("hidup") : "-");
        ((TextView) convertView.findViewById(R.id.tv_b_edd)).setText(pc.getDetails().get("htp") != null ? pc.getDetails().get("htp") : "");

//        gravida.setText(pc.getDetails().get("gravida") != null ? pc.getDetails().get("gravida") : "-");
//        parity.setText(pc.getDetails().get("partus") != null ? pc.getDetails().get("partus") : "-");
//        number_of_abortus.setText(pc.getDetails().get("abortus") != null ? pc.getDetails().get("abortus") : "-");
//        number_of_alive.setText(pc.getDetails().get("hidup") != null ? pc.getDetails().get("hidup") : "-");
        ((TextView) convertView.findViewById(R.id.tv_kb_method)).setText(pc.getDetails().get("jenisKontrasepsi") != null ? pc.getDetails().get("jenisKontrasepsi") : "");
        ((TextView) convertView.findViewById(R.id.tv_kb_mulai)).setText(pc.getDetails().get("tanggalkunjungan") != null ? pc.getDetails().get("tanggalkunjungan") : "");
        ((TextView) convertView.findViewById(R.id.tv_risk_HB)).setText(pc.getDetails().get("alkihb") != null ? pc.getDetails().get("alkihb") : "-");
        ((TextView) convertView.findViewById(R.id.tv_risk_LILA)).setText(pc.getDetails().get("alkilila") != null ? pc.getDetails().get("alkilila") : "-");
        ((TextView) convertView.findViewById(R.id.tv_risk_IMS)).setText(pc.getDetails().get("alkiPenyakitIms") != null ? pc.getDetails().get("alkiPenyakitIms") : "");
        ((TextView) convertView.findViewById(R.id.tv_risk_PenyakitKronis)).setText(pc.getDetails().get("alkiPenyakitKronis") != null ? pc.getDetails().get("alkiPenyakitKronis") : "");

        convertView.findViewById(R.id.iv_hrp_badge).setVisibility(View.INVISIBLE);
        convertView.findViewById(R.id.iv_hrl_badge).setVisibility(View.INVISIBLE);

        LinearLayout follow_layout = (LinearLayout) convertView.findViewById(R.id.follow_layout);
        TextView follow_due = (TextView) convertView.findViewById(R.id.tv_b_edd);
        TextView follow_up_due = (TextView) convertView.findViewById(R.id.mother_status);
        TextView follow_status = (TextView) convertView.findViewById(R.id.visit_status);

        follow_due.setText("");
        follow_up_due.setText("");
        follow_layout.setBackgroundColor(mContext.getResources().getColor(R.color.status_bar_text_almost_white));
        follow_status.setText("");

        String jenis = pc.getDetails().get("jenisKontrasepsi") != null ? pc.getDetails().get("jenisKontrasepsi") : "-";
        if ("suntik".equals(jenis)) {
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "KB Injection Cyclofem");
            //alertlist_for_client.get(i).
            if (alertlist_for_client.size() == 0) {
                // viewHolder.follow_up_due.setText("Not Synced");
                follow_layout.setBackgroundColor(mContext.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for (int i = 0; i < alertlist_for_client.size(); i++) {
                follow_due.setText(R.string.followUpDue);
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                    follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    follow_layout.setBackgroundColor(mContext.getResources().getColor(R.color.alert_upcoming_light_blue));
                    follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                    follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    follow_layout.setBackgroundColor(mContext.getResources().getColor(R.color.alert_upcoming_light_blue));
                    follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                    follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    follow_layout.setBackgroundColor(mContext.getResources().getColor(R.color.alert_urgent_red));
                    follow_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                    follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    follow_layout.setBackgroundColor(mContext.getResources().getColor(R.color.client_list_header_dark_grey));
                    follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).isComplete()) {
                    follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    follow_layout.setBackgroundColor(mContext.getResources().getColor(R.color.status_bar_text_almost_white));
                    follow_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }

        convertView.setLayoutParams(clientViewLayoutParams);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {
        Log.e(TAG, "getView: ");
        LoginActivity.setLanguage();
        getView(smartRegisterClient, view);
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

//    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(R.layout.smart_register_kb_client, null);
    }
}
