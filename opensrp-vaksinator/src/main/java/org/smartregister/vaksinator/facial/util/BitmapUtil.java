package org.smartregister.vaksinator.facial.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import org.smartregister.vaksinator.application.VaksinatorApplication;
import org.smartregister.vaksinator.facial.domain.ProfileImage;
import org.smartregister.vaksinator.facial.repository.ImageRepository;
import org.smartregister.vaksinator.facial.utils.FaceConstants;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by wildan on 10/5/17.
 */

public class BitmapUtil {
    public static String[] photoDirs = new String[]{DrishtiApplication.getAppDir(),
            DrishtiApplication.getAppDir() + File.separator + ".thumbs"};
    public static final String TAG = BitmapUtil.class.getSimpleName();

    public void BitmapUtil(){
    }

    public void saveAndClose(Context mContext, String uid, boolean updated, FacialProcessing objFace, int arrayPossition, Bitmap mBitmap, String str_origin_class, ProfileImage tag) {

        if (saveToFile(mBitmap, uid)) {
            Log.e(TAG, "saveAndClose: " + "Saved File Success! uid= " + uid);
            if (saveToDb(updated, uid, objFace, tag)) Log.e(TAG, "saveAndClose: " + "Stored DB Success!");

        } else {
            Log.e(TAG, "saveAndClose: "+"Failed saved file!" );
        }

    }

    private static boolean saveToFile(Bitmap mBitmap, String uid) {

        try {
            // Raw image
            File jpegId = new File(String.format("%s%s%s.jpg", photoDirs[0], File.separator, uid));
            FileOutputStream oriFos = new FileOutputStream(jpegId);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, oriFos);
            oriFos.close();
            Log.e(TAG, "Wrote Raw image to " + jpegId.getAbsolutePath());

            // Thumbnail Image
            File thumbsFolder = new File(photoDirs[1]);
            boolean success = true;
            if (!thumbsFolder.exists()) {
                success = thumbsFolder.mkdir();
            }
            if (success) {
                File thumbId = new File(String.format("%s%s%s.jpg", photoDirs[1], File.separator, "th_"+uid));
                FileOutputStream thumbsFos = new FileOutputStream(thumbId);
                final int THUMBSIZE = FaceConstants.THUMBSIZE;
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(jpegId.getAbsolutePath()), THUMBSIZE, THUMBSIZE);
                if(ThumbImage.compress(Bitmap.CompressFormat.PNG, 100, thumbsFos)){
                    thumbsFos.close();
                    Log.e(TAG, "Wrote Thumbs image to " + thumbId.getAbsolutePath());
                } else Log.e(TAG, "saveToFile: Thumbs "+ "Failed" );
            } else {
                Log.e(TAG, "saveToFile: "+"Folder Thumbs failed Created!" );
            }

