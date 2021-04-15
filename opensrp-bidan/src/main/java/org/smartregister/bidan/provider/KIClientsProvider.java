package org.smartregister.bidan.provider;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.bidan.R;
import org.smartregister.bidan.utils.AllConstantsINA;
import org.smartregister.bidan.utils.Support;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.joda.time.LocalDateTime.parse;
import static org.smartregister.bidan.R.layout.smart_register_ki_client;

public class KIClientsProvider extends BaseClientsProvider {

    private static final String TAG = KIClientsProvider.class.getName();

    private final Context mContext;
    private final View.OnClickListener onClickListener;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    protected CommonPersonObjectController controller;

    public KIClientsProvider(Context context, View.OnClickListener onClickListener, AlertService alertService) {
        super(context);
        this.onClickListener = onClickListener;
        this.mContext = context;
        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));
        Log.i(TAG, "KIClientsProvider:alertService " + alertService);
    }

    public void getView(SmartRegisterClient smartRegisterClient, View convertView) {

        // ========================================================================================
        // Load Value from Local DB
        // ========================================================================================
        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);
        AllCommonsRepository ibuRep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
        AllCommonsRepository pncRep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_pnc");
        AllCommonsRepository anakRep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        ArrayList<String> list = new ArrayList<>();
        list.add((pc.entityId()));
        List<CommonPersonObject> allchild = anakRep.findByRelational_IDs(list);
        final CommonPersonObject ibuparent = ibuRep.findByCaseID(pc.entityId());

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
        ((TextView) convertView.findViewById(R.id.tv_no_ibu)).setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");

        profilelayout.setOnClickListener(onClickListener);
        profilelayout.setTag(smartRegisterClient);

        ImageButton follow_up = (ImageButton) convertView.findViewById(R.id.ib_cm_edit);
        if (follow_up != null) {
            follow_up.setOnClickListener(onClickListener);
            follow_up.setTag(smartRegisterClient);
            follow_up.setImageResource(R.drawable.ic_pencil);
            follow_up.setOnClickListener(onClickListener);
            follow_up.setTag(smartRegisterClient);
        }
        //start profile image
        profilePic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
        Support.setImagetoHolderFromUri((Activity) mContext, pc.getDetails().get("base_entity_id"), profilePic, R.mipmap.woman_placeholder);

        ((TextView) convertView.findViewById(R.id.unique_id)).setText(pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) != null ? pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) : "");
        ((TextView) convertView.findViewById(R.id.tv_gravida)).setText(pc.getDetails().get("gravida") != null ? pc.getDetails().get("gravida") : "-");
        ((TextView) convertView.findViewById(R.id.tv_parity)).setText(pc.getDetails().get("partus") != null ? pc.getDetails().get("partus") : "-");
        ((TextView) convertView.findViewById(R.id.tv_number_of_abortus)).setText(pc.getDetails().get("abortus") != null ? pc.getDetails().get("abortus") : "-");
        ((TextView) convertView.findViewById(R.id.tv_number_of_alive)).setText(pc.getDetails().get("hidup") != null ? pc.getDetails().get("hidup") : "-");
        ((TextView) convertView.findViewById(R.id.txt_edd)).setText(pc.getDetails().get("htp") != null ? pc.getDetails().get("htp") : "");

        TextView edd_due = (TextView) convertView.findViewById(R.id.txt_edd_due);
        TextView anc_status_layout = (TextView) convertView.findViewById(R.id.mother_status);
        TextView date_status = (TextView) convertView.findViewById(R.id.last_visit_status);
        TextView visit_status = (TextView) convertView.findViewById(R.id.visit_status);
        TextView children_age_left = (TextView) convertView.findViewById(R.id.txt_children_age_left);
        TextView children_age_right = (TextView) convertView.findViewById(R.id.txt_children_age_right);
        TextView children_age_bottom = (TextView) convertView.findViewById(R.id.txt_children_age_bottom);

//        edd_due.setText("");
        String eddDueStr = "";
        anc_status_layout.setText("");
        date_status.setText("");
        visit_status.setText("");
        children_age_left.setText("");
        children_age_right.setText("");

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

