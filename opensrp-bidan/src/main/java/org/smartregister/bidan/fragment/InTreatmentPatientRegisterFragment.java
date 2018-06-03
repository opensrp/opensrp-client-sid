package org.smartregister.bidan.fragment;

import android.view.View;

import org.smartregister.bidan.R;
import org.smartregister.bidan.helper.DBQueryHelper;

import static util.BidanConstants.KEY.BASELINE;
import static util.BidanConstants.KEY.DIAGNOSIS_DATE;
import static util.BidanConstants.KEY.NEXT_VISIT_DATE;
import static util.BidanConstants.KEY.SMR_NEXT_VISIT_DATE;
import static util.BidanConstants.KEY.TREATMENT_INITIATION_DATE;
import static util.BidanConstants.VIEW_CONFIGS.INTREATMENT_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class InTreatmentPatientRegisterFragment extends BaseRegisterFragment {

    @Override
    protected void populateClientListHeaderView(View view) {
        View headerLayout = getLayoutInflater(null).inflate(R.layout.register_intreatment_list_header, null);
        populateClientListHeaderView(view, headerLayout, INTREATMENT_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getIntreatmentPatientRegisterCondition();
    }

    @Override
    protected String[] getAdditionalColumns(String tableName) {
        return new String[]{
                tableName + "." + DIAGNOSIS_DATE,
                tableName + "." + TREATMENT_INITIATION_DATE,
                tableName + "." + BASELINE,
                tableName + "." + NEXT_VISIT_DATE,
                tableName + "." + SMR_NEXT_VISIT_DATE};
    }

}
