package org.smartregister.path.fragment;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.bidan.fragment.PathJsonFormFragment;

import shared.BaseUnitTest;

import junit.framework.Assert;

public class BidanJsonFormFragmentTest extends BaseUnitTest {

    protected PathJsonFormFragment pathJsonFormFragment;

    @Before
    public void setUp() {
        pathJsonFormFragment = PathJsonFormFragment.getFormFragment("testStep");
    }

    @Test
    public void setPathJsonFormFragmentNotNullOnInstantiation() throws Exception {
        Assert.assertNotNull(pathJsonFormFragment);

    }

    @Test
    public void motherLookUpListenerIsNotNullOnFragmentInstantiation() throws Exception {
        Assert.assertNotNull(pathJsonFormFragment.motherLookUpListener());

    }

    @Test
    public void contextNotNullOnFragmentInstantiation() throws Exception {
        Assert.assertNotNull(pathJsonFormFragment.context());

    }

}
