package org.smartregister.bidan.service;

/**
 * Created by Dani on 13/11/2017.
 */

import android.content.Context;

import org.smartregister.bidan.sync.CloudantSyncHandler;
import org.smartregister.domain.FetchStatus;

import java.util.concurrent.CountDownLatch;

public class FormSubmissionSyncService {
    private Context context;

    public FormSubmissionSyncService(Context context) {
        this.context = context;
    }

    public FetchStatus sync() {
        try {
            CloudantSyncHandler mCloudantSyncHandler = CloudantSyncHandler.getInstance(context.getApplicationContext());
            CountDownLatch mCountDownLatch = new CountDownLatch(2);
            mCloudantSyncHandler.setCountDownLatch(mCountDownLatch);
            mCloudantSyncHandler.startPullReplication();
            mCloudantSyncHandler.startPushReplication();

            mCountDownLatch.await();

//            Intent intent = new Intent(DrishtiApplication.getInstance().getApplicationContext(),
//                    ImageUploadSyncService.class);
//            DrishtiApplication.getInstance().getApplicationContext().startService(intent);

            return FetchStatus.fetched;
        } catch (Exception e) {
            return FetchStatus.fetchedFailed;
        }
    }
}
