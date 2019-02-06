package org.smartregister.bidan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import org.smartregister.bidan.service.SyncService;

import utils.ServiceTools;

import static org.smartregister.util.Log.logInfo;

public class BidanSyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");

        ServiceTools.startService(context, SyncService.class);
    }
}

