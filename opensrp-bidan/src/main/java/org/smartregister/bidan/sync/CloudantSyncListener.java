package org.smartregister.bidan.sync;

import android.util.Log;

/**
 * Created by keyman on 21/07/16
 */
class CloudantSyncListener {

    private static final String TAG = CloudantSyncListener.class.getName();

    /**
     * Called by CloudantSyncHandler when it receives a replication complete callback.
     * CloudantSyncHandler takes care of calling this on the main thread.
     */
    protected void replicationComplete() {
        Log.e(TAG, "replicationComplete: " );
    }

    /**
     * Called by TasksModel when it receives a replication error callback.
     * TasksModel takes care of calling this on the main thread.
     */
    protected void replicationError() {
        Log.e(TAG, "replicationError: " );
    }
}