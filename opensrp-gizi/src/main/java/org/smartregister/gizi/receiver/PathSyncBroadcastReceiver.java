package org.smartregister.gizi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.gizi.service.SyncService;

import util.ServiceTools;

import static org.smartregister.util.Log.logInfo;

public class PathSyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");

        ServiceTools.startService(context, SyncService.class);
    }
}

