package org.smartregister.bidan.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;

import org.smartregister.bidan.R;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

/**
 * Created by sid-tech on 12/12/17.
 */

public class BaseClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final LayoutInflater inflater;
    protected CommonPersonObjectController controller;

    BaseClientsProvider(Context context) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {

    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String s, String s1, String s2) {
        return null;
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
//        View view = inflater().inflate(R.layout.smart_register_ki_client, null);
//        return view;
        return null;
    }

    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

//    public LayoutInflater inflater() {
//        return null;
//    }


}
