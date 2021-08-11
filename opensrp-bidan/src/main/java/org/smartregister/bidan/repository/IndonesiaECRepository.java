package org.smartregister.bidan.repository;

import android.database.Cursor;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndonesiaECRepository extends EventClientRepository {
    private static final String TAG = IndonesiaECRepository.class.getCanonicalName();

    public IndonesiaECRepository(Repository repository) {
        super(repository);
    }

    public int getUnSyncedEventsSize() {
        List<JSONObject> events = new ArrayList<JSONObject>();

        String query = "select "
                + event_column.json
                + ","
                + event_column.syncStatus
                + " from "
                + Table.event.name()
                + " where "
                + event_column.syncStatus
                + " = ?  and length("
                + event_column.json
                + ")>2 order by "
                + event_column.updatedAt
                + " asc";
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(query, new String[]{BaseRepository.TYPE_Unsynced});

            while (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                if (StringUtils.isBlank(jsonEventStr)
                        || jsonEventStr.equals("{}")) { // Skip blank/empty json string
                    continue;
                }
                jsonEventStr = jsonEventStr.replaceAll("'", "");
                JSONObject jsonObectEvent = new JSONObject(jsonEventStr);
                events.add(jsonObectEvent);

            }
        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return events.size();
    }

}
