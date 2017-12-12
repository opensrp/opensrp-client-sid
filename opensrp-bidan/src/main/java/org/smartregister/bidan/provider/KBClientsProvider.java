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
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.domain.Alert;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by sid-tech on 11/30/17.
 */

public class KBClientsProvider extends BaseClientsProvider {

    private static final String TAG = KBClientsProvider.class.getName();

    private final View.OnClickListener onClickListener;
    private final Context context;
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
    @Bind(R.id.tv_no_ibu)
    TextView no_ibu;
    @Bind(R.id.tv_gravida)
    TextView gravida;
    @Bind(R.id.tv_parity)
    TextView parity;
    @Bind(R.id.tv_number_of_abortus)
    TextView number_of_abortus;
    @Bind(R.id.tv_number_of_alive)
    TextView number_of_alive;
    @Bind(R.id.iv_hr_badge)
    ImageView hr_badge;
    @Bind(R.id.iv_hrl_badge)
    ImageView img_hrl_badge;
    @Bind(R.id.iv_bpl_badge)
    ImageView bpl_badge;
    @Bind(R.id.iv_hrp_badge)
    ImageView hrp_badge;
    @Bind(R.id.iv_hrpp_badge)
    ImageView hrpp_badge;
    @Bind(R.id.tv_kb_method)
    TextView kb_method;
    @Bind(R.id.tv_kb_mulai)
    TextView kb_mulai;
    @Bind(R.id.tv_risk_HB)
    TextView risk_HB;
    @Bind(R.id.tv_risk_LILA)
    TextView LILA;
    @Bind(R.id.tv_risk_PenyakitKronis)
    TextView risk_PenyakitKronis;
    @Bind(R.id.tv_risk_IMS)
    TextView risk_IMS;
    @Bind(R.id.tv_follow_due)
    TextView follow_up_due;
    @Bind(R.id.follow_layout)
    LinearLayout follow_layout;
    @Bind(R.id.tv_follow_status)
    TextView follow_status;
    @Bind(R.id.tv_follow_up_due)
    TextView follow_due;
    @Bind(R.id.iv_mother_photo)
    ImageView profilepic;
    @Bind(R.id.ib_btn_edit)
    ImageButton follow_up;

    private Drawable iconPencilDrawable;

    public KBClientsProvider(Context context, View.OnClickListener onClickListener, AlertService alertService) {

        super(context);
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.list_item_height));
    }

    private void getView(SmartRegisterClient smartRegisterClient, View convertView) {
        try {
            ButterKnife.bind(this, convertView);
        } catch (Exception e) {
            e.getCause().printStackTrace();
        }

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);

        System.out.println("client : " + pc.getColumnmaps().toString());
        System.out.println("event : " + pc.getDetails().toString());

//        Log.e(TAG, "getView: " + pc.getDetails().toString());
//        Log.e(TAG, "getView: " + pc.getColumnmaps().toString());

        profilelayout.setOnClickListener(onClickListener);
        profilelayout.setTag(smartRegisterClient);

        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }

        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);
        follow_up.setImageDrawable(iconPencilDrawable);

        hr_badge.setVisibility(View.INVISIBLE);

        //Risk flag
        risk(pc.getDetails().get("highRiskSTIBBVs"), pc.getDetails().get("highRiskEctopicPregnancy"), pc.getDetails().get("highRiskCardiovascularDiseaseRecord"),
                pc.getDetails().get("highRiskDidneyDisorder"), pc.getDetails().get("highRiskHeartDisorder"), pc.getDetails().get("highRiskAsthma"),
                pc.getDetails().get("highRiskTuberculosis"), pc.getDetails().get("highRiskMalaria"), pc.getDetails().get("highRiskPregnancyYoungMaternalAge"),
                pc.getDetails().get("highRiskPregnancyOldMaternalAge"), hr_badge);

        profilepic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
        Support.setImagetoHolderFromUri((Activity) context, pc.getDetails().get("base_entity_id"), profilepic, R.mipmap.woman_placeholder);

        wife_name.setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "null");
        husband_name.setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "null");
        village_name.setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
        wife_age.setText(pc.getColumnmaps().get("umur") != null ? pc.getColumnmaps().get("umur") : "");
        no_ibu.setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");
        gravida.setText(pc.getDetails().get("gravida") != null ? pc.getDetails().get("gravida") : "-");
        parity.setText(pc.getDetails().get("partus") != null ? pc.getDetails().get("partus") : "-");
        number_of_abortus.setText(pc.getDetails().get("abortus") != null ? pc.getDetails().get("abortus") : "-");
        number_of_alive.setText(pc.getDetails().get("hidup") != null ? pc.getDetails().get("hidup") : "-");
        kb_method.setText(pc.getDetails().get("jenisKontrasepsi") != null ? pc.getDetails().get("jenisKontrasepsi") : "");
        kb_mulai.setText(pc.getDetails().get("tanggalkunjungan") != null ? pc.getDetails().get("tanggalkunjungan") : "");
        risk_HB.setText(pc.getDetails().get("alkihb") != null ? pc.getDetails().get("alkihb") : "-");
        LILA.setText(pc.getDetails().get("alkilila") != null ? pc.getDetails().get("alkilila") : "-");
        risk_IMS.setText(pc.getDetails().get("alkiPenyakitIms") != null ? pc.getDetails().get("alkiPenyakitIms") : "");
        risk_PenyakitKronis.setText(pc.getDetails().get("alkiPenyakitKronis") != null ? pc.getDetails().get("alkiPenyakitKronis") : "");
        hrp_badge.setVisibility(View.INVISIBLE);
        img_hrl_badge.setVisibility(View.INVISIBLE);

