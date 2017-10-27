package org.smartregister.bidan.application;

import android.util.Log;
import android.util.Pair;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan.repository.BidanRepository;
import org.smartregister.bidan.util.BidanConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Map;

import static org.smartregister.util.Log.logError;

/**
 * Created by wildan on 10/2/17.
 */

public class BidanApplication extends DrishtiApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());

        //Initialize Modules
        CoreLibrary.init(context);
    }

    public static synchronized BidanApplication getInstance() {
        return (BidanApplication) mInstance;
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new BidanRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }

    @Override
    public void logoutCurrentUser() {

    }

    public Context getContext(){
        return context;
    }
}
