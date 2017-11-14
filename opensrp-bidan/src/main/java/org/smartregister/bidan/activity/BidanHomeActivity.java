package org.smartregister.bidan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.smartregister.bidan.R;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.controller.NavigationControllerINA;
import org.smartregister.view.controller.ANMController;
import org.smartregister.view.controller.NavigationController;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by sid-tech on 11/13/17.
 */

public class BidanHomeActivity extends AppCompatActivity {

    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.smart_registers_home_bidan);
        navigationController = new NavigationControllerINA(this, anmController, BidanApplication.getInstance().context());
        setupViews();

    }

    protected void onCreation() {
        /*FlurryFacade.logEvent("home_dashboard");*/
//        String HomeStart = timer.format(new Date());
//        Map<String, String> Home = new HashMap<String, String>();
//        Home.put("start", HomeStart);
//        FlurryAgent.logEvent("home_dashboard", Home, true);

//        setContentView(R.layout.smart_registers_home_bidan);

//        navigationController = new NavigationControllerINA(this, anmController, context());
//        setupViews();
//        initialize();
//        DisplayFormFragment.formInputErrorMessage = getResources().getString(R.string.forminputerror);
//        DisplayFormFragment.okMessage = getResources().getString(R.string.okforminputerror);
        //  context.formSubmissionRouter().getHandlerMap().put("census_enrollment_form", new ANChandler());
//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (SDK_INT > 8)
//        {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
    }

    protected void onResumption() {

    }

    private void setupViews() {

        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(R.mipmap.logo));
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
