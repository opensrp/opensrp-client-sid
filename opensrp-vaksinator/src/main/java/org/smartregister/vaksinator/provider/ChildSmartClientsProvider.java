package org.smartregister.vaksinator.provider;

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

import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.vaksinator.R;
import org.smartregister.vaksinator.activity.VaksinatorDetailActivity;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.io.File;
import java.text.SimpleDateFormat;

import util.formula.Support;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.smartregister.util.Utils.fillValue;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by soran on 08/11/17.
 */

public class ChildSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private static final String TAG = ChildSmartClientsProvider.class.getSimpleName();
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final AlertService alertService;
    private Drawable iconPencilDrawable;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final CommonRepository commonRepository;

    public ChildSmartClientsProvider(Context context, View.OnClickListener onClickListener,
                                     AlertService alertService,  CommonRepository commonRepository) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.commonRepository = commonRepository;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, final View convertView) {
        ViewHolder viewHolder;

        if (convertView.getTag() == null || !(convertView.getTag() instanceof ViewHolder)) {
            viewHolder = new ViewHolder();
            viewHolder.profilelayout = (LinearLayout) convertView.findViewById(R.id.profile_info_layout);
            //----------Child Basic Information
            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_child_name);
            viewHolder.motherName = (TextView) convertView.findViewById(R.id.ParentName);
            viewHolder.village = (TextView) convertView.findViewById(R.id.txt_child_subVillage);
            viewHolder.age = (TextView) convertView.findViewById(R.id.txt_child_age);
            viewHolder.gender = (TextView) convertView.findViewById(R.id.txt_child_gender);

            //----------FrameLayout
            viewHolder.hb0Layout = (FrameLayout) convertView.findViewById(R.id.hb0Layout);
            viewHolder.bcgLayout = (FrameLayout) convertView.findViewById(R.id.bcgLayout);
            viewHolder.hb1Layout = (FrameLayout) convertView.findViewById(R.id.hb1Layout);
            viewHolder.hb2Layout = (FrameLayout) convertView.findViewById(R.id.hb2Layout);
            viewHolder.hb3Layout = (FrameLayout) convertView.findViewById(R.id.hb3Layout);
            viewHolder.campakLayout = (FrameLayout) convertView.findViewById(R.id.campakLayout);

            //----------TextView to show immunization date
            viewHolder.hb0 = (TextView) convertView.findViewById(R.id.hb1);
            viewHolder.pol1 = (TextView) convertView.findViewById(R.id.pol1);
            viewHolder.pol2 = (TextView) convertView.findViewById(R.id.pol2);
            viewHolder.pol3 = (TextView) convertView.findViewById(R.id.pol3);
            viewHolder.pol4 = (TextView) convertView.findViewById(R.id.pol4);
            viewHolder.campak = (TextView) convertView.findViewById(R.id.ipv);

            //----------Checklist logo
            viewHolder.hbLogo = (ImageView) convertView.findViewById(R.id.hbLogo);
            viewHolder.pol1Logo = (ImageView) convertView.findViewById(R.id.pol1Logo);
            viewHolder.pol2Logo = (ImageView) convertView.findViewById(R.id.pol2Logo);
            viewHolder.pol3Logo = (ImageView) convertView.findViewById(R.id.pol3Logo);
            viewHolder.pol4Logo = (ImageView) convertView.findViewById(R.id.pol4Logo);
            viewHolder.ipvLogo = (ImageView) convertView.findViewById(R.id.measlesLogo);

            viewHolder.profilepic = (ImageView) convertView.findViewById(R.id.profilepic);
            viewHolder.follow_up = (ImageButton) convertView.findViewById(R.id.btn_edit);
            viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.drawable.child_boy_infant));
