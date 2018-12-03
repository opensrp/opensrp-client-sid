package org.smartregister.bidan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan.activity.BaseRegisterActivity;
import org.smartregister.bidan.activity.LoginActivity;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.repository.IndonesiaECRepository;
import org.smartregister.bidan.sync.ClientProcessor;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormAttributeParser;
import org.smartregister.clientandeventmodel.FormData;
import org.smartregister.clientandeventmodel.FormField;
import org.smartregister.clientandeventmodel.FormInstance;
import org.smartregister.clientandeventmodel.SubFormData;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.SyncStatus;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.domain.form.SubForm;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BidanFormUtils {

    public static final String[] CLIENT_EVENTS = {"Tambah Bayi", "Identitas Ibu",
            "Dokumentasi Persalinan","Edit Ibu","Edit Bayi"};

    public static final String TAG = "EnketoFormUtils";
    public static final String ecClientRelationships = "ec_client_relationships.json";
    private static final String shouldLoadValueKey = "shouldLoadValue";
    private static final String relationalIdKey = "relational_id";
    private static final String databaseIdKey = "_id";
    private static final String injectedBaseEntityIdKey = "injectedBaseEntityId";
    private static BidanFormUtils instance;
    private Context mContext;
    private org.smartregister.Context theAppContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private IndonesiaECRepository indonesiaECRepository;
//    private BidanFormEntityConverter formEntityConverter;
    private BidanFormEntityConverter formEntityConverter;

    public BidanFormUtils(Context context) throws Exception {
        mContext = context;
        theAppContext = CoreLibrary.getInstance().context();
        FormAttributeParser formAttributeParser = new FormAttributeParser(context);
//        formEntityConverter = new BidanFormEntityConverter(formAttributeParser, mContext);
        formEntityConverter = new BidanFormEntityConverter(formAttributeParser, mContext);
        indonesiaECRepository = BidanApplication.getInstance().indonesiaECRepository();
    }

    public static BidanFormUtils getInstance(Context ctx) throws Exception {
        instance = new BidanFormUtils(ctx);
        return instance;
    }

    private static JSONObject retrieveRelationshipJsonForLink(String link, JSONArray array)
            throws Exception {
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (relationShipExist(link, object)) {
                System.out.println("Relationship found ##");

                return object;
            }
        }

        return null;
    }

    private static boolean relationShipExist(String link, JSONObject json) {
        try {
            String[] path = link.split("\\.");
            String parentTable = path[0];
            String childTable = path[1];

            String jsonParentTableString = json.getString("parent");
            String jsonChildTableString = json.getString("child");

            boolean parentToChildExist =
                    jsonParentTableString.equals(parentTable) && jsonChildTableString
                            .equals(childTable);
            boolean childToParentExist =
                    jsonParentTableString.equals(childTable) && jsonChildTableString
                            .equals(parentTable);

            if (parentToChildExist || childToParentExist) {
                return true;
            }

        } catch (Exception e) {
            android.util.Log.e(TAG, e.toString(), e);
        }

        return false;
    }

    public static int getIndexForFormName(String formName, String[] formNames) {
        for (int i = 0; i < formNames.length; i++) {
            if (formName.equalsIgnoreCase(formNames[i])) {
                return i;
            }
        }

        return -1;
    }

    public FormSubmission generateFormSubmisionFromXMLString(String entity_id, String formData,
                                                             String formName, JSONObject
                                                                     overrides) throws Exception {
        JSONObject formSubmission = XML.toJSONObject(formData);

        //FileUtilities fu = new FileUtilities();
        //fu.write("xmlform.txt", formData);
        //fu.write("xmlformsubmission.txt", formSubmission.toString());
//        System.out.println(entity_id);
//        System.out.println(formSubmission);

        // use the form_definition.json to iterate through fields
        String formDefinitionJson = readFileFromAssetsFolder(
                "www/form/" + formName + "/form_definition.json");
        JSONObject formDefinition = new JSONObject(formDefinitionJson);

        String rootNodeKey = formSubmission.keys().next();

        // retrieve the id, if it fails use the provided value by the param
        entity_id = formSubmission.getJSONObject(rootNodeKey).has(databaseIdKey) ? formSubmission
                .getJSONObject(rootNodeKey).getString(databaseIdKey) : generateRandomUUIDString();

        //String bindPath = formDefinition.getJSONObject("form").getString("bind_type");
        JSONObject fieldsDefinition = formDefinition.getJSONObject("form");
        JSONArray populatedFieldsArray = getPopulatedFieldsForArray(fieldsDefinition, entity_id,
                formSubmission, overrides);

        // replace all the fields in the form
        formDefinition.getJSONObject("form").put("fields", populatedFieldsArray);

        //get the subforms
        if (formDefinition.getJSONObject("form").has("sub_forms")) {
            JSONObject subFormDefinition = formDefinition.getJSONObject("form").
                    getJSONArray("sub_forms").getJSONObject(0);
            // get the bind path for the sub-form, helps us to locate the node that holds the data
            // in the corresponding data json
            String bindPath = subFormDefinition.getString("default_bind_path");

            // get the actual sub-form data
            JSONArray subFormDataArray = new JSONArray();
            Object subFormDataObject = getObjectAtPath(bindPath.split("/"), formSubmission);

            if (subFormDataObject instanceof JSONObject) {
                JSONObject subFormData = (JSONObject) subFormDataObject;
                subFormDataArray.put(0, subFormData);
            } else if (subFormDataObject instanceof JSONArray) {
                subFormDataArray = (JSONArray) subFormDataObject;
            }

            JSONArray subForms = getSubForms(subFormDataArray, entity_id, subFormDefinition,
                    overrides);

            // replace the subforms field with real data
            formDefinition.getJSONObject("form").put("sub_forms", subForms);
            //throw new Exception();
        }

        String instanceId = generateRandomUUIDString();
        String entityId = retrieveIdForSubmission(formDefinition);
        String formDefinitionVersionString = formDefinition
                .getString("form_data_definition_version");

        String clientVersion = String.valueOf(new Date().getTime());
        String instance = formDefinition.toString();
        android.util.Log.d(TAG, "generateFormSubmisionFromXMLString: instance="+instance);
        FormSubmission fs = new FormSubmission(instanceId, entityId, formName, instance,
                clientVersion, SyncStatus.PENDING, formDefinitionVersionString);

        generateClientAndEventModelsForFormSubmission(fs, formName);

        return fs;
    }

    private void generateClientAndEventModelsForFormSubmission(FormSubmission formSubmission,
                                                               String formName) {
        org.smartregister.clientandeventmodel.FormSubmission v2FormSubmission;

        String anmId = CoreLibrary.getInstance().context().anmService().fetchDetails().name();
        String instanceId = formSubmission.instanceId();
        String entityId = formSubmission.entityId();
        Long clientVersion = new Date().getTime();
        String formDataDefinitionVersion = formSubmission.formDataDefinitionVersion();

        String bind_type = formSubmission.getFormInstance().getForm().getBind_type();
        String default_bind_path = formSubmission.getFormInstance().getForm()
                .getDefault_bind_path();

        List<FormField> fields = convertFormFields(formSubmission.getFormInstance().getForm().
                getFields());

        List<SubFormData> sub_forms = getSubFormList(formSubmission);
        FormData formData = new FormData(bind_type, default_bind_path, fields, sub_forms);

        FormInstance formInstance = new FormInstance(formData);
        formInstance.setForm_data_definition_version(formDataDefinitionVersion);

        v2FormSubmission = new org.smartregister.clientandeventmodel.FormSubmission(anmId,
                instanceId, formName, entityId, clientVersion, formDataDefinitionVersion,
                formInstance, clientVersion);

        android.util.Log.d(TAG, "generateClientAndEventModelsForFormSubmission: v2FormSubmission="+v2FormSubmission);
        android.util.Log.d(TAG, "generateClientAndEventModelsForFormSubmission: getInstanceId="+v2FormSubmission.getInstanceId());
        android.util.Log.d(TAG, "generateClientAndEventModelsForFormSubmission: entityId="+v2FormSubmission.entityId());

        Event e = formEntityConverter.getEventFromFormSubmission(v2FormSubmission);

        android.util.Log.d(TAG, "generateClientAndEventModelsForFormSubmission: Event="+e);

        org.smartregister.util.Utils.startAsyncTask(new SavePatientAsyncTask(v2FormSubmission, mContext, e), null);
    }

    private void saveClient(Client client) {
        if (client.getGender() == null || client.getGender().isEmpty()){
            client.setGender("female");
        }

        Log.logDebug("============== CLIENT ================");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        String clientJson = gson.toJson(client);
        Log.logDebug(clientJson);
        try {
            indonesiaECRepository.addorUpdateClient(client.getBaseEntityId(), new JSONObject(clientJson));
        } catch (JSONException e) {
            android.util.Log.e(TAG, e.toString(), e);
        }
        Log.logDebug("====================================");

    }

    private void saveEvent(Event event) {
        Log.logDebug("============== EVENT ================");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        String eventJson = gson.toJson(event);
        Log.logDebug("event ==> "+eventJson);
        try {
            indonesiaECRepository.addEvent(event.getBaseEntityId(), new JSONObject(eventJson));
        } catch (JSONException e) {
            android.util.Log.e(TAG, e.toString(), e);
        }
    }

    private List<SubFormData> getSubFormList(FormSubmission formSubmission) {
        List<SubFormData> sub_forms = new ArrayList<>();
        List<SubForm> subForms = formSubmission.getFormInstance().getForm().getSub_forms();
        if (subForms != null) {
            for (SubForm sf : subForms) {
                SubFormData sd = new SubFormData();
                sd.setDefault_bind_path(sf.getDefaultBindPath());
                sd.setBind_type(sf.getBindType());

                List<FormField> subFormFields = convertFormFields(sf.getFields());
                sd.setFields(subFormFields);

                sd.setInstances(sf.getInstances());
                sd.setName(sf.getName());
                sub_forms.add(sd);
            }
        }

        return sub_forms;
    }

    private List<FormField> convertFormFields(List<org.smartregister.domain.form.FormField>
                                                      formFields) {
        List<FormField> fields = new ArrayList<>();
        for (org.smartregister.domain.form.FormField ff : formFields) {
            FormField f = new FormField(ff.getName(), ff.getValue(), ff.getSource());
            fields.add(f);
        }

        return fields;
    }

    private JSONArray getSubForms(JSONArray subFormDataArray, String entity_id, JSONObject
            subFormDefinition, JSONObject overrides) throws Exception {
        JSONArray subForms = new JSONArray();

        JSONArray subFormFields = getFieldsArrayForSubFormDefinition(subFormDefinition);
        JSONArray subFormInstances = new JSONArray();

        // the id of each subform is contained in the attribute of the enclosing element
        for (int i = 0; i < subFormDataArray.length(); i++) {
            JSONObject subFormData = subFormDataArray.getJSONObject(i);
            String relationalId =
                    subFormData.has(relationalIdKey) ? subFormData.getString(relationalIdKey)
                            : entity_id;
            String id = subFormData.has(databaseIdKey) ? subFormData.getString(databaseIdKey)
                    : generateRandomUUIDString();
            JSONObject subFormInstance = getFieldValuesForSubFormDefinition(subFormDefinition,
                    relationalId, id, subFormData, overrides);
            subFormInstances.put(i, subFormInstance);
        }

        subFormDefinition.put("instances", subFormInstances);
        subFormDefinition.put("fields", subFormFields);
        subForms.put(0, subFormDefinition);

        return subForms;
    }

    public String generateXMLInputForFormWithEntityId(String entityId, String formName, String
            overrides) {
        try {
            // get the field overrides map
            JSONObject fieldOverrides = new JSONObject();
            if (overrides != null) {
                fieldOverrides = new JSONObject(overrides);
                String overridesStr = fieldOverrides.getString("fieldOverrides");
                fieldOverrides = new JSONObject(overridesStr);
            }

            // use the form_definition.json to get the form mappings
            String formDefinitionJson = readFileFromAssetsFolder(
                    "www/form/" + formName + "/form_definition.json");
            JSONObject formDefinition = new JSONObject(formDefinitionJson);
            String ec_bind_path = formDefinition.getJSONObject("form").getString("ec_bind_type");

            String sql =
                    "select * from " + ec_bind_path + " where base_entity_id='" + entityId + "'";
            Map<String, String> dbEntity = theAppContext.formDataRepository().
                    getMapFromSQLQuery(sql,null);
            Map<String, String> detailsMap = new HashMap<>();
            if(entityId != null){
                detailsMap = theAppContext.detailsRepository().getAllDetailsForClient(entityId);
            }
            detailsMap.putAll(dbEntity);

            JSONObject entityJson = new JSONObject();
            if (detailsMap != null && !detailsMap.isEmpty()) {
                entityJson = new JSONObject(detailsMap);
            }

            //read the xml form model, the expected form model that is passed to the form mirrors it
            String formModelString = readFileFromAssetsFolder(
                    "www/form/" + formName + "/model" + ".xml").replaceAll("\n", " ")
                    .replaceAll("\r", " ");
            android.util.Log.d(TAG, "generateXMLInputForFormWithEntityId: formModelString="+formModelString);
            InputStream is = new ByteArrayInputStream(formModelString.getBytes());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(is);

            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);

            //skip processing <model><instance>
            NodeList els = ((Element) document.getElementsByTagName("model").item(0)).
                    getElementsByTagName("instance");
            Element el = (Element) els.item(0);
            NodeList entries = el.getChildNodes();
            int num = entries.getLength();
            for (int i = 0; i < num; i++) {
                Node n = entries.item(i);
                if (n instanceof Element) {
                    Element node = (Element) n;
                    writeXML(node, serializer, fieldOverrides, formDefinition, entityJson, null);
                }
            }

            serializer.endDocument();

            String xml = writer.toString();
            // Add model and instance tags
            xml = xml.substring(56);
//            System.out.println(xml);
            android.util.Log.d(TAG, "generateXMLInputForFormWithEntityId: "+xml);

            return xml;

        } catch (Exception e) {
            android.util.Log.e(TAG, e.toString(), e);
        }
        return "";
    }

    private void writeXML(Element node, XmlSerializer serializer, JSONObject fieldOverrides,
                          JSONObject formDefinition, JSONObject entityJson, String parentId) {
        try {
            String nodeName = node.getNodeName();
            android.util.Log.d(TAG, "writeXML: nodeName="+nodeName);
            String entityId =
                    entityJson.has("id") ? entityJson.getString("id") : generateRandomUUIDString();
            android.util.Log.d(TAG, "writeXML: entityId="+entityId);
            String relationalId =
                    entityJson.has(relationalIdKey) ? entityJson.getString(relationalIdKey)
                            : parentId;

            serializer.startTag("", nodeName);

            // write the xml attributes
            writeXMLAttributes(node, serializer, entityId, relationalId);

            String nodeValue = retrieveValueForNodeName(nodeName, entityJson, formDefinition);
            //overwrite the node value with contents from overrides map
            if (fieldOverrides.has(nodeName)) {
                nodeValue = fieldOverrides.getString(nodeName);
            }
            //write the node value
            if (nodeValue != null) {
                serializer.text(nodeValue);
            }

            List<String> subFormNames = getSubFormNames(formDefinition);

            // get all child nodes
            NodeList entries = node.getChildNodes();
            int num = entries.getLength();
            for (int i = 0; i < num; i++) {
                if (entries.item(i) instanceof Element) {
                    Element child = (Element) entries.item(i);
                    String fieldName = child.getNodeName();
                    android.util.Log.d(TAG, "writeXML: fieldName="+fieldName);

                    // its a subform element process it
                    if (!subFormNames.isEmpty() && subFormNames.contains(fieldName)) {
                        // its a subform element process it
                        // get the subform definition
                        JSONArray subForms = formDefinition.getJSONObject("form").
                                getJSONArray("sub_forms");
                        JSONObject subFormDefinition = retriveSubformDefinitionForBindPath(subForms,
                                fieldName);
                        if (subFormDefinition != null) {

                            String childTableName = subFormDefinition.getString("ec_bind_type");
                            String sql = "select * from '" + childTableName + "' where "
                                    + "relational_id = '" + entityId + "'";
                            String childRecordsString = theAppContext.formDataRepository().
                                    queryList(sql,null);
                            JSONArray childRecords = new JSONArray(childRecordsString);

                            JSONArray fieldsArray = subFormDefinition.getJSONArray("fields");
                            // check whether we are supposed to load the id of the child record
                            JSONObject idFieldDefn = getJsonFieldFromArray("id", fieldsArray);

                            // definition for id
                            boolean shouldLoadId =
                                    idFieldDefn.has(shouldLoadValueKey) && idFieldDefn
                                            .getBoolean(shouldLoadValueKey);

                            if (shouldLoadId && childRecords.length() > 0) {
                                for (int k = 0; k < childRecords.length(); k++) {
                                    JSONObject childEntityJson = childRecords.getJSONObject(k);
                                    JSONObject obj = getCombinedJsonObjectForObject(
                                            childEntityJson);
                                    writeXML(child, serializer, fieldOverrides, subFormDefinition,
                                            childEntityJson, entityId);
                                }

                            }
                        }
                    } else {
                        // it's not a sub-form element write its value
                        serializer.startTag("", fieldName);
                        // write the xml attributes
                        // a value node doesn't have id or relationalId fields
                        writeXMLAttributes(child, serializer, null, null);
                        // write the node value
                        String value = retrieveValueForNodeName(fieldName, entityJson,
                                formDefinition);
                        // write the node value

                        android.util.Log.d(TAG, "writeXML: value="+value);

                        // overwrite the node value with contents from overrides map
                        if (fieldOverrides.has(fieldName)) {
                            serializer.text(fieldOverrides.getString(fieldName));
                        } else if (value != null) {
                            serializer.text(value);
                        }

                        serializer.endTag("", fieldName);
                    }
                }
            }

            serializer.endTag("", node.getNodeName());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve additional details for this record from the details Table.
     *
     * @param entityJson JSONObject
     * @return entityJson
     */
    private JSONObject getCombinedJsonObjectForObject(JSONObject entityJson) {
        try {
            String baseEntityId = entityJson.getString("base_entity_id");
            Map<String, String> map = theAppContext.detailsRepository().
                    getAllDetailsForClient(baseEntityId);

            for (String key : map.keySet()) {
                if (!entityJson.has(key)) {
                    entityJson.put(key, map.get(key));
                }
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, e.toString(), e);
        }
        return entityJson;
    }

    /**
     * Iterate through the provided array and retrieve a json object whose name attribute matches
     * the name supplied
     *
     * @param fieldName
     * @param array
     * @return
     */
    private JSONObject getJsonFieldFromArray(String fieldName, JSONArray array) {
        try {
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject field = array.getJSONObject(i);
                    String name = field.has("name") ? field.getString("name") : null;
                    if (name.equals(fieldName)) {
                        return field;
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, e.toString(), e);
        }
        return null;
    }

    /**
     * retrieves node value for cases which the nodename don't match the name of the xml element
     *
     * @param nodeName
     * @param entityJson
     * @param formDefinition
     * @return
     */
    private String retrieveValueForNodeName(String nodeName, JSONObject entityJson, JSONObject
            formDefinition) {
        try {
            if (entityJson != null && entityJson.length() > 0) {
                JSONObject fieldsObject =
                        formDefinition.has("form") ? formDefinition.getJSONObject("form")
                                : formDefinition.has("sub_forms") ? formDefinition
                                .getJSONObject("sub_forms") : formDefinition;
                if (fieldsObject.has("fields")) {
                    JSONArray fields = fieldsObject.getJSONArray("fields");
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject field = fields.getJSONObject(i);
                        String bindPath = field.has("bind") ? field.getString("bind") : null;
                        String name = field.has("name") ? field.getString("name") : null;

                        boolean matchingNodeFound =
                                bindPath != null && name != null && bindPath.endsWith(nodeName)
                                        || name != null && name.equals(nodeName);

                        if (matchingNodeFound) {
                            if (field.has("shouldLoadValue") && field
                                    .getBoolean("shouldLoadValue")) {
                                String keyName = entityJson.has(nodeName) ? nodeName : name;
                                if (entityJson.has(keyName)) {
                                    return entityJson.getString(keyName);
                                } else {
                                    return "";
                                }
                            } else {
                                // the shouldLoadValue flag isn't set
                                return "";
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, e.toString(), e);
        }

        return "";
    }

    private List<String> getSubFormNames(JSONObject formDefinition) throws Exception {
        List<String> subFormNames = new ArrayList<String>();
        if (formDefinition.has("form") && formDefinition.getJSONObject("form").has("sub_forms")) {
            JSONArray subForms = formDefinition.getJSONObject("form").getJSONArray("sub_forms");
            for (int i = 0; i < subForms.length(); i++) {
                JSONObject subForm = subForms.getJSONObject(i);
                String subFormNameStr = subForm.getString("default_bind_path");
                String[] path = subFormNameStr.split("/");
                String subFormName = path[path.length - 1]; // the last token
                subFormNames.add(subFormName);
            }
        }

        return subFormNames;
    }

    private JSONObject retriveSubformDefinitionForBindPath(JSONArray subForms, String fieldName)
            throws Exception {
        for (int i = 0; i < subForms.length(); i++) {
            JSONObject subForm = subForms.getJSONObject(i);
            String subFormNameStr = subForm.getString("default_bind_path");
            String[] path = subFormNameStr.split("/");
            String subFormName = path[path.length - 1]; // the last token

            if (fieldName.equalsIgnoreCase(subFormName)) {
                return subForm;
            }
        }

        return null;
    }

    private void writeXMLAttributes(Element node, XmlSerializer serializer, String id, String
            relationalId) {
        try {
            // get a map containing the attributes of this node
            NamedNodeMap attributes = node.getAttributes();

            // get the number of nodes in this map
            int numAttrs = attributes.getLength();

            if (id != null) {
                serializer.attribute("", databaseIdKey, id);
            }

            if (relationalId != null) {
                serializer.attribute("", relationalIdKey, relationalId);
            }

            for (int i = 0; i < numAttrs; i++) {
                Attr attr = (Attr) attributes.item(i);
                String attrName = attr.getNodeName();
                String attrValue = attr.getNodeValue();
                serializer.attribute("", attrName, attrValue);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }

    private String retrieveIdForSubmission(JSONObject jsonObject) throws Exception {
        JSONArray fields = jsonObject.getJSONObject("form").getJSONArray("fields");

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);

            if (field.has("name") && field.getString("name").equalsIgnoreCase("id")) {
                return field.getString("value");
            }
        }
        return null;
    }

    public Object getObjectAtPath(String[] path, JSONObject jsonObject) throws Exception {
        JSONObject object = jsonObject;
        int i = 0;
        while (i < path.length - 1) {
            if (object.has(path[i])) {
                Object o = object.get(path[i]);
                if (o instanceof JSONObject) {
                    object = object.getJSONObject(path[i]);
                } else if (o instanceof JSONArray) {
                    object = object.getJSONArray(path[i]).getJSONObject(0);
                }
            }
            i++;
        }
        return object.has(path[i]) ? object.get(path[i]) : null;
    }

    public JSONArray getPopulatedFieldsForArray(JSONObject fieldsDefinition, String entityId,
                                                JSONObject jsonObject, JSONObject overrides)
            throws Exception {
        String bindPath = fieldsDefinition.getString("bind_type");
        String sql = "select * from " + bindPath + " where id='" + entityId + "'";
        String dbEntity = theAppContext.formDataRepository().queryUniqueResult(sql,null);

        JSONObject entityJson = new JSONObject();

        if (dbEntity != null && !dbEntity.isEmpty()) {
            entityJson = new JSONObject(dbEntity);
        }

        JSONArray fieldsArray = fieldsDefinition.getJSONArray("fields");

        for (int i = 0; i < fieldsArray.length(); i++) {
            JSONObject item = fieldsArray.getJSONObject(i);

            if (!item.has("name")) {
                continue; // skip elements without name
            }

            String itemName = item.getString("name");
            boolean shouldLoadValue =
                    item.has("shouldLoadValue") && item.getBoolean("shouldLoadValue");

            if (item.has("bind")) {
                String pathSting = item.getString("bind");
                pathSting = pathSting.startsWith("/") ? pathSting.substring(1) : pathSting;
                String[] path = pathSting.split("/");
                String value = getValueForPath(path, jsonObject);
                item.put("value", value);
            }

            if (shouldLoadValue && overrides.has(item.getString("name"))) {
                // if the value is not set use the value in the overrides filed
                if (!item.has("value")) {
                    item.put("value", overrides.getString(item.getString("name")));
                }
            }

            // map the id field for child elements
            if (isForeignIdPath(item)) {
                String value = null;
                if (entityJson.length() > 0 && shouldLoadValue) {
                    //retrieve the child attributes
                    value = retrieveValueForLinkedRecord(item.getString("source"), entityJson);
                }

                // generate uuid if its still not available
                if (item.getString("source").endsWith(".id") && value == null) {
                    value = generateRandomUUIDString();
                }

                if (value != null && !item.has("value")) {
                    item.put("value", value);
                }
            }

            // add source property if not available
            if (!item.has("source")) {
                item.put("source", bindPath + "." + item.getString("name"));
            }

            if (itemName.equalsIgnoreCase("id") && !isForeignIdPath(item)) {
                assert entityId != null;
                item.put("value", entityId);
            }

            if (itemName.equalsIgnoreCase(injectedBaseEntityIdKey)) {
                assert entityId != null;
                item.put("value", entityId);
            }

            if (itemName.equalsIgnoreCase("start") || itemName.equalsIgnoreCase("end")) {
                try {
                    boolean isEndTime = itemName.equalsIgnoreCase("end");
                    String val =
                            item.has("value") ? item.getString("value") : sdf.format(new Date());

                    if (isEndTime) {
                        val = formatter.format(new Date());
                    } else {
                        Date d = sdf.parse(val);
                        //parse the date to match OpenMRS format
                        val = formatter.format(d);
                    }

                    item.put("value", val);
                } catch (Exception e) {
                    android.util.Log.e(TAG, e.toString(), e);
                }
            }
        }
        return fieldsArray;
    }

    private boolean isForeignIdPath(JSONObject item) throws Exception {
        // e.g ibu.anak.id
        return item.has("source") && item.getString("source").split("\\.").length > 2;
    }

    public String retrieveValueForLinkedRecord(String link, JSONObject entityJson) {
        try {
            String entityRelationships = readFileFromAssetsFolder(
                    "www/form/entity_relationship" + ".json");
            JSONArray json = new JSONArray(entityRelationships);
            Log.logInfo(json.toString());

            JSONObject rJson;

            if ((rJson = retrieveRelationshipJsonForLink(link, json)) != null) {
                String[] path = link.split("\\.");
                String parentTable = path[0];
                String childTable = path[1];

                String joinValueKey =
                        parentTable.equals(rJson.getString("parent")) ? rJson.getString("from")
                                : rJson.getString("to");
                joinValueKey = joinValueKey.contains(".") ? joinValueKey
                        .substring(joinValueKey.lastIndexOf(".") + 1) : joinValueKey;

                String val = entityJson.getString(joinValueKey);

                String joinField =
                        parentTable.equals(rJson.getString("parent")) ? rJson.getString("to")
                                : rJson.getString("from");
                String sql =
                        "select * from " + childTable + " where " + joinField + "='" + val + "'";
                Log.logInfo(sql);
                String dbEntity = theAppContext.formDataRepository().queryUniqueResult(sql,null);
                JSONObject linkedEntityJson = new JSONObject();

                if (dbEntity != null && !dbEntity.isEmpty()) {
                    linkedEntityJson = new JSONObject(dbEntity);
                }

                // finally retrieve the value from the child entity, need to improve or remove
                // entirely these hacks
                String sourceKey = link.substring(link.lastIndexOf(".") + 1);

                if (linkedEntityJson.has(sourceKey)) {
                    return linkedEntityJson.getString(sourceKey);
                }
            }

        } catch (Exception e) {
            android.util.Log.e(TAG, e.toString(), e);
        }
        return null;
    }

    public JSONArray getFieldsArrayForSubFormDefinition(JSONObject fieldsDefinition) throws
            Exception {
        JSONArray fieldsArray = fieldsDefinition.getJSONArray("fields");
        String bindPath = fieldsDefinition.getString("bind_type");

        JSONArray subFormFieldsArray = new JSONArray();

        for (int i = 0; i < fieldsArray.length(); i++) {
            JSONObject field = new JSONObject();
            JSONObject item = fieldsArray.getJSONObject(i);

            if (!item.has("name")) {
                continue; // skip elements without name
            }

            field.put("name", item.getString("name"));

            if (!item.has("source")) {
                field.put("source", bindPath + "." + item.getString("name"));
            } else {
                field.put("source", bindPath + "." + item.getString("source"));
            }

            subFormFieldsArray.put(i, field);
        }

        return subFormFieldsArray;
    }

    public JSONObject getFieldValuesForSubFormDefinition(JSONObject fieldsDefinition, String
            relationalId, String entityId, JSONObject jsonObject, JSONObject overrides) throws
            Exception {

        JSONArray fieldsArray = fieldsDefinition.getJSONArray("fields");

        JSONObject fieldsValues = new JSONObject();

        for (int i = 0; i < fieldsArray.length(); i++) {
            JSONObject item = fieldsArray.getJSONObject(i);

            if (!item.has("name")) {
                continue; // skip elements without name
            }

            if (item.has("bind")) {
                String pathSting = item.getString("bind");
                pathSting = pathSting.startsWith("/") ? pathSting.substring(1) : pathSting;
                String[] path = pathSting.split("/");

                //check if we need to override this val
                if (overrides.has(item.getString("name"))) {
                    fieldsValues.put(item.getString("name"),
                            overrides.getString(item.getString("name")));
                } else {
                    String value = getValueForPath(path, jsonObject);
                    fieldsValues.put(item.getString("name"), value);
                }
            }

            //TODO: generate the id for the record
            if (item.has("name") && item.getString("name").equalsIgnoreCase("id")) {
                String id = entityId != null ? entityId : generateRandomUUIDString();
                fieldsValues.put(item.getString("name"), id);
            }

            //TODO: generate the relational for the record
            if (item.has("name") && item.getString("name").equalsIgnoreCase(relationalIdKey)) {
                fieldsValues.put(item.getString("name"), relationalId);
            }

            //TODO: generate the injectedBaseEntityIdKey for the record
            if (item.has("name") && item.getString("name")
                    .equalsIgnoreCase(injectedBaseEntityIdKey)) {
                fieldsValues.put(item.getString("name"), relationalId);
            }

            populateRelationField(item, fieldsValues, relationalId);
        }
        return fieldsValues;
    }

    private String getECClientRelationships() {
        return AssetHandler.readFileFromAssetsFolder(ecClientRelationships, mContext);
    }

    /**
     * see if the current field is a client_relationship field, if so set it's value to the
     * relationId since that's the parent base_entity_id
     *
     * @param fieldItem
     * @param fieldsValues
     * @param relationalId
     */
    private void populateRelationField(JSONObject fieldItem, JSONObject fieldsValues, String
            relationalId) {
        try {
            if (fieldItem.has("name")) {
                String fieldName = fieldItem.getString("name");
                String relationships = getECClientRelationships();
                JSONArray jsonArray = new JSONArray(relationships);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject rObject = jsonArray.getJSONObject(i);
                    if (rObject.has("field") && rObject.getString("field")
                            .equalsIgnoreCase(fieldName)) {
                        fieldsValues.put(fieldName, relationalId);
                    }
                }
            }

        } catch (JSONException e) {
            android.util.Log.e(TAG, e.toString(), e);
        }
    }

    public String getValueForPath(String[] path, JSONObject jsonObject) throws Exception {
        JSONObject object = jsonObject;
        String value = null;
        int i = 0;

        while (i < path.length - 1) {
            if (object.has(path[i])) {
                Object o = object.get(path[i]);
                if (o instanceof JSONObject) {
                    object = object.getJSONObject(path[i]);
                } else if (o instanceof JSONArray) {
                    object = object.getJSONArray(path[i]).getJSONObject(0);
                }
            }

            i++;
        }
        Object valueObject = object.has(path[i]) ? object.get(path[i]) : null;

        if (valueObject == null) {
            return value;
        }
        if (valueObject instanceof JSONObject && ((JSONObject) valueObject).has("content")) {
            value = ((JSONObject) object.get(path[i])).getString("content");
        } else if (valueObject instanceof JSONArray) {
            value = ((JSONArray) valueObject).get(0).toString();
        } else if (!(valueObject instanceof JSONObject)) {
            value = valueObject.toString();
        }

        return value;
    }

    private String readFileFromAssetsFolder(String fileName) {
        String fileContents = null;
        try {
            InputStream is = mContext.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            android.util.Log.e(TAG, ex.toString(), ex);

            return null;
        }

        //Log.d("File", fileContents);

        return fileContents;
    }

    /*private void createNewEventDocument(org.smartregister.cloudant.models.Event event) {
        mCloudantDataHandler.createEventDocument(event);
    }

    private void createNewClientDocument(org.smartregister.cloudant.models.Client client) {
        mCloudantDataHandler.createClientDocument(client);
    }

    private void updateClientDocument(org.smartregister.cloudant.models.Client client) throws ConflictException {
        mCloudantDataHandler.updateDocument(client);
    }*/
    private Event tagSyncMetadata(Event event) {
        AllSharedPreferences sharedPreferences = BidanApplication.getInstance().getContext().userService().getAllSharedPreferences();
        String locations = org.smartregister.util.Utils.getPreference(mContext, LoginActivity.PREF_TEAM_LOCATIONS, "");
        event.setLocationId(locations);

        return event;
    }

    class SavePatientAsyncTask extends android.os.AsyncTask<Void, Void, Void> {
        private final org.smartregister.clientandeventmodel.FormSubmission formSubmission;
        private Context context;
        private Event event;

        public SavePatientAsyncTask(org.smartregister.clientandeventmodel.FormSubmission formSubmission, Context context, Event event) {
            this.formSubmission = formSubmission;
            this.context = context;
            this.event = event;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            android.util.Log.e(TAG, "onPostExecute: " );
            if (context instanceof BaseRegisterActivity) {
                final BaseRegisterActivity registerActivity = ((BaseRegisterActivity) context);
                registerActivity.refreshList(FetchStatus.fetched);
                registerActivity.hideProgressDialog();
            }

//            Utils.postEvent(new EnketoFormSaveCompleteEvent(this.formSubmission.formName()));
        }

        @Override
        protected void onPreExecute() {
//            Utils.postEvent(new ShowProgressDialogEvent());
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

                event = tagSyncMetadata(event);
                String eventType = event.getEventType();
                saveEvent(event);

                Gson gson = new GsonBuilder().create();

                if (eventType.equals(AllConstantsINA.FormNames.KI_FORM_TITLE)) {
                    Client client = formEntityConverter.getClientFromFormSubmission(formSubmission);
                    saveClient(client);
                } else if (eventType.equals(AllConstantsINA.FormNames.ANC_REGISTRATION)) {
                    JSONObject json = indonesiaECRepository.getClientByBaseEntityId(event.getBaseEntityId());
                    Client client = gson.fromJson(json.toString(), Client.class);
                    Client cin = formEntityConverter.getClientFromFormSubmission(formSubmission);
                    client.addAttribute("anc_id",cin.getAttributes().get("anc_id"));
                    saveClient(client);
                } else if (eventType.equals(AllConstantsINA.FormNames.DOKUMENTASI_PERSALINAN)) {
                    JSONObject json = indonesiaECRepository.getClientByBaseEntityId(event.getBaseEntityId());
                    Client client = gson.fromJson(json.toString(), Client.class);

                    Map<String, Map<String, Object>> dep = formEntityConverter.
                            getDependentClientsFromFormSubmission(formSubmission);
                    for (Map<String, Object> cm : dep.values()) {
                        Client cin = (Client) cm.get("client");
                        String uuid = UUID.randomUUID().toString();
                        cin.setBaseEntityId(uuid);
                        saveClient(cin);
                        Event evin = (Event) cm.get("event");
                        evin = tagSyncMetadata(evin);
                        evin.setBaseEntityId(uuid);
                        saveEvent(evin);
                        List<String> childs = client.findRelatives("childId");
                        if (childs == null){
                            childs = new ArrayList<>();
                        }
                        childs.add(0,uuid);
                        Map<String, List<String>> relationships = new HashMap<>();
                        relationships.put("childId",childs);
                        client.withRelationships(relationships);
                    }

                    saveClient(client);
                } else if (eventType.equals(AllConstantsINA.FormNames.CHILD_FORM_TITLE)) {
                    JSONObject json = indonesiaECRepository.getClientByBaseEntityId(event.getBaseEntityId());
                    Client client = gson.fromJson(json.toString(), Client.class);
                    Map<String, Map<String, Object>> dep = formEntityConverter.
                            getDependentClientsFromFormSubmission(formSubmission);
                    for (Map<String, Object> cm : dep.values()) {
                        Client cin = (Client) cm.get("client");
                        String uuid = UUID.randomUUID().toString();
                        cin.setBaseEntityId(uuid);
                        saveClient(cin);
                        Event evin = (Event) cm.get("event");
                        evin = tagSyncMetadata(evin);
                        evin.setBaseEntityId(uuid);
                        saveEvent(evin);
                        List<String> childs = client.findRelatives("childId");
                        if (childs == null){
                            childs = new ArrayList<>();
                        }
                        childs.add(0,uuid);
                        Map<String, List<String>> relationships = new HashMap<>();
                        relationships.put("childId",childs);
                        client.withRelationships(relationships);
                    }

                    saveClient(client);
                } else if (eventType.equals(AllConstantsINA.FormNames.EC_EDIT)) {
                    Client client = formEntityConverter.getClientFromFormSubmission(formSubmission);
                    saveClient(client);
                }
                else if (eventType.equals(AllConstantsINA.FormNames.CHILD_EDIT)) {
                    Client client = formEntityConverter.getClientFromFormSubmission(formSubmission);
                    String ibuCaseId = formSubmission.instance().form().getField("ibuCaseId");
                    client.addRelationship("ibuCaseId",ibuCaseId);
                    saveClient(client);
                }

                long lastSyncTimeStamp = allSharedPreferences.fetchLastUpdatedAtDate(0);
                Date lastSyncDate = new Date(lastSyncTimeStamp);
                ClientProcessor.getInstance(context).processClient(indonesiaECRepository.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
                allSharedPreferences.saveLastUpdatedAtDate(lastSyncDate.getTime());
            } catch (Exception e) {
                android.util.Log.e(TAG, e.toString(), e);
            }
            return null;
        }
    }

}