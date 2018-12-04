package org.smartregister.bidan.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.controller.NavigationControllerINA;
import org.smartregister.bidan.facial.repository.ImageRepository;
import org.smartregister.bidan.fragment.KISmartRegisterFragment;
import org.smartregister.bidan.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.bidan.repository.IndonesiaECRepository;
import org.smartregister.bidan.sync.ECSyncUpdater;
import org.smartregister.bidan.sync.UpdateActionsTask;
import org.smartregister.bidan.utils.AllConstantsINA;
import org.smartregister.bidan.utils.Support;
import org.smartregister.bidan.utils.Tools;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.FetchStatus;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.event.Listener;
import org.smartregister.service.PendingFormSubmissionService;
import org.smartregister.bidan.sync.ClientProcessor;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.contract.HomeContext;
import org.smartregister.view.controller.NativeAfterANMDetailsFetchListener;
import org.smartregister.view.controller.NativeUpdateANMDetailsTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.valueOf;
import static org.smartregister.event.Event.ACTION_HANDLED;
import static org.smartregister.event.Event.FORM_SUBMITTED;

//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;

public class BidanHomeActivity extends SecuredActivity implements SyncStatusBroadcastReceiver.SyncStatusListener,LocationListener {
    private static final String TAG = BidanHomeActivity.class.getName();
    //    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    private MenuItem updateMenuItem;
    private MenuItem lastSyncMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private PendingFormSubmissionService pendingFormSubmissionService;
    private IndonesiaECRepository indonesiaECRepository;
    private ImageRepository imageRepository;
    private SyncStatusBroadcastReceiver syncStatusBroadcastReceiver;
    private TextView ecRegisterClientCountView;
    private TextView kartuIbuANCRegisterClientCountView;
    private TextView kartuIbuPNCRegisterClientCountView;
    private TextView anakRegisterClientCountView;
    private TextView kohortKbCountView;
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
    private View.OnClickListener onRegisterStartListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_kartu_ibu_register:
                    KISmartRegisterFragment.criteria = "!";
                    navigationController.startECSmartRegistry();
                    break;

                case R.id.btn_kohort_kb_register:
                    navigationController.startFPSmartRegistry();
                    break;

                case R.id.btn_kartu_ibu_anc_register:
                    navigationController.startANCSmartRegistry();
                    break;

                case R.id.btn_anak_register:
                    navigationController.startChildSmartRegistry();
                    break;

                case R.id.btn_kartu_ibu_pnc_register:
                    navigationController.startPNCSmartRegistry();
                    break;

                default:
                    break;
            }
//            FlurryAgent.logEvent("home_dashboard",Home, true);
        }
    };
    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_reporting:
                    navigationController.startReports();
                    break;
                case R.id.btn_map:
                    Intent intent = new Intent(BidanHomeActivity.this, MapActivity.class);
                    startActivity(intent);
                    break;
                default:
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
        /*FlurryFacade.logEvent("home_dashboard");*/
