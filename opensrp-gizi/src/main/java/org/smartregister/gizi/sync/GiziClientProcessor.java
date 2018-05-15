package org.smartregister.gizi.sync;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;
import org.smartregister.sync.ClientProcessor;

import java.util.List;

public class GiziClientProcessor extends ClientProcessor {

    public static final String[] CLIENT_EVENTS = {"Registrasi Vaksinator", "Child Registration"};
    private static GiziClientProcessor instance;

    public GiziClientProcessor(Context context) {
        super(context);
    }

    public static GiziClientProcessor getInstance(Context context) {
        if (instance == null) {
            instance = new GiziClientProcessor(context);
        }

        return instance;
    }

    @Override
    public synchronized void processClient(List<JSONObject> events) throws Exception {
        String clientClassificationStr = getFileContents("ec_client_classification.json");

        if (!events.isEmpty()) {
            for (JSONObject event : events) {

                String eventType = event.has("eventType") ? event.getString("eventType") : null;
                if (eventType == null) {
                    Log.e("TAG", "processClient: eventType NULL" );
//                    continue;
                } else {
                    JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);
                    if (isNullOrEmptyJSONObject(clientClassificationJson)) {
                        continue;
                    }
                    //iterate through the events
                    if (event.has("client")) {
                        processEvent(event, event.getJSONObject("client"), clientClassificationJson);
                    }
                }
            }
        }
    }


}
