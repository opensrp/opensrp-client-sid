package org.smartregister.gizi.options;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.gizi.option.IbuServiceModeOption;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import shared.BaseUnitTest;

/**
 * Created by sid-tech on 4/24/18
 */
public class IbuServiceModeTest extends BaseUnitTest {

    @Mock
    private SmartRegisterClientsProvider smartRegisterClientsProvider;

    private IbuServiceModeOption allKartuIbuServiceMode;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        allKartuIbuServiceMode = new IbuServiceModeOption(smartRegisterClientsProvider);
        allKartuIbuServiceMode.name();
        SecuredNativeSmartRegisterActivity.ClientsHeaderProvider clientsHeaderProvider = allKartuIbuServiceMode.getHeaderProvider();
        clientsHeaderProvider.count();
        clientsHeaderProvider.headerTextResourceIds();
        clientsHeaderProvider.weights();
        clientsHeaderProvider.weightSum();
    }

    @Test
    public void modeNameIsHouseHold() {
        Assert.assertEquals(allKartuIbuServiceMode.name(), "Ibu");
    }

}