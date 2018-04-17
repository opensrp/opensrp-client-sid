package org.smartregister.gizi.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.flurry.android.FlurryAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.gizi.R;
import org.smartregister.gizi.fragment.GiziIbuSmartRegisterFragment;
import org.smartregister.gizi.pageradapter.BaseRegisterActivityPagerAdapter;
import org.smartregister.gizi.utils.KmsHandler;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.service.ZiggyService;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.dialog.OpenFormOption;
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

//import org.smartregister.gizi.fragment.HouseHoldSmartRegisterFragment;

public class IbuSmartRegisterActivity extends SecuredNativeSmartRegisterActivity implements
        LocationSelectorDialogFragment.OnLocationSelectedListener{

    private SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");

    public static final String TAG = "GiziIbuActivity";
    @Bind(R.id.view_pager) private OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;
    private ClientProcessor clientProcessor;
    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;

    private ZiggyService ziggyService;

    private GiziIbuSmartRegisterFragment nf = new GiziIbuSmartRegisterFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        String GiziStart = timer.format(new Date());
                Map<String, String> Gizi = new HashMap<String, String>();
                Gizi.put("start", GiziStart);
//                FlurryAgent.logEvent("Gizi_dashboard",Gizi, true );
       // FlurryFacade.logEvent("Gizi_dashboard");

        formNames = this.buildFormNameList();

        //        WD
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            boolean mode_face = extras.getBoolean("org.smartregister.indonesia.face.face_mode");
//            String base_id = extras.getString("org.smartregister.indonesia.face.base_id");
//            double proc_time = extras.getDouble("org.smartregister.indonesia.face.proc_time");
////            Log.e(TAG, "onCreate: "+proc_time );
//
//            if (mode_face) {
//                nf.setCriteria(base_id);
//                mBaseFragment = new GiziIbuSmartRegisterFragment();
//
//                Log.e(TAG, "onCreate: id " + base_id);
////                showToast("id "+base_id);
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Is it Right Person ?");
////                builder.setTitle("Is it Right Clients ?" + base_id);
////                builder.setTitle("Is it Right Clients ?"+ pc.getName());
//
//                // TODO : get name by base_id
////                builder.setMessage("Process Time : " + proc_time + " s");
//
//                builder.setNegativeButton("CANCEL", listener );
//                builder.setPositiveButton("YES", listener );
//                builder.show();
//            }
//        } else {
            mBaseFragment = new GiziIbuSmartRegisterFragment();
//        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment);
        mPager.setOffscreenPageLimit(formNames.length);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }
        });

        ziggyService = context().ziggyService();
    }
    public void onPageChanged(int page){
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LoginActivity.setLanguage();
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {return null;}

//    @Override
//    protected void setupViews() {
//        // do nothing
//    }

//    @Override
//    protected void onResumption(){
//        // do nothing
//    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {return null;}

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {return null;}

    @Override
    protected void onInitialization() {
        context().formSubmissionRouter().getHandlerMap().put("kunjungan_gizi", new KmsHandler());
    }

    @Override
    public void startRegistration() {
        // do nothing
    }

    public DialogOption[] getEditOptions() {
            return new DialogOption[]{
                new OpenFormOption("Kunjungan Per Bulan ", "kunjungan_gizi", formController),
                new OpenFormOption("Edit Registrasi Gizi ", "edit_registrasi_gizi", formController),
                new OpenFormOption("Close Form","close_form",formController)
            };
    }


    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try{
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);
            ziggyService.saveForm(getParams(submission), submission.instance());
            ClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);
            //switch to forms list fragment
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

        }catch (Exception e){
            // TODO: show error dialog on the formfragment if the submission fails
            DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(currentPage);
            if (displayFormFragment != null) {
                displayFormFragment.hideTranslucentProgressDialog();
            }
            e.printStackTrace();
        }
      //  KMSCalculation();
    }

    /*@Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try{
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            ziggyService.saveForm(getParams(submission), submission.instance());

            //switch to forms list fragment
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

        }catch (Exception e){
            // TODO: show error dialog on the formfragment if the submission fails
            DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(currentPage);
            if (displayFormFragment != null) {
                displayFormFragment.hideTranslucentProgressDialog();
            }
            e.printStackTrace();
        }
      *//*  if(formName.equals("registrasi_gizi")) {
            saveuniqueid();
        }*//*
        //end capture flurry log for FS
                        String end = timer.format(new Date());
                        Map<String, String> FS = new HashMap<String, String>();
                        FS.put("end", end);
                        FlurryAgent.logEvent(formName,FS, true);

    }
*/
    @Override
    public void OnLocationSelected(String locationJSONString) {
        JSONObject combined = null;

        try {
            JSONObject locationJSON = new JSONObject(locationJSONString);
            //   JSONObject uniqueId = new JSONObject(context().uniqueIdController().getUniqueIdJson());

            combined = locationJSON;
            //     Iterator<String> iter = uniqueId.keys();

       /*     while (iter.hasNext()) {
                String key = iter.next();
                combined.put(key, uniqueId.get(key));
            }
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (combined != null) {
            FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());
            startFormActivity("registrasi_gizi", null, fieldOverrides.getJSONString());
        }
    }

   /* public void saveuniqueid() {
        try {
            JSONObject uniqueId = new JSONObject(context().uniqueIdController().getUniqueIdJson());
            String uniq = uniqueId.getString("unique_id");
            context().uniqueIdController().updateCurrentUniqueId(uniq);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
       // FlurryFacade.logEvent(formName);
//        Log.v("fieldoverride", metaData);
        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
            if (entityId != null || metaData != null){
                String data = null;
                //check if there is previously saved data for the form
                data = getPreviouslySavedDataForForm(formName, metaData, entityId);
                if (data == null){
                    data = FormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                }

                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.setFormData(data);
                    displayFormFragment.setRecordId(entityId);
                    displayFormFragment.setFieldOverides(metaData);
                }
            }

            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /*@Override
    public void startFormActivity(String formName, String entityId, String metaData) {
       // Log.v("fieldoverride", metaData);
        String start = timer.format(new Date());
                        Map<String, String> FS = new HashMap<String, String>();
                        FS.put("start", start);
                        FlurryAgent.logEvent(formName,FS, true );
        //FlurryFacade.logEvent(formName);
        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
            if (entityId != null || metaData != null){
                String data = null;
                //check if there is previously saved data for the form
                data = getPreviouslySavedDataForForm(formName, metaData, entityId);
                if (data == null){
                    data = FormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                }

                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.setFormData(data);
                    displayFormFragment.loadFormData();
                    displayFormFragment.setRecordId(entityId);
                    displayFormFragment.setFieldOverides(metaData);
                }
            }

            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears

        }catch (Exception e){
            e.printStackTrace();
        }

    }*/

    public void switchToBaseFragment(final String data){
        final int prevPageIndex = currentPage;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(0, false);
                SecuredNativeSmartRegisterFragment registerFragment = (SecuredNativeSmartRegisterFragment) findFragmentByPosition(0);
                if (registerFragment != null && data != null) {
                    registerFragment.refreshListView();
                }

                //hack reset the form
                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.hideTranslucentProgressDialog();
                    displayFormFragment.setFormData(null);

                }

                displayFormFragment.setRecordId(null);
            }
        });

    }

    public android.support.v4.app.Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return  (DisplayFormFragment)findFragmentByPosition(index);
    }

    @Override
    public void onBackPressed() {
        nf.setCriteria("");
        if (currentPage != 0) {
            switchToBaseFragment(null);
        } else if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    private String[] buildFormNameList(){
        List<String> formNames = new ArrayList<String>();
       formNames.add("registrasi_gizi");
        formNames.add("kunjungan_gizi");
        formNames.add("edit_registrasi_gizi");
        formNames.add("close_form");
       

     //   formNames.add("census_enrollment_form");
//        DialogOption[] options = getEditOptions();
//        for (int i = 0; i < options.length; i++){
//            formNames.add(((OpenFormOption) options[i]).getFormName());
//        }
        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        retrieveAndSaveUnsubmittedFormData();
  /*      String GiziEnd = timer.format(new Date());
        Map<String, String> Gizi = new HashMap<String, String>();
        Gizi.put("end", GiziEnd);
        FlurryAgent.logEvent("Gizi_dashboard",Gizi, true );*/
    }

    public void retrieveAndSaveUnsubmittedFormData(){
        if (currentActivityIsShowingForm()){
            DisplayFormFragment formFragment = getDisplayFormFragmentAtIndex(currentPage);
            formFragment.saveCurrentFormData();
        }
    }

    private boolean currentActivityIsShowingForm(){
        return currentPage != 0;
    }

    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if (which == -1 ){
                nf.setCriteria("!");
                currentPage = 0;
                Log.e(TAG, "onClick: YES " + currentPage);
                FlurryAgent.logEvent(TAG+"search_by_face OK", true);

            } else {
                nf.setCriteria("");
                onBackPressed();
                Log.e(TAG, "onClick: NO " + currentPage);
                FlurryAgent.logEvent(TAG + "search_by_face NOK", true);

                Intent intent= new Intent(IbuSmartRegisterActivity.this, IbuSmartRegisterActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }


        }
    };

}
