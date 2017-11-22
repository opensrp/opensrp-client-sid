package org.smartregister.bidan_cloudant.repository;

import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.bidan_cloudant.face.camera.utils.ProfileImageFR;
import org.smartregister.domain.ProfileImage;
import org.smartregister.repository.ImageRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sid-tech on 11/22/17.
 */

public class ImageRepositoryFR extends ImageRepository {

    private static final String TAG = ImageRepositoryFR.class.getName();
    private List<ProfileImage> allVectorImages;

    public void add(ProfileImage Image) {
        SQLiteDatabase database = this.masterRepository.getWritableDatabase();
    }

    public void add(ProfileImage profileImage, String entityId) {

    }

    public void updateByEntityId(String entityId, String faceVector) {

    }

    public List<ProfileImageFR> getAllVectorImages() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, null, null, null, null, null);
        return readAll(cursor);    }

    public List<String> findAllUnDownloaded() {
        return null;
    }

    protected List<ProfileImageFR> readAll(Cursor cursor) {
        List<ProfileImageFR> profileImages = new ArrayList<>();

        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (cursor.getCount() > 0 && !cursor.isAfterLast()) {

                    profileImages.add(new ProfileImageFR(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)));

                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return profileImages;
    }
}
