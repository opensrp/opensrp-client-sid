package utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormAttributeParser;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.clientandeventmodel.FormEntityConstants.Encounter;
import org.smartregister.clientandeventmodel.FormEntityConstants.FormEntity;
import org.smartregister.clientandeventmodel.FormEntityConstants.Person;
import org.smartregister.clientandeventmodel.FormFieldMap;
import org.smartregister.clientandeventmodel.FormSubmission;
import org.smartregister.clientandeventmodel.FormSubmissionMap;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.clientandeventmodel.SubformMap;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class BidanFormEntityConverter {
    private static final String TAG = "FormEntityConverter";
    private FormAttributeParser formAttributeParser;
    private Context mContext;

    public BidanFormEntityConverter(FormAttributeParser formAttributeParser, Context _context) {
        this.formAttributeParser = formAttributeParser;
        mContext = _context;
    }

    /**
     * Extract Event from given form submission
     *
     * @param fs FormSubmissionMap
     * @return createEvent
     * @throws ParseException
     */
    public Event getEventFromFormSubmission(FormSubmissionMap fs) throws ParseException {
        return createEvent(fs.entityId(), fs.formAttributes().get("encounter_type"), fs.fields(),
                fs);
    }

    private Event createEvent(String entityId, String eventType, List<FormFieldMap> fields,
                              FormSubmissionMap fs) throws ParseException {
        String encounterDateField = getFieldName(Encounter.encounter_date, fs);
        String encounterLocation = getFieldName(Encounter.location_id, fs);

        //TODO
        String encounterStart = getFieldName(Encounter.encounter_start, fs);
        String encounterEnd = getFieldName(Encounter.encounter_end, fs);

        Date encounterDate = new DateTime(FormEntityConstants.FORM_DATE.format(new Date()))
                .toDate();
        if (fs.getFieldValue(encounterDateField) != null) {
            encounterDate = new DateTime(
                    FormEntityConstants.FORM_DATE.parse(fs.getFieldValue(encounterDateField)))
                    .toDate();
        }

        Event e = (Event) new Event().withBaseEntityId(entityId)//should be different for main
                // and subform
                .withEventDate(encounterDate).withEventType(eventType)
                .withLocationId(fs.getFieldValue(encounterLocation)).withProviderId(fs.providerId())
                .withEntityType(fs.bindType()).withFormSubmissionId(fs.instanceId())
                .withDateCreated(new Date());

        for (FormFieldMap fl : fields) {
            Map<String, String> fat = fl.fieldAttributes();
            if (!fl.values().isEmpty() && !StringUtils.isEmpty(fl.values().get(0)) && fat
                    .containsKey("openmrs_entity") && fat.get("openmrs_entity")
                    .equalsIgnoreCase("concept")) {
                List<Object> vall = new ArrayList<>();
                List<Object> humanReadableValues = new ArrayList<>();
                for (String vl : fl.values()) {
                    String val = fl.valueCodes(vl) == null ? null
                            : fl.valueCodes(vl).get("openmrs_code");
                    // String hval=fl.getValues()==null?null:fl.getValues();
                    val = StringUtils.isEmpty(val) ? vl : val;
                    vall.add(val);

                    if (fl.valueCodes(vl) != null && fl.valueCodes(vl).get("openmrs_code")
                            != null) {// this value is in concept id form
                        String hval = fl.getValues() == null ? null : fl.getValues().get(0);
                        humanReadableValues.add(hval);
                    }
                }
                e.addObs(new Obs("concept", fl.type(), fat.get("openmrs_entity_id"),
                        fat.get("openmrs_entity_parent"), vall, humanReadableValues, null,
                        fl.name()));
            }
        }
        return e;
    }

    public Event getEventFromFormSubmission(FormSubmission fs) throws IllegalStateException {
        try {
            return getEventFromFormSubmission(formAttributeParser.createFormSubmissionMap(fs));
        } catch (JsonIOException | JsonSyntaxException | XPathExpressionException
                | ParseException | ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Extract Event for given subform with given data mapped to specified Encounter Type.
     *
     * @param fs FormSubmissionMap
     * @param eventType String
     * @param subformInstance SubformMap
     * @return createEvent
     * @throws ParseException
     */
    private Event getEventForSubform(FormSubmissionMap fs, String eventType, SubformMap
            subformInstance) throws ParseException {
        return createEvent(subformInstance.entityId(),
                subformInstance.formAttributes().get("openmrs_entity_id"), subformInstance.fields(),
                fs);
    }

    /**
     * Get field name for specified openmrs entity in given form submission
     *
     * @param en
     * @param fs
     * @return
     */
    String getFieldName(FormEntity en, FormSubmissionMap fs) {
        return getFieldName(en, fs.fields());
    }

    /**
     * Get field name for specified openmrs entity in given form submission for given subform
     *
     * @param en
     * @param
     * @param
     * @return
     */
    String getFieldName(FormEntity en, SubformMap subf) {
        return getFieldName(en, subf.fields());
    }

    String getFieldName(FormEntity en, List<FormFieldMap> fields) {
        for (FormFieldMap f : fields) {
            if (f.fieldAttributes().containsKey("openmrs_entity") && f.fieldAttributes()
                    .get("openmrs_entity").equalsIgnoreCase(en.entity()) && f.fieldAttributes()
                    .get("openmrs_entity_id").equalsIgnoreCase(en.entityId())) {
                return f.name();
            }
        }
        return null;
    }

    Map<String, Address> extractAddresses(FormSubmissionMap fs) throws ParseException {
        Map<String, Address> paddr = new HashMap<>();
        for (FormFieldMap fl : fs.fields()) {
            fillAddressFields(fl, paddr);
        }
        return paddr;
    }

    Map<String, Address> extractAddressesForSubform(SubformMap subf) throws ParseException {
        Map<String, Address> paddr = new HashMap<>();
        for (FormFieldMap fl : subf.fields()) {
            fillAddressFields(fl, paddr);
        }
        return paddr;
    }

    void fillAddressFields(FormFieldMap fl, Map<String, Address> addresses) throws ParseException {
        Map<String, String> att = fl.fieldAttributes();
        if (att.containsKey("openmrs_entity") && att.get("openmrs_entity")
                .equalsIgnoreCase("person_address")) {
            String addressType = att.get("openmrs_entity_parent");
            String addressField = att.get("openmrs_entity_id");
            Address ad = addresses.get(addressType);
            if (ad == null) {
                ad = new Address(addressType, null, null, null, null, null, null, null, null);
            }

            if (addressField.equalsIgnoreCase("startDate") || addressField
                    .equalsIgnoreCase("start_date")) {
                ad.setStartDate(DateUtil.parseDate(fl.value()));
            } else if (addressField.equalsIgnoreCase("endDate") || addressField
                    .equalsIgnoreCase("end_date")) {
                ad.setEndDate(DateUtil.parseDate(fl.value()));
            } else if (addressField.equalsIgnoreCase("latitude")) {
                ad.setLatitude(fl.value());
            } else if (addressField.equalsIgnoreCase("longitute")) {
                ad.setLongitude(fl.value());
            } else if (addressField.equalsIgnoreCase("geopoint")) {
                // example geopoint 34.044494 -84.695704 4 76 = lat lon alt prec
                String geopoint = fl.value();
                if (!StringUtils.isEmpty(geopoint)) {
                    String[] g = geopoint.split(" ");
                    ad.setLatitude(g[0]);
                    ad.setLongitude(g[1]);
                    ad.setGeopoint(geopoint);
                }
            } else if (addressField.equalsIgnoreCase("postal_code") || addressField
                    .equalsIgnoreCase("postalCode")) {
                ad.setPostalCode(fl.value());
            } else if (addressField.equalsIgnoreCase("sub_town") || addressField
                    .equalsIgnoreCase("subTown")) {
                ad.setSubTown(fl.value());
            } else if (addressField.equalsIgnoreCase("town")) {
                ad.setTown(fl.value());
            } else if (addressField.equalsIgnoreCase("sub_district") || addressField
                    .equalsIgnoreCase("subDistrict")) {
                ad.setSubDistrict(fl.value());
            } else if (addressField.equalsIgnoreCase("district") || addressField
                    .equalsIgnoreCase("county") || addressField.equalsIgnoreCase("county_district")
                    || addressField.equalsIgnoreCase("countyDistrict")) {
                ad.setCountyDistrict(fl.value());
            } else if (addressField.equalsIgnoreCase("city") || addressField
                    .equalsIgnoreCase("village") || addressField.equalsIgnoreCase("cityVillage")
                    || addressField.equalsIgnoreCase("city_village")) {
                ad.setCityVillage(fl.value());
            } else if (addressField.equalsIgnoreCase("state") || addressField
                    .equalsIgnoreCase("state_province") || addressField
                    .equalsIgnoreCase("stateProvince")) {
                ad.setStateProvince(fl.value());
            } else if (addressField.equalsIgnoreCase("country")) {
                ad.setCountry(fl.value());
            } else {
                ad.addAddressField(addressField, fl.value());
            }
            addresses.put(addressType, ad);
        }
    }

    Map<String, String> extractIdentifiers(FormSubmissionMap fs) {
        Map<String, String> pids = new HashMap<>();
        fillIdentifiers(pids, fs.fields());
        return pids;
    }

    Map<String, String> extractIdentifiers(SubformMap subf) {
        Map<String, String> pids = new HashMap<>();
        fillIdentifiers(pids, subf.fields());
        return pids;
    }

    void fillIdentifiers(Map<String, String> pids, List<FormFieldMap> fields) {
        for (FormFieldMap fl : fields) {
            if (fl.values().size() < 2 && !StringUtils.isEmpty(fl.value())) {
                Map<String, String> att = fl.fieldAttributes();

                if (att.containsKey("openmrs_entity") && att.get("openmrs_entity")
                        .equalsIgnoreCase("person_identifier")) {
                    pids.put(att.get("openmrs_entity_id"), fl.value());
                }
            }
        }
    }

    Map<String, Object> extractAttributes(FormSubmissionMap fs) {
        Map<String, Object> pattributes = new HashMap<>();
        fillAttributes(pattributes, fs.fields());
        return pattributes;
    }

    Map<String, Object> extractAttributes(SubformMap subf) {
        Map<String, Object> pattributes = new HashMap<>();
        fillAttributes(pattributes, subf.fields());
        return pattributes;
    }

    Map<String, Object> fillAttributes(Map<String, Object> pattributes, List<FormFieldMap> fields) {
        for (FormFieldMap fl : fields) {
            if (fl.values().size() < 2 && !StringUtils.isEmpty(fl.value())) {
                Map<String, String> att = fl.fieldAttributes();
                if (att.containsKey("openmrs_entity") && att.get("openmrs_entity")
                        .equalsIgnoreCase("person_attribute")) {
                    pattributes.put(att.get("openmrs_entity_id"), fl.value());
                }
            }
        }
        return pattributes;
    }

    /**
     * Extract Client from given form submission
     *
     * @param
     * @return
     * @throws ParseException
     */
    public Client getClientFromFormSubmission(FormSubmission fsubmission) throws
            IllegalStateException {
        FormSubmissionMap fs;
        try {
            fs = formAttributeParser.createFormSubmissionMap(fsubmission);
            return createBaseClient(fs);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Client createBaseClient(FormSubmissionMap fs) throws ParseException {
        Log.d(TAG, "createBaseClient: "+fs.formAttributes());


        String firstName = fs.getFieldValue(getFieldName(Person.first_name, fs));
        String middleName = fs.getFieldValue(getFieldName(Person.middle_name, fs));
        String lastName = fs.getFieldValue(getFieldName(Person.last_name, fs));
        String bd = fs.getFieldValue(getFieldName(Person.birthdate, fs));
        DateTime birthdate = bd == null ? null : new DateTime(bd).withTimeAtStartOfDay();
        String dd = fs.getFieldValue(getFieldName(Person.deathdate, fs));
        DateTime deathdate = dd == null ? null : new DateTime(dd).withTimeAtStartOfDay();
        String aproxbd = fs.getFieldValue(getFieldName(Person.birthdate_estimated, fs));
        Boolean birthdateApprox = false;
        if (!StringUtils.isEmpty(aproxbd) && NumberUtils.isNumber(aproxbd)) {
            int bde = 0;
            try {
                bde = Integer.parseInt(aproxbd);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            birthdateApprox = bde > 0;
        }
        String aproxdd = fs.getFieldValue(getFieldName(Person.deathdate_estimated, fs));
        Boolean deathdateApprox = false;
        if (!StringUtils.isEmpty(aproxdd) && NumberUtils.isNumber(aproxdd)) {
            int dde = 0;
            try {
                dde = Integer.parseInt(aproxdd);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            deathdateApprox = dde > 0;
        }
        String gender = fs.getFieldValue(getFieldName(Person.gender, fs));

        List<Address> addresses = new ArrayList<>(extractAddresses(fs).values());

        Client c = (Client) new Client(fs.entityId()).withFirstName(firstName)
                .withMiddleName(middleName).withLastName(lastName)
                .withBirthdate((birthdate != null ? birthdate.toDate() : null), birthdateApprox)
                .withDeathdate(deathdate != null ? deathdate.toDate() : null, deathdateApprox)
                .withGender(gender).withDateCreated(new Date());

        c.withAddresses(addresses).withAttributes(extractAttributes(fs))
                .withIdentifiers(extractIdentifiers(fs));
        for (FormFieldMap ffm:fs.fields()){
            if (ffm.name().equals("relationalid")){
                c.addRelationship(ffm.fieldAttributes().get("openmrs_entity_id"), ffm.value());
            }
        }
        return c;
    }

    public Client createSubformClient(SubformMap subf) throws ParseException {
        String firstName = subf.getFieldValue(getFieldName(Person.first_name, subf));
        String gender = subf.getFieldValue(getFieldName(Person.gender, subf));
        String bb = subf.getFieldValue(getFieldName(Person.birthdate, subf));

        Map<String, String> idents = extractIdentifiers(subf);
        //these bunch of lines are making it impossible to create a child model since a child
        // doesnt have a firstname but only gender
//        if (StringUtils.isEmpty(firstName)
//                && StringUtils.isEmpty(bb)
//                && idents.size() < 1 && StringUtils.isEmpty(gender)) {//we need to ignore uuid
// of entity
//            // if empty repeat group leave this entry and move to next
//            return null;
//        }

        String middleName = subf.getFieldValue(getFieldName(Person.middle_name, subf));
        String lastName = subf.getFieldValue(getFieldName(Person.last_name, subf));
        DateTime birthdate = new DateTime(bb).withTimeAtStartOfDay();
        String dd = subf.getFieldValue(getFieldName(Person.deathdate, subf));
        DateTime deathdate = dd == null ? null : new DateTime(dd).withTimeAtStartOfDay();
        String aproxbd = subf.getFieldValue(getFieldName(Person.birthdate_estimated, subf));
        Boolean birthdateApprox = false;
        if (!StringUtils.isEmpty(aproxbd) && NumberUtils.isNumber(aproxbd)) {
            int bde = 0;
            try {
                bde = Integer.parseInt(aproxbd);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            birthdateApprox = bde > 0;
        }
        String aproxdd = subf.getFieldValue(getFieldName(Person.deathdate_estimated, subf));
        Boolean deathdateApprox = false;
        if (!StringUtils.isEmpty(aproxdd) && NumberUtils.isNumber(aproxdd)) {
            int dde = 0;
            try {
                dde = Integer.parseInt(aproxdd);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            deathdateApprox = dde > 0;
        }

        List<Address> addresses = new ArrayList<>(extractAddressesForSubform(subf).values());

        Client c = (Client) new Client(subf.getFieldValue("id")).withFirstName(firstName)
                .withMiddleName(middleName).withLastName(lastName)
                .withBirthdate(new DateTime(birthdate).toDate(), birthdateApprox)
                .withDeathdate(new DateTime(deathdate).toDate(), deathdateApprox).withGender(gender)
                .withDateCreated(new Date());

        c.withAddresses(addresses).withAttributes(extractAttributes(subf)).withIdentifiers(idents);

        addRelationship(subf, c);

        return c;
    }

    private void addRelationship(SubformMap subformMap, Client client) {
        try {
            String relationships = AssetHandler
                    .readFileFromAssetsFolder(FormUtils.ecClientRelationships, mContext);
            JSONArray jsonArray = null;

            jsonArray = new JSONArray(relationships);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rObject = jsonArray.getJSONObject(i);
                if (rObject.has("field")) {
                    //is this a new child registration, add person relationships -mother
                    if (subformMap.getField(rObject.getString("field")) != null) {

                        client.addRelationship(rObject.getString("client_relationship"),
                                subformMap.getField(rObject.getString("field")).value());

                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    /**
     * Extract Client and Event from given form submission for entities dependent on main
     * beneficiary (excluding main beneficiary).
     * The dependent entities are specified via subforms (repeat groups) in xls forms.
     *
     * @param
     * @return The clients and events Map with id of dependent entity as key. Each entry in Map
     * contains an
     * internal map that holds Client and Event info as "client" and "event" respectively for that
     * dependent entity (whose id is the key of main Map).
     * Ex:
     * {222222-55d555-ffffff-232323-ffffff: {client: ClientObjForGivenID, event:
     * EventObjForGivenIDAndForm}},
     * {339393-545445-ffdddd-333333-ffffff: {client: ClientObjForGivenID, event:
     * EventObjForGivenIDAndForm}},
     * {278383-765766-dddddd-767666-ffffff: {client: ClientObjForGivenID, event:
     * EventObjForGivenIDAndForm}}
     * @throws ParseException
     */
    public Map<String, Map<String, Object>> getDependentClientsFromFormSubmission(
            FormSubmission fsubmission) throws IllegalStateException {
        FormSubmissionMap fs;
        try {
            fs = formAttributeParser.createFormSubmissionMap(fsubmission);
            Map<String, Map<String, Object>> map = new HashMap<>();
            for (SubformMap sbf : fs.subforms()) {
                Map<String, String> att = sbf.formAttributes();
                if (att.containsKey("openmrs_entity") && att.get("openmrs_entity")
                        .equalsIgnoreCase("person")) {
                    Map<String, Object> cne = new HashMap<>();

                    Client subformClient = createSubformClient(sbf);

                    if (subformClient != null) {
                        cne.put("client", subformClient);
                        cne.put("event", getEventForSubform(fs, att.get("openmrs_entity_id"), sbf));

                        map.put(sbf.entityId(), cne);
                    }
                }
            }
            return map;
        } catch (JsonIOException | JsonSyntaxException | XPathExpressionException |
                ParserConfigurationException | SAXException | IOException | ParseException e) {
            Log.e(TAG, e.toString(), e);
            throw new IllegalStateException(e);
        }
    }
}