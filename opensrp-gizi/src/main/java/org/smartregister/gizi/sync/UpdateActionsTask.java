package org.smartregister.gizi.sync;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.FetchStatus;
import org.smartregister.gizi.service.FormSubmissionSyncService;
import org.smartregister.service.ActionService;
import org.smartregister.service.AllFormVersionSyncService;
import org.smartregister.sync.AdditionalSyncService;
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
//        ActionService actionService1 = actionService;
        this.context = context;
        this.formSubmissionSyncService = formSubmissionSyncService;
        this.allFormVersionSyncService = allFormVersionSyncService;
        AdditionalSyncService additionalSyncService = null;
        task = new LockingBackgroundTask(progressIndicator);
        Log.e(TAG, "UpdateActionsTask: "+ actionService );
        Log.e(TAG, "UpdateActionsTask: "+ additionalSyncService);
    }

//    public void setAdditionalSyncService(AdditionalSyncService additionalSyncService) {
//        this.additionalSyncService = additionalSyncService;
//    }

    public void updateFromServer(final AfterFetchListener afterFetchListener) {
        if (org.smartregister.Context.getInstance().IsUserLoggedOut()) {
            logInfo("Not updating from server as user is not logged in.");
            return;
        }

        task.doActionInBackground(new BackgroundAction<FetchStatus>() {
            public FetchStatus actionToDoInBackgroundThread() {

                FetchStatus fetchStatusForForms = formSubmissionSyncService.sync();

                if(org.smartregister.Context.getInstance().configuration().shouldSyncForm()) {

                    allFormVersionSyncService.verifyFormsInFolder();
                    FetchStatus fetchVersionStatus = allFormVersionSyncService.pullFormDefinitionFromServer();
                    DownloadStatus downloadStatus = allFormVersionSyncService.downloadAllPendingFormFromServer();

                    if(downloadStatus == DownloadStatus.downloaded) {
                        allFormVersionSyncService.unzipAllDownloadedFormFile();
                    }

                    if(fetchVersionStatus == fetched || downloadStatus == DownloadStatus.downloaded) {
                        return fetched;
                    }
                }


                if(nothingFetched == fetched || fetchStatusForForms == fetched)
                    return fetched;

                return fetchStatusForForms;
            }

            public void postExecuteInUIThread(FetchStatus result) {
                if (result != null && context != null && result != nothingFetched) {
                    Toast.makeText(context, result.displayValue(), Toast.LENGTH_SHORT).show();
                }
                afterFetchListener.afterFetch(result);
            }
        });
    }
}