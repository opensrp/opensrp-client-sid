package org.smartregister.vaksinator.activity;

import org.smartregister.vaksinator.R;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.fragment.DisplayFormFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.flurry.android.FlurryAgent.logEvent;

/**
 * Created by sid on 10/15/17.
 */

public class VaksinatorHomeActivity extends SecuredActivity {

    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");

    @Override
    protected void onCreation() {
        // Get Starting Time
        String homeStart = timer.format(new Date());
        Map<String, String> home = new HashMap<>();
        home.put("start", homeStart);

        //logEvent("home_dashboard", home, true);

        setContentView(R.layout.smart_registers_jurim_home);

//        navigationController = new NavigationControllerINA(this, anmController, context());
//        setupViews();
//        initialize();

        DisplayFormFragment.formInputErrorMessage = getResources().getString(R.string.forminputerror);
        DisplayFormFragment.okMessage = getResources().getString(R.string.okforminputerror);

    }

    @Override
    protected void onResumption() {

    }
}
