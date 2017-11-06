package org.smartregister.vaksinator.application;

import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.util.Pair;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.vaksinator.activity.LoginActivity;
import org.smartregister.vaksinator.lib.ErrorReportingFacade;
import org.smartregister.vaksinator.lib.FlurryFacade;
import org.smartregister.vaksinator.repository.VaksinatorRepository;
import org.smartregister.vaksinator.util.VaksinatorConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.SyncBroadcastReceiver;

import java.util.Locale;
import java.util.Map;

import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

/**
 * Created by Dani on 03/11/17.
 */

public class VaksinatorApplication extends DrishtiApplication {

    private static final String TAG = VaksinatorApplication.class.getSimpleName();
    private static CommonFtsObject commonFtsObject;
    private static Map<String, Pair<String, Boolean>> alertScheduleMap;
    private static Context crashlyticsUser;

    public static Map<String,Pair<String,Boolean>> getAlertScheduleMap() {
        return alertScheduleMap;
    }

    public static void setCrashlyticsUser(Context crashlyticsUser) {
        VaksinatorApplication.crashlyticsUser = crashlyticsUser;
    }

    @Override
    public void onCreate() {
        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);
        super.onCreate();
        //  ACRA.init(this);

//        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);
//        ErrorReportingFacade.initErrorHandler(getApplicationContext());
        /**
         * ENABLE THIS AGAIN AFTER FINISH TESTING*/
        FlurryFacade.init(this);
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());
        applyUserLanguagePreference();
        cleanUpSyncState();

        // Init Module
        CoreLibrary.init(context);
    }

    @Override
    public void logoutCurrentUser(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    private void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        logInfo("Application is terminating. Stopping Dristhi Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
    }

    private void applyUserLanguagePreference() {
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

    private String[] getFtsSearchFields(String tableName){
        if(tableName.equals("ec_anak")){
            String[] ftsSearchFields =  { "namaBayi","tanggalLahirAnak" };
            return ftsSearchFields;
        } else if (tableName.equals("ec_ibu")){
            String[] ftsSearchFields =  { "namalengkap", "namaSuami" };
            return ftsSearchFields;
        }
        return null;
    }

    private String[] getFtsSortFields(String tableName){
        if(tableName.equals("ec_anak")){
            String[] sortFields = { "namaBayi","tanggalLahirAnak"};
            return sortFields;
        } else if(tableName.equals("ec_ibu")){
            String[] sortFields = { "namalengkap", "namaSuami"};
            return sortFields;
        }
        return null;
    }

    private String[] getFtsMainConditions(String tableName){
        if(tableName.equals("ec_anak")){
            String[] mainConditions = {"is_closed", "details" , "namaBayi"};
            return mainConditions;
        } else if(tableName.equals("ec_ibu")){
            String[] mainConditions = { "is_closed", "namalengkap"};
            return mainConditions;
        }
        return null;
    }
    private String[] getFtsTables(){
        String[] ftsTables = { "ec_anak", "ec_ibu"};
        return ftsTables;
    }

    public CommonFtsObject createCommonFtsObject(){
        CommonFtsObject commonFtsObject = new CommonFtsObject(getFtsTables());
        for(String ftsTable: commonFtsObject.getTables()){
            commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
            commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            commonFtsObject.updateMainConditions(ftsTable, getFtsMainConditions(ftsTable));
        }
        return commonFtsObject;
    }

    public static synchronized VaksinatorApplication getInstance(){
        return (VaksinatorApplication) mInstance;
    }

    public Context context() {
        return context;
    }

}
