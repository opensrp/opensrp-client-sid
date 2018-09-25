package org.smartregister.bidan.sync;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;
import org.smartregister.bidan.utils.AllConstantsINA;
import org.smartregister.sync.ClientProcessor;

import java.util.Arrays;
import java.util.List;

public class BidanClientProcessor extends ClientProcessor {

    public static final String[] CLIENT_EVENTS = {"Registrasi Bidan", "Child Registration", "Identitas Ibu",
            "Dokumentasi Persalinan","Edit Ibu","Edit Bayi"};//, "Tambah Bayi", "Tambah KB"};
    private static final String TAG = BidanClientProcessor.class.getName();
    private static BidanClientProcessor instance;
    private static final String EVENT_TYPE_KEY = "eventType";

    public BidanClientProcessor(Context context) {
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
            Log.e(TAG, "processClient: "+ events.size() );
            for (JSONObject event : events) {

                String eventType = event.has(EVENT_TYPE_KEY) ? event.getString(EVENT_TYPE_KEY) : null;
                Log.e(TAG, "processClient: "+ eventType );
                if (eventType != null) {
                    JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);
                    if (isNullOrEmptyJSONObject(clientClassificationJson)) {
                        Log.e(TAG, "processClient: exit " );
                        continue;
                    }
                    Log.e(TAG, "processClient: event "+ event.toString() );
                    //iterate through the events
                    if (event.has(AllConstantsINA.KEY.CLIENT)) {
                        Log.e(TAG, "processClient: "+event.getJSONObject(AllConstantsINA.KEY.CLIENT) );
                        Log.e(TAG, "processClient:clientClassificationJson "+clientClassificationJson );
                        processEvent(event, event.getJSONObject(AllConstantsINA.KEY.CLIENT), clientClassificationJson);
                    } else {
                        Log.e(TAG, "processClient: no client" );
                    }
                }

//                if (Arrays.asList(RESULT_TYPES).contains(eventType)) {
//                    JSONObject clientResultJson = new JSONObject(clientResultStr);
//                    if (isNullOrEmptyJSONObject(clientResultJson)) {
//                        continue;
//                    }
//                    processResult(event, clientResultJson);
//                } else {
////                    if (Arrays.asList(BMI_EVENT_TYPES).contains(eventType)) {
////                        JSONObject clientBMIJson = new JSONObject(clientBMIStr);
////                        if (!isNullOrEmptyJSONObject(clientBMIJson)) {
////                            processBMI(event, clientBMIJson);
////                        }
////                    }
//
//                    JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);
//                    if (isNullOrEmptyJSONObject(clientClassificationJson)) {
//                        continue;
//                    }
//                    //iterate through the events
//                    if (event.has(AllConstantsINA.KEY.CLIENT)) {
//                        processEvent(event, event.getJSONObject(AllConstantsINA.KEY.CLIENT), clientClassificationJson);
//
//                        // processEvent(event, event.getJSONObject(Constants.KEY.CLIENT), clientClassificationJson, Arrays.asList(new String[]{"deathdate", "attributes.dateRemoved"}));
//                    }
//                }
            }
        }
    }
}
