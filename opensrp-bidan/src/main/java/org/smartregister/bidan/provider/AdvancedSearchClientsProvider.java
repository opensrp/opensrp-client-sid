package org.smartregister.bidan.provider;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import org.smartregister.bidan.fragment.AdvancedSearchFragment;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.bidan.R;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;

//public class AdvancedSearchClientsProvider extends ChildSmartClientsProvider {
public class AdvancedSearchClientsProvider extends ClientsProvider {

//    public AdvancedSearchClientsProvider(Context context, View.OnClickListener onClickListener,
//                                         AlertService alertService, VaccineRepository vaccineRepository, WeightRepository weightRepository, CommonRepository commonRepository) {
//        super(context, onClickListener, alertService, vaccineRepository, weightRepository, commonRepository);
//
//    }

    public AdvancedSearchClientsProvider(Context context, View.OnClickListener onClickListener,
                                         AlertService alertService,
                                         CommonRepository commonRepository) {
        super(context, onClickListener, alertService, commonRepository);

    }

    public void getView(Cursor cursor, SmartRegisterClient client, View convertView) {
        super.getView(cursor, client, convertView);

    }

    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(R.layout.advanced_search_client, null);
    }
}
