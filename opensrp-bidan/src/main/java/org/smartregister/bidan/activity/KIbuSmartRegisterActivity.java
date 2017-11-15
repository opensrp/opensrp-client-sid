package org.smartregister.bidan.activity;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.smartregister.adapter.SmartRegisterPaginatedAdapter;
import org.smartregister.bidan.R;
import org.smartregister.bidan.adapter.BidanRegisterActivityPagerAdapter;
import org.smartregister.bidan.fragment.BaseSmartRegisterFragment;
import org.smartregister.bidan.fragment.IbuSmartRegisterFragment;
import org.smartregister.domain.FetchStatus;
import org.smartregister.event.Event;
import org.smartregister.event.Listener;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.dialog.DialogOptionModel;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sid-tech on 11/14/17.
 */

public class KIbuSmartRegisterActivity extends BaseRegisterActivity {

    @Bind(R.id.view_pager)
    protected OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private static final int REQUEST_CODE_GET_JSON = 3432;
    private int currentPage;
    public static final int ADVANCED_SEARCH_POSITION = 1;
    private ProgressDialog progressDialog;

    private android.support.v4.app.Fragment mBaseFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mBaseFragment = new IbuSmartRegisterFragment();
//        Fragment[] otherFragments = {new AdvancedSearchFragment()};
        Fragment[] otherFragments = {};

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new BidanRegisterActivityPagerAdapter(getSupportFragmentManager(), mBaseFragment, otherFragments);
        mPager.setOffscreenPageLimit(otherFragments.length);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
        });

        Event.ON_DATA_FETCHED.addListener(onDataFetchedListener);
//        initializeProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Event.ON_DATA_FETCHED.removeListener(onDataFetchedListener);
    }

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected void setupViews() {
    }

    @Override
    protected void onResumption() {
//        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        LinearLayout childregister = (LinearLayout) drawer.findViewById(R.id.child_register);
//        childregister.setBackgroundColor(getResources().getColor(R.color.tintcolor));

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
    public void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        try {
            LoginActivity.setLanguage();
        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), e.getMessage());
        }
        super.showFragmentDialog(dialogOptionModel, tag);
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
//        try {
//            if (mBaseFragment instanceof IbuSmartRegisterFragment) {
//                LocationPickerView locationPickerView = ((IbuSmartRegisterFragment) mBaseFragment).getLocationPickerView();
//                String locationId = JsonFormUtils.getOpenMrsLocationId(context(), locationPickerView.getSelectedItem());
//                JsonFormUtils.startForm(this, context(), REQUEST_CODE_GET_JSON, formName, entityId,
//                        metaData, locationId);
//            }
//        } catch (Exception e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        }

    }

    private final Listener<FetchStatus> onDataFetchedListener = new Listener<FetchStatus>() {
        @Override
        public void onEvent(FetchStatus fetchStatus) {
            refreshList(fetchStatus);
        }
    };

    public void refreshList(final FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            BaseSmartRegisterFragment registerFragment = (BaseSmartRegisterFragment) findFragmentByPosition(0);
            if (registerFragment != null && fetchStatus.equals(FetchStatus.fetched)) {
                registerFragment.refreshListView();
            }
        } else {
            Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    BaseSmartRegisterFragment registerFragment = (BaseSmartRegisterFragment) findFragmentByPosition(0);
                    if (registerFragment != null && fetchStatus.equals(FetchStatus.fetched)) {
                        registerFragment.refreshListView();
                    }
                }
            });
        }

    }

    public android.support.v4.app.Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    public void updateAdvancedSearchFilterCount(int count) {
//        AdvancedSearchFragment advancedSearchFragment = (AdvancedSearchFragment) findFragmentByPosition(ADVANCED_SEARCH_POSITION);
//        if (advancedSearchFragment != null) {
//            advancedSearchFragment.updateFilterCount(count);
//        }
    }
}
