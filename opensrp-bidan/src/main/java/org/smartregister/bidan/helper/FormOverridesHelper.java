package org.smartregister.bidan.helper;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.bidan.util.Constants;
import org.smartregister.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import util.BidanConstants;

/**
 * Created by ndegwamartin on 27/01/2018.
 */

public class FormOverridesHelper {

    private String TAG = FormOverridesHelper.class.getCanonicalName();

    private Map<String, String> patientDetails;

    public FormOverridesHelper(Map<String, String> patientDetails) {
        this.patientDetails = patientDetails;
    }

    public void setPatientDetails(Map<String, String> patientDetails) {
        this.patientDetails = patientDetails;
    }

    public Map populateFieldOverrides() {
        Map fields = new HashMap();
        fields.put(BidanConstants.KEY.PARTICIPANT_ID, patientDetails.get(BidanConstants.KEY.PARTICIPANT_ID));
        fields.put(BidanConstants.KEY.FIRST_NAME, patientDetails.get(BidanConstants.KEY.FIRST_NAME));
        fields.put(BidanConstants.KEY.LAST_NAME, patientDetails.get(BidanConstants.KEY.LAST_NAME));
        fields.put(BidanConstants.KEY.PROGRAM_ID, patientDetails.get(BidanConstants.KEY.PROGRAM_ID));
        return fields;
    }

    public FieldOverrides getFieldOverrides() {
        Map fields = populateFieldOverrides();
        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    public FieldOverrides getFollowUpFieldOverrides() {
        Map fields = populateFieldOverrides();
        fields.put(BidanConstants.KEY.TREATMENT_INITIATION_DATE, patientDetails.get(BidanConstants.KEY.TREATMENT_INITIATION_DATE));
        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    public FieldOverrides getTreatmentFieldOverrides() {
        Map fields = populateFieldOverrides();
        String gender = patientDetails.get(BidanConstants.KEY.GENDER);
        if (gender != null) {
            fields.put(BidanConstants.KEY.GENDER, gender);
        }
        String dobString = patientDetails.get(BidanConstants.KEY.DOB);
        String age = "";
        if (StringUtils.isNotBlank(dobString)) {
            try {
                DateTime birthDateTime = new DateTime(dobString);
                String duration = DateUtil.getDuration(birthDateTime);
                if (duration != null) {
                    age = duration.substring(0, duration.length() - 1);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
        fields.put(BidanConstants.KEY.AGE, age);
        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    public FieldOverrides getAddContactFieldOverrides() {
        Map fields = new HashMap();
        fields.put(BidanConstants.KEY.PARTICIPANT_ID, patientDetails.get(BidanConstants.KEY.PARTICIPANT_ID));
        fields.put(BidanConstants.KEY.PARENT_ENTITY_ID, patientDetails.get(Constants.KEY._ID));
        return new FieldOverrides(new JSONObject(fields).toString());
    }

    public FieldOverrides getContactScreeningFieldOverrides() {
        Map fields = populateFieldOverrides();
        fields.remove(BidanConstants.KEY.PARTICIPANT_ID);
        return new FieldOverrides(new JSONObject(fields).toString());
    }
}
