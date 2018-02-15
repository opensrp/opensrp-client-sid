package org.smartregister.bidan.provider;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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
 * Created by Dimas Ciputra on 2/16/15.
 */
public class ANCClientsProvider extends BaseClientsProvider {

    private static final String TAG = ANCClientsProvider.class.getName();
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    protected CommonPersonObjectController controller;
    AlertService alertService;
    private final LayoutInflater inflater;

    //    @Bind(R.id.profile_info_layout)
//    LinearLayout profilelayout;
//    @Bind(R.id.tv_wife_name)
//    TextView wife_name;
//    @Bind(R.id.tv_husband_name)
//    TextView husband_name;
//    @Bind(R.id.tv_village_name)
//    TextView village_name;
//    @Bind(R.id.tv_wife_age)
//    TextView wife_age;
//    @Bind(R.id.tv_no_ibu)
//    TextView no_ibu;
//    @Bind(R.id.unique_id)
//    TextView unique_id;
//    @Bind(R.id.tv_gravida)
//    TextView gravida;
//    @Bind(R.id.tv_parity)
//    TextView parity;
//    @Bind(R.id.tv_number_of_abortus)
//    TextView number_of_abortus;
//    @Bind(R.id.tv_number_of_alive)
//    TextView number_of_alive;
//    @Bind(R.id.iv_hr_badge)
//    ImageView hr_badge;
//    @Bind(R.id.iv_hrl_badge)
//    ImageView img_hrl_badge;
//    @Bind(R.id.iv_bpl_badge)
//    ImageView bpl_badge;
//    @Bind(R.id.iv_hrp_badge)
//    ImageView hrp_badge;
//    @Bind(R.id.iv_hrpp_badge)
//    ImageView hrpp_badge;
//    @Bind(R.id.txt_edd)
//    TextView edd;
//    @Bind(R.id.txt_edd_due)
//    TextView edd_due;
//    @Bind(R.id.txt_children_age_left)
//    TextView children_age_left;
//    @Bind(R.id.txt_children_age_right)
//    TextView children_age_right;
//    @Bind(R.id.mother_status)
//    TextView anc_status_layout;
//    @Bind(R.id.last_visit_status)
//    TextView date_status;
//    @Bind(R.id.visit_status)
//    TextView visit_status;
//    @Bind(R.id.iv_mother_photo)
//    ImageView profilepic;
//    @Bind(R.id.ib_btn_edit)
//    ImageButton follow_up;
    private Drawable iconPencilDrawable;

    // ==>>>>>>>>>>>>>>>>>>>>>>>>
    @Bind(R.id.profile_info_layout)
    LinearLayout profilelayout;
    @Bind(R.id.iv_mother_photo)
    ImageView profilepic;
    @Bind(R.id.tv_wife_name)
    TextView wife_name;
    @Bind(R.id.tv_husband_name)
    TextView husband_name;
    @Bind(R.id.tv_village_name)
    TextView village_name;
    @Bind(R.id.tv_wife_age)
    TextView wife_age;
    @Bind(R.id.no_ibu)
    TextView no_ibu;
    @Bind(R.id.unique_id)
    TextView unique_id;
    
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
    @Bind(R.id.txt_usia_klinis)
    TextView usia_klinis;
    @Bind(R.id.txt_htpt)
    TextView htpt;
    @Bind(R.id.txt_edd_due)
    TextView edd_due;
    @Bind(R.id.txt_ki_lila_bb)
    TextView ki_lila_bb;
    @Bind(R.id.txt_ki_beratbadan_tb)
    TextView beratbadan_tb;
    @Bind(R.id.txt_tanggal_kunjungan_anc)
    TextView tanggal_kunjungan_anc;
    @Bind(R.id.txt_anc_number)
    TextView anc_number;
    @Bind(R.id.txt_kunjugan_ke)
    TextView kunjugan_ke;
    @Bind(R.id.anc_status_layout)
    RelativeLayout status_layout;
    @Bind(R.id.txt_status_type)
    TextView status_type;
    @Bind(R.id.txt_status_date_anc)
    TextView status_date;
    @Bind(R.id.txt_alert_status)
    TextView alert_status;

    @Bind(R.id.btn_anc_edit)
    ImageButton follow_up;

