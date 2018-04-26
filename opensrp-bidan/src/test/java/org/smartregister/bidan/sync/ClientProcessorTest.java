package org.smartregister.bidan.sync;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import shared.BaseUnitTest;

/**
 * Created by sid-tech on 4/26/18
 */
public class ClientProcessorTest extends BaseUnitTest {

    @Mock
    private ClientProcessor clientProcessor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        clientProcessor = new ClientProcessor(RuntimeEnvironment.application);

        clientProcessor.getContext();
        clientProcessor.processClient();

    }

    @Test
    public void testInstance() {

    }

}