package org.smartregister.bidan.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.sync.CloudantDataHandler;
import org.smartregister.util.AssetHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.ANC_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.ANC_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_PNC_REGISTRATION;
import static org.smartregister.bidan.utils.LocationHelper.implode;
import static org.smartregister.event.Event.FORM_SUBMITTED;

public class ClientProcessor {

    public static final String baseEntityIdJSONKey = "baseEntityId";
    protected static final String providerIdJSONKey = "providerId";
    protected static final String VALUES_KEY = "values";
    private static final String TAG = "ClientProcessor";
    private static final String detailsUpdated = "detailsUpdated";
    private static final String[] openmrs_gen_ids = {"zeir_id"};
    private static ClientProcessor instance;
    Context mContext;
    private CloudantDataHandler mCloudantDataHandler;

    public ClientProcessor(Context context) {
        mContext = context;

        try {
            mCloudantDataHandler = CloudantDataHandler.getInstance(context);
        } catch (Exception e) {
//            Log.e(TAG, e.toString(), e);
        }

    }

    public static ClientProcessor getInstance(Context context) {
        if (instance == null) {
            instance = new ClientProcessor(context);
        }

        return instance;
    }

    public synchronized void processClient() throws Exception {
        CloudantDataHandler handler = CloudantDataHandler.getInstance(mContext);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
        long lastSyncTimeStamp = allSharedPreferences.fetchLastSyncDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        String clientClassificationStr = getFileContents("ec_client_classification.json");
        String clientAlertsStr = getFileContents("ec_client_alerts.json");

        //this seems to be easy for now cloudant json to events model is crazy
        List<JSONObject> eventsAndAlerts = handler.getUpdatedEventsAndAlerts(lastSyncDate);
        Log.d(TAG, "processClient: eventsAndAlerts.length=" + eventsAndAlerts.size());
        Log.d(TAG, "processClient: eventsAndAlerts=" + eventsAndAlerts.size());
        if (!eventsAndAlerts.isEmpty()) {
            for (JSONObject eventOrAlert : eventsAndAlerts) {
                String type = eventOrAlert.has("type") ? eventOrAlert.getString("type") : null;

                if (type.equals("Event")) {

                    JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);

                    if (isNullOrEmptyJSONObject(clientClassificationJson)) {
                        continue;
                    }

                    // Iterate through the events
                    processEvent(eventOrAlert, clientClassificationJson);
                } else if (type.equals("Action")) {
                    JSONObject clientAlertClassificationJson = new JSONObject(clientAlertsStr);

                    if (isNullOrEmptyJSONObject(clientAlertClassificationJson)) {
                        continue;
                    }

                    processAlert(eventOrAlert, clientAlertClassificationJson);
                }
            }
        }

