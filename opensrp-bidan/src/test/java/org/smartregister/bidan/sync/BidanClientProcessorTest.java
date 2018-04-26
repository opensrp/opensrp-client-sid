package org.smartregister.bidan.sync;

import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import shared.BaseUnitTest;

import static org.junit.Assert.assertEquals;

/**
 * Created by sid-tech on 4/26/18
 */
public class BidanClientProcessorTest extends BaseUnitTest {

    private final static int NUM_THREADS = 10;
    private final static int NUM_ITERATIONS = 1000;


    @Mock
    private BidanClientProcessor bidanClientProcessor;
    private BidanClientProcessor bidanClientNotProcessor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bidanClientProcessor = new BidanClientProcessor(RuntimeEnvironment.application);
        bidanClientNotProcessor = new BidanClientProcessor(RuntimeEnvironment.application);

        BidanClientProcessor.getInstance(RuntimeEnvironment.application);
//        BidanClientProcessor.processClient();

    }

    @Test
    public void testSynchronizedSameValue() throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<JSONObject> events = new ArrayList<>();
        final List<JSONObject> result = Lists.newArrayList();
        try {
            JSONObject jsonProperty = new JSONObject()
                    .put(Key.eventType.name(), "property")
                    .put(Key.client.name(), "")
                    .put(Key.hidden.name(), false)
                    .put(Key.values.name(), new JSONArray().put(false).put(true));
            events.add(jsonProperty);
            result.add(jsonProperty);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < NUM_ITERATIONS; i++) {
                        try {
                            bidanClientProcessor.processClient(events);
                            assertEquals(result, events);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        notSync.inc();
                    }
                }
            });
        }
    }

    private enum Key {
        eventType,
        client,
        hidden,
        values
    }
}