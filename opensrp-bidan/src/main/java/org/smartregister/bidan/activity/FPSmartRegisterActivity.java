package org.smartregister.bidan.activity;

import android.support.v4.app.Fragment;

import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.FPSmartRegisterFragment;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.OpenFormOption;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_KB_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_KB_REGISTER;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_KB_UPDATE;

/**
 * Created by sid-tech on 11/30/17.
 */

public class FPSmartRegisterActivity extends BaseRegisterActivity {

    public static final String TAG = FPSmartRegisterActivity.class.getName();

    @Override
    protected Fragment mBaseFragment() {

        return new FPSmartRegisterFragment();
    }

    @Override
    protected List<String> buildFormNameList() {

        List<String> formNames = new ArrayList<>();
//        formNames.add(KOHORT_KB_REGISTER);
        formNames.add(KOHORT_KB_UPDATE);
        formNames.add(KOHORT_KB_CLOSE);

        return formNames;
    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_kb_update), KOHORT_KB_UPDATE, formController),
                new OpenFormOption(getString(R.string.str_kb_close), KOHORT_KB_CLOSE, formController),
        };
    }

}
