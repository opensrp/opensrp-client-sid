package org.smartregister.bidan.fragment;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.smartregister.bidan.activity.KISmartRegisterActivity;
import org.smartregister.view.fragment.SecuredFragment;

import shared.BaseUnitTest;

/**
 * Created by sid-tech on 2/23/18
 */
public class KISmartRegisterFragmentTest extends BaseUnitTest {

    KISmartRegisterActivity mainActivity;
    KISmartRegisterFragment kiSmartRegisterFragment;

    @Before
    public void setUp() throws Exception {
        mainActivity = Robolectric.setupActivity(KISmartRegisterActivity.class);
        kiSmartRegisterFragment = new KISmartRegisterFragment();
        startFragment(kiSmartRegisterFragment);
    }

    private void startFragment(SecuredFragment fragment) {
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null);
        fragmentTransaction.commit();
    }

    @Test
    public void instantiate() throws Exception {
    }

    public void testMainActivity() {
        Assert.assertNotNull(mainActivity);
    }

    @Test
    public void getDefaultOptionsProviderNotNull() throws Exception {
        Assert.assertNotNull(kiSmartRegisterFragment.getDefaultOptionsProvider());

    }

    @Test
    public void getNavBarOptionsProviderNotNull() throws Exception {
        Assert.assertNotNull(kiSmartRegisterFragment.getNavBarOptionsProvider());

    }

    @Test
    public void filterStringForAllNotNull() throws Exception {
        Assert.assertNotNull(kiSmartRegisterFragment.filterStringForAll());

    }

//    private void startFragment(Fragment fragment) {
//        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(fragment, null);
//        fragmentTransaction.commit();
//    }

}