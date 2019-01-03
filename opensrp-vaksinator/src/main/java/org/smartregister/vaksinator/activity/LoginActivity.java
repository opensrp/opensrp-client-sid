package org.smartregister.vaksinator.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.domain.jsonmapping.util.TeamLocation;
import org.smartregister.event.Listener;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.util.Utils;
import org.smartregister.vaksinator.BuildConfig;
import org.smartregister.vaksinator.R;
import org.smartregister.vaksinator.application.VaksinatorApplication;
import org.smartregister.vaksinator.utils.AllConstantsINA;
import org.smartregister.view.BackgroundAction;
import org.smartregister.view.LockingBackgroundTask;
import org.smartregister.view.ProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;
import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.SUCCESS;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;
import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logVerbose;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Context context = VaksinatorApplication.getInstance().context();
    private EditText userNameEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;
    public static final String ENGLISH_LOCALE = "en";
    public static final String BAHASA_LOCALE = "in";
    public static final String ENGLISH_LANGUAGE = "English";
    public static final String BAHASA_LANGUAGE = "Bahasa";
    public static final String PREF_TEAM_LOCATIONS = "PREF_TEAM_LOCATIONS";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logVerbose("Initializing ...");

        try{
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(this));
            String preferredLocale = allSharedPreferences.fetchLanguagePreference();
            Resources res = getOpenSRPContext().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(preferredLocale);
            res.updateConfiguration(conf, dm);
        } catch(Exception e){
            android.util.Log.e(TAG, "onCreate: "+ e.getCause() );
        }
        setContentView(org.smartregister.R.layout.login);

        positionViews();
        context = Context.getInstance().updateApplicationContext(this.getApplicationContext());
        initializeLoginFields();
        initializeBuildDetails();
        setDoneActionHandlerOnPasswordField();
        initializeProgressDialog();
        setLanguage();

        debugApp();

    }

    private void debugApp() {
        String uname = "demo_vaksin3", pwd = "Demo@123";
        android.util.Log.e(TAG, "debugApp: uname="+uname+", pwd="+pwd);

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.login, null);
        if (context.userService().hasARegisteredUser()){
            localLoginWith(uname, pwd);
            //localLogin(view, uname, pwd);
        } else {
            remoteLogin(view, uname, pwd);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onResume() {
        super.onResume();

        if (!getOpenSRPContext().IsUserLoggedOut()) {
            goToHome();
        }

        if (BuildConfig.DEBUG) {
            if (getOpenSRPContext().userService().hasARegisteredUser()) {
                userNameEditText.setText(context.allSharedPreferences().fetchRegisteredANM());
                userNameEditText.setEnabled(false);
            }
        }
    }

    public static Context getOpenSRPContext() {
        return VaksinatorApplication.getInstance().context();
    }

    private void positionViews() {
        ImageView loginglogo = findViewById(R.id.login_logo);
        loginglogo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.opensrp_indonesia_vaccine_logo, null));
        ResourcesCompat.getDrawable(getResources(), R.drawable.opensrp_indonesia_vaccine_logo, null);
//        getActionBar().setTitle("");
//        getActionBar().setIcon(getResources().getDrawable(R.mipmap.logo));
//        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_background));

    }

    public String getUserDefaultLocationId(String userInfo) {
        android.util.Log.e(TAG, "getUserDefaultLocationId: "+ userInfo );
        try {
            JSONObject userLocationJSON = new JSONObject(userInfo);
            return userLocationJSON
                    .getJSONObject(AllConstantsINA.SyncFilters.FILTER_TEAM)
                    .getJSONArray(AllConstantsINA.SyncFilters.FILTER_LOCATION_ID)
                    .getJSONObject(0)
                    .getString("name");

        } catch (JSONException e) {
            android.util.Log.v("Error : ", e.getMessage());
        }

        return null;
    }

    public static String switchLanguagePreference() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(Context.getInstance().applicationContext()));
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        Resources res = Context.getInstance().applicationContext().getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();

        if (ENGLISH_LOCALE.equals(preferredLocale)) {
            allSharedPreferences.saveLanguagePreference(BAHASA_LOCALE);
            conf.locale = new Locale(BAHASA_LOCALE);
            res.updateConfiguration(conf, dm);
            return BAHASA_LANGUAGE;

        } else {
            allSharedPreferences.saveLanguagePreference(ENGLISH_LOCALE);
//            Resources res = Context.getInstance().applicationContext().getResources();
            // Change locale settings in the app.
//            DisplayMetrics dm = res.getDisplayMetrics();
//            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(ENGLISH_LOCALE);
            res.updateConfiguration(conf, dm);
            return ENGLISH_LANGUAGE;
        }
    }

    protected String getVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return packageInfo.versionName;
    }

    private String getBuildDate() {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(BuildConfig.BUILD_TIMESTAMP));
    }

    public static void setLanguage() {

        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(Context.getInstance().applicationContext()));
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();

        Resources res = Context.getInstance().applicationContext().getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
