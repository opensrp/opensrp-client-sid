package org.smartregister.bidan.fragment.mock;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.LinearLayout;

import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.BaseSmartRegisterFragment;

public class FragmentMockActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTheme(R.style.AppTheme); //we need this here
        LinearLayout linearLayout;
        linearLayout = new LinearLayout(this);
        setContentView(linearLayout);

    }

    public void startBaseSmartRegisterFragment() {
        BaseSmartRegisterFragment fragment = new BaseSmartRegisterFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, "");
        fragmentTransaction.commit();
    }


}
