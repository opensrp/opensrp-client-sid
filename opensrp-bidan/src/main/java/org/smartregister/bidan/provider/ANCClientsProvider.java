package org.smartregister.bidan.provider;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
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
import org.smartregister.Context;
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

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.joda.time.LocalDateTime.parse;

import org.joda.time.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by Dimas Ciputra on 2/16/15
 */
public class ANCClientsProvider extends BaseClientsProvider {

    private static final String TAG = ANCClientsProvider.class.getName();
    private final android.content.Context mContext;
    private final View.OnClickListener onClickListener;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    protected CommonPersonObjectController controller;

//    @Bind(R.id.profile_info_layout)
//    private LinearLayout profilelayout;
//    @Bind(R.id.iv_mother_photo)
//    private ImageView profilepic;
//    @Bind(R.id.tv_wife_name)
//    private TextView wife_name;
//    @Bind(R.id.tv_husband_name)
//    private TextView husband_name;
//    @Bind(R.id.tv_village_name)
//    private TextView village_name;
//    @Bind(R.id.tv_wife_age)
//    private TextView wife_age;
//    @Bind(R.id.no_ibu)
//    private TextView no_ibu;
//    @Bind(R.id.unique_id)
//    private TextView unique_id;
//    @Bind(R.id.iv_hr_badge)
//    private ImageView hr_badge;
//    @Bind(R.id.iv_hrl_badge)
//    private ImageView hrl_badge;
//    @Bind(R.id.iv_hrp_badge)
//    private ImageView hrp_badge;
//    //    @Bind(R.id.iv_bpl_badge)
////    private ImageView bpl_badge;
////    @Bind(R.id.iv_hrpp_badge)
////    private ImageView hrpp_badge;
//    @Bind(R.id.txt_usia_klinis)
//    private TextView usia_klinis;
//    @Bind(R.id.txt_htpt)
//    private TextView htpt;
//    @Bind(R.id.txt_edd_due)
//    private TextView edd_due;
//    @Bind(R.id.txt_ki_lila_bb)
//    private TextView ki_lila_bb;
//    @Bind(R.id.txt_ki_beratbadan_tb)
//    private TextView beratbadan_tb;
//    @Bind(R.id.txt_tanggal_kunjungan_anc)
//    private TextView tanggal_kunjungan_anc;
//    @Bind(R.id.txt_anc_number)
//    private TextView anc_number;
//    @Bind(R.id.txt_kunjugan_ke)
//    private TextView kunjugan_ke;
//    @Bind(R.id.anc_status_layout)
//    private RelativeLayout status_layout;
//    @Bind(R.id.txt_status_type)
//    private TextView status_type;
//    @Bind(R.id.txt_status_date_anc)
//    private TextView status_date;
//    @Bind(R.id.txt_alert_status)
//    private TextView alert_status;
//    @Bind(R.id.btn_anc_edit)
//    private ImageButton follow_up;
    private AlertService alertService;

    public ANCClientsProvider(android.content.Context context, View.OnClickListener onClickListener, AlertService alertService) {
        super(context);
        this.onClickListener = onClickListener;
        this.mContext = context;
        this.alertService = alertService;
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));

    }

    public void getView(SmartRegisterClient smartRegisterClient, View convertView) {

//        try {
//            ButterKnife.bind(this, convertView);
//
//        } catch (Exception e) {
//            e.getCause().printStackTrace();
//            Log.e(TAG, "getView: " + e.getMessage());
//        }
//        // Load data from DB

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        AllCommonsRepository allancRepository = Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
        CommonPersonObject ancobject = allancRepository.findByCaseID(pc.entityId());
        DetailsRepository detailsRepository = Context.getInstance().detailsRepository();
        if(ancobject != null) {
            detailsRepository.updateDetails(ancobject);

//            Log.e(TAG, "getView: client : " + ancobject.getColumnmaps().toString());
//            Log.e(TAG, "getView: event : " + ancobject.getDetails().toString());

            Map<String, String> details = detailsRepository.getAllDetailsForClient(pc.entityId());
            details.putAll(ancobject.getColumnmaps());

            if (pc.getDetails() != null) pc.getDetails().putAll(details);
            else pc.setDetails(details);
        }
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
        ((TextView) convertView.findViewById(R.id.no_ibu)).setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");

        profilelayout.setOnClickListener(onClickListener);
        profilelayout.setTag(smartRegisterClient);

        ImageButton follow_up = (ImageButton) convertView.findViewById(R.id.btn_anc_edit);
        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);
        follow_up.setImageResource(R.drawable.ic_pencil);
        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);

