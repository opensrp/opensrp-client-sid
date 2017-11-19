package org.smartregister.vaksinator.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.vaksinator.service.FormSubmissionSyncService;
import org.smartregister.vaksinator.sync.SyncAfterFetchListener;
import org.smartregister.vaksinator.sync.SyncProgressIndicator;
import org.smartregister.vaksinator.sync.UpdateActionsTask;

import static org.smartregister.util.Log.logInfo;

/**
 + * Created by Dimas on 9/17/2015.
 + */
public class SyncVaksinatorBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                context,
                org.smartregister.Context.getInstance().actionService(),
                new FormSubmissionSyncService(org.smartregister.Context.getInstance().applicationContext()),
                new SyncProgressIndicator(),
                org.smartregister.Context.getInstance().allFormVersionSyncService());

      //  updateActionsTask.setAdditionalSyncService(org.ei.opensrp.Context.getInstance().uniqueIdService());

        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }
}