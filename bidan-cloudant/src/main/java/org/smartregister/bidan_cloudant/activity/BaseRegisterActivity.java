package org.smartregister.bidan_cloudant.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.smartregister.Context;
import org.smartregister.bidan_cloudant.R;
import org.smartregister.bidan_cloudant.application.BidanApplication;
import org.smartregister.bidan_cloudant.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.bidan_cloudant.service.intent.SyncIntentService;
import org.smartregister.bidan_cloudant.sync.ECSyncUpdater;
import org.smartregister.domain.FetchStatus;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import java.util.Calendar;

/**
 * Created by sid-tech on 11/14/17.
 */

public class BaseRegisterActivity extends SecuredNativeSmartRegisterActivity
        implements NavigationView.OnNavigationItemSelectedListener, SyncStatusBroadcastReceiver.SyncStatusListener {

    public static final String IS_REMOTE_LOGIN = "is_remote_login";
    private Snackbar syncStatusSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_bidan);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

//        BaseActivityToggle toggle = new BaseActivityToggle(this, drawer,
//                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
//
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//
//            }
//        };
//
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            boolean isRemote = extras.getBoolean(IS_REMOTE_LOGIN);
            if (isRemote) {
                updateFromServer();
            }
        }
    }


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

    private void updateFromServer() {
        startService(new Intent(getApplicationContext(), SyncIntentService.class));
    }



    private void updateLastSyncText() {
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        if (navigationView != null && navigationView.getMenu() != null) {
//            TextView syncMenuItem = ((TextView) navigationView.findViewById(R.id.nav_synctextview));
//            if (syncMenuItem != null) {
//                String lastSync = getLastSyncTime();
//
//                if (!TextUtils.isEmpty(lastSync)) {
//                    lastSync = " " + String.format(getString(R.string.last_sync), lastSync);
//                }
//                syncMenuItem.setText(String.format(getString(R.string.sync_), lastSync));
//            }
//        }
    }

    private String getLastSyncTime() {
        String lastSync = "";
        long milliseconds = ECSyncUpdater.getInstance(this).getLastCheckTimeStamp();
        if (milliseconds > 0) {
            DateTime lastSyncTime = new DateTime(milliseconds);
            DateTime now = new DateTime(Calendar.getInstance());
            Minutes minutes = Minutes.minutesBetween(lastSyncTime, now);
            if (minutes.getMinutes() < 1) {
                Seconds seconds = Seconds.secondsBetween(lastSyncTime, now);
                lastSync = seconds.getSeconds() + "s";
            } else if (minutes.getMinutes() >= 1 && minutes.getMinutes() < 60) {
                lastSync = minutes.getMinutes() + "m";
            } else if (minutes.getMinutes() >= 60 && minutes.getMinutes() < 1440) {
                Hours hours = Hours.hoursBetween(lastSyncTime, now);
                lastSync = hours.getHours() + "h";
            } else {
                Days days = Days.daysBetween(lastSyncTime, now);
                lastSync = days.getDays() + "d";
            }
        }
        return lastSync;
    }

    @Override
    protected Context context() {
        return BidanApplication.getInstance().context();
    }


    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////
    private class BaseActivityToggle extends ActionBarDrawerToggle {

        private BaseActivityToggle(Activity activity, DrawerLayout drawerLayout, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
            super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        /*public BaseActivityToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }*/

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            if (!SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
                updateLastSyncText();
            }
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
        }
    }

}
