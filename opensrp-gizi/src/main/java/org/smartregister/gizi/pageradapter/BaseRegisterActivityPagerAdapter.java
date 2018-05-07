package org.smartregister.gizi.pageradapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.smartregister.enketo.view.fragment.DisplayFormFragment;



/**
 * Created by koros on 11/2/15.
 */
public class BaseRegisterActivityPagerAdapter extends FragmentPagerAdapter {
    public static final String ARG_PAGE = "page";
    private String[] dialogOptions;
    private Fragment mBaseFragment;
    private Fragment mProfileFragment;
    public int offset = 0;

    public BaseRegisterActivityPagerAdapter(FragmentManager fragmentManager, String[] dialogOptions, Fragment baseFragment) {
        super(fragmentManager);
        this.dialogOptions = dialogOptions;
        this.mBaseFragment = baseFragment;
        offset += 1;
    }
    public BaseRegisterActivityPagerAdapter(FragmentManager fragmentManager, String[] dialogOptions, Fragment baseFragment, Fragment mProfileFragment) {
        super(fragmentManager);
        this.dialogOptions = dialogOptions;
        this.mBaseFragment = baseFragment;
        this.mProfileFragment = mProfileFragment;
        offset += 2;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = mBaseFragment;
                break;
            case 1:
                if(mProfileFragment != null) {
                    fragment = mProfileFragment;
                    break;
                }
            default:
                String formName = dialogOptions[position - offset]; // account for the base fragment
                DisplayFormFragment f = new DisplayFormFragment();
                f.setFormName(formName);
                fragment = f;
                break;
        }

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return dialogOptions.length + offset; // index 0 is always occupied by the base fragment
    }

    public int offset() {
        return offset;
    }

}