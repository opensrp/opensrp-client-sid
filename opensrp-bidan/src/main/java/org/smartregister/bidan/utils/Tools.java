package org.smartregister.bidan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import org.apache.commons.io.FilenameUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sid-tech on 1/24/18.
 */

public class Tools {
    private static String TAG = Tools.class.getName();

    public static void savefile(Bitmap sourceuri, String destinationFilename) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            sourceuri.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(destinationFilename);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            String basename = FilenameUtils.getName(destinationFilename);
            // Create Thumbs
            String pathTh = DrishtiApplication.getAppDir() + File.separator + "th" + File.separator + basename;
            FileOutputStream tfos = new FileOutputStream(pathTh);
            final int THUMBSIZE = AllConstantsINA.THUMBSIZE;

            Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(tfos.toString()), THUMBSIZE, THUMBSIZE);
            if (thumbImage != null) thumbImage.compress(Bitmap.CompressFormat.PNG, 100, tfos);
            else Log.e(TAG, "savefile: ");

            tfos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "savefile: " + e.getCause());
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(maxImageSize / ((float) realImage.getWidth()), maxImageSize / ((float) realImage.getHeight()));
        return Bitmap.createScaledBitmap(realImage, Math.round(((float) realImage.getWidth()) * ratio), Math.round(((float) realImage.getHeight()) * ratio), filter);
    }

    public static Bitmap getThumbnailBitmap(String path, int thumbnailSize) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1)) {
            return null;
        }
        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / thumbnailSize;
        return BitmapFactory.decodeFile(path, opts);
    }
}
