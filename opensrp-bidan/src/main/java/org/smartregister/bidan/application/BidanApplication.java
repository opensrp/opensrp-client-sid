package org.smartregister.bidan.application;

import android.util.Log;
import android.util.Pair;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan.lib.ErrorReportingFacade;
import org.smartregister.bidan.lib.FlurryFacade;
import org.smartregister.bidan.repository.BidanRepository;
import org.smartregister.bidan.util.BidanConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Map;

/**
 * Created by wildan on 10/2/17.
 */

public class BidanApplication extends DrishtiApplication {

    private static final String TAG = BidanApplication.class.getSimpleName();
    private static CommonFtsObject commonFtsObject;
    private static Map<String, Pair<String, Boolean>> alertScheduleMap;
    private static Context crashlyticsUser;

    public static Map<String,Pair<String,Boolean>> getAlertScheduleMap() {
        return alertScheduleMap;
    }

    public static void setCrashlyticsUser(Context crashlyticsUser) {
        BidanApplication.crashlyticsUser = crashlyticsUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        //Init Tracker
//        ErrorReportingFacade.initErrorHandler(getApplicationContext());
//        FlurryFacade.init(this);

        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());

        // Init Module
        CoreLibrary.init(context);



    }

    @Override
    public void logoutCurrentUser() {

    }

    public static synchronized BidanApplication getInstance(){
        return (BidanApplication) mInstance;
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null)
                repository = new BidanRepository(getInstance().getApplicationContext(), context);
        } catch (UnsatisfiedLinkError e){
            Log.e(TAG, "Error on getRepository: "+ e.getMessage() );
        }
        return repository;
    }

    private static String[] getFtsTables() {
        return new String[]{BidanConstants.CHILD_TABLE_NAME, BidanConstants.MOTHER_TABLE_NAME};
    }

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        }
        commonFtsObject.updateAlertScheduleMap(getAlertScheduleMap());
        return commonFtsObject;
    }

    private static String[] getFtsSortFields(String ftsTable) {
        return new String[0];
    }

    private static String[] getFtsSearchFields(String ftsTable) {
        return new String[0];
    }

    public Context context() {
        return context;
    }
}
