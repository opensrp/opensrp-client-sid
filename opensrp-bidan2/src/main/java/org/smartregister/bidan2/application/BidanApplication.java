package org.smartregister.bidan2.application;

import android.util.Log;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan2.receiver.BidanSyncBroadcastReceiver;
import org.smartregister.bidan2.repository.BidanRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.Repository;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;

import static org.smartregister.util.Log.logError;

public class BidanApplication extends DrishtiApplication {

    private static final String TAG = BidanApplication.class.getName();
    private static CommonFtsObject commonFtsObject;

    @Override
    public void onCreate() {
        super.onCreate();

        // Minimum Requirements:

        // 1. Initialize Instance
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());

        // 2. Setup Tables
        context.updateCommonFtsObject(createCommonFtsObject());

        // 3. Initialize Modules
        CoreLibrary.init(context);
        // 4. Initialize Sync
        DrishtiSyncScheduler.setReceiverClass(BidanSyncBroadcastReceiver.class);

    }

    public Context context() {
        return context;
    }

    public static synchronized BidanApplication getInstance() {
        return (BidanApplication) mInstance;
    }

    @Override
    public void logoutCurrentUser() {

    }

    // Require
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


    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
                // OPTIONAL
                commonFtsObject.updateMainConditions(ftsTable, getFtsMainConditions(ftsTable));
            }
        }

        return commonFtsObject;
    }

    private static String[] getFtsTables() {

        return new String[]{"ec_kartu_ibu", "ec_anak", "ec_ibu", "ec_pnc" };

    }

    private static String[] getFtsSearchFields(String tableName) {

        if(tableName.equals("ec_kartu_ibu")){
            return new String[]{ "namalengkap", "namaSuami" };
        }
        else if(tableName.equals("ec_anak")){
            return new String[]{ "namaBayi" };
        }
        else if (tableName.equals("ec_ibu")){
            return new String[]{ "namalengkap", "namaSuami"};
        }
        else if (tableName.equals("ec_pnc")) {
            return new String[]{"namalengkap", "namaSuami"};
        }
        return null;

    }

    private static String[] getFtsSortFields(String tableName) {

        if(tableName.equals("ec_kartu_ibu")) {
            return new String[]{ "namalengkap", "umur",  "noIbu", "htp"};
        }
        else if(tableName.equals("ec_anak")){
            return new String[]{ "namaBayi", "tanggalLahirAnak" };
        }
        else if(tableName.equals("ec_ibu")){
            return new String[]{ "namalengkap", "umur", "noIbu", "pptest" , "htp" };
        }
        else if(tableName.equals("ec_pnc")){
            return new String[]{ "namalengkap", "umur", "noIbu", "keadaanIbu"};
        }
        return null;

    }

    private  static String[] getFtsMainConditions(String tableName){
        if(tableName.equals("ec_kartu_ibu")) {
            return new String[]{ "is_closed", "jenisKontrasepsi" };
        }
        else if(tableName.equals("ec_anak")){
            return new String[]{ "is_closed", "relational_id" };
        }
        else if(tableName.equals("ec_ibu")){
            return new String[]{ "is_closed", "type", "pptest" , "kartuIbuId" };
        }
        else if(tableName.equals("ec_pnc")){
            return new String[]{ "is_closed", "keadaanIbu" , "type"};
        }
        return null;

    }


}
