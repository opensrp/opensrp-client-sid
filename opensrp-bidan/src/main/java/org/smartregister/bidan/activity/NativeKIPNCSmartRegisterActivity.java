package org.smartregister.bidan.activity;

import android.support.v4.app.Fragment;

import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.PNCSmartRegisterFragment;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.OpenFormOption;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_PNC_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_PNC_OA;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_PNC_POSPARTUM_KB;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_PNC_VISIT;

/**
 * Created by sid-tech on 11/28/17.
 */

public class NativeKIPNCSmartRegisterActivity extends BaseRegisterActivity {

    public static final String TAG = NativeKIPNCSmartRegisterActivity.class.getName();

    @Override
    protected String[] formNames() {

        return this.buildFormNameList();
    }

    @Override
    protected Fragment mBaseFragment() {

        return new PNCSmartRegisterFragment();
    }


    private String[] buildFormNameList() {

        List<String> formNames = new ArrayList<>();

        formNames.add(KARTU_IBU_PNC_VISIT);
        formNames.add(KARTU_IBU_PNC_POSPARTUM_KB);
        formNames.add(KARTU_IBU_PNC_CLOSE);
        formNames.add(KARTU_IBU_PNC_OA);
        formNames.add(KARTU_IBU_PNC_CLOSE);

        return formNames.toArray(new String[formNames.size()]);
    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_pnc_visit_form), KARTU_IBU_PNC_VISIT, formController),
                new OpenFormOption(getString(R.string.str_pnc_postpartum_family_planning_form), KARTU_IBU_PNC_POSPARTUM_KB, formController),
                new OpenFormOption(getString(R.string.str_pnc_close_form), KARTU_IBU_PNC_CLOSE, formController),
        };
    }

}