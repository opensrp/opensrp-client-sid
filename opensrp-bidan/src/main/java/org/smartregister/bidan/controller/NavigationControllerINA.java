package org.smartregister.bidan.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.bidan.activity.KBSmartRegisterActivity;
import org.smartregister.bidan.activity.KIANCSmartRegisterActivity;
import org.smartregister.bidan.activity.KIAnakSmartRegisterActivity;
import org.smartregister.bidan.activity.KIPNCSmartRegisterActivity;
import org.smartregister.bidan.activity.KISmartRegisterActivity;
import org.smartregister.view.controller.ANMController;
import org.smartregister.view.controller.NavigationController;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by sid-tech on 11/7/17.
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

    @Override
    public void startECSmartRegistry() {
        activity.startActivity(new Intent(activity, KISmartRegisterActivity.class));
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this.activity);

        if(sharedPreferences.getBoolean("firstlauch",true)) {
            sharedPreferences.edit().putBoolean("firstlauch",false).commit();
        }
    }

    @Override
    public void startFPSmartRegistry() {
        activity.startActivity(new Intent(activity, KBSmartRegisterActivity.class));
    }

    @Override
    public void startANCSmartRegistry() {
        activity.startActivity(new Intent(activity, KIANCSmartRegisterActivity.class));
    }

    @Override
    public void startPNCSmartRegistry() {
        activity.startActivity(new Intent(activity, KIPNCSmartRegisterActivity.class));
    }

    @Override
    public void startChildSmartRegistry() {
        activity.startActivity(new Intent(activity, KIAnakSmartRegisterActivity.class));
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

}