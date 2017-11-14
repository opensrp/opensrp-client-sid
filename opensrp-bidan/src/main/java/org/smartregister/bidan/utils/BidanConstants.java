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

}