//            edd_due.setText(_dueEdd);
            eddDueStr = _dueEdd;

        } else {
//            edd_due.setText("");
            eddDueStr = "";
        }

        if (ibuparent != null) {

            short anc_isclosed = ibuparent.getClosed();

            //check anc  status
            if (anc_isclosed == 0) {
                detailsRepository.updateDetails(ibuparent);
                if (pc.getDetails().get("htp") == null) {

                    Support.checkMonth(mContext, pc.getDetails().get("htp"), edd_due);

                }
                String ancKe = "-";

                if (pc.getDetails().containsKey("ancKe")) {
                    if (pc.getDetails().get("ancKe") != null) {
                        ancKe = pc.getDetails().get("ancKe");
                    }
                }
                checkLastVisit(pc.getDetails().get("ancDate"),
                        mContext.getString(R.string.anc_ke) + ancKe,
                        mContext.getString(R.string.service_anc),
                        anc_status_layout, date_status, visit_status);
            }
            //if anc is 1(closed) set status to pnc
            if (anc_isclosed == 1) {
                final CommonPersonObject pncparent = pncRep.findByCaseID(pc.entityId());
                if (pncparent != null) {
                    short pnc_isclosed = pncparent.getClosed();
                    if (pnc_isclosed == 0) {
                        detailsRepository.updateDetails(pncparent);
                        /*  checkMonth("delivered",edd_due);*/
                        edd_due.setTextColor(mContext.getResources().getColor(R.color.alert_complete_green));
                        String deliver = mContext.getString(R.string.delivered);
//                        edd_due.setText(deliver);
                        eddDueStr = deliver;
                        String hariKeKF = pc.getDetails().get("hariKeKF");
                        if (hariKeKF == null)
                            hariKeKF = "-";
                        checkLastVisit(pc.getDetails().get("PNCDate"), mContext.getString(R.string.pnc_ke) + " " + hariKeKF, mContext.getString(R.string.service_pnc),
                                anc_status_layout, date_status, visit_status);
                    } else {
                        ((TextView) convertView.findViewById(R.id.txt_edd)).setText("");
//                        edd_due.setText("");
                        eddDueStr = "";
                    }
                } else {
                    ((TextView) convertView.findViewById(R.id.txt_edd)).setText("");
//                    edd_due.setText("");
                    eddDueStr = "";
                }

            }
        } else
            //last check if mother in PF (KB) service
            if (!StringUtils.isNumeric(pc.getDetails().get("jenisKontrasepsi"))) {
                checkLastVisit(pc.getDetails().get("tanggalkunjungan"),
                        mContext.getString(R.string.fp_methods) + ": " + pc.getDetails().get("jenisKontrasepsi"),
                        mContext.getString(R.string.service_fp), anc_status_layout, date_status, visit_status);
            }
        edd_due.setText(eddDueStr);

        //anak
        for (int i = 0; i < allchild.size(); i++) {
            CommonPersonObject commonPersonObject = allchild.get(i);
            detailsRepository.updateDetails(commonPersonObject);
            if (commonPersonObject.getDetails().get("closeReason") != null) {
                Log.d(TAG, "getView: " + commonPersonObject.getDetails());
                if ("death_of_child".equals(commonPersonObject.getDetails().get("closeReason"))) {
                    children_age_left.setText(mContext.getString(R.string.txt_death));
                    String cause = getDeathCause(commonPersonObject);
                    children_age_right.setText(mContext.getString(R.string.death_reason) + " :");
                    children_age_bottom.setText(cause);
                    continue;
                }
                if ("wrong_entry".equals(commonPersonObject.getDetails().get("closeReason"))) {
                    continue;
                }
            }
            children_age_left.setText(commonPersonObject.getColumnmaps().get("namaBayi") != null ? "Name : " + commonPersonObject.getColumnmaps().get("namaBayi") : "");
            children_age_right.setText(commonPersonObject.getColumnmaps().get("tanggalLahirAnak") != null ? "DOB : " + commonPersonObject.getColumnmaps().get("tanggalLahirAnak").substring(0, commonPersonObject.getColumnmaps().get("tanggalLahirAnak").indexOf("T")) : "");
        }

        ImageView hr_badge = (ImageView) convertView.findViewById(R.id.iv_hr_badge);
        ImageView hrp_badge = (ImageView) convertView.findViewById(R.id.iv_hrp_badge);
        ImageView hrl_badge = (ImageView) convertView.findViewById(R.id.iv_hrl_badge);

        hr_badge.setVisibility(View.INVISIBLE);
        hrp_badge.setVisibility(View.INVISIBLE);
        hrl_badge.setVisibility(View.INVISIBLE);

        //Risk flag
        if ("yes".matches(pc.getDetails().get("highRiskSTIBBVs") + "||" + pc.getDetails().get("highRiskEctopicPregnancy") + "||" + pc.getDetails().get("highRiskCardiovascularDiseaseRecord") + "||" +
                pc.getDetails().get("highRiskDidneyDisorder") + "||" + pc.getDetails().get("highRiskHeartDisorder") + "||" + pc.getDetails().get("highRiskAsthma") + "||" +
                pc.getDetails().get("highRiskTuberculosis") + "||" + pc.getDetails().get("highRiskMalaria") + "||" + pc.getDetails().get("highRiskPregnancyYoungMaternalAge") + "||" +
                pc.getDetails().get("highRiskPregnancyOldMaternalAge")))
            hr_badge.setVisibility(View.VISIBLE);

        if ("yes".matches(pc.getDetails().get("highRiskPregnancyPIH") + "||" + pc.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") + "||" +
                pc.getDetails().get("HighRiskPregnancyTooManyChildren") + "||" +
                pc.getDetails().get("highRiskPregnancyDiabetes") + "||" + pc.getDetails().get("highRiskPregnancyAnemia")))
            hrp_badge.setVisibility(View.VISIBLE);

        if ("yes".matches(pc.getDetails().get("highRiskLabourFetusMalpresentation") + "||" + pc.getDetails().get("highRiskLabourFetusSize") + "||" +
                pc.getDetails().get("highRisklabourFetusNumber") + "||" + pc.getDetails().get("HighRiskLabourSectionCesareaRecord") + "||" +
                pc.getDetails().get("highRiskLabourTBRisk")))
            hrl_badge.setVisibility(View.VISIBLE);


        convertView.setLayoutParams(clientViewLayoutParams);
        //  return convertView;

    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {
//        LoginActivity.setLanguage();
        getView(smartRegisterClient, view);
    }

    private void checkLastVisit(String date, String visitNumber, String status, TextView tvVisitStatus, TextView tvVisitDate, TextView tvVisitNumber) {
        String visit_date = date != null ? mContext.getString(R.string.date_visit_title) + " " + date : "";

        tvVisitNumber.setText(visitNumber);
        tvVisitDate.setText(visit_date);
        tvVisitStatus.setText(status);
    }

    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(smart_register_ki_client, null);
    }

    private String getDeathCause(CommonPersonObject commonPersonObject) {
        String cause = "";
        if ("sepsis".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.sepsis);
        else if ("asphyxia".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.asphyxia);
        else if ("lbw".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.lbw);
        else if ("pneumonia".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.pneumonia);
        else if ("diarrhea".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.diarrhea);
        else if ("measles".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.measles);
        else if ("malnutrition".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.malnutrition);
        else if ("Infeksi_pernafasan_akut".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.Infeksi_pernafasan_akut);
        else if ("infeksi_pernapasan_atas".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.infeksi_pernapasan_atas);
        else if ("malaria".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.malaria);
        else if ("tetanus_neonatorum".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.tetanus_neonatorum);
        else if ("ikterus".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.ikterus);
        else if ("demam_berdarah".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.demam_berdarah);
        else if ("congenital_abnormality".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.congenital_abnormality);
        else if ("kelainan_saluran_cerna".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.kelainan_saluran_cerna);
        else if ("Kelainan_syaraf".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.Kelainan_syaraf);
        else if ("others".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.others);
        else if ("cause_not_identified".equals(commonPersonObject.getDetails().get("childDeathCause")))
            cause = mContext.getString(R.string.cause_not_identified);

        return cause;
    }

}