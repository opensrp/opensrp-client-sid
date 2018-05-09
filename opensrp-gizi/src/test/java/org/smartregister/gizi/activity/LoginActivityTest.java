package org.smartregister.gizi.activity;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import junit.framework.Assert;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.LoginResponse;
import org.smartregister.gizi.BuildConfig;
import org.smartregister.gizi.R;
import org.smartregister.gizi.activity.LoginActivity;
import org.smartregister.gizi.activity.mock.LoginActivityMock;
import org.smartregister.gizi.activity.shadow.ShadowContext;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.UserService;
import org.smartregister.sync.DrishtiSyncScheduler;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import shared.BaseUnitTest;
import shared.customshadows.FontTextViewShadow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

/**
 * Created by sid-tech on 4/24/18
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class})
@Config(shadows = {FontTextViewShadow.class, ShadowContext.class})
public class LoginActivityTest extends BaseUnitTest {

    @InjectMocks
    private LoginActivityMock activity;
    @Mock
    private org.smartregister.Context context_;

    @Mock
    private InputMethodManager inputManager;
    @Mock
    private android.content.Context applicationContext;
    @Mock
    private UserService userService;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private AlarmManager alarmManager;

    private ActivityController<LoginActivityMock> controller;


    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context_);
        LoginActivityMock.mockactivitycontext = context_;
        LoginActivityMock.inputManager = inputManager;

        DrishtiSyncScheduler.setReceiverClass(LoginActivityMock.class);

        when(context_.applicationContext()).thenReturn(applicationContext);
        when(context_.updateApplicationContext(any(android.content.Context.class))).thenReturn(context_);
        when(context_.userService()).thenReturn(userService);
        when(applicationContext.getSystemService(android.content.Context.ALARM_SERVICE)).thenReturn(alarmManager);
        when(allSharedPreferences.fetchRegisteredANM()).thenReturn("admin");
        when(inputManager.hideSoftInputFromWindow(isNull(IBinder.class), anyInt())).thenReturn(true);
        Intent intent = new Intent(RuntimeEnvironment.application, LoginActivityMock.class);
        controller = Robolectric.buildActivity(LoginActivityMock.class, intent);
        controller.create().start().resume().visible();
        activity = controller.get();
    }

    @After
    public void tearDown() throws Exception {
        // do nothing
    }

    @Test
    public void assertActivityNotNull() {
        Assert.assertNotNull(activity);
    }

    @Test
    public void localLoginTest() {
        when(userService.hasARegisteredUser()).thenReturn(true);
        when(userService.isValidLocalLogin(anyString(), anyString())).thenReturn(true);
        when(userService.isValidRemoteLogin(anyString(), anyString())).thenReturn(LoginResponse.SUCCESS);
        when(context_.allSharedPreferences()).thenReturn(allSharedPreferences);
        EditText username = (EditText) activity.findViewById(R.id.login_userNameText);
        EditText password = (EditText) activity.findViewById(R.id.login_passwordText);
        username.setText("admin");
        password.setText("password");
        Button login_button = (Button) activity.findViewById(R.id.login_loginButton);
        login_button.performClick();
//        Mockito.verify(userService, Mockito.atLeastOnce()).localLogin(anyString(), anyString());
        destroyController();

    }

    private void destroyController() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), e.getMessage());
        }

        System.gc();
    }

    @Test
    public void appVersionEqualsVersionName(){
        Assert.assertEquals(activity.getAppVersion(), BuildConfig.VERSION_NAME);
    }

    @Test
    public void defaultLocationIdEqualsPendem(){
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("dataTest.json");
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(inputStream, writer, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(activity.getUserDefaultLocationId(writer.toString()), "Pendem");
    }

    @Test
    public void switchLanguageEnToIn(){
        Assert.assertEquals(LoginActivity.switchLanguagePreference(), "Bahasa");
    }

//    @Test
//    public void buildDate_equals_date(){
//        try {
//            Assert.assertEquals(activity.getBuildDate(), BuildConfig.VERSION_NAME);
//        } catch (PackageManager.NameNotFoundException | IOException e) {
//            e.printStackTrace();
//        }
//    }
}