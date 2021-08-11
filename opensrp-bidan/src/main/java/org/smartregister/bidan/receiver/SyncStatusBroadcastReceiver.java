package org.smartregister.bidan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.bidan.activity.BidanHomeActivity;
import org.smartregister.domain.FetchStatus;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jason Rogena - jrogena@ona.io on 12/05/2017.
 */

public class SyncStatusBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_SYNC_STATUS = "sync_status";
    public static final String EXTRA_FETCH_STATUS = "fetch_status";
    public static final String EXTRA_COMPLETE_STATUS = "complete_status";
    public static final String EXTRA_ARGS = "extra_args";

    private final BidanHomeActivity syncStatusListeners;

    public SyncStatusBroadcastReceiver(BidanHomeActivity bidanHomeActivity) {
        syncStatusListeners = bidanHomeActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        if (data != null) {
            Serializable fetchStatusSerializable = data.getSerializable(EXTRA_FETCH_STATUS);
            final String[] stringArray = data.getStringArray(EXTRA_ARGS);

            if (fetchStatusSerializable != null && fetchStatusSerializable instanceof FetchStatus) {
                FetchStatus fetchStatus = (FetchStatus) fetchStatusSerializable;
                if (fetchStatus.equals(FetchStatus.fetchStarted)) {
                    started();
                } else {
                    boolean isComplete = data.getBoolean(EXTRA_COMPLETE_STATUS);
                    if (isComplete) {
                        complete(fetchStatus);
                        //  startExtendedSyncAndAlarms(context);
                    } else {
                        if (stringArray != null) {

                            inProgress(fetchStatus, stringArray);
                        }
                        inProgress(fetchStatus);
                    }
                }
            }
        }
    }

    private void started() {
        syncStatusListeners.onSyncStart();
    }

    private void inProgress(FetchStatus fetchStatus, String... args) {
        syncStatusListeners.onSyncInProgress(fetchStatus, args);
    }

    private void complete(FetchStatus fetchStatus) {
        syncStatusListeners.onSyncComplete(fetchStatus);
    }

    /*private void startExtendedSyncAndAlarms(Context context) {
        startExtendedSync(context);
        if (!alarmsTriggered) {
            GiziApplication.setAlarms(context);
            alarmsTriggered = true;
        }
    }*/

    /*private void startExtendedSync(Context context) {
        Intent intent = new Intent(context, ExtendedSyncIntentService.class);
        context.startService(intent);
    }*/

    public interface SyncStatusListener {
        void onSyncStart();

        void onSyncInProgress(FetchStatus fetchStatus, String... args);

        void onSyncComplete(FetchStatus fetchStatus);
    }
}
