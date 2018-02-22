package org.smartregister.gizi.sync;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.sync.CloudantDataHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.Utils;

public class PathClientProcessor extends ClientProcessor {

    private static final String TAG = "PathClientProcessor";
    private static PathClientProcessor instance;

    private PathClientProcessor(Context context) {
        super(context);
    }

    public static PathClientProcessor getInstance(Context context) {
        if (instance == null) {
            instance = new PathClientProcessor(context);
        }
        return instance;
    }

    @Override
    public synchronized void processClient() throws Exception {
        CloudantDataHandler handler = CloudantDataHandler.getInstance(getContext());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
        long lastSyncTimeStamp = allSharedPreferences.fetchLastSyncDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        String clientClassificationStr = getFileContents("ec_client_classification.json");

        //this seems to be easy for now cloudant json to events model is crazy
        List<JSONObject> events = handler.getUpdatedEventsAndAlerts(lastSyncDate);
        if (!events.isEmpty()) {
            List<JSONObject> unsyncEvents = new ArrayList<>();
            for (JSONObject event : events) {
                String type = event.has("eventType") ? event.getString("eventType") : null;
                if (type == null) {
                    continue;
                }

               /* if (type.equals(VaccineIntentService.EVENT_TYPE) || type.equals(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT)) {
                    JSONObject clientVaccineClassificationJson = new JSONObject(clientVaccineStr);
                    if (isNullOrEmptyJSONObject(clientVaccineClassificationJson)) {
                        continue;
                    }

                    processVaccine(event, clientVaccineClassificationJson, type.equals(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
                } else if (type.equals(WeightIntentService.EVENT_TYPE) || type.equals(WeightIntentService.EVENT_TYPE_OUT_OF_CATCHMENT)) {
                    JSONObject clientWeightClassificationJson = new JSONObject(clientWeightStr);
                    if (isNullOrEmptyJSONObject(clientWeightClassificationJson)) {
                        continue;
                    }

                    processWeight(event, clientWeightClassificationJson, type.equals(WeightIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
                } else if (type.equals(RecurringIntentService.EVENT_TYPE)) {
                    JSONObject clientServiceClassificationJson = new JSONObject(clientServiceStr);
                    if (isNullOrEmptyJSONObject(clientServiceClassificationJson)) {
                        continue;
                    }
                    processService(event, clientServiceClassificationJson);
                } else if (type.equals(MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT)) {
                    unsyncEvents.add(event);
                } else if (type.equals(PathConstants.EventType.DEATH)) {
                    unsyncEvents.add(event);
                } */else {
                    JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);
                    if (isNullOrEmptyJSONObject(clientClassificationJson)) {
                        continue;
                    }
                    //iterate through the events
                    processEvent(event, clientClassificationJson);
                }
            }

            // Unsync events that are should not be in this device
            if (!unsyncEvents.isEmpty()) {
               // unSync(unsyncEvents);
            }
        }

