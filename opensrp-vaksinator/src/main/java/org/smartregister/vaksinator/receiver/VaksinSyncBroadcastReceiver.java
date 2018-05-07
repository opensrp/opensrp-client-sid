package org.smartregister.vaksinator.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.vaksinator.service.SyncService;

import util.ServiceTools;

import static org.smartregister.util.Log.logInfo;

public class VaksinSyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");

        ServiceTools.startService(context, SyncService.class);
    }
}

