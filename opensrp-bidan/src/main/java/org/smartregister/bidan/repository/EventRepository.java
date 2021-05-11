package org.smartregister.bidan.repository;

import android.database.Cursor;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private static final String TAG = EventRepository.class.getCanonicalName();

    public static List<JSONObject> getEventsByBaseIdAndEventType(String eventType, String baseEntityId) {
        List<JSONObject> events = new ArrayList<JSONObject>();
        Cursor cursor = null;
        try {
            Repository repository = BidanApplication.getInstance().getRepository();
            cursor = repository.getReadableDatabase().rawQuery("SELECT json, eventDate FROM event WHERE eventType = ? AND baseEntityId = ? ORDER BY eventDate", new String[]{eventType, baseEntityId});
            while (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                if (StringUtils.isBlank(jsonEventStr)
                        || jsonEventStr.equals("{}")) { // Skip blank/empty json string
                    continue;
                }
                jsonEventStr = jsonEventStr.replaceAll("'", "");
                JSONObject jsonObectEvent = new JSONObject(jsonEventStr);
                JSONObject event = new JSONObject();
                event.put("eventDate", jsonObectEvent.getString("eventDate"));
                JSONArray obs = jsonObectEvent.getJSONArray("obs");
                int size = obs.length();
                for (int i = 0; i < size; i++) {
                    JSONObject field = obs.getJSONObject(i);
                    String value = field.getJSONArray("values").getString(0);
                    if (value.contains("AAAA")) {
                        value = field.getJSONArray("humanReadableValues").getString(0);
                    }
                    event.put(field.getString("formSubmissionField"), value);
                }
                Log.d(TAG, "getANCByBaseEntityId: event=" + event);
                events.add(event);
            }
        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return events;
    }

    public static List<JSONObject> getANCByBaseEntityId(String baseEntityId) {
        return getEventsByBaseIdAndEventType("Kunjungan ANC", baseEntityId);
    }

    public static List<JSONObject> getPNCByBaseEntityId(String baseEntityId) {
        return getEventsByBaseIdAndEventType("Kunjungan PNC", baseEntityId);
    }
}
