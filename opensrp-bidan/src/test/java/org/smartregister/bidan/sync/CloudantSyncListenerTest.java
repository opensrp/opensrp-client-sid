package org.smartregister.bidan.sync;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.sync.SyncAfterFetchListener;

import shared.BaseUnitTest;

import static org.junit.Assert.*;

/**
 * Created by sid-tech on 4/25/18
 */
public class CloudantSyncListenerTest extends BaseUnitTest {

    @Mock
    private CloudantSyncListener cloudantSyncListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cloudantSyncListener = new CloudantSyncListener();
        cloudantSyncListener.replicationComplete();
        cloudantSyncListener.replicationError();
    }


    @Test
    public void testReplicationComplete(){
        // do nothing
    }

}