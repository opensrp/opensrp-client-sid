package org.smartregister.bidan.options;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import shared.BaseUnitTest;

/**
 * Created by sid-tech on 4/24/18
 */
public class AllKBServiceModeTest extends BaseUnitTest {

    @Mock
    private SmartRegisterClientsProvider smartRegisterClientsProvider;

    private AllKBServiceMode allKBServiceMode;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        allKBServiceMode = new AllKBServiceMode(smartRegisterClientsProvider);
        allKBServiceMode.name();
        SecuredNativeSmartRegisterActivity.ClientsHeaderProvider clientsHeaderProvider = allKBServiceMode.getHeaderProvider();
        clientsHeaderProvider.count();
        clientsHeaderProvider.headerTextResourceIds();
        clientsHeaderProvider.weights();
        clientsHeaderProvider.weightSum();


    }

    @Test
    public void modeNameIsKB() {
        Assert.assertEquals(allKBServiceMode.name(), "KB");
    }

}