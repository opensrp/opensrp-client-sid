package org.smartregister.bidan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wildan on 11/1/17.
 */

public class SyncStatusBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    }

    public interface SyncStatusListener {
    }
}
