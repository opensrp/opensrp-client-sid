package org.smartregister.bidan.helper;

import util.BidanConstants;

/**
 * Created by ndegwamartin on 28/01/2018.
 */

public class DBQueryHelper {

    public static final String getPresumptivePatientRegisterCondition() {
        return " " + BidanConstants.KEY.PRESUMPTIVE + " =\"yes\" AND " + BidanConstants.KEY.CONFIRMED_TB + " IS NULL AND " + BidanConstants.KEY.DATE_REMOVED + " =\"\" ";
    }

    public static final String getPositivePatientRegisterCondition() {
        return " " + BidanConstants.KEY.CONFIRMED_TB + " = \"yes\" AND " + BidanConstants.KEY.TREATMENT_INITIATION_DATE + " IS NULL AND " + BidanConstants.KEY.DATE_REMOVED + " =\"\" ";
    }

    public static final String getIntreatmentPatientRegisterCondition() {
        return BidanConstants.KEY.TREATMENT_INITIATION_DATE + " IS NOT NULL AND " + BidanConstants.KEY.DATE_REMOVED + " =\"\" ";
    }
}