//        String HomeStart = timer.format(new Date());
//        Map<String, String> Home = new HashMap<>();
//        Home.put("start", HomeStart);
//        FlurryAgent.logEvent("home_dashboard", Home, true);
        preferences = getDefaultSharedPreferences(this);
        locationManager = (LocationManager) getSystemService(android.content.Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);
        if ( provider == null ) {
            provider = LocationManager.GPS_PROVIDER;
        }
        setContentView(R.layout.smart_registers_home_bidan);
        navigationController = new NavigationControllerINA(this, anmController, context());
        setupViews();
        initialize();
        DisplayFormFragment.formInputErrorMessage = getResources().getString(R.string.forminputerror);
        DisplayFormFragment.okMessage = getResources().getString(R.string.okforminputerror);
        //  context.formSubmissionRouter().getHandlerMap().put("census_enrollment_form", new ANChandler());
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        if(!hasPermissions(BidanApplication.getInstance(), PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private void setupViews() {
        findViewById(R.id.btn_kartu_ibu_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kartu_ibu_anc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kartu_ibu_pnc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_anak_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kohort_kb_register).setOnClickListener(onRegisterStartListener);

        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_map).setOnClickListener(onButtonsClickListener);

        ecRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_register_client_count);
        kartuIbuANCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_anc_register_client_count);
        kartuIbuPNCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_pnc_register_client_count);
        anakRegisterClientCountView = (TextView) findViewById(R.id.txt_anak_client_count);
        kohortKbCountView = (TextView) findViewById(R.id.txt_kohort_kb_register_count);
    }

    private void initialize() {
        pendingFormSubmissionService = context().pendingFormSubmissionService();
        indonesiaECRepository = BidanApplication.getInstance().indonesiaECRepository();
        imageRepository = BidanApplication.getInstance().imageRepository();

        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);

        registerMyReceiver();

        //noinspection ConstantConditions
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setIcon(getResources().getDrawable(org.smartregister.bidan.R.mipmap.logo));
        getSupportActionBar().setIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.logo, null));
        getSupportActionBar().setLogo(org.smartregister.bidan.R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        LoginActivity.setLanguage();
//        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_background));
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
//        LoginActivity.setLanguage();
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
                Log.d(TAG, "afterFetch: " + anmDetails);
                SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
                Cursor kiCountCursor = context().commonrepository("ec_kartu_ibu").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_kartu_ibu_search", "ec_kartu_ibu_search.is_closed=0 AND namalengkap != '' AND namalengkap IS NOT NULL"));
                kiCountCursor.moveToFirst();
                int kicount = kiCountCursor.getInt(0);
                kiCountCursor.close();

                Cursor kbCountCursor = context().commonrepository("ec_kartu_ibu").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_kartu_ibu_search", "ec_kartu_ibu_search.is_closed=0 AND jenisKontrasepsi !='0' AND namalengkap != '' AND namalengkap IS NOT NULL"));
                kbCountCursor.moveToFirst();
                int kbcount = kbCountCursor.getInt(0);
                kbCountCursor.close();

                Cursor anccountcursor = context().commonrepository("ec_ibu").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_ibu_search", "ec_ibu_search.is_closed=0 AND namalengkap !='' AND namalengkap IS NOT NULL"));
                anccountcursor.moveToFirst();
                int anccount = anccountcursor.getInt(0);
                anccountcursor.close();

                Cursor pnccountcursor = context().commonrepository("ec_pnc").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_pnc_search", "ec_pnc_search.is_closed=0 AND (ec_pnc_search.keadaanIbu ='hidup' OR ec_pnc_search.keadaanIbu IS NULL) AND namalengkap !='' AND namalengkap IS NOT NULL")); // and ec_pnc_search.keadaanIbu LIKE '%hidup%'
                pnccountcursor.moveToFirst();
                int pnccount = pnccountcursor.getInt(0);
                pnccountcursor.close();

                Cursor childcountcursor = context().commonrepository("anak").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_anak_search", "ec_anak_search.is_closed=0"));
                childcountcursor.moveToFirst();
                int childcount = childcountcursor.getInt(0);
                childcountcursor.close();

                ecRegisterClientCountView.setText(valueOf(kicount));
                kartuIbuANCRegisterClientCountView.setText(valueOf(anccount));
                kartuIbuPNCRegisterClientCountView.setText(valueOf(pnccount));
                anakRegisterClientCountView.setText(valueOf(childcount));
                kohortKbCountView.setText(valueOf(kbcount));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        attachLogoutMenuItem(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateMenuItem = menu.findItem(R.id.updateMenuItem);
        lastSyncMenuItem = menu.findItem(R.id.lastSyncDate);
        remainingFormsToSyncMenuItem = menu.findItem(R.id.remainingFormsToSyncMenuItem);

        updateSyncIndicator();
        updateLastSyncTime();
        updateRemainingFormsToSyncCount();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updateMenuItem:
                updateLocation();
                String isLocal = Utils.getPreference(BidanApplication.getInstance().getApplicationContext(), "LOCAL_DEBUG", "False");
                if (isLocal.equals("True")){
                    loadDummyData();
                    Toast.makeText(this, "You are working in local", LENGTH_SHORT).show();
                    return true;
                }
                updateDataFromServer();
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

                Tools.getDbRecord(context());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadDummyData(){
        final ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(getApplicationContext());
        final String dummy_data = AssetHandler.readFileFromAssetsFolder("dummy_data.json", getApplicationContext());
        try {
            JSONObject jsonObject = new JSONObject(dummy_data);
            ecUpdater.saveAllClientsAndEvents(jsonObject);
            Long now = Calendar.getInstance().getTimeInMillis();
            List<JSONObject> allEvents = ecUpdater.allEvents(0, now);
            Log.d(TAG, "loadDummyData: allEvents="+allEvents);
            ClientProcessor.getInstance(getApplicationContext()).processClient(allEvents);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void updateDataFromServer() {
        FlurryFacade.logEvent("clicked_update_from_server");
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                this, context().actionService(), context().formSubmissionSyncService(),
                new SyncProgressIndicator(), context().allFormVersionSyncService());
        updateActionsTask.updateDataFromServer(new SyncAfterFetchListener());
        String locationJSON = context().anmLocationController().get();
        LocationTree locationTree = EntityUtils.fromJson(locationJSON, LocationTree.class);

//        Map<String,TreeNode<String, Location>> locationMap =
//                locationTree.getLocationsHierarchy();

//        if(LoginActivity.generator.uniqueIdController().needToRefillUniqueId(LoginActivity.generator.UNIQUE_ID_LIMIT))  // unique id part
//            LoginActivity.generator.requestUniqueId();                                                                  // unique id part
    }*/
    public void updateDataFromServer() {
        Log.e("Home", "updateDataFromServer: tombol update");
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(this);
//        FlurryFacade.logEvent("click_update_from_server");
        updateActionsTask.updateFromServer();

//        if (LoginActivity.generator.uniqueIdController().needToRefillUniqueId(LoginActivity.generator.UNIQUE_ID_LIMIT))  // unique id part
//            LoginActivity.generator.requestUniqueId();                                                                  // unique id part

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

        long size = BidanApplication.getInstance().getECRepository().getUnSyncedEventsSize();
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
        if (BidanApplication.getInstance().isFRSupported()) BidanApplication.getInstance().refreshFaceData();
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
                if (ActivityCompat.checkSelfPermission(BidanApplication.getInstance(), permission) != PackageManager.PERMISSION_GRANTED) {
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
                        BidanApplication.getInstance().getLocationHelper().getLocation(this,BidanApplication.getInstance().getLocationHelper().locationResult);
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
            BidanApplication.getInstance().getLocationHelper().getLocation(this,BidanApplication.getInstance().getLocationHelper().locationResult);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
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