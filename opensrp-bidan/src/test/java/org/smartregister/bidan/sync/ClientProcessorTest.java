package org.smartregister.bidan.sync;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan.sync.mock.MockEditor;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.util.AssetHandler;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Created by sid-tech on 4/26/18
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class, AssetHandler.class, CloudantDataHandler.class, PreferenceManager.class, CoreLibrary.class})
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
public class ClientProcessorTest {

    @Mock
    private Context context;

//    @Rule
//    public PowerMockRule rule = new PowerMockRule();

    private ClientProcessor clientProcessor;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private DetailsRepository detailsRepository;
    @Mock
    private CloudantDataHandler cloudantDataHandler;
    @Mock
    private CommonRepository cr;
    @Mock
    private SharedPreferences sharedPreferences;
    private static final String LAST_SYNC_DATE = "LAST_SYNC_DATE";

    //    private ActivityController<MockActivity> controller;
//    private MockActivity activity;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
//        CoreLibrary.init(context);
//        Mockito.when(context.detailsRepository()).thenReturn(detailsRepository);
//        PowerMockito.mockStatic(CoreLibrary.class);
//        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
//        PowerMockito.when(coreLibrary.context()).thenReturn(context);
//        PowerMockito.when(context.commonrepository(Mockito.anyString())).thenReturn(cr);
//        PowerMockito.when(cr.executeInsertStatement(Mockito.any(ContentValues.class), Mockito.anyString())).thenReturn(1l);

