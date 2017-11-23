package org.smartregister.bidan2.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import org.joda.time.DateTime;
import org.smartregister.Context;
import org.smartregister.bidan2.BuildConfig;
import org.smartregister.bidan2.R;
import org.smartregister.bidan2.application.BidanApplication;
import org.smartregister.bidan2.utils.BidanConstants;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.TimeStatus;
import org.smartregister.event.Listener;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.util.Utils;
import org.smartregister.view.BackgroundAction;
import org.smartregister.view.LockingBackgroundTask;
import org.smartregister.view.ProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static android.R.attr.password;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;
import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.SUCCESS;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;
import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logVerbose;

public class LoginActivity extends AppCompatActivity {

    private EditText userNameEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;
    public static final String ENGLISH_LOCALE = "en";
    private static final String ENGLISH_LANGUAGE = "English";
    private android.content.Context appContext;
    private static final String BAHASA_LOCALE = "in";
    private static final String BAHASA_LANGUAGE = "Bahasa";
    private static final String LOCALE_LANG = BAHASA_LOCALE;
    private Context context;

//    private RemoteLoginTask remoteLoginTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

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

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.login_logo_bidan);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.black)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        }
        appContext = this;
        context = BidanApplication.getInstance().context();
        //Logo
        ImageView loginglogo = (ImageView)findViewById(R.id.login_logo);
        loginglogo.setImageDrawable(getResources().getDrawable(R.drawable.login_logo_bidan));

//        positionViews();
        initializeLoginFields();
        initializeBuildDetails();
        setDoneActionHandlerOnPasswordField();
        initializeProgressDialog();
        setLanguage();

        if (BuildConfig.DEBUG) {
            debugApp();
        }


    }

    private void debugApp() {
        String uname = getResources().getString(R.string.uname);
        String pwd = getResources().getString(R.string.pwd);

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.login, null);

        if (BidanApplication.getInstance().context().userService().hasARegisteredUser()) {
            localLogin(view, uname, pwd);
        } else {
            remoteLogin(view, uname, pwd);
        }
    }

    private void localLogin(View view, String userName, String password) {
        view.setClickable(true);
        if (getOpenSRPContext().userService().isUserInValidGroup(userName, password)
                && (!BidanConstants.TIME_CHECK || TimeStatus.OK.equals(getOpenSRPContext().userService().validateStoredServerTimeZone()))) {
            localLoginWith(userName, password);

        } else {

            login(findViewById(R.id.login_loginButton), false);
        }

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


    private void localLoginWith(String userName, String password) {
        getOpenSRPContext().userService().localLogin(userName, password);
        goToHome(false);
//        startZScoreIntentService();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
            }
        }).start();
    }

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

//    private void tryRemoteLogin(final String userName, final String password, final Listener<LoginResponse> afterLoginCheck) {
//        if (remoteLoginTask != null && !remoteLoginTask.isCancelled()) {
//            remoteLoginTask.cancel(true);
//        }
//
//        remoteLoginTask = new RemoteLoginTask(userName, password, afterLoginCheck);
//        remoteLoginTask.execute();
//    }

    private void remoteLoginWith(String userName, String password, String userInfo) {
        getOpenSRPContext().userService().remoteLogin(userName, password, userInfo);
        goToHome(true);
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

    private void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            logError("Error hideKeyboard: " + e);
        }
    }

    private void goToHome(boolean remote) {
        if (!remote) {
//            startZScoreIntentService();
//            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        } else {
//            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        }

        Intent intent = new Intent(this, BidanHomeActivity.class);
//        Intent intent = new Intent(this, KMotherSmartRegisterActivity.class);

        // TODO ERROR GET FRAGMENT
//        intent.putExtra(BaseRegisterActivity.IS_REMOTE_LOGIN, remote);
        startActivity(intent);

//        IMDatabaseUtils.accessAssetsAndFillDataBaseForVaccineTypes(this, null);

        finish();
    }

    public static void setLanguage() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(getOpenSRPContext().applicationContext()));
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        if(!preferredLocale.equals(LOCALE_LANG)) {
            switchLanguagePreference();
            preferredLocale = allSharedPreferences.fetchLanguagePreference();
        }
        Resources res = getOpenSRPContext().applicationContext().getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(preferredLocale);
        res.updateConfiguration(conf, dm);

    }

    public static String switchLanguagePreference() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(Context.getInstance().applicationContext()));

        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        if (ENGLISH_LOCALE.equals(preferredLocale)) {
            allSharedPreferences.saveLanguagePreference(BAHASA_LOCALE);
            Resources res = Context.getInstance().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(BAHASA_LOCALE);
            res.updateConfiguration(conf, dm);
            return BAHASA_LANGUAGE;
        } else {
            allSharedPreferences.saveLanguagePreference(ENGLISH_LOCALE);
            Resources res = Context.getInstance().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(ENGLISH_LOCALE);
            res.updateConfiguration(conf, dm);
            return ENGLISH_LANGUAGE;
        }
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.smartregister.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.smartregister.R.string.loggin_in_dialog_message));
    }

    private void setDoneActionHandlerOnPasswordField() {
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    login(findViewById(org.smartregister.R.id.login_loginButton));
                }
                return false;
            }
        });
    }

    private void initializeBuildDetails() {
        TextView buildDetailsTextView = (TextView) findViewById(org.smartregister.R.id.login_build);
        try {
            buildDetailsTextView.setText("Version " + getVersion() + ", Built on: " + getBuildDate());
        } catch (Exception e) {
            logError("Error fetching build details: " + e);
        }
    }

    private void initializeLoginFields() {
        userNameEditText = (EditText) findViewById(org.smartregister.R.id.login_userNameText);
        passwordEditText = (EditText) findViewById(org.smartregister.R.id.login_passwordText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Context getOpenSRPContext() {
        return BidanApplication.getInstance().context();
    }

    public String getVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return packageInfo.versionName;
    }

    public String getBuildDate() {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(BuildConfig.TIMESTAMP));
    }
}
