package org.smartregister.bidan.provider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import org.smartregister.bidan.R;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


/**
 * Created by sid-tech on 11/27/17.
 */

public class ChildClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
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

    public void getView(SmartRegisterClient client, View convertView) {

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }

                convertView.findViewById(R.id.profile_info_layout);

                convertView.findViewById(R.id.child_name);
                convertView.findViewById(R.id.mother_name);
                convertView.findViewById(R.id.txt_village_name);
                convertView.findViewById(R.id.child_age);

                convertView.findViewById(R.id.img_hr_badge);

        //delivery documentation
                convertView.findViewById(R.id.anak_register_dob);
                convertView.findViewById(R.id.tempat_lahir);
                convertView.findViewById(R.id.berat_lahir);
                convertView.findViewById(R.id.tipe_lahir);
                convertView.findViewById(R.id.txt_status_gizi);

                convertView.findViewById(R.id.icon_immuni_hb_no);
                convertView.findViewById(R.id.icon_immuni_hb_yes);
                convertView.findViewById(R.id.icon_vit_k_no);
                convertView.findViewById(R.id.icon_vit_k_yes);
                convertView.findViewById(R.id.icon_campak_no);
                convertView.findViewById(R.id.icon_campak_yes);
                convertView.findViewById(R.id.icon_ivp_no);
                convertView.findViewById(R.id.icon_ivp_yes);

                convertView.findViewById(R.id.icon_pol1_no);
                convertView.findViewById(R.id.icon_pol1_yes);
                convertView.findViewById(R.id.icon_pol2_no);
                convertView.findViewById(R.id.icon_pol2_yes);
                convertView.findViewById(R.id.icon_pol3_no);
                convertView.findViewById(R.id.icon_pol3_yes);
                convertView.findViewById(R.id.icon_pol4_no);
                convertView.findViewById(R.id.icon_pol4_yes);

                convertView.findViewById(R.id.txt_current_weight);
                convertView.findViewById(R.id.txt_visit_date);
                convertView.findViewById(R.id.txt_current_height);
                convertView.findViewById(R.id.img_profile);
                convertView.findViewById(R.id.btn_edit);
//        viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.child_boy));

//        //convertView.findViewById(R.id.btn_edit).setBackgroundDrawable(iconPencilDrawable);
//        convertView.findViewById(R.id.btn_edit).setTag(client);
//        convertView.findViewById(R.id.btn_edit).setOnClickListener(onClickListener);
//
//        convertView.findViewById(R.id.profile_info_layout).setTag(client);
//        convertView.findViewById(R.id.profile_info_layout).setOnClickListener(onClickListener);
//
//        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
//        detailsRepository.updateDetails(pc);
//
//        String firstName = getValue(pc.getColumnmaps(), "namaBayi", true);
//        fillValue((TextView) convertView.findViewById(R.id.txt_child_name), firstName);
//
//        // get parent
//        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
//        CommonPersonObject childobject = childRepository.findByCaseID(pc.entityId());
//        //    Log.d("IDssssssssssssss",pc.entityId());
//        AllCommonsRepository kirep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
//        final CommonPersonObject kiparent = kirep.findByCaseID(getValue(pc.getColumnmaps(), "relational_id", true));
//
//        if (kiparent != null) {
//            detailsRepository.updateDetails(kiparent);
//            //  String namaayah = getValue(kiparent.getColumnmaps(), "namaSuami", true);
//            String namaibu = getValue(kiparent.getColumnmaps(), "namalengkap", true);
//            fillValue((TextView) convertView.findViewById(R.id.ParentName), namaibu);
//            String subVillages = getValue(kiparent.getDetails(), "address1", true);
//            fillValue((TextView) convertView.findViewById(R.id.txt_child_subVillage), subVillages);
//        }
//        String dob = pc.getColumnmaps().get("tanggalLahirAnak").substring(0, pc.getColumnmaps().get("tanggalLahirAnak").indexOf("T"));
//        String age = "" + monthRangeToToday(dob);
//        //get child detail value
//
//
//        //  String ages = getValue(pc.getColumnmaps(), "namaBayi", true);
//        String dateOfBirth = getValue(pc.getColumnmaps(), "tanggalLahirAnak", true);
//        String gender = getValue(pc.getDetails(), "gender", true);
//        String visitDate = getValue(pc.getDetails(), "tanggalPenimbangan", true);
//        String height = getValue(pc.getDetails(), "tinggiBadan", true);
//
//        String weight = getValue(pc.getDetails(), "beratBadan", true);
//        String underweight = getValue(pc.getDetails(), "underweight", true);
//        String stunting_status = getValue(pc.getDetails(), "stunting", true);
//        String wasting_status = getValue(pc.getDetails(), "wasting", true);
//
//        //set child detail value
//
//        fillValue((TextView) convertView.findViewById(R.id.txt_child_age), age);
//        fillValue((TextView) convertView.findViewById(R.id.txt_child_date_of_birth), "DOB :" + dob);
//        fillValue((TextView) convertView.findViewById(R.id.txt_child_gender), gender);
//
//
//        fillValue((TextView) convertView.findViewById(R.id.txt_child_visit_date), context.getString(R.string.tanggal) + visitDate);
//        fillValue((TextView) convertView.findViewById(R.id.txt_child_height), context.getString(R.string.height) + height);
//        fillValue((TextView) convertView.findViewById(R.id.txt_child_weight), context.getString(R.string.weight) + weight);
//
//        if (pc.getDetails().get("tanggalPenimbangan") != null) {
//            fillValue((TextView) convertView.findViewById(R.id.txt_child_underweight), context.getString(R.string.wfa) + setStatus(underweight));
//            fillValue((TextView) convertView.findViewById(R.id.txt_child_stunting), context.getString(R.string.stunting) + setStatus(stunting_status));
//            fillValue((TextView) convertView.findViewById(R.id.txt_child_wasting), context.getString(R.string.wasting) + setStatus(wasting_status));
//        } else {
//            fillValue((TextView) convertView.findViewById(R.id.txt_child_underweight), context.getString(R.string.wfa));
//            fillValue((TextView) convertView.findViewById(R.id.txt_child_stunting), context.getString(R.string.stunting));
//            fillValue((TextView) convertView.findViewById(R.id.txt_child_wasting), context.getString(R.string.wasting));
//        }
    }



        @Override
    public void getView(Cursor cursor, SmartRegisterClient client, final View convertView) {

            getView (client, convertView);

    }


//    private String setStatus(String status) {
//        switch (status.toLowerCase()) {
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
//            default:
//                return "";
//        }
//    }

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
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String
            metaData) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(R.layout.smart_register_anak_client, null);
    }

    public LayoutInflater inflater() {
        return inflater;
    }


}
