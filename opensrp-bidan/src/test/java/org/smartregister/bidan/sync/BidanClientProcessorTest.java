package org.smartregister.bidan.sync;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.AssetHandler;

import java.util.ArrayList;

/**
 * Created by sid-tech on 4/26/18
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AssetHandler.class, CloudantDataHandler.class, PreferenceManager.class})
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
public class BidanClientProcessorTest {

    private final static int NUM_THREADS = 10;
    private final static int NUM_ITERATIONS = 10;

    @Mock
    private BidanClientProcessor bidanClientProcessor;
    @Mock
    private BidanClientProcessor bidanClientNotProcessor;
    @Mock
    private Context context;
    @Mock
    private DetailsRepository detailsRepository;
    @Mock
    private CloudantDataHandler cloudantDataHandler;
    private ClientProcessor clientProcessor;
    @Mock
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context);
        Mockito.when(context.detailsRepository()).thenReturn(detailsRepository);

        PowerMockito.mockStatic(AssetHandler.class);
        PowerMockito.when(AssetHandler.readFileFromAssetsFolder("ec_client_classification.json", context.applicationContext())).thenReturn(ClientData.clientClassificationJson);
        PowerMockito.when(AssetHandler.readFileFromAssetsFolder("ec_client_fields.json", context.applicationContext())).thenReturn(ClientData.ec_client_fields_json);
        PowerMockito.when(AssetHandler.readFileFromAssetsFolder("ec_client_alerts.json", context.applicationContext())).thenReturn(ClientData.ec_client_alerts);

        PowerMockito.mockStatic(CloudantDataHandler.class);
        BidanClientProcessor.getInstance(RuntimeEnvironment.application);
        clientProcessor = new ClientProcessor(context.applicationContext());
        bidanClientProcessor = new BidanClientProcessor(RuntimeEnvironment.application);

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

        bidanClientProcessor.processClient(eventList);
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
        bidanClientProcessor = new BidanClientProcessor(context.applicationContext());
        JSONArray fieldArray = fieldObject.getJSONArray("bindobjects");
        Assert.assertEquals(bidanClientProcessor.processField(fieldArray.getJSONObject(0), eventList.get(0), clientList.get(0)), Boolean.TRUE);
    }


}