package org.smartregister.gizi.helper;

import util.GiziConstants;

/**
 * Created by ndegwamartin on 28/01/2018.
 */

public class DBQueryHelper {

    public static final String getPresumptivePatientRegisterCondition() {
        return " " + GiziConstants.KEY.PRESUMPTIVE + " =\"yes\" AND " + GiziConstants.KEY.CONFIRMED_TB + " IS NULL AND " + GiziConstants.KEY.DATE_REMOVED + " =\"\" ";
    }

    public static final String getPositivePatientRegisterCondition() {
        return " " + GiziConstants.KEY.CONFIRMED_TB + " = \"yes\" AND " + GiziConstants.KEY.TREATMENT_INITIATION_DATE + " IS NULL AND " + GiziConstants.KEY.DATE_REMOVED + " =\"\" ";
    }

    public static final String getIntreatmentPatientRegisterCondition() {
        return GiziConstants.KEY.TREATMENT_INITIATION_DATE + " IS NOT NULL AND " + GiziConstants.KEY.DATE_REMOVED + " =\"\" ";
    }
}
