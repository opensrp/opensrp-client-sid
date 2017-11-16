package org.smartregister.bidan_cloudant.utils;

import org.smartregister.bidan_cloudant.BuildConfig;

/**
 * Created by wildan on 10/30/17.
 */

public class AllConstantsINA {
    // Flurry Bidan Testing 2
    public static final String FLURRY_KEY = "9RDM5TWW4HJ5QH8TNJ3P";
    public static int SLEEP_TIME = 15000;
    public static final int WAITING_TIME = 5000;
    public static boolean IDLE = false;
    //    Table Name
    public static final String MOTHER_TABLE_NAME = "ec_kartu_ibu";
    public static final String CHILD_TABLE_NAME = "ec_anak";
    public static final String ANCPNC_TABLE_NAME = "ec_ibu";
    public static final String PNC_TABLE_NAME = "ec_pnc";

    public static final boolean TIME_CHECK = BuildConfig.TIME_CHECK;
    public static final long MAX_SERVER_TIME_DIFFERENCE = BuildConfig.MAX_SERVER_TIME_DIFFERENCE;

    public static final class KEY {
        public static final String CHILD = "child";
        public static final String BIDAN_ID = "zeir_id";
        public static final String MOTHER_FIRST_NAME = "mother_first_name";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String DOB = "dob";
        public static final String GENDER = "gender";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";
        public static final String INACTIVE = "inactive";
        public static final String MOTHER_LAST_NAME = "mother_last_name";


    }

    public static final class GENDER {

        public static final String MALE = "male";
        public static final String FEMALE = "female";
        public static final String TRANSGENDER = "transgender";
    }


}
