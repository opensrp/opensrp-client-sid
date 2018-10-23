package org.smartregister.gizi.application;

import android.content.Intent;
import android.content.res.Configuration;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.gizi.activity.LoginActivity;
import org.smartregister.gizi.facial.FacialRecognitionLibrary;
import org.smartregister.gizi.facial.repository.ImageRepository;
import org.smartregister.gizi.facial.utils.Tools;
import org.smartregister.gizi.repository.GiziRepository;
import org.smartregister.gizi.repository.IndonesiaECRepository;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.gizi.utils.LocationHelper;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Locale;

import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

public class GiziApplication extends DrishtiApplication {

    private IndonesiaECRepository indonesiaECRepository;
    private ImageRepository imageRepository;
    private LocationHelper locationHelper;

    private boolean isFRSupported = false;

    @Override
    public void onCreate() {

        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());

        //Initialize Modules
        CoreLibrary.init(context);


        //  DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);
//        DrishtiSyncScheduler.setReceiverClass(VaksinSyncBroadcastReceiver.class);
        super.onCreate();
        //  ACRA.init(this);
        //   DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);
        //  ErrorReportingFacade.initErrorHandler(getApplicationContext());
        //
        // ENABLE THIS AGAIN AFTER FINISH TESTING*/
        //  FlurryFacade.init(this);
        // context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());
        applyUserLanguagePreference();
        cleanUpSyncState();
        isFRSupported = FacialRecognitionLibrary.init(context, getRepository());
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
                indonesiaECRepository();
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
        if("ec_anak".equals(tableName)){
            return new String[]{ "namaBayi","tanggalLahirAnak" };
            // return ftsSearchFields;
        }  else if ("ec_ibu".equals(tableName)){
            return new String[]{ "namalengkap" };
            // return ftsSearchFields;
        }else if ("ec_kartu_ibu".equals(tableName)){
            return new String[]{ "namalengkap", "namaSuami" };
            // return ftsSearchFields;
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName){
        if("ec_anak".equals(tableName)){
            return new String[]{ "namaBayi","tanggalLahirAnak"};
        } else if("ec_ibu".equals(tableName)){
            return new String[]{ "namalengkap"};
        }  else if("ec_kartu_ibu".equals(tableName)){
            return new String[]{ "namalengkap", "namaSuami"};
        }
        return null;
    }

    private  static String[] getFtsMainConditions(String tableName){
        if("ec_anak".equals(tableName)){
            return new String[]{"is_closed", "details" , "namaBayi"};
        } else if("ec_ibu".equals(tableName)){
            return new String[]{ "is_closed", "pptest"};
        } else if("ec_kartu_ibu".equals(tableName)){
            return new String[]{ "is_closed", "namalengkap"};
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

    public IndonesiaECRepository indonesiaECRepository() {
        if (indonesiaECRepository == null) {
            indonesiaECRepository = new IndonesiaECRepository(getRepository());
        }
        return indonesiaECRepository;
    }



    public ImageRepository imageRepository() {
        if (imageRepository == null) {
            imageRepository = new ImageRepository(getRepository());
        }
        if (isFRSupported) refreshFaceData();
        return imageRepository;
    }



    public void refreshFaceData(){
        Tools.setVectorsBuffered(context, imageRepository);
        Tools.loadAlbum(context.applicationContext());
    }

    public LocationHelper getLocationHelper() {
        if (locationHelper == null){
            locationHelper = new LocationHelper(GiziApplication.getInstance().getApplicationContext());
        }
        return locationHelper;
    }

    public boolean isFRSupported() {
        return isFRSupported;
    }
}
