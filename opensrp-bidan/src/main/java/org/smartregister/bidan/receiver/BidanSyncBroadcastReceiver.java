package org.smartregister.bidan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.service.intent.SyncIntentService;

import static org.smartregister.util.Log.logInfo;

public class BidanSyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");

        context.startService(new Intent(context, SyncIntentService.class));
    }

    public org.smartregister.Context getOpenSRPContext() {
        return BidanApplication.getInstance().context();
    }


}

