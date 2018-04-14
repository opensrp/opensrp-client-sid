package org.smartregister.bidan.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.bidan.activity.ANCSmartRegisterActivity;
import org.smartregister.bidan.activity.AnakSmartRegisterActivity;
import org.smartregister.bidan.activity.FPSmartRegisterActivity;
import org.smartregister.bidan.activity.KISmartRegisterActivity;
import org.smartregister.bidan.activity.PNCSmartRegisterActivity;
import org.smartregister.view.controller.ANMController;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class NavigationControllerINA extends org.smartregister.view.controller.NavigationController {
    private Activity activity;
    private ANMController anmController;
    private Context context;

    public NavigationControllerINA(Activity activity, ANMController anmController, Context context) {
        super(activity, anmController);
        this.activity = activity;
        this.anmController = anmController;
        this.context = context;
    }

    @Override
    public void startECSmartRegistry() {
//           activity.startActivity(new Intent(activity, NativeKIbuSmartRegisterActivity.class));
        activity.startActivity(new Intent(activity, KISmartRegisterActivity.class));

        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this.activity);

        if (sharedPreferences.getBoolean("firstlauch", true))
            sharedPreferences.edit().putBoolean("firstlauch", false).apply();

    }

    @Override
    public void startFPSmartRegistry() {
        activity.startActivity(new Intent(activity, FPSmartRegisterActivity.class));
    }

    @Override
    public void startANCSmartRegistry() {
        activity.startActivity(new Intent(activity, ANCSmartRegisterActivity.class));
    }

    @Override
    public void startPNCSmartRegistry() {
        activity.startActivity(new Intent(activity, PNCSmartRegisterActivity.class));
    }

    @Override
    public void startChildSmartRegistry() {
        activity.startActivity(new Intent(activity, AnakSmartRegisterActivity.class));
    }

    @Override
    public void startReports() {
        String id, pass;
        try {
            id = new JSONObject(anmController.get()).get("anmName").toString();
            pass = context.allSettings().fetchANMPassword();

        } catch (org.json.JSONException ex) {
            id = "noname";
            pass = "null";
        }

        String uri = "http://" + id + ":" + pass + "@kia-report.sid-bidan_cloudant.org/login/auth";
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

}
