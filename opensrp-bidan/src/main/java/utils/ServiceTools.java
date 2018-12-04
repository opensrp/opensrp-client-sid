package utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import org.smartregister.bidan.application.BidanApplication;

import java.util.List;

public class ServiceTools {

    private static boolean isServiceRunning(Class<?> serviceClass) {
        final ActivityManager activityManager = (ActivityManager) BidanApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo service : services) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void startService(Context context, Class<?> serviceClass) {
        if (context == null || serviceClass == null) {
            return;
        }

        if (!isServiceRunning(serviceClass)) {
            Intent intent = new Intent(context, serviceClass);
            BidanApplication.getInstance().startService(intent);
        }

    }
}