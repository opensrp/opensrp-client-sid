package org.smartregister.gizi.activity;

import android.Manifest;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.FetchStatus;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.event.Listener;
import org.smartregister.gizi.R;
import org.smartregister.gizi.application.GiziApplication;
import org.smartregister.gizi.controller.GiziNavigationController;
import org.smartregister.gizi.facial.repository.ImageRepository;
import org.smartregister.gizi.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.gizi.repository.IndonesiaECRepository;
import org.smartregister.gizi.sync.ECSyncUpdater;
import org.smartregister.gizi.sync.UpdateActionsTask;
import org.smartregister.gizi.utils.AllConstantsINA;
import org.smartregister.gizi.utils.Support;
import org.smartregister.service.PendingFormSubmissionService;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.contract.HomeContext;
import org.smartregister.view.controller.NativeAfterANMDetailsFetchListener;
import org.smartregister.view.controller.NativeUpdateANMDetailsTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.valueOf;
import static org.smartregister.event.Event.ACTION_HANDLED;
import static org.smartregister.event.Event.FORM_SUBMITTED;

public class GiziHomeActivity extends SecuredActivity implements SyncStatusBroadcastReceiver.SyncStatusListener,LocationListener {
    private static final String TAG = GiziHomeActivity.class.getName();
    private SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    private MenuItem updateMenuItem;
    private MenuItem lastSyncMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private PendingFormSubmissionService pendingFormSubmissionService;
    private IndonesiaECRepository indonesiaECRepository;
    private ImageRepository imageRepository;
    private SyncStatusBroadcastReceiver syncStatusBroadcastReceiver;

    private SharedPreferences preferences;
    private Listener<String> onFormSubmittedListener = new Listener<String>() {
        @Override
        public void onEvent(String instanceId) {
            updateRegisterCounts();
        }
    };
    private Listener<String> updateANMDetailsListener = new Listener<String>() {
        @Override
        public void onEvent(String data) {
            updateRegisterCounts();
        }
    };

    private TextView anakRegisterClientCountView;
    private TextView ibuRegisterClientCountView;
//    private int childcount;
//    private int ibucount;

    private View.OnClickListener onRegisterStartListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_gizi_register:
                    navigationController.startChildSmartRegistry();
                    break;
                case R.id.btn_gizi_ibu_register:
                    navigationController.startECSmartRegistry();
                    break;


            }
            String HomeEnd = timer.format(new Date());
            Map<String, String> Home = new HashMap<String, String>();
            Home.put("end", HomeEnd);
//            FlurryAgent.logEvent("gizi_home_dashboard", Home, true);
        }
    };
    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_reporting:
                    navigationController.startReports();
                    break;

                case R.id.btn_videos:
