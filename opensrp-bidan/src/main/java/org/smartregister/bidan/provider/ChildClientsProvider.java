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

import org.smartregister.bidan.R;
import org.smartregister.bidan.utils.Support;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.smartregister.util.Utils.fillValue;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by sid-tech on 11/27/17.
 */

public class ChildClientsProvider extends BaseClientsProvider {

    private static final String TAG = ChildClientsProvider.class.getName();
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final AlertService alertService;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final CommonRepository commonRepository;
    private Drawable iconPencilDrawable;
    private String str_current_weight;
    private String str_visit_date;
    private String str_current_height;
    private String str_status_gizi;
    private String str_motherName;

    @Bind(R.id.profile_info_layout) LinearLayout profilelayout;

    @Bind(R.id.iv_child_photo) TextView profilepic;
    @Bind(R.id.ib_btn_edit) TextView follow_up;
    @Bind(R.id.txt_child_name) TextView childName;
    @Bind(R.id.txt_mother_name) TextView motherName;
    @Bind(R.id.txt_child_age) TextView childAge;
    @Bind(R.id.tv_village_name) TextView childAddress;
    @Bind(R.id.tempat_lahir) TextView childBirthPlace;
    @Bind(R.id.anak_register_dob) TextView childBirthDate;
    @Bind(R.id.berat_lahir) TextView birthWeight;
    @Bind(R.id.tipe_lahir) TextView birthType;

    //----------Child Neonatal Visits Information
    @Bind(R.id.icon_immuni_hb_no) TextView hb0_no;
    @Bind(R.id.icon_immuni_hb_yes) TextView hb0_yes;
    @Bind(R.id.icon_vit_k_no) TextView vitk_no;
    @Bind(R.id.icon_vit_k_yes) TextView vitk_yes;
    @Bind(R.id.icon_pol1_no) TextView pol1_no;
    @Bind(R.id.icon_pol1_yes) TextView pol1_yes;
    @Bind(R.id.icon_pol2_no) TextView pol2_no;
    @Bind(R.id.icon_pol2_yes) TextView pol2_yes;

    //----------Child Immunizations Information
    @Bind(R.id.icon_campak_no) TextView campak_no;
    @Bind(R.id.icon_campak_yes) TextView campak_yes;
    @Bind(R.id.icon_ivp_no) TextView ivp_no;
    @Bind(R.id.icon_ivp_yes) TextView ivp_yes;
    @Bind(R.id.icon_pol3_no) TextView pol3_no;
    @Bind(R.id.icon_pol3_yes) TextView pol3_yes;
    @Bind(R.id.icon_pol4_no) TextView pol4_no;
    @Bind(R.id.icon_pol4_yes) TextView pol4_yes;

    @Bind(R.id.txt_current_weight) ImageView txt_current_weight;
    @Bind(R.id.txt_visit_date) ImageView txt_visit_date;
    @Bind(R.id.txt_current_height) ImageView txt_current_height;
    @Bind(R.id.txt_status_gizi) ImageView txt_status_gizi;

    public ChildClientsProvider(Context context, View.OnClickListener onClickListener, AlertService alertService, CommonRepository commonRepository) {

        super(context);

        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.commonRepository = commonRepository;
        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, final View convertView) {
        try {
            ButterKnife.bind(this, convertView);
        } catch (Exception e) {
            e.getCause().printStackTrace();
        }

        ViewHolder viewHolder = new ViewHolder();

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);
        // kartu ibu = data KI only
        // ec_ibu = data anc + pnc
        // get parent
        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(pc.entityId());
        String relationalId = getValue(pc.getColumnmaps(), "relational_id", true).toLowerCase();
        AllCommonsRepository kirep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(relationalId);

        String strMotherName = null;
        String strChildAddress = null;
        String strBirthPlace = null;

        AllCommonsRepository iburep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
        final CommonPersonObject ibuparent = iburep.findByCaseID(childobject.getColumnmaps().get("relational_id"));

        convertView.findViewById(R.id.ib_btn_edit).setTag(client);
        convertView.findViewById(R.id.ib_btn_edit).setOnClickListener(onClickListener);

        convertView.findViewById(R.id.profile_info_layout).setTag(client);
        convertView.findViewById(R.id.profile_info_layout).setOnClickListener(onClickListener);

        String firstName = getValue(pc.getColumnmaps(), "namaBayi", true);
        String birthDate = getValue(pc.getColumnmaps(), "tanggalLahirAnak", true);

        if (birthDate.length() > 10)
            birthDate = birthDate.substring(0, Support.getColumnmaps(pc, "tanggalLahirAnak").indexOf("T"));
        String age = monthRangeToToday(birthDate) + " " + context.getString(R.string.str_month);


        if (ibuparent != null) {
            detailsRepository.updateDetails(ibuparent);
            strBirthPlace = ibuparent.getDetails().get("tempatBersalin") != null ? ibuparent.getDetails().get("tempatBersalin") : "";
        }

        if (kiparent != null) {
            detailsRepository.updateDetails(kiparent);
            strMotherName = kiparent.getDetails().get("namalengkap");
            strBirthPlace = kiparent.getDetails().get("address1");
        }

