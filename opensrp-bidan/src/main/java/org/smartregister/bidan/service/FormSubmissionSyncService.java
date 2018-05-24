package org.smartregister.bidan.service;

/**
 * Created by Dani on 13/11/2017.
 */

import android.content.Context;
import android.util.Log;

import org.smartregister.domain.FetchStatus;

import utils.ServiceTools;

public class FormSubmissionSyncService {
    private static final String TAG = FormSubmissionSyncService.class.getName();
    private Context context;

    public FormSubmissionSyncService(Context context) {
        this.context = context;
    }

    public FetchStatus sync() {
        try {
            ServiceTools.startService(context, SyncService.class);

//            CloudantSyncHandler mCloudantSyncHandler = CloudantSyncHandler.getInstance(context.getApplicationContext());
//            CountDownLatch mCountDownLatch = new CountDownLatch(2);
//            mCloudantSyncHandler.setCountDownLatch(mCountDownLatch);
//            mCloudantSyncHandler.startPullReplication();
//            mCloudantSyncHandler.startPushReplication();
//            mCountDownLatch.await();
            Log.e(TAG, "sync: " );

            return FetchStatus.fetched;
        } catch (Exception e) {
            return FetchStatus.fetchedFailed;
        }
    }
}
