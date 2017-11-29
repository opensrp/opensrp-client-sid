package org.smartregister.bidan.provider;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.bidan.R;
import org.smartregister.bidan.utils.Support;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.joda.time.LocalDateTime.parse;
import static org.smartregister.bidan.activity.AnakDetailActivity.setImagetoHolderFromUri;
import static org.smartregister.util.Utils.fillValue;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by sid-tech on 11/27/17.
 */

public class ChildClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private static final String TAG = ChildClientsProvider.class.getName();
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final AlertService alertService;

    private Drawable iconPencilDrawable;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final CommonRepository commonRepository;

    public ChildClientsProvider(Context context, View.OnClickListener onClickListener,
                                AlertService alertService, CommonRepository commonRepository) {

        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.commonRepository = commonRepository;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, final View convertView){
        ViewHolder viewHolder = new ViewHolder();

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        convertView.findViewById(R.id.btn_edit).setBackgroundDrawable(iconPencilDrawable);
        convertView.findViewById(R.id.btn_edit).setTag(client);
        convertView.findViewById(R.id.btn_edit).setOnClickListener(onClickListener);

        convertView.findViewById(R.id.profile_info_layout).setTag(client);
        convertView.findViewById(R.id.profile_info_layout).setOnClickListener(onClickListener);

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);

        String firstName = getValue(pc.getColumnmaps(), "namaBayi", true);
        fillValue((TextView) convertView.findViewById(R.id.txt_child_name), firstName);

        String childBOD = getValue(pc.getColumnmaps(), "tanggalLahirAnak", true);

        if (childBOD.length()>10)
            childBOD = childBOD.substring(0, Support.getColumnmaps(pc,"tanggalLahirAnak").indexOf("T"));
        String age = monthRangeToToday(childBOD) + "Month";

        // get parent
        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(pc.entityId());
        String relationalId = getValue(pc.getColumnmaps(), "relational_id", true).toLowerCase();
        AllCommonsRepository kirep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(relationalId);

        String motherName = null;
        String childAddress = null;

        if(kiparent != null) {
            detailsRepository.updateDetails(kiparent);

            motherName = kiparent.getDetails().get("namalengkap");

            childAddress = kiparent.getDetails().get("address1");

        }

        //----------Child Basic Information
        fillValue((TextView) convertView.findViewById(R.id.txt_child_name), firstName);
        fillValue((TextView) convertView.findViewById(R.id.txt_mother_name), motherName);
        fillValue((TextView) convertView.findViewById(R.id.txt_child_age), age);
        fillValue((TextView) convertView.findViewById(R.id.txt_village_name), childAddress);
        //----------Child Deliver Information

        //----------Child Neonatal Visits Information

        //----------Child Immunizations Information

        viewHolder.hb0_no = (ImageView) convertView.findViewById(R.id.icon_immuni_hb_no);
        viewHolder.hb0_yes = (ImageView) convertView.findViewById(R.id.icon_immuni_hb_yes);
        viewHolder.vitk_no = (ImageView) convertView.findViewById(R.id.icon_vit_k_no);
        viewHolder.vitk_yes = (ImageView) convertView.findViewById(R.id.icon_vit_k_yes);
        viewHolder.campak_no = (ImageView) convertView.findViewById(R.id.icon_campak_no);
        viewHolder.campak_yes = (ImageView) convertView.findViewById(R.id.icon_campak_yes);
        viewHolder.ivp_no = (ImageView) convertView.findViewById(R.id.icon_ivp_no);
        viewHolder.ivp_yes = (ImageView) convertView.findViewById(R.id.icon_ivp_yes);

        viewHolder.pol1_no = (ImageView) convertView.findViewById(R.id.icon_pol1_no);
        viewHolder.pol1_yes = (ImageView) convertView.findViewById(R.id.icon_pol1_yes);
        viewHolder.pol2_no = (ImageView) convertView.findViewById(R.id.icon_pol2_no);
        viewHolder.pol2_yes = (ImageView) convertView.findViewById(R.id.icon_pol2_yes);
        viewHolder.pol3_no = (ImageView) convertView.findViewById(R.id.icon_pol3_no);
        viewHolder.pol3_yes = (ImageView) convertView.findViewById(R.id.icon_pol3_yes);
        viewHolder.pol4_no = (ImageView) convertView.findViewById(R.id.icon_pol4_no);
        viewHolder.pol4_yes = (ImageView) convertView.findViewById(R.id.icon_pol4_yes);

        checkVisibility(pc.getDetails().get("hb0"), null, viewHolder.hb0_no, viewHolder.hb0_yes);
        checkVisibility(pc.getDetails().get("polio1"), pc.getDetails().get("bcg"), viewHolder.pol1_no, viewHolder.pol1_yes);
        checkVisibility(pc.getDetails().get("dptHb1"), pc.getDetails().get("polio2"), viewHolder.pol2_no, viewHolder.pol2_yes);
        checkVisibility(pc.getDetails().get("dptHb2"), pc.getDetails().get("polio3"), viewHolder.pol3_no, viewHolder.pol3_yes);
        checkVisibility(pc.getDetails().get("dptHb3"), pc.getDetails().get("polio4"), viewHolder.pol4_no, viewHolder.pol4_yes);
        checkVisibility(pc.getDetails().get("jenisPelayanan"), null, viewHolder.vitk_no, viewHolder.vitk_yes);
        checkVisibility(pc.getDetails().get("campak"), null, viewHolder.campak_no, viewHolder.campak_yes);
        checkVisibility(pc.getDetails().get("ipv"), null, viewHolder.ivp_no, viewHolder.ivp_yes);

        //---------- IMMUNIZATION Status
        String berat = pc.getDetails().get("beratBayi") != null ? " " + pc.getDetails().get("beratBayi") : pc.getDetails().get("beratBadan") != null ? pc.getDetails().get("beratBadan") : "";
        String tanggal = pc.getDetails().get("tanggalKunjunganBayiPerbulan") != null ? " " + pc.getDetails().get("tanggalKunjunganBayiPerbulan") : pc.getDetails().get("tanggalPenimbangan") != null ? pc.getDetails().get("tanggalPenimbangan") : "";
        String tinggi = pc.getDetails().get("panjangBayi") != null ? " " + pc.getDetails().get("panjangBayi") : pc.getDetails().get("tinggiBadan") != null ? pc.getDetails().get("tinggiBadan") : "";

        String status_gizi = pc.getDetails().get("statusGizi") != null ? pc.getDetails().get("statusGizi") : "";
        String gizi = status_gizi.equals("GB") ? "Gizi Buruk" : status_gizi.equals("GK") ? "Gizi Kurang" : status_gizi.equals("GR") ? "Gizi Rendah" : "";

        if (pc.getDetails().get("tanggalPenimbangan") != null) {

        } else{

        }

        fillValue((TextView) convertView.findViewById(R.id.txt_current_weight), context.getString(R.string.str_weight) + berat);
        fillValue((TextView) convertView.findViewById(R.id.txt_visit_date), context.getString(R.string.date_visit_title) + tanggal);
        fillValue((TextView) convertView.findViewById(R.id.txt_current_height), context.getString(R.string.height) + tinggi);

    }

    void checkVisibility(String immunization1, String immunization2, ImageView no, ImageView yes) {
        if (immunization1 != null || immunization2 != null) {
            no.setVisibility(View.INVISIBLE);
            yes.setVisibility(View.VISIBLE);
        } else {
            no.setVisibility(View.VISIBLE);
            yes.setVisibility(View.INVISIBLE);
        }

    }