//                    navigationController.startVideos();
                    break;
            }
        }
    };

    private void flagActivator() {
        new Thread() {
            public void run() {
                try {
                    while (AllConstantsINA.TimeConstants.SLEEP_TIME > 0) {
                        sleep(1000);
                        if (AllConstantsINA.TimeConstants.IDLE)
                            AllConstantsINA.TimeConstants.SLEEP_TIME -= 1000;
                    }
                    Support.ONSYNC = false;
                } catch (InterruptedException ie) {
                    Log.e(TAG, "run: " + ie.getCause());
                }
            }
        }.start();
    }
    LocationManager locationManager;
    String provider;

    @Override
    protected void onCreation() {
        //home dashboard
        setContentView(R.layout.smart_registers_gizi_home);
        //  FlurryFacade.logEvent("gizi_home_dashboard");
        navigationController = new GiziNavigationController(this, anmController, context());
        preferences = getDefaultSharedPreferences(this);
        locationManager = (LocationManager) getSystemService(android.content.Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);
        if ( provider == null ) {
            provider = LocationManager.GPS_PROVIDER;
        }
        setupViews();
        initialize();
        DisplayFormFragment.formInputErrorMessage = getResources().getString(R.string.forminputerror);
        DisplayFormFragment.okMessage = getResources().getString(R.string.okforminputerror);

        String HomeStart = timer.format(new Date());
        Map<String, String> Home = new HashMap<String, String>();
        Home.put("start", HomeStart);
//        FlurryAgent.logEvent("gizi_home_dashboard", Home, true);

        // Require for okhttp
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        if(!hasPermissions(GiziApplication.getInstance(), PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }

    private void setupViews() {
        findViewById(R.id.btn_gizi_register).setOnClickListener(onRegisterStartListener);

        findViewById(R.id.btn_gizi_ibu_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);


        anakRegisterClientCountView = (TextView) findViewById(R.id.txt_child_register_client_count);
        ibuRegisterClientCountView = (TextView) findViewById(R.id.txt_mother_register_client_count);

    }

    private void initialize() {
        pendingFormSubmissionService = context().pendingFormSubmissionService();
        indonesiaECRepository = GiziApplication.getInstance().indonesiaECRepository();
        imageRepository = GiziApplication.getInstance().imageRepository();

        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);

        registerMyReceiver();

        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(org.smartregister.gizi.R.mipmap.logo));
        getSupportActionBar().setLogo(org.smartregister.gizi.R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        LoginActivity.setLanguage();

    }

    private void registerMyReceiver() {

        try
        {
            if(syncStatusBroadcastReceiver == null){
                syncStatusBroadcastReceiver = new SyncStatusBroadcastReceiver(this);
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
            registerReceiver(syncStatusBroadcastReceiver, intentFilter);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    protected void onResumption() {
        LoginActivity.setLanguage();
        registerMyReceiver();
        updateRegisterCounts();
        updateSyncIndicator();
        updateLastSyncTime();
        updateRemainingFormsToSyncCount();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(syncStatusBroadcastReceiver);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
        }
    }

    private void updateRegisterCounts() {
        NativeUpdateANMDetailsTask task = new NativeUpdateANMDetailsTask(Context.getInstance().anmController());
        task.fetch(new NativeAfterANMDetailsFetchListener() {
            @Override
            public void afterFetch(HomeContext anmDetails) {
                updateRegisterCounts(anmDetails);
            }
        });
    }

    private void updateRegisterCounts(HomeContext homeContext) {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
        Cursor childcountcursor = context().commonrepository("ec_anak").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_anak_search", "ec_anak_search.is_closed=0"));
        childcountcursor.moveToFirst();
        int childcount = childcountcursor.getInt(0);
        childcountcursor.close();

        Cursor ibucountcursor = context().commonrepository("ec_ibu").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_ibu", "ec_ibu.is_closed=0 and ec_ibu.pptest ='Positive'"));
        ibucountcursor.moveToFirst();
        int ibucount = ibucountcursor.getInt(0);
        ibucountcursor.close();

        ibuRegisterClientCountView.setText(valueOf(ibucount));

        anakRegisterClientCountView.setText(valueOf(childcount));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateMenuItem = menu.findItem(R.id.updateMenuItem);
        remainingFormsToSyncMenuItem = menu.findItem(R.id.remainingFormsToSyncMenuItem);

        updateSyncIndicator();
        updateRemainingFormsToSyncCount();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updateMenuItem:
                updateLocation();
                updateFromServer();
                return true;
            case R.id.switchLanguageMenuItem:
                String newLanguagePreference = LoginActivity.switchLanguagePreference();
                LoginActivity.setLanguage();
                Toast.makeText(this, "Language preference set to " + newLanguagePreference + ". Please restart the application.", LENGTH_SHORT).show();
                this.recreate();
                return true;
            case R.id.help:
                String anmID;
                try {
                    anmID = new JSONObject(context().anmController().get()).get("anmName").toString();
                } catch (org.json.JSONException e) {
                    anmID = "undefined";
                }
                Toast.makeText(this, String.format("%s current user = %s", context().getStringResource(R.string.app_name), anmID), LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void updateFromServer() {
        Log.e("Home", "updateDataFromServer: tombol update");
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(this);
//        FlurryFacade.logEvent("click_update_from_server");
        updateActionsTask.updateFromServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FORM_SUBMITTED.removeListener(onFormSubmittedListener);
        ACTION_HANDLED.removeListener(updateANMDetailsListener);
    }

    private void updateSyncIndicator() {
        if (updateMenuItem != null) {
            if (context().allSharedPreferences().fetchIsSyncInProgress()) {
                updateMenuItem.setActionView(R.layout.progress);
            } else
                updateMenuItem.setActionView(null);
        }
    }

    private void updateRemainingFormsToSyncCount() {
        if (remainingFormsToSyncMenuItem == null) {
            return;
        }

        long size = pendingFormSubmissionService.pendingFormSubmissionCount();
        if (size > 0) {
            remainingFormsToSyncMenuItem.setTitle(valueOf(size) + " " + getString(R.string.unsynced_forms_count_message));
            remainingFormsToSyncMenuItem.setVisible(true);
        } else {
            remainingFormsToSyncMenuItem.setVisible(false);
        }
    }

    private void updateLastSyncTime(){
        if (lastSyncMenuItem == null) {
            return;
        }

        long longLastSync = ECSyncUpdater.getInstance(getApplicationContext()).getLastCheckTimeStamp();
        String lastSyncDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longLastSync));
        if (longLastSync==0) {
            lastSyncMenuItem.setTitle(getString(R.string.not_synced));
            lastSyncMenuItem.setVisible(true);
        } else {
            lastSyncMenuItem.setTitle(getString(R.string.sync_last_date)+" "+lastSyncDate);
            lastSyncMenuItem.setVisible(true);
        }
    }

    @Override
    public void onSyncStart() {
        Support.ONSYNC = true;
        AllConstantsINA.TimeConstants.IDLE = false;
        AllConstantsINA.TimeConstants.SLEEP_TIME = 5000;
        if (updateMenuItem != null) {
            updateMenuItem.setActionView(R.layout.progress);
        }
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {

    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        Toast.makeText(getApplicationContext(), fetchStatus.displayValue(), Toast.LENGTH_SHORT).show();
        updateLastSyncTime();
        updateRemainingFormsToSyncCount();
        if (updateMenuItem != null) {
            updateMenuItem.setActionView(null);
        }
        updateRegisterCounts();
        if (GiziApplication.getInstance().isFRSupported()) GiziApplication.getInstance().refreshFaceData();
        flagActivator();
    }

    final int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static boolean hasPermissions(android.content.Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(GiziApplication.getInstance(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: grantResults="+grantResults);
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        GiziApplication.getInstance().getLocationHelper().getLocation(this,GiziApplication.getInstance().getLocationHelper().locationResult);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    public void updateLocation(){
        Log.e(TAG, "updateLocation: Trying to update location");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            GiziApplication.getInstance().getLocationHelper().getLocation(this,GiziApplication.getInstance().getLocationHelper().locationResult);
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        String gps = String.valueOf(location.getLatitude())+" "+String.valueOf(location.getLongitude());
        preferences.edit().putString("gpsCoordinates", gps).apply();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