//        hr_badge.setVisibility(INVISIBLE);

        profilePic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
        Support.setImagetoHolderFromUri((Activity) mContext, pc.getDetails().get("base_entity_id"), profilePic, R.mipmap.woman_placeholder);

//        wife_name.setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "null");
//        husband_name.setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "null");
//        village_name.setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
//        wife_age.setText(pc.getColumnmaps().get("umur") != null ? pc.getColumnmaps().get("umur") : "");
//        no_ibu.setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");

//        ((TextView) convertView.findViewById(R.id.unique_id)).setText(pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) != null ? pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) : "");
//        ((TextView) convertView.findViewById(R.id.tv_gravida)).setText(pc.getDetails().get("gravida") != null ? pc.getDetails().get("gravida") : "-");
//        ((TextView) convertView.findViewById(R.id.tv_parity)).setText(pc.getDetails().get("partus") != null ? pc.getDetails().get("partus") : "-");
//        ((TextView) convertView.findViewById(R.id.tv_number_of_abortus)).setText(pc.getDetails().get("abortus") != null ? pc.getDetails().get("abortus") : "-");
//        ((TextView) convertView.findViewById(R.id.tv_number_of_alive)).setText(pc.getDetails().get("hidup") != null ? pc.getDetails().get("hidup") : "-");
//        ((TextView) convertView.findViewById(R.id.tv_b_edd)).setText(pc.getDetails().get("htp") != null ? pc.getDetails().get("htp") : "");
        // ===>

        profilelayout.setOnClickListener(onClickListener);
        profilelayout.setTag(smartRegisterClient);

        follow_up.setImageResource(R.drawable.ic_pencil);
        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);

        //Risk flag
        ImageView hr_badge = (ImageView) convertView.findViewById(R.id.iv_hr_badge);
        ImageView hrp_badge = (ImageView) convertView.findViewById(R.id.iv_hrp_badge);
        ImageView hrl_badge = (ImageView) convertView.findViewById(R.id.iv_hrl_badge);

        hr_badge.setVisibility(INVISIBLE); // High Risks
        hrp_badge.setVisibility(INVISIBLE); // High Risks Pregnancy
        hrl_badge.setVisibility(INVISIBLE); // High Risk Liabirth

        //Risk flag
        if ("yes".matches(pc.getDetails().get("highRiskSTIBBVs")+"||"+ pc.getDetails().get("highRiskEctopicPregnancy")+"||"+ pc.getDetails().get("highRiskCardiovascularDiseaseRecord")+"||"+
                pc.getDetails().get("highRiskDidneyDisorder")+"||"+ pc.getDetails().get("highRiskHeartDisorder")+"||"+ pc.getDetails().get("highRiskAsthma")+"||"+
                pc.getDetails().get("highRiskTuberculosis")+"||"+ pc.getDetails().get("highRiskMalaria")+"||"+ pc.getDetails().get("highRiskPregnancyYoungMaternalAge")+"||"+
                pc.getDetails().get("highRiskPregnancyOldMaternalAge"))) hr_badge.setVisibility(VISIBLE);

        if ("yes".matches(pc.getDetails().get("highRisksSTIBBVs")+ "||" +
                pc.getDetails().get("highRiskEctopicPregnancy")+ "||" +
                pc.getDetails().get("highRiskCardiovascularDiseaseRecord")+ "||" +
                pc.getDetails().get("highRiskDidneyDisorder")+ "||" +
                pc.getDetails().get("highRiskHeartDisorder")+ "||" +
                pc.getDetails().get("highRiskAsthma")+ "||" +
                pc.getDetails().get("highRiskTuberculosis")+ "||" +
                pc.getDetails().get("highRiskMalaria")+ "||" +
                pc.getDetails().get("highRiskPregnancyYoungMaternalAge")+ "||" +
                pc.getDetails().get("highRiskPregnancyOldMaternalAge"))) hr_badge.setVisibility(VISIBLE);


        if ("yes".matches(pc.getDetails().get("highRiskPregnancyPIH")+ "||" +
                pc.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition")+ "||" +
                pc.getDetails().get("HighRiskPregnancyTooManyChildren")+ "||" +
                pc.getDetails().get("highRiskPregnancyDiabetes")+ "||" +
                pc.getDetails().get("highRiskPregnancyAnemia")))
                hrp_badge.setVisibility(VISIBLE);

        if ("yes".matches(pc.getDetails().get("highRiskLabourFetusMalpresentation")+ "||" +
                pc.getDetails().get("highRiskLabourFetusSize")+ "||" +
                pc.getDetails().get("highRisklabourFetusNumber")+ "||" +
                pc.getDetails().get("HighRiskLabourSectionCesareaRecord")+ "||" +
                pc.getDetails().get("highRiskLabourTBRisk")))
                hrl_badge.setVisibility(VISIBLE);

        //start profile image
        profilePic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
        Support.setImagetoHolderFromUri((Activity) mContext, pc.getDetails().get("base_entity_id"), profilePic, R.mipmap.woman_placeholder);

