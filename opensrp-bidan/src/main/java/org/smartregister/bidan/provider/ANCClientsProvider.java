package org.smartregister.bidan.provider;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.LoginActivity;
import org.smartregister.bidan.utils.AllConstantsINA;
import org.smartregister.bidan.utils.Support;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.domain.Alert;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.joda.time.LocalDateTime.parse;

/**
 * Created by Dimas Ciputra on 2/16/15
 */
public class ANCClientsProvider extends BaseClientsProvider {

    private static final String TAG = ANCClientsProvider.class.getName();
    private final Context mContext;
    private final View.OnClickListener onClickListener;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final LayoutInflater inflater;
    protected CommonPersonObjectController controller;

    @Bind(R.id.profile_info_layout)
    private LinearLayout profilelayout;
    @Bind(R.id.iv_mother_photo)
    private ImageView profilepic;
    @Bind(R.id.tv_wife_name)
    private TextView wife_name;
    @Bind(R.id.tv_husband_name)
    private TextView husband_name;
    @Bind(R.id.tv_village_name)
    private TextView village_name;
    @Bind(R.id.tv_wife_age)
    private TextView wife_age;
    @Bind(R.id.no_ibu)
    private TextView no_ibu;
    @Bind(R.id.unique_id)
    private TextView unique_id;
    @Bind(R.id.iv_hr_badge)
    private ImageView hr_badge;
    @Bind(R.id.iv_hrl_badge)
    private ImageView hrl_badge;
    @Bind(R.id.iv_bpl_badge)
    private ImageView bpl_badge;
    @Bind(R.id.iv_hrp_badge)
    private ImageView hrp_badge;
    @Bind(R.id.iv_hrpp_badge)
    private ImageView hrpp_badge;
    @Bind(R.id.txt_usia_klinis)
    private TextView usia_klinis;
    @Bind(R.id.txt_htpt)
    private TextView htpt;
    @Bind(R.id.txt_edd_due)
    private TextView edd_due;
    @Bind(R.id.txt_ki_lila_bb)
    private TextView ki_lila_bb;
    @Bind(R.id.txt_ki_beratbadan_tb)
    private TextView beratbadan_tb;
    @Bind(R.id.txt_tanggal_kunjungan_anc)
    private TextView tanggal_kunjungan_anc;
    @Bind(R.id.txt_anc_number)
    private TextView anc_number;
    @Bind(R.id.txt_kunjugan_ke)
    private TextView kunjugan_ke;
    @Bind(R.id.anc_status_layout)
    private RelativeLayout status_layout;
    @Bind(R.id.txt_status_type)
    private TextView status_type;
    @Bind(R.id.txt_status_date_anc)
    private TextView status_date;
    @Bind(R.id.txt_alert_status)
    private TextView alert_status;
    @Bind(R.id.btn_anc_edit)
    private ImageButton follow_up;
    private AlertService alertService;

