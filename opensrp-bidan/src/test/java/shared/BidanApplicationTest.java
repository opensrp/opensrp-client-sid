package shared;

import android.util.Log;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.TestLifecycleApplication;
import org.smartregister.Context;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.domain.Alert;
import org.smartregister.service.AlertService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by sid-tech on 2/23/18
 */
public class BidanApplicationTest extends BidanApplication implements TestLifecycleApplication {

    public static final String TAG = BidanApplicationTest.class.getCanonicalName();
    //    @Mock
//    private BidanRepository bidanRepository;
    @Mock
    private AlertService alertService;

    public static synchronized BidanApplication getInstance() {
        return (BidanApplication) mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMocks(this);

        try {
            Field field = Context.class.getDeclaredField("alertService");
            field.setAccessible(true);
            field.set(context, alertService);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        when(alertService.findByEntityIdAndAlertNames(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(new ArrayList<Alert>());


        mInstance = this;

    }

    @Override
    public void beforeTest(Method method) {
        // do nothing
    }

    @Override
    public void prepareTest(Object test) {
        // do nothing
    }

    @Override
    public void afterTest(Method method) {
        // do nothing
    }

}