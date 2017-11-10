package org.smartregister.gizi.provider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.domain.Alert;
import org.smartregister.gizi.R;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.util.DateUtil;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import util.GiziConstants;
import util.formula.Support;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.smartregister.util.Utils.fillValue;
import static org.smartregister.util.Utils.getName;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by soran on 08/11/17.
 */

public class ChildSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
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
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        //convertView.findViewById(R.id.btn_edit).setBackgroundDrawable(iconPencilDrawable);
        convertView.findViewById(R.id.btn_edit).setTag(client);
        convertView.findViewById(R.id.btn_edit).setOnClickListener(onClickListener);

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);

        String firstName = getValue(pc.getColumnmaps(), "namaBayi", true);
        fillValue((TextView) convertView.findViewById(R.id.txt_child_name), firstName);

        // get parent
        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(pc.entityId());
        Log.d("IDssssssssssssss",pc.entityId());
        AllCommonsRepository kirep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(getValue(pc.getColumnmaps(), "relational_id", true));
        Log.d("22222222222222",getValue(pc.getColumnmaps(), "relational_id", true));
        if(kiparent != null) {
            detailsRepository.updateDetails(kiparent);
            String namaayah = getValue(kiparent.getColumnmaps(), "namaSuami", true);
            String namaibu = getValue(kiparent.getColumnmaps(), "namalengkap", true);
            fillValue((TextView) convertView.findViewById(R.id.ParentName), namaibu+","+namaayah);

        }
      String dob=  pc.getColumnmaps().get("tanggalLahirAnak").substring(0, pc.getColumnmaps().get("tanggalLahirAnak").indexOf("T"));

        //get child detail value
        String subVillages = getValue(kiparent.getColumnmaps(), "address1", true);
        String ages = getValue(pc.getColumnmaps(), "namaBayi", true);
        String dateOfBirth = getValue(pc.getColumnmaps(), "tanggalLahirAnak", true);
        String gender = getValue(pc.getDetails(), "gender", true);
        String visitDate = getValue(pc.getDetails(), "tanggalPenimbangan", true);
        String height = getValue(pc.getDetails(), "tinggiBadan", true);

        String weight = getValue(pc.getDetails(), "beratBadan", true);
        String underweight = getValue(pc.getDetails(), "underweight", true);
        String stunting_status = getValue(pc.getDetails(), "stunting", true);
        String wasting_status = getValue(pc.getDetails(), "wasting", true);

        //set child detail value
        fillValue((TextView) convertView.findViewById(R.id.txt_child_subVillage), subVillages);
      //  fillValue((TextView) convertView.findViewById(R.id.txt_child_age), firstName);
        fillValue((TextView) convertView.findViewById(R.id.txt_child_date_of_birth), dob);
        fillValue((TextView) convertView.findViewById(R.id.txt_child_gender), gender);
        fillValue((TextView) convertView.findViewById(R.id.txt_child_visit_date),context.getString(R.string.tanggal)+ visitDate);
        fillValue((TextView) convertView.findViewById(R.id.txt_child_height),context.getString(R.string.height)+ height);
        fillValue((TextView) convertView.findViewById(R.id.txt_child_weight),context.getString(R.string.weight)+ weight);
        fillValue((TextView) convertView.findViewById(R.id.txt_child_underweight), context.getString(R.string.wfa)+setStatus(underweight));
        fillValue((TextView) convertView.findViewById(R.id.txt_child_stunting), context.getString(R.string.stunting)+setStatus(stunting_status));
        fillValue((TextView) convertView.findViewById(R.id.txt_child_wasting), context.getString(R.string.wasting)+setStatus(wasting_status));




    /*        viewHolder.absentAlert = (TextView)convertView.findViewById(R.id.absen);
            viewHolder.weightText = (TextView)convertView.findViewById(R.id.weightSchedule);
            viewHolder.weightLogo = (ImageView)convertView.findViewById(R.id.weightSymbol);
            viewHolder.heightText = (TextView)convertView.findViewById(R.id.heightSchedule);
            viewHolder.heightLogo = (ImageView)convertView.findViewById(R.id.heightSymbol);
            viewHolder.vitALogo = (ImageView)convertView.findViewById(R.id.vitASymbol);
            viewHolder.vitAText = (TextView)convertView.findViewById(R.id.vitASchedule);
            viewHolder.antihelminticLogo = (ImageView)convertView.findViewById(R.id.antihelminticSymbol);
            viewHolder.antihelminticText = (TextView)convertView.findViewById(R.id.antihelminticText);
*/



        /*String lastName = getValue(pc.getColumnmaps(), GiziConstants.KEY.LAST_NAME, true);
        String childName = getName(firstName, lastName);

        String motherFirstName = getValue(pc.getColumnmaps(), GiziConstants.KEY.MOTHER_FIRST_NAME, true);
        if (StringUtils.isBlank(childName) && StringUtils.isNotBlank(motherFirstName)) {
            childName = "B/o " + motherFirstName.trim();
        }
        fillValue((TextView) convertView.findViewById(R.id.child_name), childName);

        String motherName = getValue(pc.getColumnmaps(), GiziConstants.KEY.MOTHER_FIRST_NAME, true) + " " + getValue(pc, GiziConstants.KEY.MOTHER_LAST_NAME, true);
        if (!StringUtils.isNotBlank(motherName)) {
            motherName = "M/G: " + motherName.trim();
        }
        fillValue((TextView) convertView.findViewById(R.id.child_mothername), motherName);*/
        

    }


    private String setStatus(String status){
        switch (status.toLowerCase()){
            case "underweight" :
                return context.getString(R.string.underweight);
            case "severely underweight" :
                return context.getString(R.string.s_underweight);
            case "normal":
                return context.getString(R.string.normal);
            case "overweight":
                return context.getString(R.string.overweight);
            case "severely stunted" :
                return context.getString(R.string.s_stunted);
            case "stunted" :
                return context.getString(R.string.stunted);
            case "tall" :
                return context.getString(R.string.tall);
            case "severely wasted" :
                return context.getString(R.string.s_wasted);
            case "wasted" :
                return context.getString(R.string.wasted);
            default:
                return "";
        }
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
        return inflater().inflate(R.layout.smart_register_gizi_client, null);
    }

    public LayoutInflater inflater() {
        return inflater;
    }

   

    

       

  


}

