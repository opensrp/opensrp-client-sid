package org.smartregister.bidan.utils;

import android.os.Environment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.Context;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.ImageRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static javolution.util.stripped.FastMap.logger;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sid-tech on 1/25/18.
 */
@PrepareForTest({BidanApplication.class})
public class ToolsTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private CommonPersonObjectClient commonPersonObjectClient;

    @Mock
    private BidanApplication bidanApplication;

    @Mock
    private Context context;

    @Mock
    private ImageRepository imageRepository;

    @Before
    public void setUp() {

        initMocks(this);
    }

    @Test
    public void savefile() throws Exception {
        String outputPath =
                new File(Environment.getExternalStorageDirectory(), "a.jpg")
                        .getAbsolutePath();
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
    }

    @Test
    public void scaleDown() {
        logger.info("### TEST > Openshift 2 > scaleDown()");

        assertTrue(true);

    }

    @Test
    public void getThumbnailBitmap() {

    }

}