//            viewHolder.profilepic =(ImageView)convertView.findViewById(R.id.img_profile);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.follow_up.setOnClickListener(onClickListener);
        viewHolder.follow_up.setTag(client);
        viewHolder.profilelayout.setOnClickListener(onClickListener);
        viewHolder.profilelayout.setTag(client);
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        viewHolder.follow_up.setImageDrawable(iconPencilDrawable);
        viewHolder.follow_up.setOnClickListener(onClickListener);
        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);

        String firstName = getValue(pc.getColumnmaps(), "namaBayi", true);
        fillValue((TextView) convertView.findViewById(R.id.txt_child_name), firstName);

        String ages = Support.getColumnmaps(pc, "tanggalLahirAnak");
        if (ages.length()>10)
            ages = ages.substring(0, Support.getColumnmaps(pc,"tanggalLahirAnak").indexOf("T"));
        int umur = Support.getColumnmaps(pc,"tanggalLahirAnak") != "-" ? age(ages) : 0;

        // get parent
        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(pc.entityId());
        String relationalId = getValue(pc.getColumnmaps(), "relational_id", true).toLowerCase();
        AllCommonsRepository kirep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(relationalId);
        if(kiparent != null) {
            detailsRepository.updateDetails(kiparent);
            String namaayah = kiparent.getDetails().get("namaSuami");
            String namaibu = kiparent.getColumnmaps().get("namalengkap");

            viewHolder.motherName.setText(namaibu + "," + namaayah);
            viewHolder.village.setText(kiparent.getDetails().get("address1"));

        }

        viewHolder.age.setText(!Support.getColumnmaps(pc,"tanggalLahirAnak").equals("-")
                ? Integer.toString(age(ages) / 12) + " " + context.getResources().getString(R.string.year_short)
                + ", " + Integer.toString(age(ages) % 12) + " " + context.getResources().getString(R.string.month_short)
                : " ");
        viewHolder.gender.setText(!Support.getDetails(pc,"gender").equalsIgnoreCase("-")
                ? Support.getDetails(pc,"gender").contains("em")
                ? "Perempuan"
                : "Laki-laki"
                : " ");

        if (!Support.getDetails(pc,"gender").equals("-")) {
            VaksinatorDetailActivity.setImagetoHolderFromUri((Activity) context,
                    DrishtiApplication.getAppDir() + File.separator + Support.getDetails(pc, "base_entity_id") + ".JPEG",
                    viewHolder.profilepic, Support.getDetails(pc, "gender").equals("female") ? R.drawable.child_girl_infant : R.drawable.child_boy_infant);
        } else {
            Log.e(TAG, "getView: Gender is NOT SET");
        }

        viewHolder.hb0.setText(transformToddmmyyyy(latestDate(new String[]{Support.getDetails(pc,"hb0")})));

        viewHolder.pol1.setText(
                transformToddmmyyyy(latestDate(new String[]{Support.getDetails(pc,"bcg"), Support.getDetails(pc,"polio1")}))
        );

        viewHolder.pol2.setText(
                transformToddmmyyyy(latestDate(new String[]{Support.getDetails(pc,"dptHb1"), Support.getDetails(pc,"polio2")}))
        );

        viewHolder.pol3.setText(
                transformToddmmyyyy(latestDate(new String[]{Support.getDetails(pc,"dptHb2"), Support.getDetails(pc,"polio3")}))

        );

        viewHolder.pol4.setText(
                transformToddmmyyyy(latestDate(new String[]{Support.getDetails(pc,"dptHb3"), Support.getDetails(pc,"polio4"), Support.getDetails(pc,"ipv")}))
        );

        viewHolder.campak.setText(transformToddmmyyyy(Support.getDetails(pc,"campak")));

