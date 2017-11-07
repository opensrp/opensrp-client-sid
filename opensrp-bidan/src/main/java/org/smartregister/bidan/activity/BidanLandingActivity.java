package org.smartregister.bidan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import org.smartregister.bidan.R;
import org.smartregister.bidan.toolbar.LocationSwitcherToolbar;
import org.smartregister.view.customcontrols.CustomFontTextView;

/**
 * Created by sid-tech on 11/7/17.
 */

public class BidanLandingActivity extends AppCompatActivity {

    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private TextView ecRegisterClientCountView;
    private TextView kartuIbuANCRegisterClientCountView;
    private TextView kartuIbuPNCRegisterClientCountView;
    private TextView anakRegisterClientCountView;
    private TextView kohortKbCountView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bidan_landing);

        ecRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_register_client_count);
        kartuIbuANCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_anc_register_client_count);
        kartuIbuPNCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_pnc_register_client_count);
        anakRegisterClientCountView = (TextView) findViewById(R.id.txt_anak_client_count);
        kohortKbCountView = (TextView) findViewById(R.id.txt_kohort_kb_register_count);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
