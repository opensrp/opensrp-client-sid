package org.smartregister.bidan.activity;

import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.NativeKISmartRegisterFragment;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.dialog.OpenFormOption;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.ANAK_BAYI_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_KB_PELAYANAN;

/**
 * Created by sid-tech on 11/28/17.
 */

public class NativeKIbuSmartRegisterActivity extends BaseRegisterActivity implements LocationSelectorDialogFragment.OnLocationSelectedListener {

    public static final String TAG = NativeKIbuSmartRegisterActivity.class.getName();

    @Override
    protected String[] formNames() {

        return this.buildFormNameList();
    }

    @Override
    protected Fragment mBaseFragment() {

        return new NativeKISmartRegisterFragment();
    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_register_fp_form), KOHORT_KB_PELAYANAN, formController),
                new OpenFormOption(getString(R.string.str_register_anc_form), KARTU_IBU_ANC_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_register_child_form), ANAK_BAYI_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_close_ki_form), KARTU_IBU_CLOSE, formController),
        };
    }

    private String[] buildFormNameList() {
        List<String> formNames = new ArrayList<>();
        formNames.add(KARTU_IBU_REGISTRATION);
        formNames.add(KOHORT_KB_PELAYANAN);
        formNames.add(KARTU_IBU_ANC_REGISTRATION);
        formNames.add(ANAK_BAYI_REGISTRATION);
        formNames.add(KARTU_IBU_CLOSE);

        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    public void OnLocationSelected(String locationJSONString) {
//        if(Support.ONSYNC) {
//            Toast.makeText(this,"Data still Synchronizing, please wait",Toast.LENGTH_SHORT).show();
//            return;
//        }
        JSONObject combined = null;
        try {
            JSONObject locationJSON = new JSONObject(locationJSONString);
//            JSONObject uniqueId = new JSONObject(LoginActivity.generator.uniqueIdController().getUniqueIdJson());

            combined = locationJSON;
//            Iterator<String> iter = uniqueId.keys();

//            while (iter.hasNext()) {
//                String key = iter.next();
//                combined.put(key, uniqueId.get(key));
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (combined != null) {
            FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());

            startFormActivity(KARTU_IBU_REGISTRATION, null, fieldOverrides.getJSONString());
        }
    }


}
