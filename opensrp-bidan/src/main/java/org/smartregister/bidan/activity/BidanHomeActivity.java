package org.smartregister.bidan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.smartregister.bidan.R;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.controller.NavigationControllerINA;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.controller.ANMController;
import org.smartregister.view.controller.NavigationController;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

import static android.widget.Toast.LENGTH_SHORT;
import static org.smartregister.event.Event.FORM_SUBMITTED;
import static org.smartregister.event.Event.SYNC_COMPLETED;
import static org.smartregister.event.Event.SYNC_STARTED;

/**
 * Created by sid-tech on 11/7/17.
 */

public class BidanHomeActivity extends SecuredActivity {

    @Bind(R.id.view_pager)
    protected OpenSRPViewPager mPager;

    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private TextView ecRegisterClientCountView;
    private TextView kartuIbuANCRegisterClientCountView;
    private TextView kartuIbuPNCRegisterClientCountView;
    private TextView anakRegisterClientCountView;
    private TextView kohortKbCountView;
    protected NavigationController navigationController;
    protected ANMController anmController;

    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // BaseActivity
//    @Override
//    protected int getContentView() {
//        return R.layout.bidan_landing;
//    }

//    @Override
//    protected int getDrawerLayoutId() {
//        return R.id.drawer_layout;
//    }
//
//    @Override
//    protected int getToolbarId() {
//        return LocationSwitcherToolbar.TOOLBAR_ID;
//    }

//    @Override
//    protected Class onBackActivity() {
//        return null;
//    }
//

    @Override
    protected void onPause() {
    super.onPause();
}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreation() {
        setContentView(R.layout.bidan_landing);
        navigationController = new NavigationControllerINA(this, anmController, BidanApplication.getInstance().context());

        String HomeStart = timer.format(new Date());
        Map<String, String> Home = new HashMap<>();
        Home.put("start", HomeStart);
//        FlurryAgent.logEvent("home_dashboard", Home, true);

        setupViews();
        initGui();
    }

    @Override
    protected void onResumption() {
//        LoginActivity.setLanguage();
//        updateRegisterCounts();
//        updateSyncIndicator();
//        updateRemainingFormsToSyncCount();
//        initFR();
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
        remainingFormsToSyncMenuItem = menu.findItem(R.id.remainingFormsToSyncMenuItem);

//        updateSyncIndicator();
//        updateRemainingFormsToSyncCount();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updateMenuItem:
//                updateFromServer();
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
                }catch (org.json.JSONException e){
                    anmID = "undefined";
                }
                Toast.makeText(this, String.format("%s current user = %s",context().getStringResource(R.string.app_name),anmID), LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initGui() {
//        pendingFormSubmissionService = context().pendingFormSubmissionService();
//        SYNC_STARTED.addListener(onSyncStartListener);
//        SYNC_COMPLETED.addListener(onSyncCompleteListener);
//        FORM_SUBMITTED.addListener(onFormSubmittedListener);
//        ACTION_HANDLED.addListener(updateANMDetailsListener);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(R.mipmap.logo));
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        LoginActivity.setLanguage();
    }

    private void setupViews() {
        findViewById(R.id.btn_kartu_ibu_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kartu_ibu_anc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kartu_ibu_pnc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_anak_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kohort_kb_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
//        findViewById(R.id.btn_videos).setOnClickListener(onButtonsClickListener);

        ecRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_register_client_count);
        kartuIbuANCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_anc_register_client_count);
        kartuIbuPNCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_pnc_register_client_count);
        anakRegisterClientCountView = (TextView) findViewById(R.id.txt_anak_client_count);
        kohortKbCountView = (TextView) findViewById(R.id.txt_kohort_kb_register_count);
    }

    private View.OnClickListener onRegisterStartListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_kartu_ibu_register:
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
            }
            String HomeEnd = timer.format(new Date());
            Map<String, String> Home = new HashMap<String, String>();
            Home.put("end", HomeEnd);
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

//                case R.id.btn_videos:
//                    navigationController.startVideos();
//                    break;
            }
        }
    };

}
