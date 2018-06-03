package org.smartregister.gizi.activity;

import android.support.v4.app.Fragment;

import org.smartregister.gizi.fragment.ScreenedPatientDetailsFragment;
import org.smartregister.gizi.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuelgithengi on 2/1/18.
 */

public class ScreenedPatientDetailActivity extends BasePatientDetailActivity {

    @Override
    protected Fragment getDetailFragment() {
        ScreenedPatientDetailsFragment mBaseFragment = new ScreenedPatientDetailsFragment();
        Map<String, String> patientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP);
        mBaseFragment.setPatientDetails(patientDetails);
        return mBaseFragment;
    }


}
