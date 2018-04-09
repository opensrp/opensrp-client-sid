package org.smartregister.bidan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.ImageRepository;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sid-tech on 1/25/18
 */
@PrepareForTest({BidanApplication.class})
@RunWith(RobolectricTestRunner.class)
public class ToolsTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    public CommonPersonObjectClient commonPersonObjectClient;

    @Mock
    public BidanApplication bidanApplication;

    @Mock
    public Context context;

    @Mock
    public ImageRepository imageRepository;

    @Before
    public void setUp() {

        initMocks(this);
    }

    @Test
    public void checkFileSaved() throws Exception{
        Bitmap bmp1 = BitmapFactory.decodeResource(RuntimeEnvironment.application.getResources(), R.mipmap.ic_launcher);

        Tools.saveFile(bmp1, "test_foto.png");
        Assert.assertTrue(FileUtils.deleteQuietly(new File("test_foto.png")));
        System.out.println();
    }

}