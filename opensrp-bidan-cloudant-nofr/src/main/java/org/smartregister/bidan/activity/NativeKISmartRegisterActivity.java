package org.smartregister.bidan.activity;

import android.support.v4.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.NativeKISmartRegisterFragment;
import org.smartregister.bidan.utils.BidanFormUtils;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.dialog.OpenFormOption;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.ANAK_BAYI_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_EDIT;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_KB_PELAYANAN;

/**
 * Created by Dimas Ciputra on 2/18/15.
 */
public class NativeKISmartRegisterActivity extends BaseRegisterActivity implements LocationSelectorDialogFragment.OnLocationSelectedListener {

    public static final String TAG = NativeKISmartRegisterActivity.class.getSimpleName();

    @Override
    protected String[] formNames() {

        return this.buildFormNameList();
    }

    @Override
    protected Fragment mBaseFragment(){

        return new NativeKISmartRegisterFragment();
    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_edit_ki_form), KARTU_IBU_EDIT, formController),
                new OpenFormOption(getString(R.string.str_register_fp_form), "kohort_kb_pelayanan", formController),
                new OpenFormOption(getString(R.string.str_register_anc_form), "kartu_anc_registration", formController),
                new OpenFormOption(getString(R.string.str_register_child_form), ANAK_BAYI_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_close_ki_form), KARTU_IBU_CLOSE, formController),
        };
    }

    private String[] buildFormNameList(){
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
