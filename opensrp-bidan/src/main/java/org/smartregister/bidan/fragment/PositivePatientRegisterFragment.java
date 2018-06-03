package org.smartregister.bidan.fragment;

import android.view.View;

import org.smartregister.bidan.R;
import org.smartregister.bidan.helper.DBQueryHelper;

import static util.BidanConstants.KEY.DIAGNOSIS_DATE;
import static util.BidanConstants.VIEW_CONFIGS.POSITIVE_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class PositivePatientRegisterFragment extends BaseRegisterFragment {

    @Override
    protected void populateClientListHeaderView(View view) {
        View headerLayout = getLayoutInflater(null).inflate(R.layout.register_positive_list_header, null);
        populateClientListHeaderView(view, headerLayout, POSITIVE_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getPositivePatientRegisterCondition();
    }

    @Override
    protected String[] getAdditionalColumns(String tableName) {
        return new String[]{
                tableName + "." + DIAGNOSIS_DATE};
    }

}
