package org.smartregister.vaksinator.sync;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.sync.ClientProcessor;

import java.util.List;

public class VaksinClientProcessor extends ClientProcessor {

//    public static final String baseEntityIdJSONKey = "baseEntityId";
//    protected static final String providerIdJSONKey = "providerId";
//    private static final String detailsUpdated = "detailsUpdated";
//    private static final String[] openmrs_gen_ids = {"zeir_id"};
//    private Context mContext;
//    protected static final String VALUES_KEY = "values";
//    private static final String TAG = "VaksinClientProcessor";
    private static VaksinClientProcessor instance;
    public static final String[] CLIENT_EVENTS = {"Registrasi Vaksinator", "Child Registration"};

    public VaksinClientProcessor(Context context) {
        super(context);
    }

    public static VaksinClientProcessor getInstance(Context context) {
        if (instance == null) {
            instance = new VaksinClientProcessor(context);
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


//    private ContentValues processCaseModel(JSONObject entity, JSONObject clientClassificationJson) {
//        try {
//            JSONArray columns = clientClassificationJson.getJSONArray("columns");
//
//            ContentValues contentValues = new ContentValues();
//
//            for (int i = 0; i < columns.length(); i++) {
//                JSONObject colObject = columns.getJSONObject(i);
//                String columnName = colObject.getString("column_name");
//                JSONObject jsonMapping = colObject.getJSONObject("json_mapping");
//                String dataSegment = null;
//                String fieldName = jsonMapping.getString("field");
//                String fieldValue = null;
//                String responseKey = null;
//                String valueField = jsonMapping.has("value_field") ? jsonMapping.getString("value_field") : null;
//                if (fieldName != null && fieldName.contains(".")) {
//                    String fieldNameArray[] = fieldName.split("\\.");
//                    dataSegment = fieldNameArray[0];
//                    fieldName = fieldNameArray[1];
//                    fieldValue = jsonMapping.has("concept") ? jsonMapping.getString("concept") : (jsonMapping.has("formSubmissionField") ? jsonMapping.getString("formSubmissionField") : null);
//                    if (fieldValue != null) {
//                        responseKey = VALUES_KEY;
//                    }
//                }
//
//                Object jsonDocSegment = null;
//
//                if (dataSegment != null) {
//                    //pick data from a specific section of the doc
//                    jsonDocSegment = entity.has(dataSegment) ? entity.get(dataSegment) : null;
//
//                } else {
//                    //else the use the main doc as the doc segment
//                    jsonDocSegment = entity;
//
//                }
//
//                if (jsonDocSegment instanceof JSONArray) {
//
//                    JSONArray jsonDocSegmentArray = (JSONArray) jsonDocSegment;
//
//                    for (int j = 0; j < jsonDocSegmentArray.length(); j++) {
//                        JSONObject jsonDocObject = jsonDocSegmentArray.getJSONObject(j);
//                        String columnValue = null;
//                        if (fieldValue == null) {
//                            //this means field_value and response_key are null so pick the value from the json object for the field_name
//                            if (jsonDocObject.has(fieldName)) {
//                                columnValue = jsonDocObject.getString(fieldName);
//                            }
//                        } else {
//                            //this means field_value and response_key are not null e.g when retrieving some value in the events obs section
//                            String expectedFieldValue = jsonDocObject.getString(fieldName);
//                            //some events can only be differentiated by the event_type value eg pnc1,pnc2, anc1,anc2
//
//                            if (expectedFieldValue.equalsIgnoreCase(fieldValue)) {
//                                if (StringUtils.isNotBlank(valueField) && jsonDocObject.has(valueField)) {
//                                    columnValue = jsonDocObject.getString(valueField);
//                                } else {
//                                    List<String> values = getValues(jsonDocObject.get(responseKey));
//                                    if (!values.isEmpty()) {
//                                        columnValue = values.get(0);
//                                    }
//                                }
//                            }
//                        }
//                        // after successfully retrieving the column name and value store it in Content value
//                        if (columnValue != null) {
//                            if (!jsonDocObject.has(valueField))
//                                columnValue = getHumanReadableConceptResponse(columnValue, jsonDocObject);
//                            contentValues.put(columnName, columnValue);
//                        }
//                    }
//
//                } else {
//                    //e.g client attributes section
//                    String columnValue = null;
//                    JSONObject jsonDocSegmentObject = (JSONObject) jsonDocSegment;
//                    columnValue = jsonDocSegmentObject.has(fieldName) ? jsonDocSegmentObject.getString(fieldName) : "";
//                    // after successfully retrieving the column name and value store it in Content value
//                    if (columnValue != null) {
//                        columnValue = getHumanReadableConceptResponse(columnValue, jsonDocSegmentObject);
//                        contentValues.put(columnName, columnValue);
//                    }
//
//                }
//
//
//            }
//
//            return contentValues;
//        } catch (Exception e) {
//            Log.e(TAG, e.toString(), e);
//        }
//        return null;
//    }


//    private Map<String, String> getObsFromEvent(JSONObject event) {
//        Map<String, String> obs = new HashMap<String, String>();
//
//        try {
//            String obsKey = "obs";
//            if (event.has(obsKey)) {
//                JSONArray obsArray = event.getJSONArray(obsKey);
//                if (obsArray != null && obsArray.length() > 0) {
//                    for (int i = 0; i < obsArray.length(); i++) {
//                        JSONObject object = obsArray.getJSONObject(i);
//                        String key = object.has("formSubmissionField") ? object
//                                .getString("formSubmissionField") : null;
//                        List<String> values =
//                                object.has(VALUES_KEY) ? getValues(object.get(VALUES_KEY)) : null;
//                        for (String conceptValue : values) {
//                            String value = getHumanReadableConceptResponse(conceptValue, object);
//                            if (key != null && value != null) {
//                                obs.put(key, value);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, e.toString(), e);
//        }
//        return obs;
//    }

}
