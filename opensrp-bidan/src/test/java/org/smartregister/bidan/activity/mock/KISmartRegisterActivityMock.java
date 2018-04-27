package org.smartregister.bidan.activity.mock;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;

import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.KISmartRegisterActivity;

/**
 * Created by sid-tech on 4/25/18
 */

public class KISmartRegisterActivityMock extends KISmartRegisterActivity {

    public static Context mContext;
    public static InputMethodManager inputManager;

    public static void setmContext(Context Context) {
        mContext = Context;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTheme(R.style.AppTheme);
    }

    @Override
    protected Context context() {
        return mContext;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (name.equalsIgnoreCase(INPUT_METHOD_SERVICE)) {
            return inputManager;
        } else {
            return super.getSystemService(name);
        }
    }

}
