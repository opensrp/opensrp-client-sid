package org.smartregister.bidan.activity;

import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;

import org.smartregister.bidan.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.domain.FetchStatus;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

/**
 * Created by sid-tech on 11/14/17.
 */

public class BaseRegisterActivity extends SecuredNativeSmartRegisterActivity
        implements NavigationView.OnNavigationItemSelectedListener, SyncStatusBroadcastReceiver.SyncStatusListener {

    public static final String IS_REMOTE_LOGIN = "is_remote_login";
    private Snackbar syncStatusSnackbar;

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
    }

    @Override
    public void onSyncStart() {

    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {

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
