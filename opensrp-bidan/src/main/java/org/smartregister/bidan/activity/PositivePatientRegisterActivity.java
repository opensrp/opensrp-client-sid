package org.smartregister.bidan.activity;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.PositivePatientRegisterFragment;

import java.util.Arrays;
import java.util.List;

import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;
import static util.BidanConstants.ENKETO_FORMS.ADD_POSITIVE_PATIENT;
import static util.BidanConstants.ENKETO_FORMS.TREATMENT_INITIATION;
import static util.BidanConstants.VIEW_CONFIGS.COMMON_REGISTER_HEADER;
import static util.BidanConstants.VIEW_CONFIGS.COMMON_REGISTER_ROW;
import static util.BidanConstants.VIEW_CONFIGS.POSITIVE_REGISTER;
import static util.BidanConstants.VIEW_CONFIGS.POSITIVE_REGISTER_HEADER;
import static util.BidanConstants.VIEW_CONFIGS.POSITIVE_REGISTER_ROW;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class PositivePatientRegisterActivity extends BaseRegisterActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNewPatient:
                String entityId = generateRandomUUIDString();
                startFormActivity(ADD_POSITIVE_PATIENT, entityId, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Fragment getRegisterFragment() {
        return new PositivePatientRegisterFragment();
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(POSITIVE_REGISTER, POSITIVE_REGISTER_HEADER, POSITIVE_REGISTER_ROW, COMMON_REGISTER_HEADER, COMMON_REGISTER_ROW);
    }

    @Override
    protected List<String> buildFormNameList() {
        formNames = super.buildFormNameList();
        formNames.add(0, ADD_POSITIVE_PATIENT);
        formNames.add(TREATMENT_INITIATION);
        return formNames;
    }
}
