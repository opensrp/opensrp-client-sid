package org.smartregister.bidan.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.bidan.BuildConfig;
import org.smartregister.bidan.R;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.utils.BidanConstants;
import org.smartregister.bidan.utils.Config;
import org.smartregister.bidan.view.LocationPickerView;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.TimeStatus;
import org.smartregister.event.Listener;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;
import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.SUCCESS;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;
import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logVerbose;

/**
 * Created by sid-tech on 11/13/17.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();
    private EditText userNameEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;
    public static final String ENGLISH_LOCALE = "en";
    private static final String URDU_LOCALE = "ur";
    private static final String ENGLISH_LANGUAGE = "English";
    private static final String URDU_LANGUAGE = "Urdu";
    private android.content.Context appContext;
    private RemoteLoginTask remoteLoginTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logVerbose("Initializing ...");
        try {
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(this));
            String preferredLocale = allSharedPreferences.fetchLanguagePreference();
            Resources res = getOpenSRPContext().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = new Locale(preferredLocale);
            res.updateConfiguration(conf, dm);
        } catch (Exception e) {
            logError("Error onCreate: " + e);

        }

        setContentView(R.layout.login);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.black)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        }
        appContext = this;
//        positionViews();
//        initializeLoginFields();
//        initializeBuildDetails();
//        setDoneActionHandlerOnPasswordField();
        initializeProgressDialog();
//        setLanguage();
        if (BuildConfig.DEBUG) {
            debugApp();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!getOpenSRPContext().IsUserLoggedOut()) {
            goToHome(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add("Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase("Settings")) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.smartregister.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.smartregister.R.string.loggin_in_dialog_message));
    }

    private void debugApp() {
        Config config = new Config();
        String uname = null, pwd = null;
        try {
            uname = config.getCredential("uname", getApplicationContext());
            pwd = config.getCredential("pwd", getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.login, null);
        if (BidanApplication.getInstance().context().userService().hasARegisteredUser()) {
            localLogin(view, uname, pwd);
        } else {
            remoteLogin(view, uname, pwd);
        }
    }

    public static Context getOpenSRPContext() {
        return BidanApplication.getInstance().context();
    }

    private void localLogin(View view, String userName, String password) {
        view.setClickable(true);
        if (getOpenSRPContext().userService().isUserInValidGroup(userName, password)
                && (!BidanConstants.TIME_CHECK || TimeStatus.OK.equals(getOpenSRPContext().userService().validateStoredServerTimeZone()))) {
            localLoginWith(userName, password);
        } else {

            login(findViewById(org.smartregister.R.id.login_loginButton), false);
        }
    }

    private void localLoginWith(String userName, String password) {
        getOpenSRPContext().userService().localLogin(userName, password);
        goToHome(false);
//        startZScoreIntentService();
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.util.Log.i(getClass().getName(), "Starting DrishtiSyncScheduler " + DateTime.now().toString());
                DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
                android.util.Log.i(getClass().getName(), "Started DrishtiSyncScheduler " + DateTime.now().toString());
            }
        }).start();
    }

    private void login(final View view, boolean localLogin) {
        android.util.Log.i(getClass().getName(), "Hiding Keyboard " + DateTime.now().toString());
        hideKeyboard();
        view.setClickable(false);

        final String userName = userNameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            if (localLogin) {
                localLogin(view, userName, password);
            } else {
                remoteLogin(view, userName, password);
            }
        } else {
            showErrorDialog(getResources().getString(R.string.unauthorized));
            view.setClickable(true);
        }

        android.util.Log.i(getClass().getName(), "Login result finished " + DateTime.now().toString());
    }

    private void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            logError("Error hideKeyboard: " + e);
        }
    }

    private void goToHome(boolean remote) {
        android.util.Log.e(TAG, "goToHome: from "+ (remote? "remote" : "lokal") );
        if (!remote) {
//            startZScoreIntentService();
            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        } else {
            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        }
        BidanApplication.setCrashlyticsUser(getOpenSRPContext());
        Intent intent = new Intent(this, BidanHomeActivity.class);
//        Intent intent = new Intent(this, KIbuSmartRegisterActivity.class);
        intent.putExtra(BaseRegisterActivity.IS_REMOTE_LOGIN, remote);
        startActivity(intent);

//        IMDatabaseUtils.accessAssetsAndFillDataBaseForVaccineTypes(this, null);

        finish();
    }


    // REMOTE LOGIN

    /**
     *
     * @param view
     * @param userName
     * @param password
     */
    private void remoteLogin(final View view, final String userName, final String password) {

        if (!getOpenSRPContext().allSharedPreferences().fetchBaseURL("").isEmpty()) {
            tryRemoteLogin(userName, password, new Listener<LoginResponse>() {
                public void onEvent(LoginResponse loginResponse) {
                    view.setClickable(true);
                    if (loginResponse == SUCCESS) {
                        if (getOpenSRPContext().userService().isUserInPioneerGroup(userName)) {
                            TimeStatus timeStatus = getOpenSRPContext().userService().validateDeviceTime(
                                    loginResponse.payload(), BidanConstants.MAX_SERVER_TIME_DIFFERENCE);
                            if (!BidanConstants.TIME_CHECK || timeStatus.equals(TimeStatus.OK)) {
                                remoteLoginWith(userName, password, loginResponse.payload());
                                // Get Unique Id
//                                Intent intent = new Intent(appContext, PullUniqueIdsIntentService.class);
//                                appContext.startService(intent);

                            } else {
                                if (timeStatus.equals(TimeStatus.TIMEZONE_MISMATCH)) {
                                    TimeZone serverTimeZone = getOpenSRPContext().userService()
                                            .getServerTimeZone(loginResponse.payload());
                                    showErrorDialog(getString(timeStatus.getMessage(),
                                            serverTimeZone.getDisplayName()));
                                } else {
                                    showErrorDialog(getString(timeStatus.getMessage()));
                                }
                            }
                        } else { // Valid user from wrong group trying to log in
                            showErrorDialog(getResources().getString(R.string.unauthorized_group));
                        }
                    } else {
                        if (loginResponse == null) {
                            showErrorDialog("Sorry, your login failed. Please try again.");
                        } else {
                            if (loginResponse == NO_INTERNET_CONNECTIVITY) {
                                showErrorDialog(getResources().getString(R.string.no_internet_connectivity));
                            } else if (loginResponse == UNKNOWN_RESPONSE) {
                                showErrorDialog(getResources().getString(R.string.unknown_response));
                            } else if (loginResponse == UNAUTHORIZED) {
                                showErrorDialog(getResources().getString(R.string.unauthorized));
                            }
//                        showErrorDialog(loginResponse.message());
                        }
                    }
                }
            });

        } else {
            view.setClickable(true);
            showErrorDialog("OpenSRP Base URL is missing. Please add it in Settings and try again");

        }
    }

    private void tryRemoteLogin(final String userName, final String password, final Listener<LoginResponse> afterLoginCheck) {
        if (remoteLoginTask != null && !remoteLoginTask.isCancelled()) {
            remoteLoginTask.cancel(true);
        }

        remoteLoginTask = new RemoteLoginTask(userName, password, afterLoginCheck);
        remoteLoginTask.execute();
    }

    private void remoteLoginWith(String userName, String password, String userInfo) {
        getOpenSRPContext().userService().remoteLogin(userName, password, userInfo);
        goToHome(true);
        DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
    }

    private void showErrorDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(org.smartregister.R.string.login_failed_dialog_title))
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        dialog.show();
    }

    private void extractLocations(ArrayList<String> locationList, JSONObject rawLocationData) throws JSONException {

        final String NODE = "node";
        final String CHILDREN = "children";
//        String name = rawLocationData.getJSONObject(NODE).getString("locationId");
        String name = rawLocationData.getJSONObject(NODE).getString("name");
        String level = rawLocationData.getJSONObject(NODE).getJSONArray("tags").getString(0);

        if (LocationPickerView.ALLOWED_LEVELS.contains(level)) {
            locationList.add(name);
        }

        if (rawLocationData.has(CHILDREN)) {
            Iterator<String> childIterator = rawLocationData.getJSONObject(CHILDREN).keys();
            while (childIterator.hasNext()) {

                String curChildKey = childIterator.next();
                extractLocations(locationList, rawLocationData.getJSONObject(CHILDREN).getJSONObject(curChildKey));
            }
        } else {
            android.util.Log.e(TAG, "extractLocations: NO CHILDREN " );
        }

    }

