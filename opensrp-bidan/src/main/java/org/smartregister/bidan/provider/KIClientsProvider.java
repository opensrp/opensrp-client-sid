package org.smartregister.bidan.provider;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
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

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.smartregister.bidan.R.layout.smart_register_ki_client;

/**
 * Created by Dimas Ciputra on 2/16/15.
 */

public class KIClientsProvider extends BaseClientsProvider {

    private static final String TAG = KIClientsProvider.class.getName();

    private final Context context;
    private final View.OnClickListener onClickListener;
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
    @Bind(R.id.unique_id)
    TextView unique_id;
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
    @Bind(R.id.txt_edd)
    TextView edd;
    @Bind(R.id.txt_edd_due)
    TextView edd_due;
    @Bind(R.id.txt_children_age_left)
    TextView children_age_left;
    @Bind(R.id.txt_children_age_right)
    TextView children_age_right;

    @Bind(R.id.mother_status)
    TextView anc_status_layout;
    @Bind(R.id.last_visit_status)
    TextView date_status;
    @Bind(R.id.visit_status)
    TextView visit_status;
    @Bind(R.id.iv_mother_photo)
    ImageView profilepic;
    @Bind(R.id.ib_btn_edit)
    ImageButton follow_up;
    private Drawable iconPencilDrawable;

    public KIClientsProvider(Context context, View.OnClickListener onClickListener, AlertService alertService) {
        super(context);
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));

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
        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);
        Log.e(TAG, "getView:CommonPersonObjectClient "+ pc.getDetails());

//        System.out.println("client : " + pc.getColumnmaps().toString());
//        System.out.println("event : " + pc.getDetails().toString());
        AllCommonsRepository iburep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
        AllCommonsRepository ancrep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anc");
        AllCommonsRepository pncrep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_pnc");
        AllCommonsRepository anakrep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        ArrayList<String> list = new ArrayList<>();
        list.add((pc.entityId()));
        List<CommonPersonObject> allchild = anakrep.findByRelational_IDs(list);
        final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());

        // ========================================================================================
        // Set Value
        // ========================================================================================
        profilelayout.setOnClickListener(onClickListener);
        profilelayout.setTag(smartRegisterClient);

        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        follow_up.setImageDrawable(iconPencilDrawable);
        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);

        //start profile image
        profilepic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
        Support.setImagetoHolderFromUri((Activity) context, pc.getDetails().get("base_entity_id"), profilepic, R.mipmap.woman_placeholder);
//        profilepic.setTag(smartRegisterClient);
        //end profile image

        wife_name.setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "");
        husband_name.setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "");
        village_name.setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
        wife_age.setText(pc.getColumnmaps().get("umur") != null ? pc.getColumnmaps().get("umur") : "");
        no_ibu.setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");
        unique_id.setText(pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) != null ? pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) : "");
        gravida.setText(pc.getDetails().get("gravida") != null ? pc.getDetails().get("gravida") : "-");
        parity.setText(pc.getDetails().get("partus") != null ? pc.getDetails().get("partus") : "-");
        number_of_abortus.setText(pc.getDetails().get("abortus") != null ? pc.getDetails().get("abortus") : "-");
        number_of_alive.setText(pc.getDetails().get("hidup") != null ? pc.getDetails().get("hidup") : "-");
        edd.setText(pc.getDetails().get("htp") != null ? pc.getDetails().get("htp") : "");

        edd_due.setText("");
        anc_status_layout.setText("");
        date_status.setText("");
        visit_status.setText("");
        children_age_left.setText("");
        children_age_right.setText("");

        if (ibuparent != null) {
//            Log.e(TAG, "getView:ibuparent.getColumnmaps "+ ibuparent.getColumnmaps());
//            Log.e(TAG, "getView:ibuparent.getCaseId "+ ibuparent.getCaseId());
//            Log.e(TAG, "getView:ibuparent.getClosed "+ ibuparent.getClosed());
//            Log.e(TAG, "getView:ibuparent.getDetails "+ ibuparent.getDetails());
//            Log.e(TAG, "getView:ibuparent.getRelationalId "+ ibuparent.getRelationalId());

            short anc_isclosed = ibuparent.getClosed();

            //check anc  status
            if (anc_isclosed == 0) {
                Log.e(TAG, "getView: ACTIVE in ANC" );
                detailsRepository.updateDetails(ibuparent);
                if (pc.getDetails().get("htp") == null) {

                    Support.checkMonth(context, pc.getDetails().get("htp"), edd_due);

                }
                checkLastVisit(pc.getDetails().get("ancDate"), context.getString(R.string.anc_ke) + ": " + pc.getDetails().get("ancKe"), context.getString(R.string.service_anc),
                        anc_status_layout, date_status, visit_status);
            }
            //if anc is 1(closed) set status to pnc
            else if (anc_isclosed == 1) {
                final CommonPersonObject pncparent = pncrep.findByCaseID(pc.entityId());
                if (pncparent != null) {
                    short pnc_isclosed = pncparent.getClosed();
                    if (pnc_isclosed == 0) {
                        detailsRepository.updateDetails(pncparent);
                  /*  checkMonth("delivered",edd_due);*/
                        edd_due.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
                        String deliver = context.getString(R.string.delivered);
                        edd_due.setText(deliver);
                        checkLastVisit(pc.getDetails().get("PNCDate"), context.getString(R.string.pnc_ke) + " " + pc.getDetails().get("hariKeKF"), context.getString(R.string.service_pnc),
                                anc_status_layout, date_status, visit_status);
                    }
                }

            }
        }
        //last check if mother in PF (KB) service
        if (!StringUtils.isNumeric(pc.getDetails().get("jenisKontrasepsi"))) {
            checkLastVisit(pc.getDetails().get("tanggalkunjungan"),
                    context.getString(R.string.fp_methods) + ": " + pc.getDetails().get("jenisKontrasepsi"),
                    context.getString(R.string.service_fp), anc_status_layout, date_status, visit_status);
        }

        //anak
        for (int i = 0; i < allchild.size(); i++) {
            CommonPersonObject commonPersonObject = allchild.get(i);
            detailsRepository.updateDetails(commonPersonObject);
            children_age_left.setText(commonPersonObject.getColumnmaps().get("namaBayi") != null ? "Name : " + commonPersonObject.getColumnmaps().get("namaBayi") : "");
            children_age_right.setText(commonPersonObject.getColumnmaps().get("tanggalLahirAnak") != null ? "DOB : " + commonPersonObject.getColumnmaps().get("tanggalLahirAnak").substring(0, commonPersonObject.getColumnmaps().get("tanggalLahirAnak").indexOf("T")) : "");
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


        convertView.setLayoutParams(clientViewLayoutParams);
        //  return convertView;

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

    private void checkLastVisit(String date, String visitNumber, String status, TextView tvVisitStatus, TextView tvVisitDate, TextView tvVisitNumber) {
        String visit_date = date != null ? context.getString(R.string.date_visit_title) + " " + date : "";

        tvVisitNumber.setText(visitNumber);
        tvVisitDate.setText(visit_date);
        tvVisitStatus.setText(status);
    }

    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(smart_register_ki_client, null);
    }

}