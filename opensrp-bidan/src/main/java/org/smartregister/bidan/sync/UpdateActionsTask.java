package org.smartregister.bidan.sync;

import android.content.Context;
import android.util.Log;

import org.smartregister.bidan.service.FormSubmissionSyncService;
import org.smartregister.bidan.service.SyncService;
import org.smartregister.service.ActionService;
import org.smartregister.service.AllFormVersionSyncService;
import org.smartregister.sync.AfterFetchListener;
import org.smartregister.sync.SyncProgressIndicator;
import org.smartregister.view.LockingBackgroundTask;

import utils.ServiceTools;
import static org.smartregister.util.Log.logInfo;

public class UpdateActionsTask {
    private static final String TAG = UpdateActionsTask.class.getName();
    private final LockingBackgroundTask task;
    private Context context;
    private FormSubmissionSyncService formSubmissionSyncService;
    private AllFormVersionSyncService allFormVersionSyncService;

    public UpdateActionsTask(Context context, ActionService actionService, FormSubmissionSyncService formSubmissionSyncService, SyncProgressIndicator progressIndicator,
                             AllFormVersionSyncService allFormVersionSyncService) {
        this.context = context;
        this.formSubmissionSyncService = formSubmissionSyncService;
        this.allFormVersionSyncService = allFormVersionSyncService;
        task = new LockingBackgroundTask(progressIndicator);
    }

    public void updateFromServer(final AfterFetchListener afterFetchListener) {
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