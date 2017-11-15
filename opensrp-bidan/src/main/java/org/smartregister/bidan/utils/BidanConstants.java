package org.smartregister.bidan.utils;

import org.smartregister.bidan.BuildConfig;

/**
 * Created by sid-tech on 11/14/17.
 */

public class BidanConstants {

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
