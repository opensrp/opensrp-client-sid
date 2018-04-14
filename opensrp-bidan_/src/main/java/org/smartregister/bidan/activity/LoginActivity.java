package org.smartregister.bidan.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
<<<<<<< HEAD:opensrp-vaksinator/src/main/java/org/smartregister/vaksinator/activity/LoginActivity.java
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
=======
import android.support.v4.content.res.ResourcesCompat;
>>>>>>> issue4:opensrp-bidan/src/main/java/org/smartregister/bidan/activity/LoginActivity.java
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
<<<<<<< HEAD:opensrp-vaksinator/src/main/java/org/smartregister/vaksinator/activity/LoginActivity.java
import org.smartregister.vaksinator.R;
=======
import org.smartregister.bidan.R;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.utils.AllConstantsINA;
>>>>>>> issue4:opensrp-bidan/src/main/java/org/smartregister/bidan/activity/LoginActivity.java
import org.smartregister.domain.LoginResponse;
import org.smartregister.event.Listener;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.DrishtiSyncScheduler;
<<<<<<< HEAD:opensrp-vaksinator/src/main/java/org/smartregister/vaksinator/activity/LoginActivity.java
import org.smartregister.util.Log;
import org.smartregister.util.Utils;
import org.smartregister.vaksinator.application.VaksinatorApplication;
import org.smartregister.vaksinator.utils.Config;
=======
>>>>>>> issue4:opensrp-bidan/src/main/java/org/smartregister/bidan/activity/LoginActivity.java
import org.smartregister.view.BackgroundAction;
import org.smartregister.view.LockingBackgroundTask;
import org.smartregister.view.ProgressIndicator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

<<<<<<< HEAD:opensrp-vaksinator/src/main/java/org/smartregister/vaksinator/activity/LoginActivity.java
//import io.fabric.sdk.android.Fabric;

=======
>>>>>>> issue4:opensrp-bidan/src/main/java/org/smartregister/bidan/activity/LoginActivity.java
import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;
import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.SUCCESS;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;
import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logVerbose;

public class LoginActivity extends Activity {
    public static final String ENGLISH_LOCALE = "en";
    public static final String BAHASA_LOCALE = "in";
    public static final String ENGLISH_LANGUAGE = "English";
<<<<<<< HEAD:opensrp-vaksinator/src/main/java/org/smartregister/vaksinator/activity/LoginActivity.java
    public static final String KANNADA_LANGUAGE = "Kannada";
    public static final String Bengali_LANGUAGE = "Bengali";
    public static final String Bahasa_LANGUAGE = "Bahasa";
    //   public static Generator generator;
    public static final String PREF_TEAM_LOCATIONS = "PREF_TEAM_LOCATIONS";
=======
    public static final String BAHASA_LANGUAGE = "Bahasa";
    public static final String PREF_TEAM_LOCATIONS = "PREF_TEAM_LOCATIONS";
    private static final String TAG = LoginActivity.class.getName();
    private Context context = BidanApplication.getInstance().context();
    private EditText userNameEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;

    public static Context getOpenSRPContext() {
        return BidanApplication.getInstance().context();
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
        Log.e(TAG, "setLanguage: " + res.getConfiguration().locale.toString());

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

>>>>>>> issue4:opensrp-bidan/src/main/java/org/smartregister/bidan/activity/LoginActivity.java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logVerbose("Initializing ...");
        try {
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(this));
            String preferredLocale = allSharedPreferences.fetchLanguagePreference();
            Resources res = Context.getInstance().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(preferredLocale);
            res.updateConfiguration(conf, dm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(org.smartregister.R.layout.login);
        ImageView loginglogo = (ImageView) findViewById(R.id.login_logo);
//        loginglogo.setImageDrawable(getResources().getDrawable(R.drawable.login_logo_bidan));
        loginglogo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.login_logo_bidan, null));
//        ContextCompat.getDrawable(getActivity(), R.drawable.login_logo_bidan);
        ResourcesCompat.getDrawable(getResources(), R.drawable.login_logo_bidan, null);

