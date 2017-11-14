package org.smartregister.bidan2.application;

import android.util.Pair;

import com.crashlytics.android.Crashlytics;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan2.BuildConfig;
import org.smartregister.bidan2.utils.BidanConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Map;

import io.fabric.sdk.android.Fabric;

//import io.fabric.sdk.android.Fabric;
//import com.crashlytics.android.Crashlytics;

/**
 * Created by sid-tech on 11/13/17.
 */

public class BidanApplication extends DrishtiApplication {


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


    }

    /**
     * DRISHTI Method
     */
    @Override
    public void logoutCurrentUser() {

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
}
