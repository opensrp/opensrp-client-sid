package org.smartregister.gizi.service;

/**
 * Created by Dani on 13/11/2017.
 */

import android.content.Context;

import org.smartregister.domain.FetchStatus;
import org.smartregister.gizi.sync.CloudantSyncHandler;

import java.util.concurrent.CountDownLatch;

import util.ServiceTools;

public class FormSubmissionSyncService {
    private Context context;
    //private SyncService syncService;
    public FormSubmissionSyncService(Context context) {
        this.context = context;
    }

    public FetchStatus sync() {
        try {
            ServiceTools.startService(context, SyncService.class);

            /*CloudantSyncHandler mCloudantSyncHandler = CloudantSyncHandler.getInstance(context.getApplicationContext());
            CountDownLatch mCountDownLatch = new CountDownLatch(2);
            mCloudantSyncHandler.setCountDownLatch(mCountDownLatch);
            mCloudantSyncHandler.startPullReplication();
            mCloudantSyncHandler.startPushReplication();

            mCountDownLatch.await();*/

//            Intent intent = new Intent(DrishtiApplication.getInstance().getApplicationContext(),
//                    ImageUploadSyncService.class);
//            DrishtiApplication.getInstance().getApplicationContext().startService(intent);

            return FetchStatus.fetched;
        } catch (Exception e) {
            return FetchStatus.fetchedFailed;
        }
    }
}
