package org.smartregister.bidan.activity;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.bidan.activity.mock.KISmartRegisterActivityMock;
import org.smartregister.bidan.activity.shadow.SecuredActivityShadow;
import org.smartregister.bidan.activity.shadow.SecuredFragmentShadow;
import org.smartregister.bidan.activity.shadow.ShadowContext;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.ZiggyService;
import org.smartregister.view.controller.ANMLocationController;

import shared.BaseUnitTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by sid-tech on 4/25/18
 */
@PrepareForTest({CoreLibrary.class})
@Config(shadows = {SecuredActivityShadow.class, SecuredFragmentShadow.class, ShadowContext.class})
// LocationPickerViewShadow.class, JsonFormUtilsShadow.class, MyShadowAsyncTask.class, CommonRepositoryShadow.class, ShadowContextForRegistryActivity.class})
public class KISmartRegisterActivityTest extends BaseUnitTest {

    @Mock
    public org.smartregister.service.HTTPAgent httpAgent;
    @Mock
    public DristhiConfiguration configuration;
//    @Mock
//    private BidanRepository bidanRepository;
//    @Mock
//    private AlertService alertService;

    @Mock
    private InputMethodManager inputManager;

    @Mock
    private ZiggyService ziggyService;

    @Mock
    private org.smartregister.Context context_;

    @Mock
    private Context applicationContext;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private ANMLocationController anmLocationController;

    @Mock
    private org.smartregister.repository.AllSharedPreferences allSharedPreferences;
//    @Mock
//    CommonPersonObject personObject;

    @Mock
    private DetailsRepository detailsRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
//        setDataForTest();
//        details = new HashMap<>();
//        KISmartRegisterActivityMock.inputManager = inputManager;
//        context_ = ShadowContextForRegistryActivity.getInstance();
//        SecuredFragmentShadow.mContext = context_;
//        ShadowContextForRegistryActivity.commonRepository = commonRepository;
//        String[] columns = new String[]{"_id", "relationalid", "first_name", "dob", "details", "HHID", "Date_Of_Reg", "address1"};
//        MatrixCursor matrixCursor = new MatrixCursor(columns);
//        matrixCursor.addRow(new Object[]{"1", "relationalid", "first_name", "dob", "details", "HHID", "Date_Of_Reg", "address1"});
//        matrixCursor.addRow(new Object[]{"2", "relationalid", "first_name", "dob", "details", "HHID", "Date_Of_Reg", "address1"});
//        for (int i = 3; i < 22; i++) {
//            matrixCursor.addRow(new Object[]{"" + i, "relationalid" + i, "first_name" + i, "dob" + i, "details+i", "HHID" + i, "Date_Of_Reg" + i, "address"+i});
//        }
//        CommonPersonObject personObject = new CommonPersonObject("caseID","relationalID",new HashMap<String, String>(),"type");
//        personObject.setColumnmaps(details);
//        details.put(AllConstantsINA.KEY.DOB,"2018-01-01");
//        Response<String> response = new Response<>(ResponseStatus.success, advanceSearchResponsePayload);

//        KISmartRegisterActivityMock.setmContext(context_);
//        when(context_.configuration()).thenReturn(configuration);
//        when(context_.getHttpAgent()).thenReturn(httpAgent);
//        when(httpAgent.fetch(anyString())).thenReturn(response);
//        when(configuration.dristhiBaseURL()).thenReturn("http://103.247.238.31:9979/opensrp/");
//        when(context_.updateApplicationContext(isNull(Context.class))).thenReturn(context_);
//        when(context_.updateApplicationContext(any(Context.class))).thenReturn(context_);
//        when(context_.IsUserLoggedOut()).thenReturn(false);
//        when(context_.applicationContext()).thenReturn(applicationContext);
//        when(context_.anmLocationController()).thenReturn(anmLocationController);
//        when(context_.allSharedPreferences()).thenReturn(allSharedPreferences);
//        when(allSharedPreferences.fetchRegisteredANM()).thenReturn("Test User");
//        when(allSharedPreferences.getANMPreferredName(anyString())).thenReturn("Test User");
//        when(context_.commonrepository(anyString())).thenReturn(commonRepository);
//        when(context_.detailsRepository()).thenReturn(getDetailsRepository());
//        when(commonRepository.rawCustomQueryForAdapter(anyString())).thenReturn(matrixCursor);
//        when(context_.ziggyService()).thenReturn(ziggyService);
//        when(commonRepository.readAllcommonforCursorAdapter(any(Cursor.class))).thenReturn(personObject);
//        Mockito.doReturn(alertService).when(context_).alertService();
//        Mockito.doReturn(alertList).when(alertService).findByEntityIdAndAlertNames(Mockito.anyString(),Mockito.any(String[].class));

//        CoreLibrary.init(context_);

        when(context_.applicationContext()).thenReturn(applicationContext);
        when(context_.updateApplicationContext(any(android.content.Context.class))).thenReturn(context_);
//        when(context_.userService()).thenReturn(userService);
//        when(applicationContext.getSystemService(android.content.Context.ALARM_SERVICE)).thenReturn(alarmManager);
//        when(allSharedPreferences.fetchRegisteredANM()).thenReturn("admin");
//        when(inputManager.hideSoftInputFromWindow(isNull(IBinder.class), anyInt())).thenReturn(true);

