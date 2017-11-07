package org.smartregister.bidan.activity;

import org.smartregister.provider.SmartRegisterClientsProvider;

/**
 * Created by sid-tech on 11/7/17.
 */

public class NativeKBSmartRegisterActivity extends BaseRegisterActivity {

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
