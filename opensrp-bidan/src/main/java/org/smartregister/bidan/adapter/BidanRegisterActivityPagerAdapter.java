package org.smartregister.bidan.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by sid-tech on 11/14/17.
 */

public class BidanRegisterActivityPagerAdapter extends FragmentPagerAdapter {
    private static final String ARG_PAGE = "page";
    private final Fragment mBaseFragment;
    private final FragmentManager fragmentManager;
    private final Fragment[] fragments;

    public BidanRegisterActivityPagerAdapter(FragmentManager supportFragmentManager, Fragment mBaseFragment, Fragment[] otherFragments) {
        super(supportFragmentManager);
        this.fragmentManager = supportFragmentManager;
        this.mBaseFragment = mBaseFragment;
        this.fragments = otherFragments;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = mBaseFragment;
                break;
            default:
                fragment = fragments[position - 1];
                break;
        }

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, position);
        if (fragment != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments == null ? 1 : fragments.length + 1;
    }
}
