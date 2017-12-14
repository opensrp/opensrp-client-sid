package org.smartregister.bidan.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.ANCSmartRegisterFragment;
import org.smartregister.bidan.fragment.PNCSmartRegisterFragment;
import org.smartregister.bidan.pageradapter.BaseRegisterActivityPagerAdapter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.ZiggyService;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.DialogOptionModel;
import org.smartregister.view.dialog.EditOption;
import org.smartregister.view.dialog.OpenFormOption;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_RENCANA_PERSALINAN;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT_INTEGRASI;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT_LABTEST;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_PNC_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_PNC_OA;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_PNC_POSPARTUM_KB;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_PNC_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_PNC_VISIT;
import static org.smartregister.util.Utils.getValue;

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
    protected Fragment mBaseFragment(){

        return new PNCSmartRegisterFragment();
    }


    private String[] buildFormNameList(){

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