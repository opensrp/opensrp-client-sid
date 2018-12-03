package org.smartregister.bidan.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import shared.BaseUnitTest;

/**
 * Created by sid-tech on 4/25/18
 */
public class FormSubmissionSyncServiceTest extends BaseUnitTest {

    @Mock
    private FormSubmissionSyncService formSubmissionSyncService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        formSubmissionSyncService = new FormSubmissionSyncService(RuntimeEnvironment.application);

    }


    @Test
    public void testW() {
        formSubmissionSyncService.sync();
    }

}