//        wife_name.setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "");
//        husband_name.setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "");
//        village_name.setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
//        wife_age.setText(pc.getDetails().get("umur") != null ? pc.getDetails().get("umur") : "");
//        no_ibu.setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");

        String ageWeek = getAgeWeek(pc.getDetails().get("htp"));

        ((TextView) convertView.findViewById(R.id.unique_id)).setText(pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) != null ? pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) : "");
//        ((TextView) convertView.findViewById(R.id.txt_usia_klinis)).setText(pc.getDetails().get("usiaKlinis") != null ? mContext.getString(R.string.usia) +" "+ pc.getDetails().get("usiaKlinis") + mContext.getString(R.string.str_weeks) : "-");
        ((TextView) convertView.findViewById(R.id.txt_usia_klinis)).setText(ageWeek != "" ? mContext.getString(R.string.usia) +": "+ ageWeek + mContext.getString(R.string.str_weeks) : "-");
        ((TextView) convertView.findViewById(R.id.txt_htpt)).setText(pc.getDetails().get("htp") != null ? pc.getDetails().get("htp") : "-");

        TextView edd_due = (TextView) convertView.findViewById(R.id.txt_edd_due);
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


        ((TextView) convertView.findViewById(R.id.txt_ki_lila_bb)).setText(pc.getDetails().get("hasilPemeriksaanLILA") != null ? pc.getDetails().get("hasilPemeriksaanLILA") : "-");

        ((TextView) convertView.findViewById(R.id.txt_ki_beratbadan_tb)).setText(pc.getDetails().get("bbKg") != null ? pc.getDetails().get("bbKg") : "-");

        String ancDate = pc.getDetails().get("ancDate") != null ? pc.getDetails().get("ancDate") : "-";
        String ancKe = pc.getDetails().get("ancKe") != null ? pc.getDetails().get("ancKe") : "-";
        String kunjunganKe = pc.getDetails().get("kunjunganKe") != null ? pc.getDetails().get("kunjunganKe") : "-";

        ((TextView) convertView.findViewById(R.id.txt_tanggal_kunjungan_anc)).setText(String.format("%s", mContext.getString(R.string.last_visit_date)));
        ((TextView) convertView.findViewById(R.id.txt_date_tanggal_kunjungan_anc)).setText(String.format("%s", ancDate));
        ((TextView) convertView.findViewById(R.id.txt_anc_number)).setText(String.format("%s%s", mContext.getString(R.string.anc_ke), ancKe));
        ((TextView) convertView.findViewById(R.id.txt_kunjugan_ke)).setText(String.format("%s%s", mContext.getString(R.string.visit_number), kunjunganKe));

        // STATUS
        RelativeLayout status_layout = (RelativeLayout) convertView.findViewById(R.id.anc_status_layout);
        TextView status_type = (TextView) convertView.findViewById(R.id.txt_status_type);
        TextView status_date = (TextView) convertView.findViewById(R.id.txt_status_date_anc);
        TextView alert_status = (TextView) convertView.findViewById(R.id.txt_alert_status);

        status_layout.setBackgroundColor(mContext.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
        status_type.setText("");

        String textStatusDate = getNextMonth(ancDate);
        status_date.setText(textStatusDate);

        String[] alertObject = getAlertStatus(textStatusDate);

        alert_status.setText("");

        if (alertObject != null && alertObject.length == 2){
            alert_status.setText(alertObject[0]);
            if (alertObject[1] == "blue"){
                alert_status.setTextColor(mContext.getResources().getColor(org.smartregister.R.color.alert_in_progress_blue));
            }
            if (alertObject[1] == "yellow"){
                alert_status.setTextColor(mContext.getResources().getColor(org.smartregister.R.color.pnc_circle_yellow));
            }
            if (alertObject[1] == "red"){
                alert_status.setTextColor(mContext.getResources().getColor(org.smartregister.R.color.alert_urgent_red));
            }
        }

        if ("-".equals(kunjunganKe) || "".equals(kunjunganKe)) {
            status_type.setText(R.string.Visit1);
        }

        if ("1".equals(kunjunganKe)) {
            status_type.setText(R.string.Visit2);
        }

        if ("2".equals(kunjunganKe)) {
            status_type.setText(R.string.Visit3);
        }

        if ("3".equals(kunjunganKe)) {
            status_type.setText(R.string.Visit4);
        }

        if ("4".equals(kunjunganKe)) {
            status_type.setText(R.string.Visit5);
        }

        if ("5".equals(kunjunganKe)) {
            status_type.setText(R.string.Visit6);
        }

        if ("6".equals(kunjunganKe)) {
            status_type.setText(R.string.Visit7);
        }

        if ("7".equals(kunjunganKe)) {
            status_type.setText(R.string.Visit8);
        }

        if ("8".equals(kunjunganKe)) {
            status_type.setText(R.string.Visit9);
        }

        convertView.setLayoutParams(clientViewLayoutParams);

    }

    //hasep: used for get week ages based on htp
    private String getAgeWeek(String htp){
        if(htp != null ) {
//            Log.e(TAG, "getAgeWeek: " + htp);

            DateTimeFormatter datePattern = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime HTP = datePattern.parseDateTime(htp);
            DateTime now = DateTime.now();
            int days = Days.daysBetween(now.toLocalDate(), HTP.toLocalDate()).getDays();
            int weeks = Math.round(days / 7);
            return weeks+"";
        }

        return "";
    }

    private String getNextMonth(String date){
        if(date != null && date != "-" ) {
//            Log.e(TAG, "getNextMonth: " + date);
            DateTimeFormatter datePattern = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime DATE = datePattern.parseDateTime(date);
            DateTime nextMonth = DATE.plusMonths(1);

            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
            String str = fmt.print(nextMonth);
            return str;
        }
        return "";
    }

    private String[] getAlertStatus(String date){
        String[] ret = new String[2];

        String patternString1 = "\\d+-\\d+-\\d+";
        Pattern pattern = Pattern.compile(patternString1);
        Matcher matcher = pattern.matcher(date);

        if(date != null && matcher.find()) {
//            Log.e(TAG, "getAlertStatus: " + date);
            DateTimeFormatter datePattern = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime DATE = datePattern.parseDateTime(date);
            DateTime now = DateTime.now();
            DateTime nextWeek = now.plusWeeks(1);

            if(DATE.isAfter(nextWeek)){
                Duration duration = new Duration(now, DATE);
                long days = duration.getStandardDays();
                ret[0] = "Visit in "+ days +" days";
                ret[1] = "blue";
            }
            if(DATE.isAfter(now) && DATE.isBefore(nextWeek)){
                ret[0] = "Visit in 1 weeks";
                ret[1] = "yellow";
            }
            if(DATE.isBefore(now)){
                ret[0] = "Visit passed";
                ret[1] = "red";
            }
        }
        return ret;
    }

    private void oldStatusText(CommonPersonObjectClient pc, String ancKe, TextView status_type, RelativeLayout status_layout, TextView status_date, TextView alert_status){

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
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {
        LoginActivity.setLanguage();
//        Log.e(TAG, "getView: "+ mContext.getResources().getConfiguration().locale );
        getView(smartRegisterClient, view);
    }

//    public void risk(String risk1, String risk2, String risk3, String risk4, String risk5, String risk6,
//                     String risk7, String risk8, String risk9, String risk10, ImageView riskview) {
//
//        if (risk1 != null && "yes".equals(risk1)
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
//            riskview.setVisibility(VISIBLE);
//        }
//    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(R.layout.smart_register_ki_anc_client, null);
    }

}