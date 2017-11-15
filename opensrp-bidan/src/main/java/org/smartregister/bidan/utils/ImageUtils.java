package org.smartregister.bidan.utils;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.api.constants.Gender;
import org.smartregister.bidan.R;

/**
 * Created by sid-tech on 11/15/17.
 */

public class ImageUtils {

    public static int profileImageResourceByGender(String gender) {
        if (StringUtils.isNotBlank(gender)) {
            if (gender.equalsIgnoreCase(BidanConstants.GENDER.MALE)) {
                return R.drawable.child_boy_infant;
            } else if (gender.equalsIgnoreCase(BidanConstants.GENDER.FEMALE)) {
                return R.drawable.child_girl_infant;
            } else if (gender.toLowerCase().contains("trans")) {
                return R.drawable.child_transgender_inflant;
            }
        }
        return R.drawable.child_boy_infant;
    }

    public static int profileImageResourceByGender(Gender gender) {
        if (gender != null) {
            if (gender.equals(Gender.MALE)) {
                return R.drawable.child_boy_infant;
            } else if (gender.equals(Gender.FEMALE)) {
                return R.drawable.child_girl_infant;
            }
        }
        return R.drawable.child_transgender_inflant;
    }


}
