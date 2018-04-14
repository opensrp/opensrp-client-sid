package org.smartregister.gizi.application;

import android.content.Intent;
import android.content.res.Configuration;

import org.smartregister.gizi.receiver.GiziSyncBroadcastReceiver;
import org.smartregister.gizi.repository.GiziRepository;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.gizi.activity.LoginActivity;
import org.smartregister.gizi.libs.FlurryFacade;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.gizi.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;

import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

import java.util.Locale;

/**
 * Created by koros on 1/22/16.
 */

public class GiziApplication extends DrishtiApplication {

    private EventClientRepository eventClientRepository;
    @Override
    public void onCreate() {

        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());

        //Initialize Modules
        CoreLibrary.init(context);


      //  DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);
        DrishtiSyncScheduler.setReceiverClass(GiziSyncBroadcastReceiver.class);
        super.onCreate();
        //  ACRA.init(this);
     //   DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);
      //  ErrorReportingFacade.initErrorHandler(getApplicationContext());
        /**
         * ENABLE THIS AGAIN AFTER FINISH TESTING*/
        FlurryFacade.init(this);
       // context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());
        applyUserLanguagePreference();
        cleanUpSyncState();
    }
    public static synchronized GiziApplication getInstance() {
        return (GiziApplication) mInstance;
    }

    public Context getContext(){
        return context;
    }
    public Context context() {
        return context;
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new GiziRepository(getInstance().getApplicationContext(), context());
                eventClientRepository();
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
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

    private static String[] getFtsSearchFields(String tableName){
        if(tableName.equals("ec_anak")){
            return new String[]{ "namaBayi","tanggalLahirAnak" };
           // return ftsSearchFields;
        }  else if (tableName.equals("ec_ibu")){
            return new String[]{ "namalengkap" };
            // return ftsSearchFields;
        }else if (tableName.equals("ec_kartu_ibu")){
                return new String[]{ "namalengkap", "namaSuami" };
           // return ftsSearchFields;
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName){
        if(tableName.equals("ec_anak")){
            String[] sortFields = { "namaBayi","tanggalLahirAnak"};
            return sortFields;
        } else if(tableName.equals("ec_ibu")){
            String[] sortFields = { "namalengkap"};
            return sortFields;
        }  else if(tableName.equals("ec_kartu_ibu")){
            String[] sortFields = { "namalengkap", "namaSuami"};
            return sortFields;
        }
        return null;
    }

    private  static String[] getFtsMainConditions(String tableName){
        if(tableName.equals("ec_anak")){
            String[] mainConditions = {"is_closed", "details" , "namaBayi"};
            return mainConditions;
        } else if(tableName.equals("ec_ibu")){
            String[] mainConditions = { "is_closed", "pptest"};
            return mainConditions;
        } else if(tableName.equals("ec_kartu_ibu")){
            String[] mainConditions = { "is_closed", "namalengkap"};
            return mainConditions;
        }
        return null;
    }

/*    private String[] getFtsTables(){
        String[] ftsTables = { "ec_anak", "ec_kartu_ibu" };
        return ftsTables;
    }*/

    private static String[] getFtsTables() {
        return new String[]{"ec_anak", "ec_ibu","ec_kartu_ibu" };
    }
    public static CommonFtsObject createCommonFtsObject(){
        CommonFtsObject commonFtsObject = new CommonFtsObject(getFtsTables());
        for(String ftsTable: commonFtsObject.getTables()){
            commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
            commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            commonFtsObject.updateMainConditions(ftsTable, getFtsMainConditions(ftsTable));
        }
        return commonFtsObject;
    }

    public EventClientRepository eventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository(getRepository());
        }
        return eventClientRepository;
    }
}
