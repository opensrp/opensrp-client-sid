package org.smartregister.bidan.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by sid-tech on 1/24/18.
 */

public class PermissionHelper extends Fragment {
    public static final String TAG = PermissionHelper.class.getName();
    private static final String ARGS_PERMISSIONS = "args_permissions";
    private static final int REQUEST_PERMISSION = 101;
    private PermissionCallback mCallback;

    public static PermissionHelper newInstance(String[] permissions) {
        PermissionHelper permissionHelper = new PermissionHelper();
        Bundle args = new Bundle();
        args.putStringArray(ARGS_PERMISSIONS, permissions);
        permissionHelper.setArguments(args);
        return permissionHelper;
    }

    public static PermissionHelper attach(FragmentManager fragmentManager, String[] permission) {
        PermissionHelper permissionHelper = (PermissionHelper) fragmentManager.findFragmentByTag(TAG);
        if (permissionHelper == null) {
            Fragment permissionHelper2 = newInstance(permission);
            fragmentManager.beginTransaction().add(permissionHelper2, TAG).commit();
            return (PermissionHelper) permissionHelper2;
        }
        permissionHelper.getArguments().putStringArray(ARGS_PERMISSIONS, permission);
        permissionHelper.checkPermissions();
        return permissionHelper;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof PermissionCallback) {
//            this.mCallback = (PermissionCallback) context;
//            checkPermissions();
//            return;
//        }
//        throw new IllegalArgumentException(context.toString() + " must implement PermissionHelper.PermissionCallback");
//    }

    public void onDetach() {
        super.onDetach();
        this.mCallback = null;
    }

    public void checkPermissions() {
        ArrayList<String> requiredPermissions = new ArrayList<>();
        String[] permissions = getArguments().getStringArray(ARGS_PERMISSIONS);
//        String[] var3 = permissions;
        int var4 = permissions.length;
        for (int var5 = 0; var5 < var4; var5++) {
            String permission = permissions[var5];

//            if (ContextCompat.checkSelfPermission(getActivity(), permission) == 0) {
//                this.mCallback.onPermissionResult(permission, true);
//            } else {
                requiredPermissions.add(permission);
//            }
        }
//        if (requiredPermissions.size() > 0) {
//            requestPermissions(requiredPermissions.toArray(new String[requiredPermissions.size()]), 101);
//        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            int count = permissions.length;
            for (int i = 0; i < count; i++) {
                String permission = permissions[i];
                if (grantResults[i] == 0) {
                    this.mCallback.onPermissionResult(permission, true);
                } else {
                    this.mCallback.onPermissionResult(permission, false);
                }
            }
            return;
        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public interface PermissionCallback {
        void onPermissionResult(String str, boolean z);
    }
}