        Intent intent = new Intent(RuntimeEnvironment.application, KISmartRegisterActivityMock.class);
//        ActivityController<KISmartRegisterActivityMock> controller = Robolectric.buildActivity(KISmartRegisterActivityMock.class, intent);
//        activity = controller.create().start().resume().visible().get();
    }

    @Test
    public void assertActivityNotNull() {
//        ((KISmartRegisterFragment)activity.mBaseFragment).refresh();
//        Assert.assertNotNull(activity);
    }

//    @Test
//    public void listViewNavigationShouldWorkIfClientsSpanMoreThanOnePage() throws InterruptedException {
////        ((KISmartRegisterFragment) activity.mBaseFragment).refresh();
//        final ListView list = (ListView) activity.findViewById(R.id.list);
//        ViewGroup footer = (ViewGroup) tryGetAdapter(list).getView(20, null, null);
//        Button nextButton = (Button) activity.findViewById(R.id.btn_next_page);
//        Button previousButton = (Button) activity.findViewById(R.id.btn_previous_page);
//        TextView info = (TextView) activity.findViewById(R.id.txt_page_info);
//        int count = tryGetAdapter(list).getCount();
//        nextButton.performClick();
//        assertEquals("Page 1 of 1", info.getText());
//        previousButton.performClick();
//    }
//
//    @Test
//    public void childproviderClickOnRecordWeightLaunchesImmunizationActivity() throws InterruptedException {
////        ((KISmartRegisterFragment) activity.mBaseFragment).refresh();
//        final ListView list = (ListView) activity.findViewById(R.id.list);
////        View childView = tryGetAdapter(list).getView(4, null, null);
////        childView.findViewById(R.id.record_weight).performClick();
////        assertTrue(KISmartRegisterFragment.calledrecordWeight);
//
//    }
//
//
//    private ListAdapter tryGetAdapter(final ListView list) {
//        ListAdapter adapter = list.getAdapter();
//        while (adapter.getCount() == 0) {
//            ShadowLooper.idleMainLooper(1000);
//            adapter = list.getAdapter();
//        }
//        return adapter;
//    }
//
//    @Test
//    public void pressingSearchCancelButtonShouldClearSearchTextAndLoadAllClients() {
//        final ListView list = (ListView) activity.findViewById(R.id.list);
//        EditText searchText = (EditText) activity.findViewById(R.id.edt_search);
//        int count = tryGetAdapter(list).getCount();
//        searchText.setText("first_name3");
//        assertTrue("first_name3".equalsIgnoreCase(searchText.getText().toString()));
//        activity
//                .findViewById(R.id.btn_search_cancel)
//                .performClick();
//        assertEquals("", searchText.getText().toString());
////        assertEquals(count, tryGetAdapter(list).getCount());
//    }
//
//    @Test
//    public void clickOnGlobalSearchLaunchesAdvancedSearchFragment() {
//        ImageButton globalsearch = (ImageButton) activity.findViewById(R.id.global_search);
//        globalsearch.performClick();
//        Assert.assertEquals(activity.mPager.getCurrentItem(),1);
////        Mockito.verify(activity,Mockito.atLeastOnce()).startAdvancedSearch();
//
////        assertEquals(2, tryGetAdapter(list).getCount());
//    }
//
//    @Test
//    public void globalSearchAdvancedSearchFragmentShowsList() {
//        ImageButton globalsearch = (ImageButton) activity.findViewById(R.id.global_search);
//        globalsearch.performClick();
////        EditText firstname = (EditText)activity.findViewById(R.id.first_name);
////        firstname.setText("first_name");
////        Button search = (Button)activity.findViewById(R.id.search);
////        search.performClick();
//        ListView list = (ListView)activity.findViewById(R.id.list);
//        Assert.assertEquals(tryGetAdapter(list).getCount(),23);
//
////        Mockito.verify(activity,Mockito.atLeastOnce()).startAdvancedSearch();
//
////        assertEquals(2, tryGetAdapter(list).getCount());
//    }
//
//    @After
//    public void tearDown() {
//        destroyController();
//        activity = null;
//        controller = null;
//    }
//
//    private void destroyController() {
//        try {
//            activity.finish();
//            controller.pause().stop().destroy(); //destroy controller if we can
//
//        } catch (Exception e) {
//            Log.e(getClass().getCanonicalName(), e.getMessage());
//        }
//
//        System.gc();
//    }
//
//    private DetailsRepository getDetailsRepository() {
//
//        return new DetailsRepositoryLocal();
//    }
//
//    class DetailsRepositoryLocal extends DetailsRepository {
//
//        @Override
//        public Map<String, String> getAllDetailsForClient(String baseEntityId) {
//            return details;
//        }
//    }
//
//    public void setDataForTest() {
////        activity.set
//        Alert alert = new Alert("caseID","BCG","BCG", AlertStatus.normal,"1990-01-01","2200-01-01");
//        alertList.add(alert);
//
//    }

}