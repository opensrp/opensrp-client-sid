package org.smartregister.gizi.activity.mock;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.smartregister.Context;
import org.smartregister.gizi.activity.LoginActivity;

/**
 * Created by sid-tech on 4/24/18
 */

public class LoginActivityMock extends LoginActivity {

    public static Context mockactivitycontext;
    public static InputMethodManager inputManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(org.smartregister.R.style.Base_Theme_AppCompat);
        super.onCreate(savedInstanceState);
    }

    @Override
    public Object getSystemService(String name) {
        if (name.equalsIgnoreCase(INPUT_METHOD_SERVICE)) {
            return inputManager;
        } else {
            return super.getSystemService(name);
        }
    }

    @Nullable
    @Override
    public View getCurrentFocus() {
        return findViewById(org.smartregister.R.id.login_userNameText);
    }

    public String getAppVersion(){
        try {
            return getVersion();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
