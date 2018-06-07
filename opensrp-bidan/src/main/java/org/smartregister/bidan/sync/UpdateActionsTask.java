package org.smartregister.bidan.sync;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.smartregister.bidan.service.FormSubmissionSyncService;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.FetchStatus;
import org.smartregister.service.ActionService;
import org.smartregister.service.AllFormVersionSyncService;
import org.smartregister.sync.AfterFetchListener;
import org.smartregister.view.BackgroundAction;
import org.smartregister.view.LockingBackgroundTask;
import org.smartregister.view.ProgressIndicator;

import static org.smartregister.domain.FetchStatus.fetched;
import static org.smartregister.domain.FetchStatus.nothingFetched;
import static org.smartregister.util.Log.logInfo;

public class UpdateActionsTask {
    private static final String TAG = UpdateActionsTask.class.getName();
    private final LockingBackgroundTask task;
    private Context context;
    private FormSubmissionSyncService formSubmissionSyncService;
    private AllFormVersionSyncService allFormVersionSyncService;

    public UpdateActionsTask(Context context, ActionService actionService, FormSubmissionSyncService formSubmissionSyncService, ProgressIndicator progressIndicator,
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

        task.doActionInBackground(new BackgroundAction<FetchStatus>() {
            public FetchStatus actionToDoInBackgroundThread() {

                FetchStatus fetchStatusForForms = formSubmissionSyncService.sync();
                Log.e(TAG, "updateFromServer:fetchStatusForForms "+fetchStatusForForms.toString() );

                if (org.smartregister.Context.getInstance().configuration().shouldSyncForm()) {

                    allFormVersionSyncService.verifyFormsInFolder();
                    FetchStatus fetchVersionStatus = allFormVersionSyncService.pullFormDefinitionFromServer();
                    DownloadStatus downloadStatus = allFormVersionSyncService.downloadAllPendingFormFromServer();
                    Log.e(TAG,"actionToDoInBackgroundThread:downloadStatus "+ downloadStatus );

                    if (downloadStatus == DownloadStatus.downloaded) {
                        allFormVersionSyncService.unzipAllDownloadedFormFile();
                    }

                    if (fetchVersionStatus == fetched || downloadStatus == DownloadStatus.downloaded) {
                        return fetched;
                    }
                } else {
                    Log.e(TAG, "actionToDoInBackgroundThread: fetchStatusForForms " + fetchStatusForForms );
                }


                if (nothingFetched == fetched || fetchStatusForForms == fetched )
                    return fetched;

                return fetchStatusForForms;
            }

            @Override
            public void postExecuteInUIThread(FetchStatus result) {
                if (result != null && context != null && result != nothingFetched) {
                    Toast.makeText(context, result.displayValue(), Toast.LENGTH_SHORT).show();
                }
                afterFetchListener.afterFetch(result);
            }
        });
    }
}