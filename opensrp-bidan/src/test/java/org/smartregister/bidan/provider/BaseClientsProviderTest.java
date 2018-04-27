package org.smartregister.bidan.provider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import shared.BaseUnitTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by sid-tech on 4/26/18
 */
public class BaseClientsProviderTest extends BaseUnitTest {

    @Mock
    private BaseClientsProvider baseClientsProvider;

//    FilterOption fo = new FilterOption() {
//        @Override
//        public boolean filter(SmartRegisterClient smartRegisterClient) {
//            return false;
//        }
//
//        @Override
//        public String name() {
//            return null;
//        }
//    };

//    SmartRegisterClientsProvider srcp = new SmartRegisterClientsProvider() {
//        @Override
//        public View getView(SmartRegisterClient smartRegisterClient, View view, ViewGroup viewGroup) {
//            return null;
//        }
//
//        @Override
//        public SmartRegisterClients getClients() {
//            return null;
//        }
//
//        @Override
//        public SmartRegisterClients updateClients(FilterOption filterOption, ServiceModeOption serviceModeOption, FilterOption filterOption1, SortOption sortOption) {
//            return null;
//        }
//
//        @Override
//        public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
//
//        }
//
//        @Override
//        public OnClickFormLauncher newFormLauncher(String s, String s1, String s2) {
//            return null;
//        }
//    };

//    ServiceModeOption smo = new AllKartuIbuServiceMode(srcp);

//    SortOption so = new SortOption() {
//        @Override
//        public SmartRegisterClients sort(SmartRegisterClients smartRegisterClients) {
//            return null;
//        }
//
//        @Override
//        public String name() {
//            return null;
//        }
//    };

    @Before
    public void setUp() throws Exception {
        baseClientsProvider = new BaseClientsProvider(RuntimeEnvironment.application);
        baseClientsProvider.inflater();
//        baseClientsProvider.getClients();
//        baseClientsProvider.inflatelayoutForCursorAdapter();
        baseClientsProvider.onServiceModeSelected(null);

//        baseClientsProvider.updateClients(fo, smo, fo, so);
    }

    @Test
    public void testInflater() {
        assertNotNull(baseClientsProvider.inflater());
    }

    @Test
    public void testInflateCursor() {
        assertNull(baseClientsProvider.inflatelayoutForCursorAdapter());
    }

    @Test
    public void testLauncher() {
        String a = "a";
        String b = "b";
        String c = "c";
        assertNull(baseClientsProvider.newFormLauncher(a, b, c));
    }

//    @Test
//    public void testClients() {
////        assertEquals(baseClientsProvider.getClients() , null);
//    }

}