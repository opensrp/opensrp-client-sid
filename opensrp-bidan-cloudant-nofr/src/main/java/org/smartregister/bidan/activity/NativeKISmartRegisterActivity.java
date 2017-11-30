package org.smartregister.bidan.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.NativeKISmartRegisterFragment;
import org.smartregister.bidan.pageradapter.BaseRegisterActivityPagerAdapter;
import org.smartregister.bidan.utils.AllConstantsINA;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.service.ZiggyService;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;
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

/**
 * Created by sid-tech on 11/28/17.
 */

public class NativeKISmartRegisterActivity extends SecuredNativeSmartRegisterActivity {

    public static final String TAG = NativeKISmartRegisterActivity.class.getSimpleName();
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
        mBaseFragment = new NativeKISmartRegisterFragment();

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


    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_register_fp_form), "kohort_kb_pelayanan", formController),
                new OpenFormOption(getString(R.string.str_register_anc_form), "kartu_anc_registration", formController),
                new OpenFormOption(getString(R.string.str_register_child_form), AllConstantsINA.FormNames.ANAK_BAYI_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_close_ki_form), AllConstantsINA.FormNames.KARTU_IBU_CLOSE, formController),
        };


    }

    public void onPageChanged(int page) {
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LoginActivity.setLanguage();
    }

    private String[] buildFormNameList() {
        List<String> formNames = new ArrayList<>();
        formNames.add(AllConstantsINA.FormNames.KARTU_IBU_REGISTRATION);
        formNames.add(AllConstantsINA.FormNames.KOHORT_KB_PELAYANAN);
        formNames.add(AllConstantsINA.FormNames.KARTU_IBU_ANC_REGISTRATION);
        formNames.add(AllConstantsINA.FormNames.ANAK_BAYI_REGISTRATION);
        formNames.add(AllConstantsINA.FormNames.KARTU_IBU_CLOSE);

        return formNames.toArray(new String[formNames.size()]);
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
    protected void onResumption() {
    }
}
