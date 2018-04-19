package org.smartregister.bidan.activity;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by sid-tech on 4/19/18
 */
public class SettingsActivityTest extends ActivityInstrumentationTestCase2<SettingsActivity> {

    public SettingsActivityTest() {
        super(SettingsActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {

    }

    public void testOnCreate() throws Exception {
        getActivity();
//        onView(withText("Settings")).check(matches(isDisplayed()));
//        onView(withText("Plan Push Up")).check(matches(isDisplayed()));
//        onView(withText("Notifications")).check(matches(isDisplayed()));
    }

}