            return true;

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }


        return false;

    }

    private boolean saveToDb(boolean updatedMode, String uid, FacialProcessing objFace, ProfileImage tag) {
        final ImageRepository imageRepo = VaksinatorApplication.getInstance().imageRepository();

        byte[] faceVector;


        if (imageRepo != null) {

            if (!updatedMode){
                Log.d(TAG, "saveToDb: not updatedMode");
                ProfileImage profileImage = tag;
                String personId = profileImage.getPersonId();
                Log.d(TAG, "saveToDb: personId="+personId);
                String[] faceVectorContent;
                int result = objFace.addPerson(0);
                faceVector = objFace.serializeRecogntionAlbum();
                Log.d(TAG, "saveToDb: faceVector"+faceVector);
                String albumBufferArr = Arrays.toString(faceVector);
                faceVectorContent = albumBufferArr.substring(1, albumBufferArr.length() - 1).split(", ");
                // Get Face Vector Content Only by removing Header
                faceVectorContent = Arrays.copyOfRange(faceVectorContent, faceVector.length - 300, faceVector.length);
                if (personId != null){
                    faceVectorContent[0] = personId;
                }
                profileImage.setFaceVector(Arrays.toString(faceVectorContent));
                profileImage.setSyncStatus(String.valueOf(ImageRepository.TYPE_Unsynced));

                imageRepo.add(profileImage, uid);

            } else {
                Log.d(TAG, "saveToDb: updatedMode");
                ProfileImage profileImage = tag;
                int personId = Integer.valueOf(profileImage.getPersonId());
                Log.d(TAG, "saveToDb: personId="+personId);
                int result = objFace.addPerson(0);
                faceVector = objFace.serializeRecogntionAlbum();
                String albumBufferArr = Arrays.toString(faceVector);
                String[] faceVectorContent = albumBufferArr.substring(1, albumBufferArr.length() - 1).split(", ");
                // Get Face Vector Content Only by removing Header
                faceVectorContent = Arrays.copyOfRange(faceVectorContent, faceVector.length - 300, faceVector.length);
                faceVectorContent[0] = String.valueOf(personId);
                Log.d(TAG, "saveToDb: faceVectorContent="+faceVectorContent);


                profileImage.setFaceVector(Arrays.toString(faceVectorContent));
                profileImage.setSyncStatus(String.valueOf(ImageRepository.TYPE_Unsynced));

                imageRepo.add(profileImage);
            }

            return true;
        }
        return false;
    }

    public static void drawRectFace(Rect rect, Bitmap mBitmap, float pixelDensity) {
        // Extra padding around the faceRects
        rect.set(rect.left -= 20, rect.top -= 20, rect.right += 20, rect.bottom += 20);
        Canvas canvas = new Canvas(mBitmap);

        // Draw rect fill
        Paint paintForRectFill = new Paint();
        paintForRectFill.setStyle(Paint.Style.FILL);
        paintForRectFill.setColor(Color.BLACK);
        paintForRectFill.setAlpha(80);

        // Draw rect strokes
        Paint paintForRectStroke = new Paint();
        paintForRectStroke.setStyle(Paint.Style.STROKE);
        paintForRectStroke.setColor(Color.GREEN);
        paintForRectStroke.setStrokeWidth(3);

        // Draw Face detected Area
        canvas.drawRect(rect, paintForRectFill);
        canvas.drawRect(rect, paintForRectStroke);
    }

    public static void drawInfo(Rect rect, Bitmap mutableBitmap, float pixelDensity, String personName) {

        // Extra padding around the faceRects
        rect.set(rect.left -= 20, rect.top -= 20, rect.right += 20, rect.bottom += 20);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paintForRectFill = new Paint();

        // Draw rect fill
        paintForRectFill.setStyle(Paint.Style.FILL);
        paintForRectFill.setColor(Color.WHITE);
        paintForRectFill.setAlpha(80);

        // Draw rectangular strokes
        Paint paintForRectStroke = new Paint();
        paintForRectStroke.setStyle(Paint.Style.STROKE);
        paintForRectStroke.setColor(Color.GREEN);
        paintForRectStroke.setStrokeWidth(5);
        canvas.drawRect(rect, paintForRectFill);
        canvas.drawRect(rect, paintForRectStroke);

//        float pixelDensity = getResources().getDisplayMetrics().density;
        int textSize = (int) (rect.width() / 25 * pixelDensity);

        Paint paintForText = new Paint();
        Paint paintForTextBackground = new Paint();
        Typeface tp = Typeface.SERIF;
        Rect backgroundRect = new Rect(rect.left, rect.bottom, rect.right, (rect.bottom + textSize));

        paintForText.setColor(Color.WHITE);
        paintForText.setTextSize(textSize);
        paintForTextBackground.setStyle(Paint.Style.FILL);
        paintForTextBackground.setColor(Color.BLACK);
        paintForText.setTypeface(tp);
        paintForTextBackground.setAlpha(80);

        if (personName != null) {
            canvas.drawRect(backgroundRect, paintForTextBackground);
            canvas.drawText(personName, rect.left, rect.bottom + (textSize), paintForText);
        } else {
            canvas.drawRect(backgroundRect, paintForTextBackground);
            canvas.drawText("Not identified", rect.left, rect.bottom + (textSize), paintForText);
        }

//        confirmationView.setImageBitmap(mutableBitmap);

    }

}
