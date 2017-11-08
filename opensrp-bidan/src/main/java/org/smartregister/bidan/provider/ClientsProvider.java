package org.smartregister.bidan.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by sid-tech on 11/8/17.
 */
public class ClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final AlertService alertService;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final CommonRepository commonRepository;


    public ClientsProvider(Context context, View.OnClickListener onClickListener,
                                     AlertService alertService, CommonRepository commonRepository) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.commonRepository = commonRepository;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {

    }

    @Override
    public SmartRegisterClients updateClients(FilterOption filterOption, ServiceModeOption serviceModeOption, FilterOption filterOption1, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String s, String s1, String s2) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return null;
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        return null;
    }
}
