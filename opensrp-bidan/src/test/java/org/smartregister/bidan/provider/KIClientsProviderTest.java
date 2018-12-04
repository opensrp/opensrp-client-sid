package org.smartregister.bidan.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.view.contract.SmartRegisterClient;

import shared.BaseUnitTest;

import static org.junit.Assert.assertNotNull;

/**
 * Created by sid-tech on 4/26/18
 */
public class KIClientsProviderTest extends BaseUnitTest {


    @Mock
    private KIClientsProvider kiClientsProvider;
    @Mock
    private Context applicationContext;
    @Mock
    private org.smartregister.Context context_;
    @Mock
    private Cursor cursor;

    @Mock
    private SmartRegisterClient client = new SmartRegisterClient() {
        @Override
        public String entityId() {
            return "1";
        }

        @Override
        public String name() {
            return "abc";
        }

        @Override
        public String displayName() {
            return "ABC";
        }

        @Override
        public String village() {
            return "Vab";
        }

        @Override
        public String wifeName() {
            return "Wab";
        }

        @Override
        public String husbandName() {
            return "Hab";
        }

        @Override
        public int age() {
            return 20;
        }

        @Override
        public int ageInDays() {
            return 200;
        }

        @Override
        public String ageInString() {
            return "20";
        }

        @Override
        public boolean isSC() {
            return false;
        }

        @Override
        public boolean isST() {
            return false;
        }

        @Override
        public boolean isHighRisk() {
            return false;
        }

        @Override
        public boolean isHighPriority() {
            return false;
        }

        @Override
        public boolean isBPL() {
            return false;
        }

        @Override
        public String profilePhotoPath() {
            return null;
        }

        @Override
        public String locationStatus() {
            return null;
        }

        @Override
        public boolean satisfiesFilter(String s) {
            return false;
        }

        @Override
        public int compareName(SmartRegisterClient smartRegisterClient) {
            return 0;
        }
    };
    @Mock
    private View view;

    @Before
    public void setUp() throws Exception {

        kiClientsProvider = new KIClientsProvider(RuntimeEnvironment.application, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        }, null);

    }

    @Test
    public void testInflater() {
        assertNotNull(kiClientsProvider.inflatelayoutForCursorAdapter());
    }

    @Test
    public void testView() {
//        ancClientsProvider.getView(cursor, client, view);
    }


}