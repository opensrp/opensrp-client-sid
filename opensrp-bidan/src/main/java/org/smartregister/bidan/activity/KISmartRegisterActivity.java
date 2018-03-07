package org.smartregister.bidan.activity;

import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.KISmartRegisterFragment;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.dialog.OpenFormOption;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.ANAK_BAYI_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_EDIT;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_KB_PELAYANAN;

/**
 * Created by Dimas Ciputra on 2/18/15
 */
public class KISmartRegisterActivity extends BaseRegisterActivity implements LocationSelectorDialogFragment.OnLocationSelectedListener {

    public static final String TAG = KISmartRegisterActivity.class.getName();


    @Override
    protected Fragment mBaseFragment() {

        return new KISmartRegisterFragment();
    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_edit_ki_form), KARTU_IBU_EDIT, formController),
                new OpenFormOption(getString(R.string.str_register_fp_form), KOHORT_KB_PELAYANAN, formController),
                new OpenFormOption(getString(R.string.str_register_anc_form), KARTU_IBU_ANC_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_register_child_form), ANAK_BAYI_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_close_ki_form), KARTU_IBU_CLOSE, formController),
        };
    }

    @Override
    protected List<String> buildFormNameList() {
        List<String> formNames = new ArrayList<>();
        formNames.add(KARTU_IBU_EDIT);
        formNames.add(0, KARTU_IBU_REGISTRATION);
        formNames.add(KOHORT_KB_PELAYANAN);
        formNames.add(KARTU_IBU_ANC_REGISTRATION);
        formNames.add(ANAK_BAYI_REGISTRATION);
        formNames.add(KARTU_IBU_CLOSE);

        return formNames;
    }

    @Override
    public void OnLocationSelected(String locationJSONString) {
        JSONObject combined = null;

        try {

            combined = new JSONObject(locationJSONString);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (combined != null) {
            FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());

            startFormActivity(KARTU_IBU_REGISTRATION, null, fieldOverrides.getJSONString());
        }
    }


}
