package org.smartregister.bidan.activity;

import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;

/**
 * Created by sid-tech on 11/28/17.
 */

public class IbuSmartRegisterActivity extends SecuredNativeSmartRegisterActivity
        implements LocationSelectorDialogFragment.OnLocationSelectedListener   {

    @Override
    public void OnLocationSelected(String locationSelected) {

    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    public void startRegistration() {

    }
}
