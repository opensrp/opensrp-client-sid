package org.smartregister.bidan2.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;

import org.smartregister.bidan2.R;
import org.smartregister.bidan2.fragment.MotherFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sid-tech on 11/23/17.
 */

public class KMotherSmartRegisterActivity extends SecuredNativeSmartRegisterActivity implements LocationSelectorDialogFragment.OnLocationSelectedListener{

    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;

    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBaseFragment = new MotherFragment();

        // Instantiate a ViewPager and a PagerAdapter.
//        mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment);
//        mPager.setOffscreenPageLimit(formNames.length);
//        mPager.setAdapter(mPagerAdapter);
//        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                currentPage = position;
//                onPageChanged(position);
//            }
//        });

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
    public void OnLocationSelected(String s) {

    }
}
