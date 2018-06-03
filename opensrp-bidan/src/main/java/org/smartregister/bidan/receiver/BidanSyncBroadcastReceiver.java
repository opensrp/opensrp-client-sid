package org.smartregister.bidan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.bidan.service.SyncService;
import org.smartregister.bidan.sync.UserConfigurableViewsSyncTask;
import util.ServiceTools;

import static org.smartregister.util.Log.logInfo;

/**
 * Created by SGithengi on 10/23/17.
 */
public class BidanSyncBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent alarmIntent) {
        logInfo("Sync alarm triggered. Trying to Sync.");

        ServiceTools.startService(context, SyncService.class);

        UserConfigurableViewsSyncTask pathUpdateActionsTask = new UserConfigurableViewsSyncTask(context);

        pathUpdateActionsTask.syncFromServer();
    }


}