        context = Context.getInstance().updateApplicationContext(this.getApplicationContext());
//        context = BidanApplication.getInstance().context();;

        initializeLoginFields();
        initializeBuildDetails();
        setDoneActionHandlerOnPasswordField();
        initializeProgressDialog();
<<<<<<< HEAD:opensrp-vaksinator/src/main/java/org/smartregister/vaksinator/activity/LoginActivity.java
        setLanguage();

          debugApp();

    }

    private void debugApp() {
        Config config = new Config();
        String uname = "demo_ec_vaksin", pwd = "Satu2345";
        try {
            uname = config.getCredential("uname", getApplicationContext());
            pwd =  config.getCredential("pwd", getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.login, null);
        if (context.userService().hasARegisteredUser()){
            localLoginWith(uname, pwd);
            //localLogin(view, uname, pwd);
        } else {
            remoteLogin(view, uname, pwd);
        }
    }

    private void positionViews() {
        ImageView loginglogo = (ImageView)findViewById(R.id.login_logo);
        loginglogo.setImageDrawable(getResources().getDrawable(R.mipmap.login_logo));
        context = Context.getInstance().updateApplicationContext(this.getApplicationContext());
//        getActionBar().setTitle("");
=======
        getActionBar().setTitle("");
>>>>>>> issue4:opensrp-bidan/src/main/java/org/smartregister/bidan/activity/LoginActivity.java
//        getActionBar().setIcon(getResources().getDrawable(R.mipmap.logo));
        getActionBar().setIcon(ResourcesCompat.getDrawable(getResources(), R.mipmap.logo, null));
//        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_background));
        getActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.color.action_bar_background, null));
        setLanguage();

//        debugApp();
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

    private void initializeLoginFields() {
        userNameEditText = ((EditText) findViewById(org.smartregister.R.id.login_userNameText));
        userNameEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        passwordEditText = ((EditText) findViewById(org.smartregister.R.id.login_passwordText));
        passwordEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
    }

    private void initializeBuildDetails() {
        TextView buildDetailsTextView = (TextView) findViewById(org.smartregister.R.id.login_build);
        try {
            buildDetailsTextView.setText("Version " + getVersion() + ", Built on: " + getBuildDate());
        } catch (Exception e) {
            logError("Error fetching build details: " + e);
        }
    }

    private String getVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return packageInfo.versionName;
    }

    private String getBuildDate() throws PackageManager.NameNotFoundException, IOException {
        ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
        ZipFile zf = new ZipFile(applicationInfo.sourceDir);
        ZipEntry ze = zf.getEntry("classes.dex");
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new java.util.Date(ze.getTime()));
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
        hideKeyboard();
        view.setClickable(false);

        final String userName = userNameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (context.userService().hasARegisteredUser()) {
            android.util.Log.e(TAG, "login: lokal ");
            localLogin(view, userName, password);
        } else {
            android.util.Log.e(TAG, "login: remote ");
            remoteLogin(view, userName, password);
        }
    }

<<<<<<< HEAD:opensrp-vaksinator/src/main/java/org/smartregister/vaksinator/activity/LoginActivity.java
    private void initializeLoginFields() {
        userNameEditText = ((EditText) findViewById(org.smartregister.R.id.login_userNameText));
        userNameEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        // userNameEditText.setText("demo_ec");
        passwordEditText = ((EditText) findViewById(org.smartregister.R.id.login_passwordText));
        passwordEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        //  passwordEditText.setText("Satu2345");
    }

    private void setDoneActionHandlerOnPasswordField() {
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login(findViewById(R.id.login_loginButton));
                }
                return false;
            }
        });
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.smartregister.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.smartregister.R.string.loggin_in_dialog_message));
    }