/*        AllCommonsRepository iburep = Context.getInstance().allCommonsRepositoryobjects("ibu");
        if(pc.getColumnmaps().get("ibu.id") != null) {
            final CommonPersonObject ibuparent = iburep.findByCaseID(pc.getColumnmaps().get("ibu.id"));*//*

            //Risk flag
            if (ibuparent.getDetails().get("highRiskPregnancyPIH") != null && ibuparent.getDetails().get("highRiskPregnancyPIH").equals("yes")
                    || pc.getDetails().get("highRiskPregnancyPIH") != null && pc.getDetails().get("highRiskPregnancyPIH").equals("yes")
                    || ibuparent.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null && ibuparent.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition").equals("yes")
                    || pc.getDetails().get("HighRiskPregnancyTooManyChildren") != null && pc.getDetails().get("HighRiskPregnancyTooManyChildren").equals("yes")
                    || ibuparent.getDetails().get("highRiskPregnancyDiabetes") != null && ibuparent.getDetails().get("highRiskPregnancyDiabetes").equals("yes")
                    || ibuparent.getDetails().get("highRiskPregnancyAnemia") != null && ibuparent.getDetails().get("highRiskPregnancyAnemia").equals("yes")) {
                viewHolder.hrp_badge.setVisibility(View.VISIBLE);
            }
            if (ibuparent.getDetails().get("highRiskLabourFetusMalpresentation") != null && ibuparent.getDetails().get("highRiskLabourFetusMalpresentation").equals("yes")
                    || ibuparent.getDetails().get("highRiskLabourFetusSize") != null && ibuparent.getDetails().get("highRiskLabourFetusSize").equals("yes")
                    || ibuparent.getDetails().get("highRisklabourFetusNumber") != null && ibuparent.getDetails().get("highRisklabourFetusNumber").equals("yes")
                    || pc.getDetails().get("HighRiskLabourSectionCesareaRecord") != null && pc.getDetails().get("HighRiskLabourSectionCesareaRecord").equals("yes")
                    || ibuparent.getDetails().get("highRiskLabourTBRisk") != null && ibuparent.getDetails().get("highRiskLabourTBRisk").equals("yes")) {
                viewHolder.img_hrl_badge.setVisibility(View.VISIBLE);
            }
        }*/

        follow_due.setText("");
        follow_up_due.setText("");
        follow_layout.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
        follow_status.setText("");

        String jenis = pc.getDetails().get("jenisKontrasepsi") != null ? pc.getDetails().get("jenisKontrasepsi") : "-";
        if (jenis.equals("suntik")) {
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "KB Injection Cyclofem");
            //alertlist_for_client.get(i).
            if (alertlist_for_client.size() == 0) {
                // viewHolder.follow_up_due.setText("Not Synced");
                follow_layout.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for (int i = 0; i < alertlist_for_client.size(); i++) {
                follow_due.setText("Follow up due");
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                    follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    follow_layout.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_light_blue));
                    follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                    follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    follow_layout.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_light_blue));
                    follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                    follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    follow_layout.setBackgroundColor(context.getResources().getColor(R.color.alert_urgent_red));
                    follow_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                    follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    follow_layout.setBackgroundColor(context.getResources().getColor(R.color.client_list_header_dark_grey));
                    follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).isComplete()) {
                    follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    follow_layout.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                    follow_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }

        convertView.setLayoutParams(clientViewLayoutParams);
        //   return convertView;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {
        getView(smartRegisterClient, view);
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
        View view = inflater().inflate(R.layout.smart_register_kb_client, null);
        return view;
    }
}

