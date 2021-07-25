package org.smartregister.bidan.sync;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.service.SyncService;
import org.smartregister.domain.Response;
import org.smartregister.bidan.repository.IndonesiaECRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ECSyncUpdater {
    public static final String SEARCH_URL = "/rest/event/sync";

    private static final String LAST_SYNC_TIMESTAMP = "LAST_SYNC_TIMESTAMP";
    private static final String LAST_CHECK_TIMESTAMP = "LAST_SYNC_CHECK_TIMESTAMP";
    private static final String TAG = ECSyncUpdater.class.getName();
    private static ECSyncUpdater instance;
    private final IndonesiaECRepository db;
    private final Context context;
    private final float PARTITION = 5f;

    private ECSyncUpdater(Context context) {
        this.context = context;
        db = BidanApplication.getInstance().indonesiaECRepository();
    }

    public static ECSyncUpdater getInstance(Context context) {
        if (instance == null) {
            instance = new ECSyncUpdater(context);
        }
        return instance;
    }

    public JSONObject fetchAsJsonObject(String filter, String filterValue) throws Exception {
        try {
            HTTPAgent httpAgent = BidanApplication.getInstance().context().getHttpAgent();
            String baseUrl = BidanApplication.getInstance().context().
                    configuration().dristhiBaseURL();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
            }

            Long lastSyncDatetime = getLastSyncTimeStamp();
//            Log.i(ECSyncUpdater.class.getName(), "LAST SYNC DT :" + new DateTime(lastSyncDatetime));

            String url = baseUrl + SEARCH_URL + "?" + filter + "=" + filterValue + "&serverVersion=" + lastSyncDatetime + "&limit=" + SyncService.EVENT_PULL_LIMIT;
//            Log.i(ECSyncUpdater.class.getName(), "URL: " + url);

            if (httpAgent == null) {
                throw new Exception(SEARCH_URL + " http agent is null");
            }

            Response resp = httpAgent.fetch(url);
            if (resp.isFailure()) {
                throw new Exception(SEARCH_URL + " not returned data");
            }

            return new JSONObject((String) resp.payload());
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
            throw new Exception(SEARCH_URL + " threw exception", e);
        }
    }

    public boolean saveAllClientsAndEvents(JSONObject jsonObject) {
        try {
            if (jsonObject == null) {
                return false;
            }

            JSONArray events = jsonObject.has("events") ? jsonObject.getJSONArray("events") : new JSONArray();
            JSONArray clients = jsonObject.has("clients") ? jsonObject.getJSONArray("clients") : new JSONArray();

            batchSave(events, clients);


            return true;
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
            return false;
        }
    }

    public List<JSONObject> allEvents(long startSyncTimeStamp, long lastSyncTimeStamp) {
        try {
            return db.getEvents(startSyncTimeStamp, lastSyncTimeStamp);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return new ArrayList<>();
    }

    public long getLastSyncTimeStamp() {
        return Long.parseLong(Utils.getPreference(context, LAST_SYNC_TIMESTAMP, "0"));
    }

    public void updateLastSyncTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(context, LAST_SYNC_TIMESTAMP, lastSyncTimeStamp + "");
    }

    public void updateLastCheckTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(context, LAST_CHECK_TIMESTAMP, lastSyncTimeStamp + "");
    }

    public long getLastCheckTimeStamp() {
        return Long.parseLong(Utils.getPreference(context, LAST_CHECK_TIMESTAMP, "0"));
    }

    public void batchSave(JSONArray events, final JSONArray clients) throws Exception {
        final int part = Math.round(PARTITION);
        final ExecutorService executorService = Executors.newFixedThreadPool(part);
        Log.i("BATCH_SAVE", "SAVING CLIENTS TOTAL " + clients.length());
        for (int i = 0; i < part; i++) {
            executorService.execute(new BatchSave(clients, i) {
                @Override
                public void rep(JSONArray arr) {
                    Log.i("BATCH_SAVE", "Saving partial client (total " + arr.length() + ")");
                    db.batchInsertClients(arr);
                }
            });
        }
        final long lastSyncTimeStamp = getLastSyncTimeStamp();
//        SaveBatch eventBatch = new SaveBatch() ;
        Log.i("BATCH_SAVE", "SAVING EVENTS TOTAL " + events.length());
        for (int i = 0; i < part; i++) {
            executorService.execute(new BatchSave(events, i) {
                @Override
                public void rep(JSONArray arr) {
                    Log.i("BATCH_SAVE", "Saving partial event (total " + arr.length() + ")");
                    db.batchInsertEvents(arr, lastSyncTimeStamp);
                }
            });
        }
    }

    public interface SaveBatch {
        void rep(JSONArray arr);
    }

    private static abstract class BatchSave implements Runnable, SaveBatch {
        private final JSONArray clients;
        private final int i;
        private final int clientLength;
        private final int part;

        private BatchSave(JSONArray clients, int i) {
            this.clients = clients;
            this.i = i;
            this.clientLength = clients.length();
            part = (int) Math.ceil((clientLength / instance.PARTITION));
        }

        @Override
        public void run() {
            JSONArray ds = new JSONArray();
            int firstPart = part * i;
            int parts = firstPart + part;
            if (parts > clientLength) {
                parts = clientLength;
            }
            for (int j = firstPart; j < parts; j++) {
//                ds.(clients.getJSONArray(j));
                try {
                    ds.put(clients.getJSONObject(j));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            rep(ds);
        }
    }
    /*public boolean deleteEventsByBaseEntityId(String baseEntityId) {
        return db.deleteEventsByBaseEntityId(baseEntityId, MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT);
    }*/
}
