package org.smartregister.gizi.options;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.gizi.option.GiziServiceModeOption;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import shared.BaseUnitTest;

/**
 * Created by sid-tech on 4/24/18
 */
public class GiziServiceModeTest extends BaseUnitTest {

    @Mock
    private SmartRegisterClientsProvider smartRegisterClientsProvider;

    private GiziServiceModeOption allKBServiceMode;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        allKBServiceMode = new GiziServiceModeOption(smartRegisterClientsProvider);
        allKBServiceMode.name();
        SecuredNativeSmartRegisterActivity.ClientsHeaderProvider clientsHeaderProvider = allKBServiceMode.getHeaderProvider();
        clientsHeaderProvider.count();
        clientsHeaderProvider.headerTextResourceIds();
        clientsHeaderProvider.weights();
        clientsHeaderProvider.weightSum();

    }

    @Test
    public void modeNameTest() {
        Assert.assertEquals(allKBServiceMode.name(), "Test Register");
    }

}