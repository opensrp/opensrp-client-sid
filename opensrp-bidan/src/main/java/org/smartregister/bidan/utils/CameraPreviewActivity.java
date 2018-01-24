package org.smartregister.bidan.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.FrameLayout;

/**
 * Created by sid-tech on 1/24/18.
 */

public class CameraPreviewActivity extends Activity {
//public class CameraPreviewActivity { implements PermissionHelper.PermissionCallback {

    public static final String REQUEST_TYPE = "org.nusabit.fbpkh.REQUEST_TYPE";
    private int mScreenMode;
    private FrameLayout mViewContent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        if (Build.VERSION.SDK_INT >= 16) {
            getWindow().getDecorView().setSystemUiVisibility(1284);
        } else {
            getWindow().addFlags(1024);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0);
        }
//        if (Build.VERSION.SDK_INT >= 23) {
//            PermissionHelper.attach(getSupportFragmentManager(), new String[]{"android.permission.CAMERA"});
//            return;
//        }
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), getIntent().getIntExtra(REQUEST_TYPE, -1));
    }

    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode != -1) {
            setResult(-1, data);
            finish();
        }
    }

    public void onPermissionResult(String s, boolean success) {
        if (success) {
            startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), getIntent().getIntExtra(REQUEST_TYPE, -1));
            return;
        }
        setResult(0);
        finish();
    }
}
