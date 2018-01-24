package org.smartregister.bidan.utils;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.api.constants.Gender;
import org.smartregister.bidan.R;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;

import static org.smartregister.util.Utils.getValue;

/**
 * Created by sid-tech on 1/24/18.
 */

public class ImageUtils {

    public static int profileImageResourceByGender(String gender) {
        if (StringUtils.isNotBlank(gender)) {
            if (gender.equalsIgnoreCase(AllConstantsINA.GENDER.MALE)) {
                return R.drawable.child_boy_infant;
            } else if (gender.equalsIgnoreCase(AllConstantsINA.GENDER.FEMALE)) {
                return R.drawable.child_girl_infant;
            }
        }
        return R.drawable.child_girl_infant;
    }

    public static int profileImageResourceByGender(Gender gender) {
        if (gender != null) {
            if (gender.equals(Gender.MALE)) {
                return R.drawable.child_boy_infant;
            } else if (gender.equals(Gender.FEMALE)) {
                return R.drawable.child_girl_infant;
            }
        }
        return R.drawable.child_girl_infant;
    }

    public static Photo profilePhotoByClient(CommonPersonObjectClient client) {
        Photo photo = new Photo();
        ProfileImage profileImage =  BidanApplication.getInstance().context().imageRepository().findByEntityId(client.entityId());
        if (profileImage != null) {
            photo.setFilePath(profileImage.getFilepath());
        } else {
            String gender = getValue(client, "gender", true);
            photo.setResourceId(profileImageResourceByGender(gender));
        }
        return photo;
    }

}