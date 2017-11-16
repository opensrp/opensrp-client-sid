package org.smartregister.bidan_cloudant.provider;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import org.smartregister.bidan_cloudant.R;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.smartregister.util.Utils.fillValue;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by soran on 08/11/17.
 */

public class MotherSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final AlertService alertService;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final CommonRepository commonRepository;

    public MotherSmartClientsProvider(Context context, View.OnClickListener onClickListener,
                                      AlertService alertService, CommonRepository commonRepository) {
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

        /*String lastName = getValue(pc.getColumnmaps(), VaksinatorConstants.KEY.LAST_NAME, true);
        String childName = getName(firstName, lastName);

        String motherFirstName = getValue(pc.getColumnmaps(), VaksinatorConstants.KEY.MOTHER_FIRST_NAME, true);
        if (StringUtils.isBlank(childName) && StringUtils.isNotBlank(motherFirstName)) {
            childName = "B/o " + motherFirstName.trim();
        }
        fillValue((TextView) convertView.findViewById(R.id.child_name), childName);

        String motherName = getValue(pc.getColumnmaps(), VaksinatorConstants.KEY.MOTHER_FIRST_NAME, true) + " " + getValue(pc, VaksinatorConstants.KEY.MOTHER_LAST_NAME, true);
        if (!StringUtils.isNotBlank(motherName)) {
            motherName = "M/G: " + motherName.trim();
        }
        fillValue((TextView) convertView.findViewById(R.id.child_mothername), motherName);*/
        

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
        return inflater().inflate(R.layout.smart_register_jurim_client, null);
    }

    public LayoutInflater inflater() {
        return inflater;
    }

   

    

       

  


}

