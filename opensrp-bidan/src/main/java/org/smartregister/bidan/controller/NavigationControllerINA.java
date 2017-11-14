package org.smartregister.bidan.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.bidan.activity.BidanHomeActivity;
import org.smartregister.bidan.activity.KChildSmartRegisterActivity;
import org.smartregister.bidan.activity.KIbuSmartRegisterActivity;
import org.smartregister.bidan.activity.KKBSmartRegisterActivity;
import org.smartregister.bidan.activity.KPNCSmartRegisterActivity;
import org.smartregister.view.controller.ANMController;
import org.smartregister.view.controller.NavigationController;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by sid-tech on 11/14/17.
 */

public class NavigationControllerINA extends NavigationController {
    private Activity activity;
    private ANMController anmController;
    private Context context;

    public NavigationControllerINA(Activity activity, ANMController anmController,Context context) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
        this.context = context;
    }

    /**
     * Activity for KOHORT IBU
     */
    @Override
    public void startECSmartRegistry() {
        openActivity(KIbuSmartRegisterActivity.class);
//        activity.startActivity(new Intent(activity, KIbuSmartRegisterActivity.class));
//
//        SharedPreferences sp = getDefaultSharedPreferences(this.activity);
//        if(sp.getBoolean("firstlaunch", true))
//            sp.edit().putBoolean("firstlaunch", false).apply();

    }


    @Override
    public void startFPSmartRegistry() {
        openActivity(KIbuSmartRegisterActivity.class);
    }

    @Override
    public void startANCSmartRegistry() {
        openActivity(KKBSmartRegisterActivity.class);
    }

    @Override
    public void startPNCSmartRegistry() {
        openActivity(KPNCSmartRegisterActivity.class);
    }

    @Override
    public void startChildSmartRegistry() {
        openActivity(KChildSmartRegisterActivity.class);
    }

    @Override
    public void startReports() {
        String id, pass;
        try{
            id = new JSONObject(anmController.get()).get("anmName").toString();
            pass = context.allSettings().fetchANMPassword();
        }catch(org.json.JSONException ex){
            id="noname";
            pass="null";
        }
        String uri = "http://"+id+":"+pass+"@kia-report.sid-indonesia.org/login/auth";
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

    private void openActivity(Class<?> targetClass) {
        activity.startActivity(new Intent(activity, targetClass));

        SharedPreferences sp = getDefaultSharedPreferences(this.activity);
        if(sp.getBoolean("firstlaunch", true))
            sp.edit().putBoolean("firstlaunch", false).apply();

    }


}