        //---------- Child Basic Information
//        viewHolder.profilepic = (ImageView) convertView.findViewById(R.id.iv_profile);
//        viewHolder.follow_up = (ImageButton) convertView.findViewById(R.id.ib_btn_edit);
//        viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.child_boy));

        if (pc.getDetails().get("gender") != null) {
            Support.setImagetoHolderFromUri((Activity) context, pc.getDetails().get("base_entity_id"),
                    viewHolder.profilepic, pc.getDetails().get("gender").equals("female") ? R.drawable.child_girl_infant : R.drawable.child_boy_infant);

        } else {
            Log.e(TAG, "getView: Gender is NOT SET");
        }

        fillValue(childName, firstName);
        fillValue(motherName, strMotherName);
        fillValue( childAge, age);
        fillValue( childAddress, strBirthPlace);

        //----------Child Deliver Information

        String childBirthWeight = pc.getDetails().get("beratLahir") != null ? pc.getDetails().get("beratLahir") : "";
        String childBirthType = "";

        fillValue((TextView) convertView.findViewById(R.id.tempat_lahir), strBirthPlace);
        fillValue((TextView) convertView.findViewById(R.id.anak_register_dob), birthDate);
        fillValue((TextView) convertView.findViewById(R.id.berat_lahir), childBirthWeight);
        fillValue((TextView) convertView.findViewById(R.id.tipe_lahir), childBirthType);

        //----------Child Neonatal Visits Information
        viewHolder.hb0_no = (ImageView) convertView.findViewById(R.id.icon_immuni_hb_no);
        viewHolder.hb0_yes = (ImageView) convertView.findViewById(R.id.icon_immuni_hb_yes);
        viewHolder.vitk_no = (ImageView) convertView.findViewById(R.id.icon_vit_k_no);
        viewHolder.vitk_yes = (ImageView) convertView.findViewById(R.id.icon_vit_k_yes);
        viewHolder.pol1_no = (ImageView) convertView.findViewById(R.id.icon_pol1_no);
        viewHolder.pol1_yes = (ImageView) convertView.findViewById(R.id.icon_pol1_yes);
        viewHolder.pol2_no = (ImageView) convertView.findViewById(R.id.icon_pol2_no);
        viewHolder.pol2_yes = (ImageView) convertView.findViewById(R.id.icon_pol2_yes);

        checkVisibility(pc.getDetails().get("hb0"), null, viewHolder.hb0_no, viewHolder.hb0_yes);
        checkVisibility(pc.getDetails().get("polio1"), pc.getDetails().get("bcg"), viewHolder.pol1_no, viewHolder.pol1_yes);
        checkVisibility(pc.getDetails().get("dptHb1"), pc.getDetails().get("polio2"), viewHolder.pol2_no, viewHolder.pol2_yes);

        //----------Child Immunizations Information
        viewHolder.campak_no = (ImageView) convertView.findViewById(R.id.icon_campak_no);
        viewHolder.campak_yes = (ImageView) convertView.findViewById(R.id.icon_campak_yes);
        viewHolder.ivp_no = (ImageView) convertView.findViewById(R.id.icon_ivp_no);
        viewHolder.ivp_yes = (ImageView) convertView.findViewById(R.id.icon_ivp_yes);

        viewHolder.pol3_no = (ImageView) convertView.findViewById(R.id.icon_pol3_no);
        viewHolder.pol3_yes = (ImageView) convertView.findViewById(R.id.icon_pol3_yes);
        viewHolder.pol4_no = (ImageView) convertView.findViewById(R.id.icon_pol4_no);
        viewHolder.pol4_yes = (ImageView) convertView.findViewById(R.id.icon_pol4_yes);

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
            str_current_weight = berat;
            str_visit_date = tanggal;
            str_current_height = tinggi;
            str_status_gizi = gizi;
        }

        fillValue((TextView) convertView.findViewById(R.id.txt_current_weight), context.getString(R.string.str_weight) + berat);
        fillValue((TextView) convertView.findViewById(R.id.txt_visit_date), context.getString(R.string.date_visit_title) + tanggal);
        fillValue((TextView) convertView.findViewById(R.id.txt_current_height), context.getString(R.string.height) + tinggi);
        fillValue((TextView) convertView.findViewById(R.id.txt_status_gizi), context.getString(R.string.nutrition) + gizi);

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

    private int monthRangeToToday(String lastVisitDate) {
        String currentDate[] = new SimpleDateFormat("yyyy-MM").format(new Date()).substring(0, 7).split("-");
        return ((Integer.parseInt(currentDate[0]) - Integer.parseInt(lastVisitDate.substring(0, 4))) * 12 +
                (Integer.parseInt(currentDate[1]) - Integer.parseInt(lastVisitDate.substring(5, 7))));
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        View view = inflater().inflate(R.layout.smart_register_child_client, null);
        return view;
    }

    class ViewHolder {

        public TextView hb0, complete, name, village, age, campak, gender;
        ImageButton follow_up;
        ImageView hp_badge, hb0_no, hb0_yes, pol1_no, pol1_yes, pol2_no, pol2_yes, pol3_no, pol3_yes;
        ImageView pol4_no, pol4_yes, vitk_no, vitk_yes, campak_no, campak_yes, ivp_no, ivp_yes, profilepic;
    }
}