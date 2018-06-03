package org.smartregister.gizi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.gizi.service.SyncService;
import org.smartregister.gizi.sync.UserConfigurableViewsSyncTask;
import util.ServiceTools;

import static org.smartregister.util.Log.logInfo;

/**
 * Created by SGithengi on 10/23/17.
 */
public class GiziSyncBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent alarmIntent) {
        logInfo("Sync alarm triggered. Trying to Sync.");

        ServiceTools.startService(context, SyncService.class);

        UserConfigurableViewsSyncTask pathUpdateActionsTask = new UserConfigurableViewsSyncTask(context);

        pathUpdateActionsTask.syncFromServer();
    }


}