//    Inner Class

    private class RemoteLoginTask extends AsyncTask<Void, Void, LoginResponse> {
        private final String userName;
        private final String password;
        private final Listener<LoginResponse> afterLoginCheck;

        private RemoteLoginTask(String userName, String password, Listener<LoginResponse> afterLoginCheck) {
            this.userName = userName;
            this.password = password;
            this.afterLoginCheck = afterLoginCheck;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected LoginResponse doInBackground(Void... params) {
            return getOpenSRPContext().userService().isValidRemoteLogin(userName, password);
        }

        @Override
        protected void onPostExecute(LoginResponse loginResponse) {
            super.onPostExecute(loginResponse);
            if (!isDestroyed()) {
                progressDialog.dismiss();
                afterLoginCheck.onEvent(loginResponse);
            }
        }
    }

    private class SaveTeamLocationsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> locationsCSV = locationsCSV();

            android.util.Log.e(TAG, "doInBackground: "+ locationsCSV.toString() );
            if (locationsCSV.isEmpty()) {
                android.util.Log.e(TAG, "doInBackground: locationCSV empty" );
                return null;
            }

            android.util.Log.e(TAG, "doInBackground: value "+ StringUtils.join(locationsCSV, ",") );

            Utils.writePreference(BidanApplication.getInstance().getApplicationContext(), LocationPickerView.PREF_VILLAGE_LOCATIONS, StringUtils.join(locationsCSV, ","));
            return null;
        }

        // TODO Solve Get Location failed
        public ArrayList<String> locationsCSV() {
            final String LOCATIONS_HIERARCHY = "locationsHierarchy";
            final String MAP = "map";
            JSONObject locationData;
            ArrayList<String> locations = new ArrayList<>();

            try {
                locationData = new JSONObject(BidanApplication.getInstance().context().anmLocationController().get());
                if (locationData.has(LOCATIONS_HIERARCHY) && locationData.getJSONObject(LOCATIONS_HIERARCHY).has(MAP)) {
                    JSONObject map = locationData.getJSONObject(LOCATIONS_HIERARCHY).getJSONObject(MAP);
                    Iterator<String> keys = map.keys();
                    while (keys.hasNext()) {
                        String curKey = keys.next();
                        extractLocations(locations, map.getJSONObject(curKey));
                    }
                } else {
                    android.util.Log.e(TAG, "locationsCSV: Not Found" );
                }
            } catch (Exception e) {
                android.util.Log.e(getClass().getCanonicalName(), android.util.Log.getStackTraceString(e));
            }
            android.util.Log.e(TAG, "locationsCSV: "+ locations );
            return locations;
        }
    }

}
