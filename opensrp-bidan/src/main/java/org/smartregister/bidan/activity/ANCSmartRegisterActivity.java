package org.smartregister.bidan.activity;

import android.support.v4.app.Fragment;

import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.ANCSmartRegisterFragment;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.OpenFormOption;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_RENCANA_PERSALINAN;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT_INTEGRASI;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT_LABTEST;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_PNC_REGISTRATION;

/**
 * Created by sid-tech on 11/28/17
 */

public class ANCSmartRegisterActivity extends BaseRegisterActivity {

    public static final String TAG = ANCSmartRegisterActivity.class.getName();

    @Override
    protected Fragment mBaseFragment() {

        return new ANCSmartRegisterFragment();
    }

    @Override
    protected List<String> buildFormNameList() {

        List<String> formNames = new ArrayList<>();
        formNames.add(KARTU_IBU_ANC_VISIT);
        formNames.add(KARTU_IBU_ANC_VISIT_INTEGRASI);
        formNames.add(KARTU_IBU_ANC_VISIT_LABTEST);
        formNames.add(KARTU_IBU_ANC_RENCANA_PERSALINAN);
        formNames.add(KARTU_IBU_PNC_REGISTRATION);
        formNames.add(KARTU_IBU_ANC_CLOSE);

        return formNames;
    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_register_anc_visit_form), KARTU_IBU_ANC_VISIT, formController),
                new OpenFormOption(getString(R.string.anc_visit_integrasi), KARTU_IBU_ANC_VISIT_INTEGRASI, formController),
                new OpenFormOption(getString(R.string.anc_visit_labtest), KARTU_IBU_ANC_VISIT_LABTEST, formController),
                new OpenFormOption(getString(R.string.str_rencana_persalinan_anc_form), KARTU_IBU_ANC_RENCANA_PERSALINAN, formController),
                new OpenFormOption(getString(R.string.str_register_pnc_form), KARTU_IBU_PNC_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_register_anc_close_form), KARTU_IBU_ANC_CLOSE, formController),
        };
    }

}