    public ANCClientsProvider(Context context, View.OnClickListener onClickListener, AlertService alertService) {
        super(context);
        this.onClickListener = onClickListener;
        this.mContext = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));

    }

    public void getView(SmartRegisterClient smartRegisterClient, View convertView) {

        try {
            ButterKnife.bind(this, convertView);

        } catch (Exception e) {
            e.getCause().printStackTrace();
            Log.e(TAG, "getView: " + e.getMessage());
        }
//        // Load data from DB
        profilelayout.setOnClickListener(onClickListener);
        profilelayout.setTag(smartRegisterClient);

        follow_up.setImageResource(R.drawable.ic_pencil);
        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        AllCommonsRepository allancRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
        CommonPersonObject ancobject = allancRepository.findByCaseID(pc.entityId());
        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(ancobject);

        Log.e(TAG, "getView: client : " + ancobject.getColumnmaps().toString());
        Log.e(TAG, "getView: event : " + ancobject.getDetails().toString());

        Map<String, String> details = detailsRepository.getAllDetailsForClient(pc.entityId());
        details.putAll(ancobject.getColumnmaps());

        if (pc.getDetails() != null) pc.getDetails().putAll(details);
        else pc.setDetails(details);

        //Risk flag
        hr_badge.setVisibility(View.INVISIBLE); // High Risks
        hrp_badge.setVisibility(View.INVISIBLE); // High Risks Pregnancy
        hrl_badge.setVisibility(View.INVISIBLE); // High Risk Liabirth

        if ("yes".matches(pc.getDetails().get("highRisksSTIBBVs")+ "||" +
                pc.getDetails().get("highRiskEctopicPregnancy")+ "||" +
                pc.getDetails().get("highRiskCardiovascularDiseaseRecord")+ "||" +
                pc.getDetails().get("highRiskDidneyDisorder")+ "||" +
                pc.getDetails().get("highRiskHeartDisorder")+ "||" +
                pc.getDetails().get("highRiskAsthma")+ "||" +
                pc.getDetails().get("highRiskTuberculosis")+ "||" +
                pc.getDetails().get("highRiskMalaria")+ "||" +
                pc.getDetails().get("highRiskPregnancyYoungMaternalAge")+ "||" +
                pc.getDetails().get("highRiskPregnancyOldMaternalAge"))) hr_badge.setVisibility(View.VISIBLE);


        if ("yes".matches(pc.getDetails().get("highRiskPregnancyPIH")+ "||" +
                pc.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition")+ "||" +
                pc.getDetails().get("HighRiskPregnancyTooManyChildren")+ "||" +
                pc.getDetails().get("highRiskPregnancyDiabetes")+ "||" +
                pc.getDetails().get("highRiskPregnancyAnemia")))
                hrp_badge.setVisibility(View.VISIBLE);

        if ("yes".matches(pc.getDetails().get("highRiskLabourFetusMalpresentation")+ "||" +
                pc.getDetails().get("highRiskLabourFetusSize")+ "||" +
                pc.getDetails().get("highRisklabourFetusNumber")+ "||" +
                pc.getDetails().get("HighRiskLabourSectionCesareaRecord")+ "||" +
                pc.getDetails().get("highRiskLabourTBRisk")))
                hrl_badge.setVisibility(View.VISIBLE);

        //start profile image
        profilepic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk

        Support.setImagetoHolderFromUri((Activity) mContext, pc.getDetails().get("base_entity_id"), profilepic, R.mipmap.woman_placeholder);

        wife_name.setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "");
        husband_name.setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "");
        village_name.setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
        wife_age.setText(pc.getDetails().get("umur") != null ? pc.getDetails().get("umur") : "");
        no_ibu.setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");
        unique_id.setText(pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) != null ? pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) : "");

        usia_klinis.setText(pc.getDetails().get("usiaKlinis") != null ? mContext.getString(R.string.usia) + pc.getDetails().get("usiaKlinis") + mContext.getString(R.string.str_weeks) : "-");
        htpt.setText(pc.getDetails().get("htp") != null ? pc.getDetails().get("htp") : "-");

        String edd = pc.getDetails().get("htp");
        if (StringUtils.isNotBlank(pc.getDetails().get("htp"))) {
            String _dueEdd = "";
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            LocalDate date = parse(edd, formatter).toLocalDate();
            LocalDate dateNow = LocalDate.now();
            date = date.withDayOfMonth(1);
            dateNow = dateNow.withDayOfMonth(1);
            int months = Months.monthsBetween(dateNow, date).getMonths();
            if (months >= 1) {
                edd_due.setTextColor(mContext.getResources().getColor(R.color.alert_in_progress_blue));
                _dueEdd = "" + months + " " + mContext.getString(R.string.months_away);
            } else if (months == 0) {
                edd_due.setTextColor(mContext.getResources().getColor(R.color.light_blue));
                _dueEdd = mContext.getString(R.string.this_month);
            } else if (months < 0) {
                edd_due.setTextColor(mContext.getResources().getColor(R.color.alert_urgent_red));
                _dueEdd = mContext.getString(R.string.edd_passed);
            }

            edd_due.setText(_dueEdd);

        } else {
            edd_due.setText("-");
        }


        ki_lila_bb.setText(pc.getDetails().get("hasilPemeriksaanLILA") != null ? pc.getDetails().get("hasilPemeriksaanLILA") : "-");

        beratbadan_tb.setText(pc.getDetails().get("bbKg") != null ? pc.getDetails().get("bbKg") : "-");

        String ancDate = pc.getDetails().get("ancDate") != null ? pc.getDetails().get("ancDate") : "-";
        String ancKe = pc.getDetails().get("ancKe") != null ? pc.getDetails().get("ancKe") : "-";
        String kunjunganKe = pc.getDetails().get("kunjunganKe") != null ? pc.getDetails().get("kunjunganKe") : "-";

        tanggal_kunjungan_anc.setText(String.format("%s%s", mContext.getString(R.string.last_visit_date), ancDate));
        anc_number.setText(String.format("%s%s", mContext.getString(R.string.anc_ke), ancKe));
        kunjugan_ke.setText(String.format("%s%s", mContext.getString(R.string.visit_number), kunjunganKe));

        status_type.setText("");
        status_date.setText("");
        status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
        alert_status.setText("");

        if ("-".equals(ancKe) || "".equals(ancKe)) {
            status_type.setText(R.string.ANC1);
        }
        if ("1".equals(ancKe)) {
            status_type.setText(R.string.ANC2);
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "ANC 2");
            //alertlist_for_client.get(i).
            if (alertlist_for_client.size() == 0) {
                //  due_visit_date.setText("Not Synced to Server");
                status_layout.setBackgroundColor(mContext.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for (int i = 0; i < alertlist_for_client.size(); i++) {

                //  status_date.setText(alertlist_for_client.get(i).startDate());
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.alert_urgent_red));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.client_list_header_dark_grey));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).isComplete()) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }

        if ("2".equals(ancKe)) {
            status_type.setText(R.string.ANC3);
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "ANC 3");
            //alertlist_for_client.get(i).
            if (alertlist_for_client.size() == 0) {
                //  due_visit_date.setText("Not Synced to Server");
                status_layout.setBackgroundColor(mContext.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for (int i = 0; i < alertlist_for_client.size(); i++) {

                // status_date.setText(alertlist_for_client.get(i).startDate());
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.alert_urgent_red));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.client_list_header_dark_grey));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).isComplete()) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }

        if ("3".equals(ancKe)) {
            status_type.setText(R.string.ANC4);
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "ANC 4");
            //alertlist_for_client.get(i).
            if (alertlist_for_client.size() == 0) {
                //  due_visit_date.setText("Not Synced to Server");
                status_layout.setBackgroundColor(mContext.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for (int i = 0; i < alertlist_for_client.size(); i++) {

                //   status_date.setText(alertlist_for_client.get(i).startDate());
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.alert_urgent_red));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.client_list_header_dark_grey));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).isComplete()) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }

        convertView.setLayoutParams(clientViewLayoutParams);

    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {
        LoginActivity.setLanguage();
//        Log.e(TAG, "getView: "+ mContext.getResources().getConfiguration().locale );
        getView(smartRegisterClient, view);
    }

    public void risk(String risk1, String risk2, String risk3, String risk4, String risk5, String risk6,
                     String risk7, String risk8, String risk9, String risk10, ImageView riskview) {

        if (risk1 != null && "yes".equals(risk1)
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
        return inflater().inflate(R.layout.smart_register_ki_anc_client, null);
    }

}