        allSharedPreferences.saveLastSyncDate(lastSyncDate.getTime());
    }

    public synchronized void processClient(List<JSONObject> events) throws Exception {

        String clientClassificationStr = getFileContents("ec_client_classification.json");

        if (!events.isEmpty()) {
            for (JSONObject event : events) {
                if (event.getString("eventType").equals(ANC_REGISTRATION)) {
                    // START: find existing anc_id in ec_pnc (Dokumentasi Persalinan)
                    // get obs of event
                    JSONArray obs = event.has("obs") ? event.getJSONArray("obs") : null;
                    if (obs != null) {
                        // ANC ID is possible to be more than 1,
                        // so we use List
                        List<String> ancList = null;
                        // get ANC ID list
                        for (int i = 0; i < obs.length(); i++) {
                            JSONObject ob = obs.getJSONObject(i);
                            String fieldCode = ob.has("fieldCode") ? ob.getString("fieldCode") : null;
                            // check whether the fieldCode is ancId
                            if (fieldCode != null && fieldCode.equalsIgnoreCase("ancId")) {
                                JSONArray values = ob.has("values") ? ob.getJSONArray("values") : null;
                                // store values to ancList
                                ancList = getValues(values);
                                break;
                            }
                        }

                        if (ancList != null) {
                            // building question marks for query
                            String inQuestionMarks = "?";
                            for (int i = 2; i < ancList.size(); i++) {
                                inQuestionMarks += ", ?";
                            }

                            // query, to get whether there is existing data in ec_pnc (Dokumentasi Persalinan)
                            // that matches ANC ID list
                            net.sqlcipher.Cursor temp = BidanApplication
                                    .getInstance()
                                    .getContext()
                                    .initRepository()
                                    //Set to getReadableDatabase cause query only select
                                    .getReadableDatabase()
                                    .rawQuery("SELECT * FROM ec_pnc WHERE anc_id IN (" + inQuestionMarks + ")", ancList.toArray(new String[ancList.size()]));

                            //close cursor when finished get data to prevent leaked memory
                            try {
                                // if yes, then skip both removePncData and removeAncData
                                if (temp.getCount() > 0) {
                                    continue;
                                }
                            } finally {
                                temp.close();
                            }
                        }
                    }
                    // END: find existing anc_id in ec_pnc (Dokumentasi Persalinan)

                    removePncData(event.getString("baseEntityId"));
                    removeAncData(event.getString("baseEntityId"));
                }

                JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);
                if (isNullOrEmptyJSONObject(clientClassificationJson)) {
                    continue;
                }

                // Iterate through the events
                if (event.has("client")) {
                    processEvent(event, event.getJSONObject("client"), clientClassificationJson);
                }

                if (event.getString("eventType").equals(ANC_CLOSE)) {
                    removeAncData(event.getString("baseEntityId"));
                }
            }
        }

    }

    public Boolean processEvent(JSONObject event, JSONObject clientClassificationJson) throws
            Exception {

        try {
            String baseEntityId = event.getString(baseEntityIdJSONKey);

            if (event.has("creator")) {
//                Log.i(TAG, "EVENT from openmrs");
            }

            // For data integrity check if a client exists, if not pull one from cloudant and
            // insert in drishti sqlite db
            JSONObject client = getClient(baseEntityId);
            if (isNullOrEmptyJSONObject(client)) {
                return false;
            }

            // Check if child is deceased and skip
            if (client.has("deathdate") && !client.getString("deathdate").isEmpty()) {
                return false;
            }

            // Get the client type classification
            JSONArray clientClasses = clientClassificationJson
                    .getJSONArray("case_classification_rules");
            if (isNullOrEmptyJSONArray(clientClasses)) {

                return false;
            }

            for (int i = 0; i < clientClasses.length(); i++) {
                JSONObject clientClass = clientClasses.getJSONObject(i);
                processClientClass(clientClass, event, client);
            }

            // Incase the details have not been updated
            boolean updated = event.has(detailsUpdated) && event.getBoolean(detailsUpdated);
            if (!updated) {
                updateClientDetailsTable(event, client);
            }

            return true;
        } catch (Exception e) {
//            Log.e(TAG, e.toString(), e);

            return null;
        }
    }

    public Boolean processEvent(JSONObject event, JSONObject client, JSONObject
            clientClassificationJson) throws Exception {

        try {
            String baseEntityId = event.getString(baseEntityIdJSONKey);
            if (event.has("creator")) {
//                Log.i(TAG, "EVENT from openmrs");
            }
            // For data integrity check if a client exists, if not pull one from cloudant and
            // insert in drishti sqlite db

            if (isNullOrEmptyJSONObject(client)) {
                return false;
            }

            // Get the client type classification
            JSONArray clientClasses = clientClassificationJson
                    .getJSONArray("case_classification_rules");
            if (isNullOrEmptyJSONArray(clientClasses)) {
                return false;
            }

            // Check if child is deceased and skip
            if (client.has("deathdate") && !client.getString("deathdate").isEmpty()) {

                return false;
            }

            for (int i = 0; i < clientClasses.length(); i++) {
                JSONObject clientClass = clientClasses.getJSONObject(i);
                processClientClass(clientClass, event, client);
            }

            // Incase the details have not been updated
            boolean updated = event.has(detailsUpdated) && event.getBoolean(detailsUpdated);

            if (!updated) {
                updateClientDetailsTable(event, client);
            }

            return true;
        } catch (Exception e) {
//            Log.e(TAG, e.toString(), e);

            return null;
        }
    }

    public Boolean processClientClass(JSONObject clientClass, JSONObject event, JSONObject client) {

        try {
            if (clientClass == null || clientClass.length() == 0) {
                return false;
            }

            if (event == null || event.length() == 0) {
                return false;
            }

            if (client == null || client.length() == 0) {
                return false;
            }

            JSONObject ruleObject = clientClass.getJSONObject("rule");
            JSONArray fields = ruleObject.getJSONArray("fields");

            for (int i = 0; i < fields.length(); i++) {
                JSONObject fieldJson = fields.getJSONObject(i);
                processField(fieldJson, event, client);
            }
            return true;
        } catch (Exception e) {
//            Log.e(TAG, e.toString(), e);
            return null;
        }
    }

    public Boolean processField(JSONObject fieldJson, JSONObject event, JSONObject client) {

        try {
            if (fieldJson == null || fieldJson.length() == 0) {
                return false;
            }

            // keep checking if the event data matches the values expected by each rule, break the
            // moment the rule fails
            String dataSegment = null;
            String fieldName = fieldJson.has("field") ? fieldJson.getString("field") : null;
            String fieldValue =
                    fieldJson.has("field_value") ? fieldJson.getString("field_value") : null;
            String responseKey = null;

            if (fieldName != null && fieldName.contains(".")) {
                String fieldNameArray[] = fieldName.split("\\.");
                dataSegment = fieldNameArray[0];
                fieldName = fieldNameArray[1];
                String concept = fieldJson.has("concept") ? fieldJson.getString("concept") : null;

                if (concept != null) {
                    fieldValue = concept;
                    responseKey = VALUES_KEY;
                }
            }

            JSONArray createsCase =
                    fieldJson.has("creates_case") ? fieldJson.getJSONArray("creates_case") : null;
            JSONArray closesCase =
                    fieldJson.has("closes_case") ? fieldJson.getJSONArray("closes_case") : null;

            // some fields are in the main doc e.g event_type so fetch them from the main doc
            if (dataSegment != null && !dataSegment.isEmpty()) {

                JSONArray responseValue =
                        fieldJson.has(responseKey) ? fieldJson.getJSONArray(responseKey) : null;
                List<String> responseValues = getValues(responseValue);

                if (event.has(dataSegment)) {
                    JSONArray jsonDataSegment = event.getJSONArray(dataSegment);

                    // Iterate in the segment e.g obs segment
                    for (int j = 0; j < jsonDataSegment.length(); j++) {
                        JSONObject segmentJsonObject = jsonDataSegment.getJSONObject(j);
                        // let's discuss this further, to get the real value in the doc we've to
                        // use the keys 'fieldcode' and 'value'
                        String docSegmentFieldValue =
                                segmentJsonObject.has(fieldName) ? segmentJsonObject.get(fieldName)
                                        .toString() : "";
                        List<String> docSegmentResponseValues =
                                segmentJsonObject.has(responseKey) ? getValues(
                                        segmentJsonObject.get(responseKey)) : null;

                        if (docSegmentFieldValue.equalsIgnoreCase(fieldValue) && (!Collections
                                .disjoint(responseValues, docSegmentResponseValues))) {
                            // this is the event obs we're interested in put it in the respective
                            // bucket specified by type variable
                            processCaseModel(event, client, createsCase);
                            closeCase(client, closesCase);
                        }

                    }
                }

            } else {
                //fetch from the main doc
                String docSegmentFieldValue =
                        event.has(fieldName) ? event.get(fieldName).toString() : "";

                if (docSegmentFieldValue.equalsIgnoreCase(fieldValue)) {
                    processCaseModel(event, client, createsCase);
                    closeCase(client, closesCase);
                }

            }

            return true;
        } catch (Exception e) {
//            Log.e(TAG, e.toString(), e);

            return null;
        }
    }

    public Boolean processAlert(JSONObject alert, JSONObject clientAlertClassificationJson)
            throws Exception {

        try {
            if (alert == null || alert.length() == 0) {
                return false;
            }

            if (clientAlertClassificationJson == null
                    || clientAlertClassificationJson.length() == 0) {
                return false;
            }

            JSONArray columns = clientAlertClassificationJson.getJSONArray("columns");

            ContentValues contentValues = new ContentValues();

            for (int i = 0; i < columns.length(); i++) {
                JSONObject colObject = columns.getJSONObject(i);
                String columnName = colObject.getString("column_name");
                JSONObject jsonMapping = colObject.getJSONObject("json_mapping");
                String dataSegment = null;
                String fieldName = jsonMapping.getString("field");

                if (fieldName != null && fieldName.contains(".")) {
                    String fieldNameArray[] = fieldName.split("\\.");
                    dataSegment = fieldNameArray[0];
                    fieldName = fieldNameArray[1];
                }

                Object jsonDocSegment = null;

                if (dataSegment != null) {
                    //pick data from a specific section of the doc
                    jsonDocSegment = alert.get(dataSegment);

                } else {
                    //else the use the main doc as the doc segment
                    jsonDocSegment = alert;

                }

                //e.g client attributes section
                String columnValue = null;
                JSONObject jsonDocSegmentObject = (JSONObject) jsonDocSegment;
                columnValue = jsonDocSegmentObject.has(fieldName) ? jsonDocSegmentObject
                        .getString(fieldName) : "";

                // after successfully retrieving the column name and value store it in Content value
                if (columnValue != null) {
                    columnValue = getHumanReadableConceptResponse(columnValue,
                            jsonDocSegmentObject);
                    contentValues.put(columnName, columnValue);
                }
            }

            // save the values to db
            if (contentValues.size() > 0) {
                executeInsertAlert(contentValues);
            }

            return true;

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);

            return null;
        }
    }

    public Boolean closeCase(JSONObject client, JSONArray closesCase) {
        try {
            if (closesCase == null || closesCase.length() == 0) {
                return false;
            }

            String baseEntityId = client.getString(baseEntityIdJSONKey);

            for (int i = 0; i < closesCase.length(); i++) {
                String tableName = closesCase.getString(i);
                closeCase(tableName, baseEntityId);
                updateFTSsearch(tableName, baseEntityId, null);
            }

            return true;
        } catch (JSONException e) {
            Log.e(TAG, e.toString(), e);

            return null;
        }
    }

    public Boolean processCaseModel(JSONObject event, JSONObject client, JSONArray createsCase) {
        Log.d(TAG, "Trace: from processCaseModel");
        Log.d(TAG, "processCaseModel: event=" + event);
        Log.d(TAG, "processCaseModel: client=" + client);
        Log.d(TAG, "processCaseModel: createsCase=" + createsCase);
        try {

            if (createsCase == null || createsCase.length() == 0) {
                return false;
            }
            for (int openCase = 0; openCase < createsCase.length(); openCase++) {

                String clientType = createsCase.getString(openCase);
                Log.d(TAG, "processCaseModel: clientType=" + clientType);
                JSONObject columnMappings = getColumnMappings(clientType);
                JSONArray columns = columnMappings.getJSONArray("columns");
                Log.d(TAG, "processCaseModel: columns=" + columns);
                String baseEntityId = client.getString(baseEntityIdJSONKey);
                String expectedEncounterType =
                        event.has("eventType") ? event.getString("eventType") : null;

                ContentValues contentValues = new ContentValues();
                //Add the base_entity_id
                contentValues.put("base_entity_id", baseEntityId);
                contentValues.put("is_closed", 0);

                for (int i = 0; i < columns.length(); i++) {
                    JSONObject colObject = columns.getJSONObject(i);
                    String docType = colObject.getString("type");
                    String columnName = colObject.getString("column_name");
                    Log.d(TAG, "processCaseModel: columnName=" + columnName);
                    JSONObject jsonMapping = colObject.getJSONObject("json_mapping");
                    String dataSegment = null;
                    String fieldName = jsonMapping.getString("field");
                    String fieldValue = null;
                    String responseKey = null;

                    if (fieldName != null && fieldName.contains(".")) {
                        String fieldNameArray[] = fieldName.split("\\.");
                        dataSegment = fieldNameArray[0];
                        fieldName = fieldNameArray[1];
                        fieldValue = jsonMapping.has("concept") ? jsonMapping.getString("concept")
                                : (jsonMapping.has("formSubmissionField") ? jsonMapping
                                .getString("formSubmissionField") : null);
                        if (fieldValue != null) {
                            responseKey = VALUES_KEY;
                        }
                    }

                    JSONObject jsonDocument = docType.equalsIgnoreCase("Event") ? event : client;

                    Object jsonDocSegment;
                    Log.d(TAG, "processCaseModel: dataSegment=" + dataSegment);

                    if (dataSegment != null) {
                        // pick data from a specific section of the doc
                        jsonDocSegment =
                                jsonDocument.has(dataSegment) ? jsonDocument.get(dataSegment)
                                        : null;

                    } else {
                        // else the use the main doc as the doc segment
                        jsonDocSegment = jsonDocument;
                    }

                    // special handler needed to process address,
                    if (dataSegment != null && dataSegment.equalsIgnoreCase("addresses")) {
                        Map<String, String> addressMap = getClientAddressAsMap(client);
                        if (addressMap.containsKey(fieldName)) {
                            contentValues.put(columnName, addressMap.get(fieldName));
                        }
                        Log.d(TAG, "processCaseModel: special handler needed to process address");
                        Log.d(TAG, "processCaseModel: contentValues=" + contentValues);
                        continue;
                    }

                    // special handler for relationalid
                    if (dataSegment != null && dataSegment.equalsIgnoreCase("relationships")) {
                        JSONObject relationshipsObject = jsonDocument.has("relationships") ? jsonDocument
                                .getJSONObject("relationships") : null;
                        if (relationshipsObject != null) {
                            JSONArray relationshipsArray = relationshipsObject.getJSONArray(fieldName);
                            if (relationshipsArray != null && relationshipsArray.length() > 0) {
                                List<String> relationalIds = getValues(relationshipsArray);
                                contentValues.put(columnName, relationalIds.get(0));
                            }
                        }
                        Log.d(TAG, "processCaseModel: special handler for relationalid");
                        Log.d(TAG, "processCaseModel: contentValues=" + contentValues);

                        continue;
                    }

                    String encounterType =
                            jsonMapping.has("event_type") ? jsonMapping.getString("event_type")
                                    : null;

                    Log.d(TAG, "processCaseModel: before jsonDocSegment instanceof JSONArray");
                    Log.d(TAG, "processCaseModel: contentValues=" + contentValues);

                    if (jsonDocSegment instanceof JSONArray) {
                        Log.d(TAG, "processCaseModel: jsonDocSegment=" + jsonDocSegment);

                        JSONArray jsonDocSegmentArray = (JSONArray) jsonDocSegment;

                        for (int j = 0; j < jsonDocSegmentArray.length(); j++) {
                            JSONObject jsonDocObject = jsonDocSegmentArray.getJSONObject(j);
                            String columnValue = null;

                            if (fieldValue == null) {
                                // This means field_value and response_key are null so pick the
                                // value from the json object for the field_name
                                if (jsonDocObject.has(fieldName)) {
                                    Log.d(TAG, "processCaseModel: jsonDocObject.has(fieldName");
                                    Log.d(TAG, "processCaseModel: contentValues=" + contentValues);
                                    columnValue = jsonDocObject.getString(fieldName);
                                }
                            } else {
                                // this means field_value and response_key are not null e.g when
                                // retrieving some value in the events obs section
                                String expectedFieldValue = jsonDocObject.getString(fieldName);
                                // some events can only be differentiated by the event_type value
                                // eg pnc1,pnc2, anc1,anc2
                                // check if encountertype (the one in ec_client_fields.json) is
                                // null or it matches the encounter type from the ec doc we're
                                // processing
                                boolean encounterTypeMatches =
                                        (encounterType == null) || (encounterType != null
                                                && encounterType
                                                .equalsIgnoreCase(expectedEncounterType));

                                if (encounterTypeMatches && expectedFieldValue
                                        .equalsIgnoreCase(fieldValue)) {
                                    Log.d(TAG, "processCaseModel: encounterTypeMatches && expectedFieldValue.equalsIgnoreCase(fieldValue)");
                                    Log.d(TAG, "processCaseModel: contentValues=" + contentValues);
                                    columnValue = getValues(jsonDocObject.get(responseKey)).get(0);
                                }
                            }

                            // after successfully retrieving the column name and value store it
                            // in Content value
                            if (columnValue != null) {
                                columnValue = getHumanReadableConceptResponse(columnValue,
                                        jsonDocObject);
                                Log.d(TAG, "processCaseModel: getHumanReadableConceptResponse");
                                Log.d(TAG, "processCaseModel: contentValues=" + contentValues);
                                contentValues.put(columnName, columnValue);
                            }
                        }

                    } else {
                        //e.g client attributes section
                        Log.d(TAG, "processCaseModel: client attributes section");
                        String columnValue = null;
                        JSONObject jsonDocSegmentObject = (JSONObject) jsonDocSegment;
                        Log.d(TAG, "processCaseModel: jsonDocSegmentObject=" + jsonDocSegmentObject);
                        columnValue = jsonDocSegmentObject.has(fieldName) ? jsonDocSegmentObject
                                .getString(fieldName) : "";
                        Log.d(TAG, "processCaseModel: columnValue=" + columnValue);

                        // after successfully retrieving the column name and value store it in
                        // Content value
                        if (columnValue != null) {
                            columnValue = getHumanReadableConceptResponse(columnValue,
                                    jsonDocSegmentObject);
                            Log.d(TAG, "processCaseModel: columnValue=" + columnValue);
                            contentValues.put(columnName, columnValue);
                        }
                        Log.d(TAG, "processCaseModel: contentValues=" + contentValues);
                    }
                    Log.d(TAG, "processCaseModel: next loop");
                    Log.d(TAG, "processCaseModel: contentValues=" + contentValues);
                }
                Log.d(TAG, "processCaseModel: before updateIdenitifier");
                Log.d(TAG, "processCaseModel: contentValues=" + contentValues);
                // Modify openmrs generated identifier, Remove hyphen if it exists
                updateIdenitifier(contentValues);
                Log.d(TAG, "processCaseModel: after updateIdenitifier");
                Log.d(TAG, "processCaseModel: contentValues=" + contentValues);
                // save the values to db
                Long id = executeInsertStatement(contentValues, clientType);
                updateFTSsearch(clientType, baseEntityId, contentValues);
                Long timestamp = getEventDate(event.get("eventDate"));
                addContentValuesToDetailsTable(contentValues, timestamp, clientType);
                updateClientDetailsTable(event, client);
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);

            return null;
        }

    }

    /**
     * Save the populated content values to details table
     *
     * @param values
     * @param eventDate
     */
    protected void addContentValuesToDetailsTable(ContentValues values, Long eventDate, String clientType) {
        try {
            Log.d(TAG, "Trace: from addContentValuesToDetailsTable");
            Log.d(TAG, "addContentValuesToDetailsTable: " + values);
            Log.d(TAG, "clientType: " + clientType);
            String baseEntityId = values.getAsString("base_entity_id");
            String ec_kartu_ibu = new String("ec_kartu_ibu");
            String htp = new String("htp");
            String relationalid = new String("relationalid");

            for (String key : values.keySet()) {
                Log.d(TAG, "key: " + key);
                String value = values.getAsString(key);
                if (ec_kartu_ibu.equals(clientType) && value == null) {
                    //get previous value if current value null
                    Log.d(TAG, "get previous " + key + " value");
                    Map<String, String> clientDetail = getClientDetails(baseEntityId);
                    if (clientDetail.get(key) != null) {
                        saveClientDetails(baseEntityId, key, clientDetail.get(key), eventDate);
                    } else {
                        Log.d(TAG, "previous value " + key + " is null");
                        Log.d(TAG, clientDetail.toString());
                    }
                } else {
                    saveClientDetails(baseEntityId, key, value, eventDate);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    /**
     * Update the details table with the new info, All the obs are extracted and saved as key
     * value with the key being the formSubmissionField and value being the value field.
     * If the key value already exists then the row will simply be updated with the value if the
     * event date is most recent
     *
     * @param event
     * @param client
     */
    public void updateClientDetailsTable(JSONObject event, JSONObject client) {
//        Log.d(TAG, "Trace: from updateClientDetailsTable");
//        Log.d(TAG, "updateClientDetailsTable: event=" + event);
//        Log.d(TAG, "updateClientDetailsTable: client=" + client);
        try {
//            Log.i(TAG, "Started updateClientDetailsTable");

            String baseEntityId = client.getString(baseEntityIdJSONKey);
            Long timestamp = getEventDate(event.get("eventDate"));

            Map<String, String> genderInfo = getClientSingleValueAttribute(client, "gender");
            saveClientDetails(baseEntityId, genderInfo, timestamp);

            Map<String, String> addressInfo = getClientAddressAsMap(client);
            saveClientDetails(baseEntityId, addressInfo, timestamp);

            Map<String, String> attributes = getClientAttributes(client);
            saveClientDetails(baseEntityId, attributes, timestamp);

            Map<String, String> obs = getObsFromEvent(event);
            saveClientDetails(baseEntityId, obs, timestamp);

            event.put(detailsUpdated, true);

//            Log.i(TAG, "Finished updateClientDetailsTable");
            // save the other misc, client info date of birth...
        } catch (Exception e) {
//            Log.e(TAG, e.toString(), e);
        }
    }

    /**
     * Retrieve the obs as key value pair <br/>
     * Key being the formSubmissionField and value being entered value
     *
     * @param event
     * @return
     */
    private Map<String, String> getObsFromEvent(JSONObject event) {
        Map<String, String> obs = new HashMap<String, String>();

        try {
            String obsKey = "obs";
            if (event.has(obsKey)) {
                JSONArray obsArray = event.getJSONArray(obsKey);
                if (obsArray != null && obsArray.length() > 0) {
                    for (int i = 0; i < obsArray.length(); i++) {
                        JSONObject object = obsArray.getJSONObject(i);
                        String key = object.has("formSubmissionField") ? object
                                .getString("formSubmissionField") : null;
                        List<String> values =
                                object.has(VALUES_KEY) ? getValues(object.get(VALUES_KEY)) : null;

                        for (String conceptValue : values) {
                            String value = getHumanReadableConceptResponse(conceptValue, object);
                            if (key != null && value != null) {
                                obs.put(key, value);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return obs;
    }

    private Map<String, String> getClientAttributes(JSONObject client) {
        Map<String, String> attributes = new HashMap<String, String>();

        try {
            String attributesKey = "attributes";
            if (client.has(attributesKey)) {
                JSONObject attributesJson = client.getJSONObject(attributesKey);
                if (attributesJson != null && attributesJson.length() > 0) {
                    //retrieve the other fields as well
                    Iterator<String> it = attributesJson.keys();
                    while (it.hasNext()) {
                        String key = it.next();
                        boolean shouldSkipNode =
                                attributesJson.get(key) instanceof JSONArray || attributesJson
                                        .get(key) instanceof JSONObject;
                        if (!shouldSkipNode) {
                            String value = attributesJson.getString(key);
                            attributes.put(key, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }

        return attributes;
    }

    private Map<String, String> getClientSingleValueAttribute(JSONObject client, String key) {
        Map<String, String> map = new HashMap<String, String>();

        try {
            if (client.has(key)) {
                String value = client.getString(key);
                map.put(key, value);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }

        return map;
    }

    public void saveClientDetails(String baseEntityId, Map<String, String> values, Long timestamp) {
        Log.d(TAG, "Trace: from saveClientDetails");
        Log.d(TAG, "saveClientDetails: " + values);
        for (String key : values.keySet()) {
            String value = values.get(key);
            saveClientDetails(baseEntityId, key, value, timestamp);
        }
    }

    /**
     * Save a single details row to the db
     *
     * @param baseEntityId
     * @param key
     * @param value
     * @param timestamp
     */
    private void saveClientDetails(String baseEntityId, String key, String value, Long timestamp) {
        Log.d(TAG, "saveClientDetails: key=" + key);
        Log.d(TAG, "saveClientDetails: value=" + value);
        DetailsRepository detailsRepository = org.smartregister.CoreLibrary.getInstance().context().
                detailsRepository();
        detailsRepository.add(baseEntityId, key, value, timestamp);
    }

    /**
     * Get a single details row to the db
     *
     * @param baseEntityId
     */
    private Map<String, String> getClientDetails(String baseEntityId) {
        Log.d(TAG, "getClientDetails: baseEntityId=" + baseEntityId);
        DetailsRepository detailsRepository = org.smartregister.CoreLibrary.getInstance().context().
                detailsRepository();
        return detailsRepository.getAllDetailsForClient(baseEntityId);
    }

    /**
     * Get human readable values from the json doc humanreadablevalues key if the key is empty
     * return value
     *
     * @param value
     * @param jsonDocObject
     * @return
     * @throws Exception
     */
    protected String getHumanReadableConceptResponse(String value, JSONObject jsonDocObject)
            throws Exception {

        JSONArray humanReadableValues = jsonDocObject.has("humanReadableValues") ? jsonDocObject
                .getJSONArray("humanReadableValues") : null;

        if (jsonDocObject == null || humanReadableValues == null
                || humanReadableValues.length() == 0) {
            String humanReadableValue = org.smartregister.CoreLibrary.getInstance().context().
                    customHumanReadableConceptResponse().get(value);

            if (StringUtils.isNotBlank(humanReadableValue)) {
                return humanReadableValue;
            }

            return value;
        }

        String humanReadableValue =
                humanReadableValues.length() == 1 ? humanReadableValues.get(0).toString()
                        : humanReadableValues.toString();

        return humanReadableValue;
    }

    public Map<String, String> getClientAddressAsMap(JSONObject client) {
        Map<String, String> addressMap = new HashMap<String, String>();
        try {
            String addressFieldsKey = "addressFields";
            String addressesKey = "addresses";

            if (client.has(addressesKey)) {
                JSONArray addressJsonArray = client.getJSONArray(addressesKey);

                if (addressJsonArray != null && addressJsonArray.length() > 0) {
                    JSONObject addressJson = addressJsonArray.getJSONObject(0);

                    // Need to handle multiple addresses as well
                    if (addressJson.has(addressFieldsKey)) {
                        JSONObject addressFields = addressJson.getJSONObject(addressFieldsKey);
                        Iterator<String> it = addressFields.keys();

                        while (it.hasNext()) {
                            String key = it.next();
                            String value = addressFields.getString(key);
                            addressMap.put(key, value);
                        }
                    }

                    //retrieve the other fields as well
                    Iterator<String> it = addressJson.keys();

                    while (it.hasNext()) {
                        String key = it.next();
                        boolean shouldSkipNode =
                                addressJson.get(key) instanceof JSONArray || addressJson
                                        .get(key) instanceof JSONObject;

                        if (!shouldSkipNode) {
                            String value = addressJson.getString(key);
                            addressMap.put(key, value);
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return addressMap;
    }

    /**
     * Insert the a new record to the database and returns its id
     **/
    public Long executeInsertStatement(ContentValues values, String tableName) {
        CommonRepository cr = org.smartregister.CoreLibrary.getInstance().context().commonrepository(tableName);
        Long id = cr.executeInsertStatement(values, tableName);
        return id;
    }

    public void closeCase(String tableName, String baseEntityId) {
        CommonRepository cr = org.smartregister.CoreLibrary.getInstance().context().commonrepository(tableName);
        cr.closeCase(baseEntityId, tableName);
    }

    public boolean deleteCase(String tableName, String baseEntityId) {
        CommonRepository cr = org.smartregister.CoreLibrary.getInstance().context().commonrepository(tableName);
        return cr.deleteCase(baseEntityId, tableName);
    }

    public void executeInsertAlert(ContentValues contentValues) {
        if (!contentValues.getAsString(AlertRepository.ALERTS_STATUS_COLUMN).isEmpty()) {
            Alert alert = new Alert(contentValues.getAsString(AlertRepository.ALERTS_CASEID_COLUMN),
                    contentValues.getAsString(AlertRepository.ALERTS_SCHEDULE_NAME_COLUMN),
                    contentValues.getAsString(AlertRepository.ALERTS_VISIT_CODE_COLUMN), AlertStatus
                    .from(contentValues.getAsString(AlertRepository.ALERTS_STATUS_COLUMN)),
                    contentValues.getAsString(AlertRepository.ALERTS_STARTDATE_COLUMN),
                    contentValues.getAsString(AlertRepository.ALERTS_EXPIRYDATE_COLUMN));
            AlertService alertService = org.smartregister.CoreLibrary.getInstance().context().alertService();
            List<Alert> alerts = alertService
                    .findByEntityIdAndAlertNames(alert.caseId(), alert.visitCode());

            if (alerts.isEmpty()) {
                alertService.create(alert);
            }
        }
    }

    private Cursor queryTable(String sql, String tableName) {
        CommonRepository cr = org.smartregister.CoreLibrary.getInstance().context().commonrepository(tableName);
        Cursor c = cr.queryTable(sql);

        return c;
    }

    public JSONObject getColumnMappings(String registerName) {

        try {
            String clientClassificationStr = getFileContents("ec_client_fields.json");
            JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);
            JSONArray bindObjects = clientClassificationJson.getJSONArray("bindobjects");

            for (int i = 0; i < bindObjects.length(); i++) {
                JSONObject bindObject = bindObjects.getJSONObject(i);

                if (bindObject.getString("name").equalsIgnoreCase(registerName)) {
                    return bindObject;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }

    protected String getFileContents(String fileName) {
        return AssetHandler.readFileFromAssetsFolder(fileName, mContext);
    }

    protected List<String> getValues(Object jsonObject) throws JSONException {
        List<String> values = new ArrayList<String>();
        if (jsonObject == null) {
            return values;
        } else if (jsonObject instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                values.add(jsonArray.get(i).toString());
            }
        } else {
            values.add(jsonObject.toString());
        }
        return values;
    }

    private long getEventDate(Object eventDate) {
        if (eventDate instanceof Long) {
            return (Long) eventDate;
        } else {
            Date date = DateUtil.toDate(eventDate);
            if (date != null) {
                return date.getTime();
            }
        }
        return new Date().getTime();
    }

    public void updateFTSsearch(String tableName, String entityId, ContentValues contentValues) {
//        Log.i(TAG, "Starting updateFTSsearch table: " + tableName);
        AllCommonsRepository allCommonsRepository = org.smartregister.CoreLibrary.getInstance().context().
                allCommonsRepositoryobjects(tableName);

        if (allCommonsRepository != null) {
            allCommonsRepository.updateSearch(entityId);
            updateRegisterCount(entityId);
        }

//        Log.i(TAG, "Finished updateFTSsearch table: " + tableName);
    }

    private JSONObject getClient(String baseEntityId) {
        try {
            return mCloudantDataHandler.getClientByBaseEntityId(baseEntityId);
        } catch (Exception e) {
//            Log.e(getClass().getName(), "", e);

            return null;
        }
    }

    private void updateRegisterCount(String entityId) {
        FORM_SUBMITTED.notifyListeners(entityId);
    }

    public void setCloudantDataHandler(CloudantDataHandler mCloudantDataHandler) {
        this.mCloudantDataHandler = mCloudantDataHandler;
    }

    protected boolean isNullOrEmptyJSONObject(JSONObject jsonObject) {
        return (jsonObject == null || jsonObject.length() == 0);
    }

    private boolean isNullOrEmptyJSONArray(JSONArray jsonArray) {
        return (jsonArray == null || jsonArray.length() == 0);
    }

    /**
     * Update given identifier, removes hyphen
     *
     * @param values
     */
    private void updateIdenitifier(ContentValues values) {
        try {
            for (String identifier : openmrs_gen_ids) {
                Object value = values.get(identifier);
                if (value != null && value instanceof String) {
                    String sValue = value.toString();
                    if (StringUtils.isNotBlank(sValue)) {
                        values.remove(identifier);
                        values.put(identifier, sValue.replace("-", ""));
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public Context getContext() {
        return mContext;
    }

    private void removeAncData(String baseEntityId) {
        android.util.Log.d(TAG, "removeAncData: baseEntityId=" + baseEntityId);
        try {
            String formDefinitionJson = getFileContents(
                    "www/form/" + KARTU_IBU_ANC_REGISTRATION + "/close_field.json");
            JSONObject formDefinition = new JSONObject(formDefinitionJson);
            android.util.Log.d(TAG, "removeAncData: formDefinition=" + formDefinition);
            JSONArray fields = formDefinition.getJSONArray("fields");
            ArrayList<String> fieldsSql = new ArrayList<String>();
            String[] whereArgs = new String[fields.length() + 1];
            whereArgs[0] = baseEntityId;
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                String name = field.has("name") ? field.getString("name") : null;
                fieldsSql.add("?");
                whereArgs[i + 1] = name;
            }
            android.util.Log.d(TAG, "removeAncData: fieldsName=" + whereArgs);
            String fieldsNameSql = "key = " + implode(" OR key = ", fieldsSql.toArray(new String[0]));
            android.util.Log.d(TAG, "removeAncData: fieldsNameSql=" + fieldsNameSql);
            BidanApplication.getInstance().getContext().initRepository().getWritableDatabase().delete("ec_details", "base_entity_id = ? AND (" + fieldsNameSql + ")", whereArgs);
            BidanApplication.getInstance().getContext().initRepository().getWritableDatabase().delete("ec_ibu", "base_entity_id = ?", new String[]{baseEntityId});

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removePncData(String baseEntityId) {
        try {
            String formDefinitionJson = getFileContents(
                    "www/form/" + KARTU_IBU_PNC_REGISTRATION + "/close_field.json");
            JSONObject formDefinition = new JSONObject(formDefinitionJson);
            JSONArray fields = formDefinition.getJSONArray("fields");
            ArrayList<String> fieldsSql = new ArrayList<String>();
            String[] whereArgs = new String[fields.length() + 1];
            whereArgs[0] = baseEntityId;
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                String name = field.has("name") ? field.getString("name") : null;
                fieldsSql.add("?");
                whereArgs[i + 1] = name;
            }
            String fieldsNameSql = "key = " + implode(" OR key = ", fieldsSql.toArray(new String[0]));
            BidanApplication.getInstance().getContext().initRepository().getWritableDatabase().delete("ec_details", "base_entity_id = ? AND (" + fieldsNameSql + ")", whereArgs);
            BidanApplication.getInstance().getContext().initRepository().getWritableDatabase().delete("ec_pnc", "base_entity_id = ?", new String[]{baseEntityId});

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
