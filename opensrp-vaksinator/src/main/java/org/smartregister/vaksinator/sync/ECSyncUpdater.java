package org.smartregister.vaksinator.sync;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Response;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.Utils;
import org.smartregister.vaksinator.application.VaksinatorApplication;
import org.smartregister.vaksinator.service.SyncService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ECSyncUpdater {
    public static final String SEARCH_URL = "/rest/event/sync";

    private static final String LAST_SYNC_TIMESTAMP = "LAST_SYNC_TIMESTAMP";
    private static final String LAST_CHECK_TIMESTAMP = "LAST_SYNC_CHECK_TIMESTAMP";

    private final EventClientRepository db;
    private final Context context;

    private static ECSyncUpdater instance;

    public static ECSyncUpdater getInstance(Context context) {
        if (instance == null) {
            instance = new ECSyncUpdater(context);
        }
        return instance;
    }

    private ECSyncUpdater(Context context) {
        this.context = context;
        db = VaksinatorApplication.getInstance().indonesiaECRepository();
    }
    
    public JSONObject fetchAsJsonObject(String filter, String filterValue) throws JSONException {
        try {
            HTTPAgent httpAgent = VaksinatorApplication.getInstance().context().getHttpAgent();
            String baseUrl = VaksinatorApplication.getInstance().context().
                    configuration().dristhiBaseURL();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
            }

            Long lastSyncDatetime = getLastSyncTimeStamp();
            Log.i(ECSyncUpdater.class.getName(), "LAST SYNC DT :" + new DateTime(lastSyncDatetime));

            String url = baseUrl + SEARCH_URL + "?" + filter + "=" + filterValue + "&serverVersion=" + lastSyncDatetime + "&limit=" + SyncService.EVENT_PULL_LIMIT;
            Log.i(ECSyncUpdater.class.getName(), "URL: " + url);

            if (httpAgent == null) {
                throw new IllegalAccessException(SEARCH_URL + " http agent is null");
            }

            Response resp = httpAgent.fetch(url);
            if (resp.isFailure()) {
                throw new IllegalAccessException(SEARCH_URL + " not returned data");
            }

            return new JSONObject((String) resp.payload());
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
            throw new JSONException(SEARCH_URL + " threw exception "+ e.getCause());
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

    public List<JSONObject> getEvents(Date lastSyncDate) {
        try {
            return db.getEvents(lastSyncDate);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return new ArrayList<>();
    }

    public List<JSONObject> getEventsByBaseEnityId(String baseEntityId) {
        try {
            return db.getEventsByBaseEntityId(baseEntityId);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return new ArrayList<>();
    }

    public List<JSONObject> getEvents(Date lastSyncDate, String syncStatus) {
        try {
            return db.getEvents(lastSyncDate, syncStatus);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return new ArrayList<>();
    }

    public JSONObject getClient(String baseEntityId) {
        try {
            return db.getClientByBaseEntityId(baseEntityId);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return null;
    }

    public void addClient(String baseEntityId, JSONObject jsonObject) {
        try {
            db.addorUpdateClient(baseEntityId, jsonObject);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    public void addEvent(String baseEntityId, JSONObject jsonObject) {
        try {
            db.addEvent(baseEntityId, jsonObject);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    public long getLastSyncTimeStamp() {
        return Long.parseLong(Utils.getPreference(context, LAST_SYNC_TIMESTAMP, "0"));
    }

    public void updateLastSyncTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(context, LAST_SYNC_TIMESTAMP, lastSyncTimeStamp + "");
    }

    public long getLastCheckTimeStamp() {
        return Long.parseLong(Utils.getPreference(context, LAST_CHECK_TIMESTAMP, "0"));
    }

    public void updateLastCheckTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(context, LAST_CHECK_TIMESTAMP, lastSyncTimeStamp + "");
    }

    public void batchSave(JSONArray events, JSONArray clients) throws Exception {
        db.batchInsertClients(clients);
        db.batchInsertEvents(events, getLastSyncTimeStamp());
    }

    public <T> T convert(JSONObject jo, Class<T> t) {
        return db.convert(jo, t);
    }

    public JSONObject convertToJson(Object object) {
        return db.convertToJson(object);
    }

    public boolean deleteClient(String baseEntityId) {
        return db.deleteClient(baseEntityId);
    }

    /*public boolean deleteEventsByBaseEntityId(String baseEntityId) {
        return db.deleteEventsByBaseEntityId(baseEntityId, MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT);
    }*/
}
