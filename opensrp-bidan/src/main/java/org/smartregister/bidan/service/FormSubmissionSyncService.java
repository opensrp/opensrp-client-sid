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

//            Log.e(TAG, "sync: started" );

            return FetchStatus.fetched;
        } catch (Exception e) {
            return FetchStatus.fetchedFailed;
        }
    }
}
