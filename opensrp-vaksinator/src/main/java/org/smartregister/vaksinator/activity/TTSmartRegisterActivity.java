package org.smartregister.vaksinator.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.FormUtils;
import org.smartregister.vaksinator.R;
import org.smartregister.vaksinator.fragment.TTSmartRegisterFragment;
import org.smartregister.vaksinator.pageradapter.BaseRegisterActivityPagerAdapter;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

//import org.smartregister.vaksinator.vaksinator.FlurryFacade;
//import org.smartregister.test.fragment.HouseHoldSmartRegisterFragment;

public class TTSmartRegisterActivity extends SecuredNativeSmartRegisterActivity implements
        LocationSelectorDialogFragment.OnLocationSelectedListener{
//    private SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");

    public static final String TAG = "TT REGISTER";
    @Bind(R.id.view_pager) public OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;

    private String[] formNames = new String[]{};


    private TTSmartRegisterFragment nf = new TTSmartRegisterFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
/*
        String ttStart = timer.format(new Date());
        Map<String, String> ttRegister = new HashMap<String, String>();
        ttRegister.put("start", ttStart);
        FlurryAgent.logEvent("TT_Register_dashboard", ttRegister, true);*/

        // FlurryFacade.logEvent("anc_dashboard");
        formNames = this.buildFormNameList();

        //        WD
//        Bundle extras = getIntent().getExtras();
//        if (extras != null){
//            boolean mode_face = extras.getBoolean("org.smartregister.indonesia.face.face_mode");
//            String base_id = extras.getString("org.smartregister.indonesia.face.base_id");
////            double proc_time = extras.getDouble("org.smartregister.indonesia.face.proc_time");
////            Log.e(TAG, "onCreate: "+proc_time );
//
//            if (mode_face){
//                nf.setCriteria(base_id);
//                mBaseFragment = new TTSmartRegisterFragment();
//
//                Log.e(TAG, "onCreate: id " + base_id);
////                showToast("id "+base_id);
//                AlertDialog.Builder builder= new AlertDialog.Builder(this);
//                builder.setTitle("Is it Right Person ?");
////                builder.setTitle("Is it Right Clients ?" + base_id);
////                builder.setTitle("Is it Right Clients ?"+ pc.getName());
//
//                // TODO : get name by base_id
////                builder.setMessage("Process Time : " + proc_time + " s");
//
//                builder.setNegativeButton("CANCEL", listener);
//                builder.setPositiveButton("YES", listener);
//                builder.show();
//            }
//        } else {
        Fragment mBaseFragment = new TTSmartRegisterFragment();
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

//        ZiggyService ziggyService = context().ziggyService();
    }

    public void onPageChanged(int page){
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LoginActivity.setLanguage();
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {return null;}

    @Override
    protected void setupViews() {
        // do nothing
    }

    @Override
    protected void onResumption(){
        // odo nothing
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {return null;}

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {return null;}

    @Override
    protected void onInitialization() {
        // do nothing
    }

    @Override
    public void startRegistration() {
        // do nothing
    }

//    public DialogOption[] getEditOptions() {
//        return new DialogOption[]{
//                // do nothing
////                new OpenFormOption("Bayi Immunisasi", "kohort_bayi_immunization", formController),
////
////                new OpenFormOption("Tutup Bayi", "kohort_anak_tutup", formController),
//
//        };
//    }

    /*@Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try{
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            ziggyService.saveForm(getParams(submission), submission.instance());

            context.formSubmissionService().updateFTSsearch(submission);

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
    }*/
    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try{
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);
//            ziggyService.saveForm(getParams(submission), submission.instance());
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
    }

    @Override
    public void OnLocationSelected(String locationJSONString) {
        JSONObject combined = null;

        try {
            JSONObject locationJSON = new JSONObject(locationJSONString);
            //   JSONObject uniqueId = new JSONObject(context.uniqueIdController().getUniqueIdJson());

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
            startFormActivity("registrasi_ibu", null, fieldOverrides.getJSONString());
        }
    }
  /*  public void saveuniqueid() {
        try {
            JSONObject uniqueId = new JSONObject(context.uniqueIdController().getUniqueIdJson());
            String uniq = uniqueId.getString("unique_id");
            context.uniqueIdController().updateCurrentUniqueId(uniq);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
//        FlurryFacade.logEvent(formName);
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


    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return  (DisplayFormFragment)findFragmentByPosition(index);
    }

    @Override
    public void onBackPressed() {
        nf.setCriteria("!");
        if (currentPage != 0) {
            switchToBaseFragment(null);
        } else if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    private String[] buildFormNameList(){
        List<String> formNames = new ArrayList<>();
        formNames.add("registrasi_ibu");
        formNames.add("kohort_anak_tutup");
        formNames.add("kohort_bayi_immunization");
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
   /*     String ttEnd = timer.format(new Date());
        Map<String, String> ttRegister = new HashMap<String, String>();
        ttRegister.put("end", ttEnd);
        FlurryAgent.logEvent("TT_Register_dashboard", ttRegister, true);*/
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

//    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//
//            if (which == -1 ){
//                nf.setCriteria("!");
//                currentPage = 0;
//                Log.e(TAG, "onClick: YES " + currentPage);
//                FlurryAgent.logEvent(TAG + "search_by_face OK", true);
//
//            } else {
//                nf.setCriteria("!");
//                Log.e(TAG, "onClick: NO "+currentPage);
//                FlurryAgent.logEvent(TAG + "search_by_face NOK", true);
//
//                onBackPressed();
//
//                Intent intent= new Intent(TTSmartRegisterActivity.this, TTSmartRegisterActivity.class);
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
//            }
//
//
//        }
//    };
}
