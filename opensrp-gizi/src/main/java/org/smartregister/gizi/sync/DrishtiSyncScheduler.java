package org.smartregister.gizi.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import static org.smartregister.util.Log.logInfo;

public class DrishtiSyncScheduler {
    private static Object ReceiverClass;

    public static void setReceiverClass(Class receiverClass) {
        ReceiverClass = receiverClass;
    }

//    public static void start(final Context context) {
//        if (CoreLibrary.getInstance().context().IsUserLoggedOut()) {
//            return;
//        }
//
//        PendingIntent syncBroadcastReceiverIntent = PendingIntent
//                .getBroadcast(context, 0, new Intent(context, (Class) ReceiverClass), 0);
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + SYNC_START_DELAY,
//                SYNC_INTERVAL, syncBroadcastReceiverIntent);
//
//        logInfo(format("Scheduled to sync from server every {0} seconds.", SYNC_INTERVAL / 1000));
//
//        attachListenerToStopSyncOnLogout(context);
//    }

//    private static void attachListenerToStopSyncOnLogout(final Context context) {
//        ON_LOGOUT.removeListener(logoutListener);
//        logoutListener = new Listener<Boolean>() {
//            public void onEvent(Boolean data) {
//                logInfo("User is logged out. Stopping Dristhi Sync scheduler.");
//                stop(context);
//            }
//        };
//        ON_LOGOUT.addListener(logoutListener);
//    }

    public static void stop(Context context) {
        PendingIntent syncBroadcastReceiverIntent = PendingIntent
                .getBroadcast(context, 0, new Intent(context, (Class) ReceiverClass), 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(syncBroadcastReceiverIntent);

        logInfo("Unscheduled sync.");
    }
}
