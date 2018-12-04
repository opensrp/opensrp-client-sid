package org.smartregister.gizi.sync;

import android.content.Context;
import android.util.Log;

import org.smartregister.gizi.service.SyncService;

import util.ServiceTools;

import static org.smartregister.util.Log.logInfo;

public class UpdateActionsTask {
    private static final String TAG = UpdateActionsTask.class.getName();
    private Context context;

    public UpdateActionsTask(Context context) {
        this.context = context;
    }

    public void updateFromServer() {
        if (org.smartregister.Context.getInstance().IsUserLoggedOut()) {
            logInfo("Not updating from server as user is not logged in.");
            return;
        }

        try {
            ServiceTools.startService(context, SyncService.class);

            Log.e(TAG, "sync: started" );
        } catch (Exception e) {
            Log.e(TAG, "sync: error" );
        }
    }
}