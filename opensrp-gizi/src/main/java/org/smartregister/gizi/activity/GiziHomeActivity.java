package org.smartregister.gizi.activity;

import android.database.Cursor;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.event.Listener;
import org.smartregister.gizi.R;
import org.smartregister.gizi.controller.GiziNavigationController;
import org.smartregister.gizi.service.FormSubmissionSyncService;
import org.smartregister.gizi.sync.UpdateActionsTask;
import org.smartregister.service.PendingFormSubmissionService;
import org.smartregister.sync.SyncAfterFetchListener;
import org.smartregister.sync.SyncProgressIndicator;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.contract.HomeContext;
import org.smartregister.view.controller.NativeAfterANMDetailsFetchListener;
import org.smartregister.view.controller.NativeUpdateANMDetailsTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.formula.Support;

import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.valueOf;
import static org.smartregister.event.Event.ACTION_HANDLED;
import static org.smartregister.event.Event.FORM_SUBMITTED;
import static org.smartregister.event.Event.SYNC_COMPLETED;
import static org.smartregister.event.Event.SYNC_STARTED;

public class GiziHomeActivity extends SecuredActivity {
    private SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private PendingFormSubmissionService pendingFormSubmissionService;

    private Listener<Boolean> onSyncStartListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            Support.ONSYNC = true;
            //AllConstants.SLEEP_TIME = 15000;
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(R.layout.progress);
            }
        }
    };

    private Listener<Boolean> onSyncCompleteListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            //#TODO: RemainingFormsToSyncCount cannot be updated from a back ground thread!!
            Support.ONSYNC = true;
            updateRemainingFormsToSyncCount();
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(null);
            }
            updateRegisterCounts();

        }
    };



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
    public static CommonPersonObjectController kicontroller;
    public static CommonPersonObjectController childcontroller;
    public static int kicount;

    private int childcount;
    private int ibucount;


    @Override
    protected void onCreation() {
        //home dashboard
        setContentView(R.layout.smart_registers_gizi_home);
        //  FlurryFacade.logEvent("gizi_home_dashboard");
        navigationController = new GiziNavigationController(this,anmController,context());
        setupViews();
        initialize();
        DisplayFormFragment.formInputErrorMessage = getResources().getString(R.string.forminputerror);
        DisplayFormFragment.okMessage = getResources().getString(R.string.okforminputerror);

        String HomeStart = timer.format(new Date());
        Map<String, String> Home = new HashMap<String, String>();
        Home.put("start", HomeStart);
        FlurryAgent.logEvent("gizi_home_dashboard",Home, true );

        // Require for okhttp
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

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
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(org.smartregister.gizi.R.mipmap.logo));
        getSupportActionBar().setLogo(org.smartregister.gizi.R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        LoginActivity.setLanguage();

    }

    @Override
    protected void onResumption() {
        LoginActivity.setLanguage();
        updateRegisterCounts();
        updateSyncIndicator();
        updateRemainingFormsToSyncCount();

       // initFR();
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
        childcount = childcountcursor.getInt(0);
        childcountcursor.close();

        Cursor ibucountcursor = context().commonrepository("ec_ibu").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_ibu", "ec_ibu.is_closed=0 and ec_ibu.pptest ='Positive'"));
        ibucountcursor.moveToFirst();
        ibucount = ibucountcursor.getInt(0);
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
                }catch (org.json.JSONException e){
                    anmID = "undefined";
                }
                Toast.makeText(this, String.format("%s current user = %s",context().getStringResource(R.string.app_name),anmID), LENGTH_SHORT).show();return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*public void updateFromServer() {
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                this, context().actionService(), context().formSubmissionSyncService(),
                new SyncProgressIndicator(), context().allFormVersionSyncService());
        FlurryFacade.logEvent("click_update_from_server");
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
        String locationjson = context().anmLocationController().get();
        LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);

        Map<String, TreeNode<String, Location>> locationMap =
                locationTree.getLocationsHierarchy();


    }*/
    public void updateFromServer() {
        Log.d("Home", "updateFromServer: tombol update");
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                this, context().actionService(), new FormSubmissionSyncService(context().applicationContext()) ,new SyncProgressIndicator(), context().allFormVersionSyncService());
//        FlurryFacade.logEvent("click_update_from_server");
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());

//        if (LoginActivity.generator.uniqueIdController().needToRefillUniqueId(LoginActivity.generator.UNIQUE_ID_LIMIT))  // unique id part
//            LoginActivity.generator.requestUniqueId();                                                                  // unique id part

        String locationjson = context().anmLocationController().get();
        LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);

        Map<String, TreeNode<String, Location>> locationMap =
                locationTree.getLocationsHierarchy();



        String query  = "SELECT name FROM sqlite_master WHERE type='table'";
        String db = context().initRepository().getWritableDatabase().getPath();
        Cursor dbs = context().initRepository().getWritableDatabase().rawQuery(query, null);
        Log.d("testanak", "db: " + db);
        if (dbs.moveToFirst()){
            do{
                String data = dbs.getString(dbs.getColumnIndex("name"));
                Log.d("testanak", "table name: " + data);
                Cursor temp = context().initRepository().getWritableDatabase().rawQuery("SELECT * FROM "+data, null);
                temp.moveToFirst();
                Log.d("testanak", data+": " + temp.getCount());
                String output ="";
                for(String str: temp.getColumnNames())
                    output=output+", "+str;
                Log.d("testanak", "getColumnNames: " + output);
                String output2 ="";
                if(temp.getCount()>0){
                    if (temp.moveToFirst()){
                        do{
                            for(String d:temp.getColumnNames()){
                                String value = "";
                                if(d!=""){
                                    if(temp.getType(temp.getColumnIndex(d))== Cursor.FIELD_TYPE_BLOB){
                                        value = "blob";
                                    }else{
                                        value = temp.getString(temp.getColumnIndex(d));
                                    }
                                }
                                output2=output2+", "+value;
                            }
                        }while(temp.moveToNext());
                    }
                    Log.d("testanak", "getColumnNames: " + output2);
                }

                temp.close();
            }while(dbs.moveToNext());
        }
        Log.d("testanak", "getCount: " + dbs.getCount());
        dbs.close();


//        Cursor childcountcursor = context().commonrepository("ec_anak").rawCustomQueryForAdapter("SELECT * FROM ec_anak");
//        childcountcursor.moveToFirst();
//        Log.d("testanak", "getCount: "+childcountcursor.getCount());
//        Log.d("testanak", "getColumnCount: "+childcountcursor.getColumnCount());
//        String output ="";
//        for(String str: childcountcursor.getColumnNames())
//            output=output+", "+str;
//        Log.d("testanak", "getColumnNames: "+output);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SYNC_STARTED.removeListener(onSyncStartListener);
        SYNC_COMPLETED.removeListener(onSyncCompleteListener);
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
            FlurryAgent.logEvent("gizi_home_dashboard",Home, true);
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
}
