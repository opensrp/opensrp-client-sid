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

    public class FormNames {
        public static final String EC_REGISTRATION = "ec_registration";
        public static final String FP_COMPLICATIONS = "fp_complications";
        public static final String FP_CHANGE = "fp_change";
        public static final String RENEW_FP_PRODUCT = "renew_fp_product";
        public static final String EC_CLOSE = "ec_close";
        public static final String ANC_REGISTRATION = "anc_registration";
        public static final String ANC_REGISTRATION_OA = "anc_registration_oa";
        public static final String ANC_VISIT = "anc_visit";
        public static final String ANC_CLOSE = "anc_close";
        public static final String TT = "tt";
        public static final String TT_BOOSTER = "tt_booster";
        public static final String TT_1 = "tt_1";
        public static final String TT_2 = "tt_2";
        public static final String IFA = "ifa";
        public static final String HB_TEST = "hb_test";
        public static final String DELIVERY_OUTCOME = "delivery_outcome";
        public static final String PNC_REGISTRATION_OA = "pnc_registration_oa";
        public static final String PNC_CLOSE = "pnc_close";
        public static final String PNC_VISIT = "pnc_visit";
        public static final String PNC_POSTPARTUM_FAMILY_PLANNING = "postpartum_family_planning";
        public static final String CHILD_IMMUNIZATIONS = "child_immunizations";
        public static final String KOHORT_CHILD_REGISTRATION = "child_registration_ec";
        public static final String CHILD_REGISTRATION_OA = "child_registration_oa";
        public static final String CHILD_CLOSE = "child_close";
        public static final String CHILD_ILLNESS = "child_illness";
        public static final String VITAMIN_A = "vitamin_a";
        public static final String DELIVERY_PLAN = "delivery_plan";
        public static final String EC_EDIT = "ec_edit";
        public static final String ANC_INVESTIGATIONS = "anc_investigations";
        public static final String RECORD_ECPS = "record_ecps";
        public static final String FP_REFERRAL_FOLLOWUP = "fp_referral_followup";
        public static final String FP_FOLLOWUP = "fp_followup";

        // KOHORT IBU
        public static final String KOHORT_MOTHER_REGISTRATION = "kartu_ibu_registration";
        public static final String KARTU_IBU_EDIT = "kartu_ibu_edit";
        public static final String KARTU_IBU_CLOSE = "kartu_ibu_close";

        // ANC
        public static final String KARTU_IBU_ANC_REGISTRATION = "kartu_anc_registration";
        public static final String KARTU_IBU_ANC_OA="kartu_anc_registration_oa";
        public static final String KARTU_IBU_ANC_RENCANA_PERSALINAN = "kartu_anc_rencana_persalinan";
        public static final String KARTU_IBU_ANC_EDIT="kartu_anc_visit_edit";
        public static final String KARTU_IBU_ANC_CLOSE="kartu_anc_close";
        public static final String KARTU_IBU_ANC_VISIT="kartu_anc_visit";
        public static final String KARTU_IBU_ANC_VISIT_INTEGRASI = "kartu_anc_visit_integrasi";
        public static final String KARTU_IBU_ANC_VISIT_LABTEST = "kartu_anc_visit_labTest";

        // PNC
        public static final String KARTU_IBU_PNC_EDIT="kartu_pnc_edit";
        public static final String KOHORT_PNC_REGISTRATION = "kartu_pnc_dokumentasi_persalinan";
        public static final String KARTU_IBU_PNC_CLOSE="kartu_pnc_close";
        public static final String KARTU_IBU_PNC_OA="kartu_pnc_regitration_oa";
        public static final String KARTU_IBU_PNC_VISIT="kartu_pnc_visit";
        public static final String KARTU_IBU_PNC_POSPARTUM_KB="kartu_pnc_pospartum_kb";

        // ANAK
        public static final String KOHORT_BAYI_KUNJUNGAN="kohort_bayi_kunjungan";
        public static final String KARTU_IBU_ANAK_CLOSE="kohort_anak_tutup";
        public static final String BALITA_KUNJUNGAN="kohort_balita_kunjungan";
        public static final String BAYI_IMUNISASI="kohort_bayi_immunization";
        public static final String BAYI_NEONATAL_PERIOD="kohort_bayi_neonatal_period";
        public static final String KOHORT_BAYI_EDIT="kohort_bayi_edit";
        public static final String ANAK_BAYI_REGISTRATION = "kohort_bayi_registration";
        public static final String ANAK_NEW_REGISTRATION="kohort_bayi_registration_oa";

        // KB
        public static final String KOHORT_KB_REGISTRATION ="kohort_kb_registration";
        public static final String KOHORT_KB_PELAYANAN="kohort_kb_pelayanan";
        public static final String KOHORT_KB_CLOSE="kohort_kb_close";
        public static final String KOHORT_KB_UPDATE="kohort_kb_update";
        // public static final String KOHORT_KB_EDIT="kohort_kb_edit";

        public static final String FEEDBACK_BIDAN = "feedback_bidan";
    }


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