//        conf.locale = new Locale(BAHASA_LOCALE);
        conf.locale = new Locale(preferredLocale);
        res.updateConfiguration(conf, dm);
        android.util.Log.e(TAG, "setLanguage: " + res.getConfiguration().locale.toString());

    }

    private void initializeLoginFields() {
        userNameEditText = findViewById(org.smartregister.R.id.login_userNameText);
        userNameEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        passwordEditText = findViewById(org.smartregister.R.id.login_passwordText);
        passwordEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
    }

    private void initializeBuildDetails() {
        TextView buildDetailsTextView = findViewById(org.smartregister.R.id.login_build);
        try {
            buildDetailsTextView.setText("Version " + getVersion() + ", Built on: " + getBuildDate());
        } catch (Exception e) {
            logError("Error fetching build details: " + e);
        }
    }

    private void setDoneActionHandlerOnPasswordField() {
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login(findViewById(org.smartregister.R.id.login_loginButton));
                }
                return false;
            }
        });
    }

    public void login(final View view) {
//        Log.e(TAG, "login: "+ getOpenSRPContext().userService().hasARegisteredUser() );
        login(view, !getOpenSRPContext().allSharedPreferences().fetchForceRemoteLogin());
    }

    private void login(final View view, boolean localLogin) {
        hideKeyboard();
        view.setClickable(false);

        final String userName = userNameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

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
    }

    private void localLogin(View view, String userName, String password) {
        if (getOpenSRPContext().userService().isUserInValidGroup(userName, password)) {
            localLoginWith(userName, password);

        } else {
            showErrorDialog(getString(org.smartregister.R.string.login_failed_dialog_message));
            view.setClickable(true);
        }
    }

    private void remoteLogin(final View view, final String userName, final String password) {
        tryRemoteLogin(userName, password, new Listener<LoginResponse>() {
            public void onEvent(LoginResponse loginResponse) {
//                ErrorReportingFacade.setUsername("", userName);
//                FlurryAgent.setUserId(userName);
                if (loginResponse == SUCCESS) {
                    remoteLoginWith(userName, password, loginResponse.payload());
                    getOpenSRPContext().allSharedPreferences().saveForceRemoteLogin(false);

                } else {
                    if (loginResponse == null) {
                        showErrorDialog("Login failed. Unknown reason. Try Again");
                    } else {
                        android.util.Log.e(TAG, "onEvent: "+ loginResponse.message() );
                        if (loginResponse == NO_INTERNET_CONNECTIVITY) {
                            showErrorDialog(getResources().getString(R.string.no_internet_connectivity));
                        } else if (loginResponse == UNKNOWN_RESPONSE) {
                            showErrorDialog(getResources().getString(R.string.unknown_response));
                        } else if (loginResponse == UNAUTHORIZED) {
                            showErrorDialog(getResources().getString(R.string.unauthorized));
                        }
//                        showErrorDialog(loginResponse.message());
                    }
                    view.setClickable(true);
                }
            }
        });

/*        tryGetUniqueId(userName, password, new Listener<ResponseStatus>() {
            @Override
            public void onEvent(ResponseStatus data) {
                if (data == ResponseStatus.failure) {
                    logError("failed to fetch unique id");
                }
                goToHome();
            }
        });*/
    }

    private void tryRemoteLogin(final String userName, final String password, final Listener<LoginResponse> afterLoginCheck) {
        LockingBackgroundTask task = new LockingBackgroundTask(new ProgressIndicator() {
            @Override
            public void setVisible() {
                progressDialog.show();
            }

            @Override
            public void setInvisible() {
                progressDialog.dismiss();
            }
        });

        task.doActionInBackground(new BackgroundAction<LoginResponse>() {
            public LoginResponse actionToDoInBackgroundThread() {
                return context.userService().isValidRemoteLogin(userName, password);
            }

            public void postExecuteInUIThread(LoginResponse result) {
                afterLoginCheck.onEvent(result);
            }
        });
    }

    private void remoteLoginWith(String userName, String password, LoginResponseData userInfo) {
        context.userService().remoteLogin(userName, password, userInfo);

        Iterator<TeamLocation> teamLocationIterator = userInfo.team.location.iterator();
        TeamLocation teamLocation = teamLocationIterator.next();

        android.util.Log.e(TAG, "remoteLoginWith: location "+ teamLocation.uuid );

        Utils.writePreference(VaksinatorApplication.getInstance().getApplicationContext(), PREF_TEAM_LOCATIONS, teamLocation.uuid);

        setDefaultLocationId(userName, teamLocation.uuid);
        goToHome();
        DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
    }

    private void localLoginWith(String userName, String password) {
        context.userService().localLogin(userName, password);
//        LoginActivity.generator = new Generator(context, userName, password);
        goToHome();
        DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
    }

    private void showErrorDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.login_failed_dialog_title))
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .create();
        dialog.show();
    }

    private void goToHome() {
        startActivity(new Intent(this, VaksinatorHomeActivity.class));
        finish();
    }

    public void setDefaultLocationId(String userName, String locationId) {
        if (userName != null) {
            context.userService().getAllSharedPreferences().savePreference(userName + "-locationid", locationId);
        }
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.smartregister.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.smartregister.R.string.loggin_in_dialog_message));
    }

}
