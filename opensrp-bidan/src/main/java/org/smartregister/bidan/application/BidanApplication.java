package org.smartregister.bidan.application;

import android.content.Intent;
import android.content.res.Configuration;

import com.crashlytics.android.Crashlytics;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan.activity.LoginActivity;
import org.smartregister.bidan.repository.BidanRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.SyncBroadcastReceiver;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

public class BidanApplication extends DrishtiApplication {

//    private static SettingsRepository settingsRepository;
//    private UniqueIdRepository uniqueIdRepository;

    private EventClientRepository eventClientRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context());
        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);

        applyUserLanguagePreference();
        cleanUpSyncState();
        Fabric.with(this, new Crashlytics());
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new BidanRepository(getInstance().getApplicationContext(), context());

            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }

    @Override
    public void logoutCurrentUser() {
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

    private static String[] getFtsSearchFields(String tableName) {
        switch (tableName) {
            case "ec_kartu_ibu":
                return new String[]{"namalengkap", "namaSuami", "jenisKontrasepsi"};
            case "ec_anak":
                return new String[]{"namaBayi"};
            case "ec_ibu":
                return new String[]{"namalengkap", "namaSuami"};
            case "ec_pnc":
                return new String[]{"namalengkap", "namaSuami"};
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {
        switch (tableName) {
            case "ec_kartu_ibu":
                return new String[]{"namalengkap", "umur", "noIbu", "htp"};
            case "ec_anak":
                return new String[]{"namaBayi", "tanggalLahirAnak"};
            case "ec_ibu":
                return new String[]{"namalengkap", "umur", "noIbu", "pptest", "htp"};
            case "ec_pnc":
                return new String[]{"namalengkap", "umur", "noIbu", "keadaanIbu"};
        }
        return null;
    }

    private static String[] getFtsMainConditions(String tableName) {
        switch (tableName) {
            case "ec_kartu_ibu":
                return new String[]{"is_closed", "jenisKontrasepsi"};
            case "ec_anak":
                return new String[]{"is_closed", "relational_id"};
            case "ec_ibu":
                return new String[]{"is_closed", "type", "pptest", "kartuIbuId"};
            case "ec_pnc":
                return new String[]{"is_closed", "keadaanIbu", "type"};
        }
        return null;
    }

    private static String[] getFtsTables() {
        return new String[]{"ec_kartu_ibu", "ec_anak", "ec_ibu", "ec_pnc"};
    }

    public static CommonFtsObject createCommonFtsObject() {
        CommonFtsObject commonFtsObject = new CommonFtsObject(getFtsTables());
        for (String ftsTable : commonFtsObject.getTables()) {
            commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
            commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            commonFtsObject.updateMainConditions(ftsTable, getFtsMainConditions(ftsTable));
        }
        return commonFtsObject;
    }

//    public UniqueIdRepository uniqueIdRepository() {
//        if (uniqueIdRepository == null) {
//            uniqueIdRepository = new UniqueIdRepository((BidanRepository) getRepository());
//        }
//        return uniqueIdRepository;
//    }

//    public static Repository initializeRepositoryForUniqueId() {
//        return null;
//    }

//    public static SettingsRepository getSettingsRepositoryforUniqueId() {
//        return settingsRepository();
//    }

//    protected static SettingsRepository settingsRepository() {
//    if (settingsRepository == null) {
//        settingsRepository = new SettingsRepository();
//    }
//    return settingsRepository;
//}


    public static synchronized BidanApplication getInstance() {
        return (BidanApplication) mInstance;
    }

    private void applyUserLanguagePreference() {
        Configuration config = getBaseContext().getResources().getConfiguration();

        String lang = context.allSharedPreferences().fetchLanguagePreference();
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            updateConfiguration(config);
        }
    }

   /* @Override
    public Repository getRepository() {

        try {
            if (repository == null) {
                repository = new BidanRepository(getInstance().getApplicationContext(), context());
                uniqueIdRepository();
//                eventClientRepository();
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }*/

    private void updateConfiguration(Configuration config) {
        config.locale = locale;
        Locale.setDefault(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    public Context context() {
        return context;
    }

    public Context getContext(){
        return context;
    }

    public EventClientRepository eventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository(getRepository());
        }
        return eventClientRepository;
    }


}
