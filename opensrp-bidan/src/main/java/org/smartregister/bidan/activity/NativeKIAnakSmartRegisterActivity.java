package org.smartregister.bidan.activity;

import android.support.v4.app.Fragment;
import android.util.Log;

import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.AnakSmartRegisterFragment;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.OpenFormOption;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.BALITA_KUNJUNGAN;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.BAYI_IMUNISASI;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.BAYI_NEONATAL_PERIOD;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANAK_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_BAYI_EDIT;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_BAYI_KUNJUNGAN;

/**
 * Created by sid-tech on 11/28/17.
 */
//import com.flurry.android.FlurryAgent;
//import org.smartregister.bidan.lib.FlurryFacade;

/**
 * Created by Dimas Ciputra on 4/7/15.
 */
public class NativeKIAnakSmartRegisterActivity extends BaseRegisterActivity {

    public static final String TAG = NativeKIAnakSmartRegisterActivity.class.getName();


    @Override
    protected Fragment mBaseFragment() {

        Log.e(TAG, "mBaseFragment: ");
        return new AnakSmartRegisterFragment();
    }

    @Override
    protected List<String> buildFormNameList() {

        List<String> formNames = new ArrayList<>();

        formNames.add(BAYI_NEONATAL_PERIOD);
        formNames.add(KOHORT_BAYI_KUNJUNGAN);
        formNames.add(BALITA_KUNJUNGAN);
        formNames.add(KARTU_IBU_ANAK_CLOSE);
        formNames.add(BAYI_IMUNISASI);
        formNames.add(KOHORT_BAYI_EDIT);

        return formNames;
    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_anak_edit), KOHORT_BAYI_EDIT, formController),
                new OpenFormOption(getString(R.string.str_anak_neonatal), BAYI_NEONATAL_PERIOD, formController),
                new OpenFormOption(getString(R.string.str_anak_bayi_visit), KOHORT_BAYI_KUNJUNGAN, formController),
                new OpenFormOption(getString(R.string.str_anak_balita_visit), BALITA_KUNJUNGAN, formController),
                new OpenFormOption(getString(R.string.str_child_immunizations), BAYI_IMUNISASI, formController),
                new OpenFormOption(getString(R.string.str_child_close), KARTU_IBU_ANAK_CLOSE, formController),
        };
    }

}