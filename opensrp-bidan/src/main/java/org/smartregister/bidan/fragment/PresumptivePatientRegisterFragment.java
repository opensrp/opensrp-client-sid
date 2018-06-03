package org.smartregister.bidan.fragment;


import android.view.View;

import org.smartregister.bidan.R;
import org.smartregister.bidan.helper.DBQueryHelper;

import static util.BidanConstants.VIEW_CONFIGS.PRESUMPTIVE_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/6/17.
 */

public class PresumptivePatientRegisterFragment extends BaseRegisterFragment {


    @Override
    protected void populateClientListHeaderView(View view) {
        View headerLayout = getLayoutInflater(null).inflate(R.layout.register_presumptive_list_header, null);
        populateClientListHeaderView(view, headerLayout, PRESUMPTIVE_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getPresumptivePatientRegisterCondition();
    }

    @Override
    protected String[] getAdditionalColumns(String tableName) {
        return new String[]{};
    }


}