        allSharedPreferences.saveLastSyncDate(lastSyncDate.getTime());
    }

    @Override
    public synchronized void processClient(List<JSONObject> events) throws Exception {

        String clientClassificationStr = getFileContents("ec_client_classification.json");


        if (!events.isEmpty()) {
            List<JSONObject> unsyncEvents = new ArrayList<>();
            for (JSONObject event : events) {

                String eventType = event.has("eventType") ? event.getString("eventType") : null;
                if (eventType == null) {
                    continue;
                }

              /*  if (eventType.equals(VaccineIntentService.EVENT_TYPE) || eventType.equals(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT)) {
                    JSONObject clientVaccineClassificationJson = new JSONObject(clientVaccineStr);
                    if (isNullOrEmptyJSONObject(clientVaccineClassificationJson)) {
                        continue;
                    }

                    processVaccine(event, clientVaccineClassificationJson, eventType.equals(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
                } else if (eventType.equals(WeightIntentService.EVENT_TYPE) || eventType.equals(WeightIntentService.EVENT_TYPE_OUT_OF_CATCHMENT)) {
                    JSONObject clientWeightClassificationJson = new JSONObject(clientWeightStr);
                    if (isNullOrEmptyJSONObject(clientWeightClassificationJson)) {
                        continue;
                    }

                    processWeight(event, clientWeightClassificationJson, eventType.equals(WeightIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
                } else if (eventType.equals(RecurringIntentService.EVENT_TYPE)) {
                    JSONObject clientServiceClassificationJson = new JSONObject(clientServiceStr);
                    if (isNullOrEmptyJSONObject(clientServiceClassificationJson)) {
                        continue;
                    }
                    processService(event, clientServiceClassificationJson);
                } else if (eventType.equals(MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT)) {
                    unsyncEvents.add(event);
                } else if (eventType.equals(PathConstants.EventType.DEATH)) {
                    unsyncEvents.add(event);
                }*/ else {
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

            // Unsync events that are should not be in this device
            if (!unsyncEvents.isEmpty()) {
              //  unSync(unsyncEvents);
            }
        }

    }



    private ContentValues processCaseModel(JSONObject entity, JSONObject clientClassificationJson) {
        try {
            JSONArray columns = clientClassificationJson.getJSONArray("columns");

            ContentValues contentValues = new ContentValues();

            for (int i = 0; i < columns.length(); i++) {
                JSONObject colObject = columns.getJSONObject(i);
                String columnName = colObject.getString("column_name");
                JSONObject jsonMapping = colObject.getJSONObject("json_mapping");
                String dataSegment = null;
                String fieldName = jsonMapping.getString("field");
                String fieldValue = null;
                String responseKey = null;
                String valueField = jsonMapping.has("value_field") ? jsonMapping.getString("value_field") : null;
                if (fieldName != null && fieldName.contains(".")) {
                    String fieldNameArray[] = fieldName.split("\\.");
                    dataSegment = fieldNameArray[0];
                    fieldName = fieldNameArray[1];
                    fieldValue = jsonMapping.has("concept") ? jsonMapping.getString("concept") : (jsonMapping.has("formSubmissionField") ? jsonMapping.getString("formSubmissionField") : null);
                    if (fieldValue != null) {
                        responseKey = VALUES_KEY;
                    }
                }

                Object jsonDocSegment = null;

                if (dataSegment != null) {
                    //pick data from a specific section of the doc
                    jsonDocSegment = entity.has(dataSegment) ? entity.get(dataSegment) : null;

                } else {
                    //else the use the main doc as the doc segment
                    jsonDocSegment = entity;

                }

                if (jsonDocSegment instanceof JSONArray) {

                    JSONArray jsonDocSegmentArray = (JSONArray) jsonDocSegment;

                    for (int j = 0; j < jsonDocSegmentArray.length(); j++) {
                        JSONObject jsonDocObject = jsonDocSegmentArray.getJSONObject(j);
                        String columnValue = null;
                        if (fieldValue == null) {
                            //this means field_value and response_key are null so pick the value from the json object for the field_name
                            if (jsonDocObject.has(fieldName)) {
                                columnValue = jsonDocObject.getString(fieldName);
                            }
                        } else {
                            //this means field_value and response_key are not null e.g when retrieving some value in the events obs section
                            String expectedFieldValue = jsonDocObject.getString(fieldName);
                            //some events can only be differentiated by the event_type value eg pnc1,pnc2, anc1,anc2

                            if (expectedFieldValue.equalsIgnoreCase(fieldValue)) {
                                if (StringUtils.isNotBlank(valueField) && jsonDocObject.has(valueField)) {
                                    columnValue = jsonDocObject.getString(valueField);
                                } else {
                                    List<String> values = getValues(jsonDocObject.get(responseKey));
                                    if (!values.isEmpty()) {
                                        columnValue = values.get(0);
                                    }
                                }
                            }
                        }
                        // after successfully retrieving the column name and value store it in Content value
                        if (columnValue != null) {
                            columnValue = getHumanReadableConceptResponse(columnValue, jsonDocObject);
                            contentValues.put(columnName, columnValue);
                        }
                    }

                } else {
                    //e.g client attributes section
                    String columnValue = null;
                    JSONObject jsonDocSegmentObject = (JSONObject) jsonDocSegment;
                    columnValue = jsonDocSegmentObject.has(fieldName) ? jsonDocSegmentObject.getString(fieldName) : "";
                    // after successfully retrieving the column name and value store it in Content value
                    if (columnValue != null) {
                        columnValue = getHumanReadableConceptResponse(columnValue, jsonDocSegmentObject);
                        contentValues.put(columnName, columnValue);
                    }

                }


            }

            return contentValues;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }

   /* @Override
    public void updateFTSsearch(String tableName, String entityId, ContentValues contentValues) {
        super.updateFTSsearch(tableName, entityId, contentValues);

        if (contentValues != null && StringUtils.containsIgnoreCase(tableName, "child")) {
            String dob = contentValues.getAsString("dob");

            if (StringUtils.isBlank(dob)) {
                return;
            }

            DateTime birthDateTime = new DateTime(dob);
           // VaccineSchedule.updateOfflineAlerts(entityId, birthDateTime, "child");
          //  ServiceSchedule.updateOfflineAlerts(entityId, birthDateTime);
        }
    }*/

    /*private boolean unSync(List<JSONObject> events) {
        try {

            if (events == null || events.isEmpty()) {
                return false;
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
            String registeredAnm = allSharedPreferences.fetchRegisteredANM();

            String clientClassificationStr = getFileContents("ec_client_fields.json");
            JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);
            JSONArray bindObjects = clientClassificationJson.getJSONArray("bindobjects");

            DetailsRepository detailsRepository = VaccinatorApplication.getInstance().context().detailsRepository();
            ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(getContext());

            for (JSONObject event : events) {
                unSync(ecUpdater, detailsRepository, bindObjects, event, registeredAnm);
            }

            return true;

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }

        return false;
    }

    private boolean unSync(ECSyncUpdater ecUpdater, DetailsRepository detailsRepository, JSONArray bindObjects, JSONObject event, String registeredAnm) {
        try {
            String baseEntityId = event.getString(baseEntityIdJSONKey);
            String providerId = event.getString(providerIdJSONKey);

            if (providerId.equals(registeredAnm)) {
                boolean eventDeleted = ecUpdater.deleteEventsByBaseEntityId(baseEntityId);
                boolean clientDeleted = ecUpdater.deleteClient(baseEntityId);
                Log.d(getClass().getName(), "EVENT_DELETED: " + eventDeleted);
                Log.d(getClass().getName(), "ClIENT_DELETED: " + clientDeleted);

                boolean detailsDeleted = detailsRepository.deleteDetails(baseEntityId);
                Log.d(getClass().getName(), "DETAILS_DELETED: " + detailsDeleted);

                for (int i = 0; i < bindObjects.length(); i++) {

                    JSONObject bindObject = bindObjects.getJSONObject(i);
                    String tableName = bindObject.getString("name");

                    boolean caseDeleted = deleteCase(tableName, baseEntityId);
                    Log.d(getClass().getName(), "CASE_DELETED: " + caseDeleted);
                }

                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return false;
    }*/

    private Integer parseInt(String string) {
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }

    private Float parseFloat(String string) {
        try {
            return Float.valueOf(string);
        } catch (NumberFormatException e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }
}