=======
>>>>>>> issue4:opensrp-bidan/src/main/java/org/smartregister/bidan/activity/LoginActivity.java
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
                } else {
                    if (loginResponse == null) {
                        showErrorDialog("Login failed. Unknown reason. Try Again");
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

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
    }

    private void localLoginWith(String userName, String password) {
        context.userService().localLogin(userName, password);
//        LoginActivity.generator = new Generator(context, userName, password);
        goToHome();
        DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
    }

    private void showErrorDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.login_failed_dialog_title))
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        dialog.show();
    }

    private void goToHome() {
        startActivity(new Intent(this, BidanHomeActivity.class));
//        startActivity(new Intent(this, FPSmartRegisterActivity.class));
        finish();
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

<<<<<<< HEAD:opensrp-vaksinator/src/main/java/org/smartregister/vaksinator/activity/LoginActivity.java
    private void fillUserIfExists() {
        if (context.userService().hasARegisteredUser()) {
            userNameEditText.setText(context.allSharedPreferences().fetchRegisteredANM());
            userNameEditText.setEnabled(false);
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
    }

    private void localLoginWith(String userName, String password) {
        context.userService().localLogin(userName, password);
        //  LoginActivity.generator = new Generator(context, userName, password);
        goToHome();
        //DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
    }

=======
>>>>>>> issue4:opensrp-bidan/src/main/java/org/smartregister/bidan/activity/LoginActivity.java
    private void remoteLoginWith(String userName, String password, String userInfo) {
        context.userService().remoteLogin(userName, password, userInfo);
        // LoginActivity.generator = new Generator(context, userName, password);
        String locationId = getUserDefaultLocationId(userInfo);
<<<<<<< HEAD:opensrp-vaksinator/src/main/java/org/smartregister/vaksinator/activity/LoginActivity.java
        Utils.writePreference(VaksinatorApplication.getInstance().getApplicationContext(), PREF_TEAM_LOCATIONS, locationId);

        saveDefaultLocationId(userName,locationId);
        // LoginActivity.generator = new Generator(context, userName, password);
        goToHome();
        String locations = Utils.getPreference(VaksinatorApplication.getInstance().getApplicationContext(), PREF_TEAM_LOCATIONS, "");
        Log.logInfo("USERINFO"+locations);
        //DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
    }


    private void goToHome() {
        startActivity(new Intent(this, VaksinatorHomeActivity.class));
        finish();
    }

    private String getVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return packageInfo.versionName;
=======
        setDefaultLocationId(userName, locationId);
        goToHome();
        DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
>>>>>>> issue4:opensrp-bidan/src/main/java/org/smartregister/bidan/activity/LoginActivity.java
    }

    public void setDefaultLocationId(String userName, String locationId) {
        if (userName != null) {
            context.userService().getAllSharedPreferences().savePreference(userName + "-locationid", locationId);
        }
    }

    public String getUserDefaultLocationId(String userInfo) {
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

<<<<<<< HEAD:opensrp-vaksinator/src/main/java/org/smartregister/vaksinator/activity/LoginActivity.java
    public void saveDefaultLocationId(String userName, String locationId) {
        if (userName != null) {
            context.userService().getAllSharedPreferences().savePreference(userName + "-locationid", locationId);
            Log.logInfo("LOKASI "+locationId);
        }
    }

   /* private void tryGetUniqueId(final String username, final String password, final Listener<ResponseStatus> afterGetUniqueId) {
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
=======
    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.smartregister.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.smartregister.R.string.loggin_in_dialog_message));
    }

    private void debugApp() {
        //    String uname = getResources().getString(R.string.uname);
        //   String pwd = getResources().getString(R.string.pwd);
        String uname = "demo_ec";
        String pwd = "Satu2345";
>>>>>>> issue4:opensrp-bidan/src/main/java/org/smartregister/bidan/activity/LoginActivity.java

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.login, null);

<<<<<<< HEAD:opensrp-vaksinator/src/main/java/org/smartregister/vaksinator/activity/LoginActivity.java
            @Override
            public void postExecuteInUIThread(ResponseStatus result) {
                afterGetUniqueId.onEvent(result);
            }
        });
    }*/
=======
        if (getOpenSRPContext().userService().hasARegisteredUser()) {
            localLogin(view, uname, pwd);
        } else {
            remoteLogin(view, uname, pwd);
        }
    }
>>>>>>> issue4:opensrp-bidan/src/main/java/org/smartregister/bidan/activity/LoginActivity.java

}
