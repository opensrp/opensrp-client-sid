package org.smartregister.bidan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import org.smartregister.domain.FetchStatus;

import java.util.ArrayList;

import static org.smartregister.util.Log.logError;

/**
 * Created by sid-tech on 11/14/17.
 */

public class SyncStatusBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_SYNC_STATUS = "sync_status";
    public static final String EXTRA_FETCH_STATUS = "fetch_status";
    private static SyncStatusBroadcastReceiver singleton;
    private boolean isSyncing;

    private final ArrayList<SyncStatusListener> syncStatusListeners;

    public SyncStatusBroadcastReceiver() {
        syncStatusListeners = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        if (data != null) {
            FetchStatus fetchStatus = (FetchStatus) data.getSerializable(EXTRA_FETCH_STATUS);
            if (fetchStatus.equals(FetchStatus.fetchStarted)) {
                isSyncing = true;
                for (SyncStatusListener syncStatusListener : syncStatusListeners) {
                    syncStatusListener.onSyncStart();
                }
            } else {
                isSyncing = false;
                for (SyncStatusListener syncStatusListener : syncStatusListeners) {
                    syncStatusListener.onSyncComplete(fetchStatus);
                }
            }
        }
    }

    public interface SyncStatusListener {
        void onSyncStart();

        void onSyncComplete(FetchStatus fetchStatus);
    }

    public static void init(Context context) {
        if (singleton != null) {
            destroy(context);
        }

        singleton = new SyncStatusBroadcastReceiver();
        context.registerReceiver(singleton,
                new IntentFilter(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS));
    }

    public static void destroy(Context context) {
        try {
            if (singleton != null) {
                context.unregisterReceiver(singleton);
            }
        } catch (IllegalArgumentException e) {
            logError("Error on destroy: " + e);
        }
    }
}
