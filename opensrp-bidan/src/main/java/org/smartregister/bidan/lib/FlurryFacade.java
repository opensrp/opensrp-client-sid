package org.smartregister.bidan.lib;

import android.content.Context;
import android.util.Log;

import com.flurry.android.*;

import org.smartregister.bidan.util.AllConstantsINA;

import java.util.Map;

/**
 * Created by sid on 10/30/17.
 */

public class FlurryFacade {


    public static void logEvent(String event) {
        com.flurry.android.FlurryAgent.logEvent(event);
    }

    public static void logEvent(String event,
                                Map<String, String> map) {
        com.flurry.android.FlurryAgent.logEvent(event, map);
    }

    public static void setUserId(String userId) {
        com.flurry.android.FlurryAgent.setUserId(userId);
    }

    public static void init(Context context) {
        // Configure Flurry
        com.flurry.android.FlurryAgent.setLogEnabled(true);
        com.flurry.android.FlurryAgent.setLogEvents(true);
        com.flurry.android.FlurryAgent.setLogLevel(Log.VERBOSE);

        // init Flurry
        com.flurry.android.FlurryAgent.init(context, AllConstantsINA.FLURRY_KEY);
    }


}