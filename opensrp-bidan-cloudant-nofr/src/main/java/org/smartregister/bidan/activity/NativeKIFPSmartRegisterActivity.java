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
import org.smartregister.bidan.fragment.FPSmartRegisterFragment;
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

import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_KB_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_KB_REGISTER;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_KB_UPDATE;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by sid-tech on 11/30/17.
 */

public class NativeKIFPSmartRegisterActivity extends BaseRegisterActivity {

    public static final String TAG = NativeKIFPSmartRegisterActivity.class.getSimpleName();

    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    ZiggyService ziggyService;
    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        formNames = this.buildFormNameList();
        mBaseFragment = new FPSmartRegisterFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment);
        mPager.setOffscreenPageLimit(formNames.length);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }
        });

        ziggyService = context().ziggyService();

    }
    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {
    }

    @Override
    public void startRegistration() {
    }

    @Override
    protected void setupViews() {
    }

    @Override
    protected void onResumption() {
    }

    public void onPageChanged(int page) {
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        LoginActivity.setLanguage();
    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_kb_update), KOHORT_KB_UPDATE, formController),
                new OpenFormOption(getString(R.string.str_kb_close), KOHORT_KB_CLOSE, formController),
        };
    }

    private String[] buildFormNameList(){
        List<String> formNames = new ArrayList<>();
        formNames.add(KOHORT_KB_REGISTER);
        formNames.add(KOHORT_KB_UPDATE);

        return formNames.toArray(new String[formNames.size()]);
    }


    @Override
    protected void onPause() {
        super.onPause();

        retrieveAndSaveUnsubmittedFormData();
        String KIEnd = timer.format(new Date());
        Map<String, String> KI = new HashMap<>();
        KI.put("end", KIEnd);
    }

    public void retrieveAndSaveUnsubmittedFormData() {
        if (currentActivityIsShowingForm()) {
            DisplayFormFragment formFragment = getDisplayFormFragmentAtIndex(currentPage);
            formFragment.saveCurrentFormData();
        }
    }

    private boolean currentActivityIsShowingForm() {
        return currentPage != 0;
    }


    public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return (DisplayFormFragment) findFragmentByPosition(index);
    }

    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    public class EditDialogOptionModel implements DialogOptionModel {

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            CommonPersonObjectClient pc = (CommonPersonObjectClient) tag;
            DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
            detailsRepository.updateDetails(pc);
            String ibuCaseId = getValue(pc.getColumnmaps(), "relational_id", true).toLowerCase();
            Log.d(TAG, "onDialogOptionSelection: "+pc.getDetails());
            JSONObject fieldOverrides = new JSONObject();
            try {
                fieldOverrides.put("Province", pc.getDetails().get("stateProvince"));
                fieldOverrides.put("District", pc.getDetails().get("countyDistrict"));
                fieldOverrides.put("Sub-district", pc.getDetails().get("address2"));
                fieldOverrides.put("Village", pc.getDetails().get("cityVillage"));
                fieldOverrides.put("Sub-village", pc.getDetails().get("address1"));
                fieldOverrides.put("jenis_kelamin", pc.getDetails().get("gender"));
                fieldOverrides.put("ibuCaseId", ibuCaseId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            FieldOverrides fo = new FieldOverrides(fieldOverrides.toString());
            onEditSelectionWithMetadata((EditOption) option, (SmartRegisterClient) tag, fo.getJSONString());
        }
    }


}
