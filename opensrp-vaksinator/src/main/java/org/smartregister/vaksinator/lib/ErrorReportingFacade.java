package org.smartregister.vaksinator.lib;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by sid on 10/15/17.
 */

public class ErrorReportingFacade {

    public static void initErrorHandler(Context context) {
        Fabric.with(context, new Crashlytics());
    }

    public static void setUsername(String fullName, String userName) {
        Crashlytics.setUserIdentifier(userName);
        Crashlytics.setUserName(userName);
    }
}
