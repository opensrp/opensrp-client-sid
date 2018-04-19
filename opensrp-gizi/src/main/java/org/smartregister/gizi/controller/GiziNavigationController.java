package org.smartregister.gizi.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONObject;
import org.smartregister.gizi.R;
import org.smartregister.gizi.activity.GiziSmartRegisterActivity;
import org.smartregister.gizi.activity.IbuSmartRegisterActivity;
import org.smartregister.view.controller.ANMController;

//import org.ei.opensrp.gizi.test.TestSmartRegisterActivity;

public class GiziNavigationController extends org.smartregister.view.controller.NavigationController {
    private Activity activity;
    private ANMController anmController;
    private org.smartregister.Context context;

    public GiziNavigationController(Activity activity, ANMController anmController) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
    }

    public GiziNavigationController(Activity activity, ANMController anmController, org.smartregister.Context context) {
        this(activity,anmController);
        this.context=context;
    }

    @Override
    public void startECSmartRegistry() {
        activity.startActivity(new Intent(activity, IbuSmartRegisterActivity.class));

    }
    @Override
    public void startFPSmartRegistry() {
     //   activity.startActivity(new Intent(activity, ElcoSmartRegisterActivity.class));
    }
    @Override
    public void startANCSmartRegistry() {
   //     activity.startActivity(new Intent(activity, IbuSmartRegisterActivity.class));
    }

    @Override
    public void startChildSmartRegistry() {
         activity.startActivity(new Intent(activity, GiziSmartRegisterActivity.class));
    }

    @Override
    public void startReports() {
        String id;
        String pass;
        try{
            id = new JSONObject(anmController.get()).get("anmName").toString();
            pass = context.allSettings().fetchANMPassword();
        }catch(org.json.JSONException ex){
            id="noname";
            pass="null";
        }
        String uri = "http://"+id+":"+pass+"@"+activity.getApplicationContext().getString(R.string.dho_site).replace("http://","");
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

}
