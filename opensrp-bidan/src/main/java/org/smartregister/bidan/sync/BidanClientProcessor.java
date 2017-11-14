package org.smartregister.bidan.sync;

import android.content.ContentValues;
import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.bidan.utils.BidanConstants;
import org.smartregister.sync.ClientProcessor;

import java.util.List;

/**
 * Created by sid-tech on 11/14/17.
 */

public class BidanClientProcessor extends ClientProcessor {

    public BidanClientProcessor(Context context) {
        super(context);
    }

    @Override
    public synchronized void processClient() throws Exception {
        super.processClient();
    }

    @Override
    public synchronized void processClient(List<JSONObject> events) throws Exception {
        super.processClient(events);
    }

    @Override
    public void updateFTSsearch(String tableName, String entityId, ContentValues contentValues) {
        super.updateFTSsearch(tableName, entityId, contentValues);
        if (contentValues != null && StringUtils.containsIgnoreCase(tableName, BidanConstants.CHILD_TABLE_NAME)) {
            String dob = contentValues.getAsString("dob");

            if (StringUtils.isBlank(dob)) {
                return;
            }

        }
    }
}
