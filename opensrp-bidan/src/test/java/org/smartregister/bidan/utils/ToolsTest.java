package org.smartregister.bidan.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowBitmapFactory;
import org.smartregister.bidan.application.BidanApplication;

import java.io.File;
import java.io.OutputStream;

import shared.BaseUnitTest;

import static org.smartregister.bidan.utils.Tools.saveFile;

/**
 * Created by sid-tech on 1/25/18
 */
@PrepareForTest({BidanApplication.class})
public class ToolsTest extends BaseUnitTest {

    @Before
    public void setUp() throws Exception {
        File file = new File(getContext().getCacheDir(), "test_file");
        file.delete();
    }

    @Test
    public void saveFileSuccess() throws Exception {
        File file = new File(getContext().getCacheDir(), "test_file");
        Bitmap bitmap = Mockito.mock(Bitmap.class);
        Mockito.when(bitmap.hasAlpha()).thenReturn(true);
        Mockito.when(
                bitmap.compress((Bitmap.CompressFormat) Mockito.any(), Mockito.anyInt(),
                        (OutputStream) Mockito.any())).
                thenReturn(true);
        Mockito.when(bitmap.getWidth()).thenReturn(400);
        Mockito.when(bitmap.getHeight()).thenReturn(400);
        Bitmap b = Bitmap.createBitmap(10, 10, Bitmap.Config.RGB_565);
        saveFile(b, "a.png");
//        boolean result = saveFile("");
//        Assert.assertTrue(result);

        ShadowBitmapFactory.provideWidthAndHeightHints(file.getAbsolutePath(), 400, 400);
        Bitmap writtenBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        Assert.assertNotNull(writtenBitmap);
        Assert.assertEquals(bitmap.getWidth(), writtenBitmap.getWidth());
        Assert.assertEquals(bitmap.getHeight(), writtenBitmap.getHeight());
    }

    private Context getContext() {
        return RuntimeEnvironment.application;
    }

}