//    @Override
//    public void getView(Cursor cursor, SmartRegisterClient client, final View convertView) {
//
//        getView(client, convertView);
//
//    }


    private String setStatus(String status) {
        switch (status.toLowerCase()) {
//            case "underweight":
//                return context.getString(R.string.underweight);
//            case "severely underweight":
//                return context.getString(R.string.s_underweight);
//            case "normal":
//                return context.getString(R.string.normal);
//            case "overweight":
//                return context.getString(R.string.overweight);
//            case "severely stunted":
//                return context.getString(R.string.s_stunted);
//            case "stunted":
//                return context.getString(R.string.stunted);
//            case "tall":
//                return context.getString(R.string.tall);
//            case "severely wasted":
//                return context.getString(R.string.s_wasted);
//            case "wasted":
//                return context.getString(R.string.wasted);
            default:
                return "";
        }
    }

    public void setVitAVisibility() {
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int visibility = month == 2 || month == 8 ? View.VISIBLE : View.INVISIBLE;
//             vitALogo.setVisibility(visibility);
//             vitAText.setVisibility(visibility);
    }

    private int monthRangeToToday(String lastVisitDate) {
        String currentDate[] = new SimpleDateFormat("yyyy-MM").format(new Date()).substring(0, 7).split("-");
        return ((Integer.parseInt(currentDate[0]) - Integer.parseInt(lastVisitDate.substring(0, 4))) * 12 +
                (Integer.parseInt(currentDate[1]) - Integer.parseInt(lastVisitDate.substring(5, 7))));
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption
            serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(R.layout.smart_register_anak_client, null);
    }

    public LayoutInflater inflater() {
        return inflater;
    }


    class ViewHolder {

        TextView village_name, tanggal_kunjungan_anc, childs_age, mother_name, childs_name, anak_register_dob;
        TextView tempat_lahir, berat_lahir, tipe_lahir, berat_badan, tinggi, status_gizi;

        LinearLayout profilelayout;
        ImageButton follow_up;

        ImageView hp_badge, hb0_no, hb0_yes, pol1_no, pol1_yes, pol2_no, pol2_yes, pol3_no, pol3_yes;
        ImageView pol4_no, pol4_yes, vitk_no, vitk_yes, campak_no, campak_yes, ivp_no, ivp_yes, profilepic;

        public TextView hb0;
        public TextView pol1;
        public TextView pol2;
        public TextView complete;
        public TextView name;
        public TextView motherName;
        public TextView village;
        public TextView age;
        public TextView pol3;
        public TextView pol4;
        public TextView campak;
        public TextView gender;
        public ImageView hbLogo;
        public ImageView pol1Logo;
        public ImageView pol2Logo;
        public ImageView pol3Logo;
        public ImageView pol4Logo;
        public ImageView ipvLogo;
        public ImageView hbAlert;
        public ImageView pol1Alert;
        public ImageView pol2Alert;
        public ImageView pol3Alert;
        public ImageView pol4Alert;
        public ImageView measlesAlert;
        public FrameLayout hb0Layout;
        public FrameLayout bcgLayout;
        public FrameLayout hb1Layout;
        public FrameLayout hb2Layout;
        public FrameLayout hb3Layout;
        public FrameLayout campakLayout;
    }
}
