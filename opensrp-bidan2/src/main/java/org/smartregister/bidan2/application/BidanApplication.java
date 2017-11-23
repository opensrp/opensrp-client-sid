package org.smartregister.bidan2.application;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan2.receiver.BidanSyncBroadcastReceiver;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;

public class BidanApplication extends DrishtiApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());

        // Minimum Requirements:
        // 1. Initialize Modules
        CoreLibrary.init(context);
        // 2. Initialize Sync
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


}
