package org.smartregister.bidan2.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by sid-tech on 11/23/17.
 */

public class SyncIntentService extends IntentService {

    //    /**
//     * Creates an IntentService.  Invoked by your subclass's constructor.
//     *
//     * @param name Used to name the worker thread, important only for debugging.
//     */
//    public SyncIntentService(String name) {
//        super(name);
//    }

    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