    public ANCClientsProvider(Context context, View.OnClickListener onClickListener, AlertService alertService) {
        super(context);
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));

    }

    public void getView(SmartRegisterClient smartRegisterClient, View convertView) {

        try {
            ButterKnife.bind(this, convertView);

        } catch (Exception e) {
            e.getCause().printStackTrace();
            Log.e(TAG, "getView: "+ e.getMessage() );
        }
//        // Load data from DB
//        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
//        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
//        detailsRepository.updateDetails(pc);
//
//        System.out.println("client : " + pc.getColumnmaps().toString());
//        System.out.println("event : " + pc.getDetails().toString());

//        AllCommonsRepository iburep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
//        AllCommonsRepository pncrep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_pnc");
//        AllCommonsRepository anakrep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
//
//        final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());
//        final CommonPersonObject pncparent = pncrep.findByCaseID(pc.entityId());
//
//        //anak
//        ArrayList<String> list = new ArrayList<>();
//        list.add((pc.entityId()));
//        List<CommonPersonObject> allchild = anakrep.findByRelational_IDs(list);
//
//        profilelayout.setOnClickListener(onClickListener);
//        profilelayout.setTag(smartRegisterClient);
//
//        follow_up.setTag(smartRegisterClient);
//        follow_up.setImageDrawable(iconPencilDrawable);
//        follow_up.setOnClickListener(onClickListener);
//
//        if (iconPencilDrawable == null) {
//            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
//        }
//
//        //start profile image
//        profilepic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
//        Support.setImagetoHolderFromUri((Activity) context, pc.getDetails().get("base_entity_id"), profilepic, R.mipmap.woman_placeholder);
//        //end profile image
//
//        wife_name.setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "");
//        husband_name.setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "");
//        village_name.setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
//        wife_age.setText(pc.getColumnmaps().get("umur") != null ? pc.getColumnmaps().get("umur") : "");
//        no_ibu.setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");
//        unique_id.setText(pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) != null ? pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) : "");
//        gravida.setText(pc.getDetails().get("gravida") != null ? pc.getDetails().get("gravida") : "-");
//        parity.setText(pc.getDetails().get("partus") != null ? pc.getDetails().get("partus") : "-");
//        number_of_abortus.setText(pc.getDetails().get("abortus") != null ? pc.getDetails().get("abortus") : "-");
//        number_of_alive.setText(pc.getDetails().get("hidup") != null ? pc.getDetails().get("hidup") : "-");
//        htpt.setText(pc.getDetails().get("htp") != null ? pc.getDetails().get("htp") : "");
//        edd_due.setText("");
//        anc_status_layout.setText("");
//        date_status.setText("");
//        visit_status.setText("");
//        children_age_left.setText("");
//        children_age_right.setText("");
//
//        if (ibuparent != null) {
//            short anc_isclosed = ibuparent.getClosed();
//            //check anc  status
//            if (anc_isclosed == 0) {
//                detailsRepository.updateDetails(ibuparent);
//                if (pc.getDetails().get("htp") == null) {
//                    Support.checkMonth(context, pc.getDetails().get("htp"), edd_due);
//
//                }
//                checkLastVisit(pc.getDetails().get("ancDate"), context.getString(R.string.anc_ke) + ": " + pc.getDetails().get("ancKe"), context.getString(R.string.service_anc),
//                        anc_status_layout, date_status, visit_status);
//            }
//            //if anc is 1(closed) set status to pnc
//            else if (anc_isclosed == 1) {
//
//                if (pncparent != null) {
//                    short pnc_isclosed = pncparent.getClosed();
//                    if (pnc_isclosed == 0) {
//                        detailsRepository.updateDetails(pncparent);
//                  /*  checkMonth("delivered",viewHolder.edd_due);*/
//                        edd_due.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
//                        String deliver = context.getString(R.string.delivered);
//                        edd_due.setText(deliver);
//                        checkLastVisit(pc.getDetails().get("PNCDate"), context.getString(R.string.pnc_ke) + " " + pc.getDetails().get("hariKeKF"), context.getString(R.string.service_pnc),
//                                anc_status_layout, date_status, visit_status);
//                    }
//                }
//
//            }
//        }
//        //last check if mother in PF (KB) service
//        else if (!StringUtils.isNumeric(pc.getDetails().get("jenisKontrasepsi"))) {
//            checkLastVisit(pc.getDetails().get("tanggalkunjungan"), context.getString(R.string.fp_methods) + ": " + pc.getDetails().get("jenisKontrasepsi"), context.getString(R.string.service_fp),
//                    anc_status_layout, date_status, visit_status);
//        }
//
//        for (int i = 0; i < allchild.size(); i++) {
//            CommonPersonObject commonPersonObject = allchild.get(i);
//            detailsRepository.updateDetails(commonPersonObject);
//            children_age_left.setText(commonPersonObject.getColumnmaps().get("namaBayi") != null ? "Name : " + commonPersonObject.getColumnmaps().get("namaBayi") : "");
//            children_age_right.setText(commonPersonObject.getColumnmaps().get("tanggalLahirAnak") != null ? "DOB : " + commonPersonObject.getColumnmaps().get("tanggalLahirAnak").substring(0, commonPersonObject.getColumnmaps().get("tanggalLahirAnak").indexOf("T")) : "");
//        }
//
//        hr_badge.setVisibility(View.INVISIBLE);
//        hrp_badge.setVisibility(View.INVISIBLE);
//        img_hrl_badge.setVisibility(View.INVISIBLE);
//
//        //Risk flag
//        risk(pc.getDetails().get("highRiskSTIBBVs"), pc.getDetails().get("highRiskEctopicPregnancy"), pc.getDetails().get("highRiskCardiovascularDiseaseRecord"),
//                pc.getDetails().get("highRiskDidneyDisorder"), pc.getDetails().get("highRiskHeartDisorder"), pc.getDetails().get("highRiskAsthma"),
//                pc.getDetails().get("highRiskTuberculosis"), pc.getDetails().get("highRiskMalaria"), pc.getDetails().get("highRiskPregnancyYoungMaternalAge"),
//                pc.getDetails().get("highRiskPregnancyOldMaternalAge"), hr_badge);
//
//        risk(pc.getDetails().get("highRiskPregnancyPIH"), pc.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"),
//                pc.getDetails().get("HighRiskPregnancyTooManyChildren"),
//                pc.getDetails().get("highRiskPregnancyDiabetes"), pc.getDetails().get("highRiskPregnancyAnemia"), null, null, null, null, null, hrp_badge);
//
//        risk(pc.getDetails().get("highRiskLabourFetusMalpresentation"), pc.getDetails().get("highRiskLabourFetusSize"),
//                pc.getDetails().get("highRisklabourFetusNumber"), pc.getDetails().get("HighRiskLabourSectionCesareaRecord"),
//                pc.getDetails().get("highRiskLabourTBRisk"), null, null, null, null, null, img_hrl_badge);


//        profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.woman_placeholder));

        profilelayout.setOnClickListener(onClickListener);
        profilelayout.setTag(smartRegisterClient);

        follow_up.setImageResource(R.drawable.ic_pencil);
        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        AllCommonsRepository allancRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
//        AllCommonsRepository pncrep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_pnc");
//        AllCommonsRepository anakrep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
//        final CommonPersonObject ibuparent = allancRepository.findByCaseID(pc.entityId());
//        final CommonPersonObject pncparent = pncrep.findByCaseID(pc.entityId());

//        ArrayList<String> list = new ArrayList<>();
//        list.add((pc.entityId()));
//        List<CommonPersonObject> allchild = anakrep.findByRelational_IDs(list);

        //set image
//        AllCommonsRepository allancRepository =  org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
        CommonPersonObject ancobject = allancRepository.findByCaseID(pc.entityId());
        // Load data from DB
//        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(ancobject);

        Log.e(TAG, "getView: client : " + ancobject.getColumnmaps().toString());
        Log.e(TAG, "getView: event : " + ancobject.getDetails().toString());

//        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        Map<String, String> details = detailsRepository.getAllDetailsForClient(pc.entityId());
        details.putAll(ancobject.getColumnmaps());

        if (pc.getDetails() != null) {
            pc.getDetails().putAll(details);
        } else {
            pc.setDetails(details);
        }

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

//        final ImageView kiview = (ImageView)convertView.findViewById(R.id.iv_profile);
        //start profile image
        profilepic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk

//        if(pc.getColumnmaps().get("_id")!=null){//image already in local storage most likey ):
//            Log.e(TAG, "getView: "+pc.getColumnmaps().get("_id") );
        //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.getColumnmaps().get("_id"), OpenSRPImageLoader.getStaticImageListener(profilepic, R.drawable.woman_placeholder, R.drawable.woman_placeholder));
//        }

        Support.setImagetoHolderFromUri((Activity) context, pc.getDetails().get("base_entity_id"), profilepic, R.mipmap.woman_placeholder);

        //end profile image

        wife_name.setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "");
        husband_name.setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "");
        village_name.setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
        wife_age.setText(pc.getDetails().get("umur") != null ? pc.getDetails().get("umur") : "");
        no_ibu.setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");
        unique_id.setText(pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) != null ? pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) : "");

        usia_klinis.setText(pc.getDetails().get("usiaKlinis") != null ? context.getString(R.string.usia) + pc.getDetails().get("usiaKlinis") + context.getString(R.string.str_weeks) : "-");
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
                edd_due.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
                _dueEdd = "" + months + " " + context.getString(R.string.months_away);
            } else if (months == 0) {
                edd_due.setTextColor(context.getResources().getColor(R.color.light_blue));
                _dueEdd = context.getString(R.string.this_month);
            } else if (months < 0) {
                edd_due.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                _dueEdd = context.getString(R.string.edd_passed);
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

        tanggal_kunjungan_anc.setText(String.format("%s%s", context.getString(R.string.last_visit_date), ancDate));
        anc_number.setText(String.format("%s%s", context.getString(R.string.anc_ke), ancKe));
        kunjugan_ke.setText(String.format("%s%s", context.getString(R.string.visit_number), kunjunganKe));

        status_type.setText("");
        status_date.setText("");
        status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
        alert_status.setText("");

        if (ancKe.equals("-") || ancKe.equals("")) {
            status_type.setText("ANC");
        }
        if (ancKe.equals("1")) {
            status_type.setText("ANC 2");
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "ANC 2");
            //alertlist_for_client.get(i).
            if (alertlist_for_client.size() == 0) {
                //  due_visit_date.setText("Not Synced to Server");
                status_layout.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for (int i = 0; i < alertlist_for_client.size(); i++) {

                //  status_date.setText(alertlist_for_client.get(i).startDate());
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_urgent_red));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.client_list_header_dark_grey));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).isComplete()) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }

        if (ancKe.equals("2")) {
            status_type.setText("ANC 3");
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "ANC 3");
            //alertlist_for_client.get(i).
            if (alertlist_for_client.size() == 0) {
                //  due_visit_date.setText("Not Synced to Server");
                status_layout.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for (int i = 0; i < alertlist_for_client.size(); i++) {

                // status_date.setText(alertlist_for_client.get(i).startDate());
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_urgent_red));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.client_list_header_dark_grey));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).isComplete()) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }
        if (ancKe.equals("3")) {
            status_type.setText("ANC 4");
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "ANC 4");
            //alertlist_for_client.get(i).
            if (alertlist_for_client.size() == 0) {
                //  due_visit_date.setText("Not Synced to Server");
                status_layout.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for (int i = 0; i < alertlist_for_client.size(); i++) {

                //   status_date.setText(alertlist_for_client.get(i).startDate());
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_urgent_red));
                    alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.client_list_header_dark_grey));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if (alertlist_for_client.get(i).isComplete()) {
                    status_date.setText(alertlist_for_client.get(i).startDate());
                    status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
                    alert_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }

        convertView.setLayoutParams(clientViewLayoutParams);

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

    public void checkLastVisit(String date, String visitNumber, String Status, TextView visitStatus, TextView visitDate, TextView VisitNumber) {
        String visit_stat = "";
        String visit_date = date != null ? context.getString(R.string.date_visit_title) + " " + date : "";

        VisitNumber.setText(visitNumber);
        visitDate.setText(visit_date);
        visitStatus.setText(Status);
    }

//    public LayoutInflater inflater() {
//        return inflater;
//    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(R.layout.smart_register_ki_anc_client, null);
    }

}