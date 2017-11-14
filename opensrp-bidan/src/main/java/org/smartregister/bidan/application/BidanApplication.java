package org.smartregister.bidan.application;

import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.util.Pair;

import com.crashlytics.android.Crashlytics;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan.BuildConfig;
import org.smartregister.bidan.activity.LoginActivity;
import org.smartregister.bidan.receiver.BidanSyncBroadcastReceiver;
import org.smartregister.bidan.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.bidan.utils.BidanConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

import static org.smartregister.util.Log.logInfo;

/**
 * Created by sid-tech on 11/13/17.
 */

public class BidanApplication extends DrishtiApplication {

    private static final String TAG = BidanApplication.class.getName();
    private EventClientRepository eventClientRepository;

    private CommonFtsObject commonFtsObject;
    private String[] ftsTables;
    private Map<String, Pair<String, Boolean>> alertScheduleMap;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        // Init Module
        CoreLibrary.init(context);

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        DrishtiSyncScheduler.setReceiverClass(BidanSyncBroadcastReceiver.class);
        SyncStatusBroadcastReceiver.init(this);
        TimeChangedBroadcastReceiver.init(this);
//        TimeChangedBroadcastReceiver.getInstance().addOnTimeChangedListener(this);

        applyUserLanguagePreference();
        cleanUpSyncState();
        setCrashlyticsUser(context);
//        setAlarms(this);

    }

    /**
     * DRISHTI Method
     */
    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    @Override
    public void onTerminate() {
        logInfo("Application is terminating. Stopping Bidan Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        SyncStatusBroadcastReceiver.destroy(this);
        TimeChangedBroadcastReceiver.destroy(this);
        super.onTerminate();
    }

    /**
     * Create Table for Data Searching
     * @return commonFtsObject
     */
    private CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateMainConditions(ftsTable, getFtsMainConditions(ftsTable));                 
//                commonFtsObject.updateCustomRelationalId(ftsTable, getFtsCustomRelationalId(ftsTable));
            }
        }
        commonFtsObject.updateAlertScheduleMap(getAlertScheduleMap());
        return commonFtsObject;
    }

    private String[] getFtsSearchFields(String tableName) {
        if(tableName.equals(BidanConstants.MOTHER_TABLE_NAME)){
            String[] ftsSearchFields =  { "namalengkap", "namaSuami" };
            return ftsSearchFields;
        } else if(tableName.equals(BidanConstants.CHILD_TABLE_NAME)){
            String[] ftsSearchFields =  { "namaBayi" };
            return ftsSearchFields;
        } else if (tableName.equals(BidanConstants.ANCPNC_TABLE_NAME)){
            String[] ftsSearchFields =  { "namalengkap", "namaSuami"};
            return ftsSearchFields;
        }
        else if (tableName.equals(BidanConstants.PNC_TABLE_NAME)) {
            String[] ftsSearchFields = {"namalengkap", "namaSuami"};
            return ftsSearchFields;
        }
        return null;
    }

    /**
     * Get Data from Tables: ec_kartu_ibu, ec_anak, ec_ibu, ec_pnc
     * @return
     */
    public String[] getFtsTables() {
        String[] ftsTables = { BidanConstants.MOTHER_TABLE_NAME, BidanConstants.CHILD_TABLE_NAME, 
                BidanConstants.ANCPNC_TABLE_NAME, BidanConstants.PNC_TABLE_NAME };
        return ftsTables;
    }

    private String[] getFtsMainConditions(String tableName){
        if(tableName.equals(BidanConstants.MOTHER_TABLE_NAME)) {
            String[] mainConditions = { "is_closed", "jenisKontrasepsi" };
            return mainConditions;
        } else if(tableName.equals(BidanConstants.CHILD_TABLE_NAME)){
            String[] mainConditions = { "is_closed", "relational_id" };
            return mainConditions;
        } else if(tableName.equals(BidanConstants.ANCPNC_TABLE_NAME)){
            String[] mainConditions = { "is_closed", "type", "pptest" , "kartuIbuId" };
            return mainConditions;
        } else if(tableName.equals(BidanConstants.PNC_TABLE_NAME)){
            String[] mainConditions = { "is_closed","keadaanIbu" , "type"};
            return mainConditions;
        }
        return null;
    }

    private String getFtsCustomRelationalId(String tableName){
        if(tableName.equals(BidanConstants.CHILD_TABLE_NAME)){
            String customRelationalId = "relational_id";
            return customRelationalId;
        } else if(tableName.equals(BidanConstants.ANCPNC_TABLE_NAME)){
            String customRelationalId =  "kartuIbuId" ;
            return customRelationalId;
        }
        return null;
    }

    public Map<String,Pair<String,Boolean>> getAlertScheduleMap() {
        return alertScheduleMap;
    }

    public static synchronized BidanApplication getInstance() {
        return (BidanApplication) mInstance;
    }

    public Context context() {
        return context;
    }

    public EventClientRepository eventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository(getRepository());
        }
        return eventClientRepository;
    }

    protected void applyUserLanguagePreference() {
        Configuration config = getBaseContext().getResources().getConfiguration();

        String lang = context.allSharedPreferences().fetchLanguagePreference();

        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            updateConfiguration(config);
        }
    }

    private void updateConfiguration(Configuration config) {
        config.locale = locale;
        Locale.setDefault(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    protected void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

    /**
     * This method sets the Crashlytics user to whichever username was used to log in last. It only
     * does so if the app is not built for debugging
     *
     * @param context The user's context
     */
    public static void setCrashlyticsUser(Context context) {
        if (!BuildConfig.DEBUG
                && context != null && context.userService() != null
                && context.userService().getAllSharedPreferences() != null) {
            Crashlytics.setUserName(context.userService().getAllSharedPreferences().fetchRegisteredANM());
        }
    }


}
