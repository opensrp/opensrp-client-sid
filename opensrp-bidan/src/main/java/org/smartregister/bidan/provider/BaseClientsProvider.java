package org.smartregister.bidan.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.smartregister.bidan.activity.LoginActivity;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

public class BaseClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final String TAG = BaseClientsProvider.class.getName();
    private final LayoutInflater inflater;
    private final Context mContext;
    protected CommonPersonObjectController controller;

    BaseClientsProvider(Context context) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        LoginActivity.setLanguage();
        Log.e(TAG, "BaseClientsProvider: ");
    }


    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {
        Log.e(TAG, "getView: ");
        Log.e(TAG, "getView: " + mContext.getResources().getConfiguration().locale);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        Log.e(TAG, "updateClients: ");
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        Log.e(TAG, "onServiceModeSelected: ");
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String s, String s1, String s2) {
        Log.e(TAG, "newFormLauncher: ");
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public View inflatelayoutForCursorAdapter() {
        Log.e(TAG, "inflatelayoutForCursorAdapter: ");
//        return inflater().inflate(R.layout.smart_register_ki_client, null);
        return null;
    }

    public SmartRegisterClients getClients() {
        Log.e(TAG, "getClients: ");
        return controller.getClients();
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }


}