        PowerMockito.mockStatic(AssetHandler.class);
        PowerMockito.when(AssetHandler.readFileFromAssetsFolder("ec_client_classification.json", context.applicationContext())).thenReturn(ClientData.clientClassificationJson);
        PowerMockito.when(AssetHandler.readFileFromAssetsFolder("ec_client_fields.json", context.applicationContext())).thenReturn(ClientData.ec_client_fields_json);
        PowerMockito.when(AssetHandler.readFileFromAssetsFolder("ec_client_alerts.json", context.applicationContext())).thenReturn(ClientData.ec_client_alerts);
    }

    @Test
    public void processClientTest() throws Exception {
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<>();
        for (int i = 0; i < eventArray.length(); i++) {
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }

        PowerMockito.mockStatic(CloudantDataHandler.class);
        PowerMockito.when(CloudantDataHandler.getInstance(context.applicationContext())).thenReturn(cloudantDataHandler);
        PowerMockito.when(cloudantDataHandler.getUpdatedEventsAndAlerts(Mockito.any(Date.class))).thenReturn(eventList);
        PowerMockito.when(cloudantDataHandler.getClientByBaseEntityId(Mockito.anyString())).thenReturn(clientList.get(0));

        clientProcessor = new ClientProcessor(context.applicationContext());
        PowerMockito.mockStatic(PreferenceManager.class);
        PowerMockito.when(PreferenceManager.getDefaultSharedPreferences(context.applicationContext())).thenReturn(sharedPreferences);
        PowerMockito.when(sharedPreferences.getLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(0L);
        SharedPreferences.Editor edit = Mockito.mock(SharedPreferences.Editor.class);
        PowerMockito.when(sharedPreferences.edit()).thenReturn(edit);
        PowerMockito.when(edit.putLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(MockEditor.getEditor());

        clientProcessor.processClient();
//        clientProcessor.processClient(eventList);
    }

    @Test
    public void assertProcessFieldReturnsTrue() throws Exception {
        final JSONObject fieldObject = new JSONObject(ClientData.ec_client_fields_json);
        final ArrayList<JSONObject> eventList = new ArrayList<>();
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        for (int i = 0; i < eventArray.length(); i++) {
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }
        PowerMockito.mockStatic(CloudantDataHandler.class);
        PowerMockito.when(CloudantDataHandler.getInstance(context.applicationContext())).thenReturn(cloudantDataHandler);
        clientProcessor = new ClientProcessor(context.applicationContext());
        JSONArray fieldArray = fieldObject.getJSONArray("bindobjects");
        Assert.assertEquals(clientProcessor.processField(fieldArray.getJSONObject(0), eventList.get(0), clientList.get(0)), Boolean.TRUE);
//        for(int i=0;i<fieldArray.length();i++){
//            JSONArray columns = fieldArray.getJSONObject(i).getJSONArray("columns");
//            for(int j =0;j<columns.length();j++){
//      Assert.assertEquals(clientProcessor.processField(columns.getJSONObject(j).getJSONObject("json_mapping"),eventList.get(0),clientList.get(0)),Boolean.TRUE);
//            }
//
//        }

    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCall() throws Exception {
        PowerMockito.mockStatic(CloudantDataHandler.class);
        PowerMockito.when(CloudantDataHandler.getInstance(Mockito.any(android.content.Context.class))).thenReturn(cloudantDataHandler);
        Assert.assertNotNull(new ClientProcessor(context.applicationContext()));
        Assert.assertNotNull(ClientProcessor.getInstance(context.applicationContext()));

    }

//    @Test
//    public void ProcessEventReturnsNotNull() throws Exception {
//        clientProcessor = new ClientProcessor(context.applicationContext());
//        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
//        final ArrayList<JSONObject> eventList = new ArrayList<>();
//        for (int i = 0; i < eventArray.length(); i++) {
//            eventList.add(eventArray.getJSONObject(i));
//        }
////        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
////        ArrayList<JSONObject> clientList = new ArrayList<>();
////        for (int i = 0; i < clientArray.length(); i++) {
////            clientList.add(clientArray.getJSONObject(i));
////        }
//        Assert.assertNotNull(clientProcessor.processEvent(eventList.get(0), new JSONObject(ClientData.clientClassificationJson)));
//
//    }

    @Test
    public void getClientAddressAsMapReturnsNotNull() throws Exception {
        clientProcessor = new ClientProcessor(context.applicationContext());
//        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
//        final ArrayList<JSONObject> eventList = new ArrayList<>();
//        for (int i = 0; i < eventArray.length(); i++) {
//            eventList.add(eventArray.getJSONObject(i));
//        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }
        Assert.assertNotNull(clientProcessor.getClientAddressAsMap(clientList.get(0)));

    }

    @Test
    public void updateClientDetailsTableCallsSaveClientDetails() throws Exception {
        clientProcessor = new ClientProcessor(context.applicationContext());
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<>();
        for (int i = 0; i < eventArray.length(); i++) {
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }
        ClientProcessor clientProcessorspy = Mockito.spy(clientProcessor);
        clientProcessorspy.updateClientDetailsTable(eventList.get(0), clientList.get(0));
        Mockito.verify(clientProcessorspy, Mockito.atLeastOnce()).saveClientDetails(anyString(), ArgumentMatchers.<String, String>anyMap(), anyLong());

    }

//    @Test
//    public void processCaseModelReturnsNotNUll() throws Exception {
//        clientProcessor = new ClientProcessor(context.applicationContext());
//        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
//        final ArrayList<JSONObject> eventList = new ArrayList<>();
//        for (int i = 0; i < eventArray.length(); i++) {
//            eventList.add(eventArray.getJSONObject(i));
//        }
//        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
//        ArrayList<JSONObject> clientList = new ArrayList<>();
//        for (int i = 0; i < clientArray.length(); i++) {
//            clientList.add(clientArray.getJSONObject(i));
//        }
////        ClientProcessor clientProcessorspy = Mockito.spy(clientProcessor);
//        JSONObject clientClassification = new JSONObject(ClientData.clientClassificationJson);
//        JSONArray clientClassificationArray = clientClassification.getJSONArray("case_classification_rules");
//        JSONObject clientClassificationRulesObject = clientClassificationArray.getJSONObject(0);
//        JSONArray creates_case = clientClassificationRulesObject.getJSONObject("rule").getJSONArray("fields").getJSONObject(0).getJSONArray("creates_case");
//        Assert.assertEquals(Boolean.TRUE, clientProcessor.processCaseModel(eventList.get(0), clientList.get(0), creates_case));
//
//    }
}