//----- logo visibility, sometimes the variable contains blank string that count as not null, so we must check both the availability and content
        boolean a = hasDate(pc, "hb0");
        viewHolder.hbLogo.setImageResource(a ? R.drawable.ic_yes_large : umur > 0 ? R.drawable.vacc_late : umur == 0 ? R.mipmap.vacc_due : R.drawable.abc_list_divider_mtrl_alpha);
        //if(!a)
        // viewHolder.hb0Layout.setBackgroundColor(context.getResources().getColor(R.color.vaccBlue));

        setIcon(viewHolder.bcgLayout, viewHolder.pol1Logo, null, "bcg,polio1", umur, 1, pc);
        setIcon(viewHolder.hb1Layout, viewHolder.pol2Logo, null, "dptHb1,polio2", umur, 2, pc);
        setIcon(viewHolder.hb2Layout, viewHolder.pol3Logo, null, "dptHb2,polio3", umur, 3, pc);
        setIcon(viewHolder.hb3Layout, viewHolder.pol4Logo, null, "dptHb3,polio4,ipv", umur, 4, pc);
        setIcon(viewHolder.campakLayout, viewHolder.ipvLogo, null, "campak", umur, 9, pc);




    }

    private String latestDate(String[] dates) {
        String max = dates[0] != null
                ? dates[0].length() == 10
                ? dates[0]
                : "0000-00-00"
                : "0000-00-00";
        for (int i = 1; i < dates.length; i++) {
            if (dates[i] == null)
                continue;
            if (dates[i].length() < 10)
                continue;
            max = (((Integer.parseInt(max.substring(0, 4)) - Integer.parseInt(dates[i].substring(0, 4))) * 360)
                    + ((Integer.parseInt(max.substring(5, 7)) - Integer.parseInt(dates[i].substring(5, 7))) * 30)
                    + (Integer.parseInt(max.substring(8, 10)) - Integer.parseInt(dates[i].substring(8, 10)))
            ) < 0 ? dates[i] : max;
        }
        return max.equals("0000-00-00") ? "" : max;
    }

    //  updating icon
    private void setIcon(FrameLayout frame, ImageView image, String oldCode, String detailID, int umur, int indicator, CommonPersonObjectClient pc) {
        if (hasDate(pc, oldCode)) {
            image.setImageResource(Support.getDetails(pc,oldCode).contains("-")
                            ? R.drawable.ic_yes_large
                            : umur > indicator
                            ? R.drawable.vacc_late
                            : R.drawable.abc_list_divider_mtrl_alpha
            );
            return;
        }

        frame.setBackgroundColor(context.getResources().getColor(R.color.abc_background_cache_hint_selector_material_light));

        String[] var = detailID.split(",");
        boolean complete = false;
        boolean someComplete = false;

        for (int i = 0; i < var.length; i++) {
            someComplete = someComplete || (Support.getDetails(pc,var[i]).length() > 6);
        }

        if (someComplete) {
            complete = true;
            for (int i = 0; i < var.length; i++) {
                complete = complete && (Support.getDetails(pc,var[i]).length() > 6);
            }
        }

        image.setImageResource(complete
                        ? R.drawable.ic_yes_large
                        : someComplete
                        ? R.drawable.vacc_due
                        : umur > indicator
                        ? R.drawable.vacc_late
                        : R.drawable.abc_list_divider_mtrl_alpha
        );

        if ((umur == indicator) && !(complete || someComplete))
            frame.setBackgroundColor(context.getResources().getColor(R.color.vaccBlue));
    }

    private boolean hasDate(CommonPersonObjectClient pc, String variable) {
        return Support.getDetails(pc,variable).length() > 6;
    }

    //  month age calculation
    private int age(String date) {
        if(date.length()<10)
            return 0;
        String[] dateOfBirth = date.split("-");
        String[] currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()).split("-");

        int tahun = Integer.parseInt(currentDate[0]) - Integer.parseInt(dateOfBirth[0]);
        int bulan = Integer.parseInt(currentDate[1]) - Integer.parseInt(dateOfBirth[1]);
        int hari = Integer.parseInt(currentDate[2]) - Integer.parseInt(dateOfBirth[2]);

        int result = ((tahun * 360) + (bulan * 30) + hari) / 30;
        result = result < 0 ? 0 : result;

        return result;
    }

    

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption
            serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        // do nothing
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String
            metaData) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(R.layout.smart_register_jurim_client, null);
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    public String transformToddmmyyyy(String date){
        if(date.length()>3) {
            if (date.charAt(4) == '-')
                date = String.format("%s/%s/%s", new String[]{date.substring(8, 10), date.substring(5, 7), date.substring(0, 4)});
        }
        return date;
    }

    class ViewHolder {
        private LinearLayout profilelayout;
        protected ImageView profilepic;
        protected ImageButton follow_up;
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

