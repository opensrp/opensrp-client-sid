package org.smartregister.bidan.sync;

import android.content.Context;
import org.smartregister.sync.ClientProcessor;
import org.json.JSONObject;
import java.util.List;

public class BidanClientProcessor extends ClientProcessor {

//    public static final String baseEntityIdJSONKey = "baseEntityId";
//    protected static final String providerIdJSONKey = "providerId";
//    protected static final String VALUES_KEY = "values";
//    private static final String detailsUpdated = "detailsUpdated";
//    private static final String[] openmrs_gen_ids = {"zeir_id"};
    private static BidanClientProcessor instance;

    public static final String[] CLIENT_EVENTS = {"Registrasi Bidan", "Child Registration", "Identitas Ibu"};

    private BidanClientProcessor(Context context) {
        super(context);
    }

    public static BidanClientProcessor getInstance(Context context) {
        if (instance == null) {
            instance = new BidanClientProcessor(context);
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
                    continue;
                }
                else {
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
