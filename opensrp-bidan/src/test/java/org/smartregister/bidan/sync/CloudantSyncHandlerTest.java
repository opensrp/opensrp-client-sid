package org.smartregister.bidan.sync;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import shared.BaseUnitTest;

import static org.junit.Assert.assertEquals;

/**
 * Created by sid-tech on 4/25/18
 */
public class CloudantSyncHandlerTest extends BaseUnitTest {

    @Mock
    private CloudantSyncHandler cloudantSyncHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        cloudantSyncHandler = new CloudantSyncHandler(RuntimeEnvironment.application);
//        cloudantSyncHandler.error();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testReadFileContents() {
        assertEquals(cloudantSyncHandler.getFileContents(""), null);
    }

    @Test
    public void testAuthentication() {
        assertEquals(cloudantSyncHandler.getAuthorization(), "Q09VQ0hfREFUQUJBU0VfVVNFUjpDT1VDSF9EQVRBQkFTRV9QQVNT\n");
    }

}