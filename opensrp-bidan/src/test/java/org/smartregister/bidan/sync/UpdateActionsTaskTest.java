package org.smartregister.bidan.sync;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.CoreLibrary;
import org.smartregister.bidan.fragment.mock.ShadowOpensrpSSLHelper;
import org.smartregister.bidan.fragment.shadow.ShadowNavigator;
import org.smartregister.bidan.service.FormSubmissionSyncService;
import org.smartregister.service.ActionService;
import org.smartregister.service.AllFormVersionSyncService;
import org.smartregister.ssl.OpensrpSSLHelper;
import org.smartregister.view.ProgressIndicator;

import shared.BaseUnitTest;
import shared.customshadows.FontTextViewShadow;

/**
 * Created by sid-tech on 4/25/18
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class, OpensrpSSLHelper.class}) //, AdvancedSearchFragment.class})
@Config(shadows = {FontTextViewShadow.class, ShadowOpensrpSSLHelper.class, ShadowNavigator.class})
//, MyShadowAsyncTask.class})
public class UpdateActionsTaskTest extends BaseUnitTest {

    @Mock
    ActionService actionService;
    @Mock
    FormSubmissionSyncService fss;
    @Mock
    ProgressIndicator pi;
    @Mock
    AllFormVersionSyncService afvss;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(RuntimeEnvironment.application, actionService, fss, pi, afvss);
//        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }


    @Test
    public void testUpdateFromServer() {
        // do nothing
    }


}