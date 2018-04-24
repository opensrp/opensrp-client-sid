package org.smartregister.bidan.options;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import shared.BaseUnitTest;

/**
 * Created by sid-tech on 4/24/18
 */
public class KIPNCOverviewServiceModeTest extends BaseUnitTest {

    @Mock
    private SmartRegisterClientsProvider smartRegisterClientsProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        KIPNCOverviewServiceMode kipncOverviewServiceMode = new KIPNCOverviewServiceMode(smartRegisterClientsProvider);
        kipncOverviewServiceMode.name();
        SecuredNativeSmartRegisterActivity.ClientsHeaderProvider clientsHeaderProvider = kipncOverviewServiceMode.getHeaderProvider();
        clientsHeaderProvider.count();
        clientsHeaderProvider.headerTextResourceIds();
        clientsHeaderProvider.weights();
        clientsHeaderProvider.weightSum();


    }

    @Test
    public void mockRunable() {
        // do nothing
    }
}