package org.smartregister.bidan.repository;

import android.database.Cursor;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static List<JSONObject> getHistoryANC(String baseEntityId, String type) {
        return getEventsByBaseIdAndEventType(type.length() > 0 ? "Kunjungan ANC " + type : "Kunjungan ANC", baseEntityId);
    }

    public static List<JSONObject> getANCLabTestByBaseEntityId(String baseEntityId) {
        return getHistoryANC(baseEntityId, "Lab Test");
    }

    public static List<JSONObject> getANCIntegrasiByBaseEntityId(String baseEntityId) {
        return getHistoryANC(baseEntityId, "integrasi");
    }

    public static List<JSONObject> getPNCByBaseEntityId(String baseEntityId) {
        return getEventsByBaseIdAndEventType("Kunjungan PNC", baseEntityId);
    }

    public static List<Map<String, Object>> getAllChildByMotherId(String baseEntityId) {
        Cursor cursor = null;
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            Repository repository = BidanApplication.getInstance().getRepository();
            cursor = repository.getReadableDatabase().rawQuery("select ec_ibu.base_entity_id,namaBayi,tanggalLahirAnak from ec_ibu\n" +
                    "left join ec_anak ea on ec_ibu.base_entity_id = ea.relational_id\n" +
                    "where ec_ibu.base_entity_id = ?\n" +
                    "order by\n" +
                    "ec_ibu.base_entity_id asc,\n" +
                    "tanggalLahirAnak desc", new String[]{baseEntityId});
            while (cursor.moveToNext()) {
                Map<String, Object> data = new LinkedHashMap<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    data.put(cursor.getColumnName(i), cursor.getString(i));
                }
                results.add(data);
            }

        } catch (Exception ex) {

        }
        return results;
    }

    public static Map<String, Object> getLastChild(String baseEntityId) {
        List<Map<String, Object>> allChildByMotherId = getAllChildByMotherId(baseEntityId);
        if (allChildByMotherId.isEmpty())
            return null;
        return allChildByMotherId.get(0);
    }
}
