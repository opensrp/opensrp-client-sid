package org.smartregister.bidan_cloudant.activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.smartregister.bidan_cloudant.BuildConfig;
import org.smartregister.bidan_cloudant.service.SaveService;
import org.smartregister.bidan_cloudant.utils.VaksinatorFormUtils;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.bidan_cloudant.R;
import org.smartregister.bidan_cloudant.fragment.NativeKISmartRegisterFragment;
import org.smartregister.bidan_cloudant.pageradapter.BaseRegisterActivityPagerAdapter;
import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.service.ZiggyService;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.dialog.OpenFormOption;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;;
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;
import org.smartregister.view.viewpager.OpenSRPViewPager;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.bidan_cloudant.AllConstantsINA;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.formula.Support;

/**
 * Created by Dimas Ciputra on 2/18/15.
 */
public class NativeKISmartRegisterActivity extends SecuredNativeSmartRegisterActivity
        implements LocationSelectorDialogFragment.OnLocationSelectedListener, DisplayFormListener {
    public static final String TAG = NativeKISmartRegisterActivity.class.getSimpleName();

    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;

    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;

    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");


    // WD need for initialize queries
    NativeKISmartRegisterFragment nf = new NativeKISmartRegisterFragment();

    Map<String, String> FS = new HashMap<>();

    ZiggyService ziggyService;
    SaveService saveService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        String KIStart = timer.format(new Date());
        Map<String, String> KI = new HashMap<>();
        KI.put("start", KIStart);
        FlurryAgent.logEvent("KI_dashboard", KI, true);
        
     //   FlurryFacade.logEvent("kohort_ibu_dashboard");
        formNames = this.buildFormNameList();

//        WD
//        mBaseFragment = new NativeKISmartRegisterFragment(); // Relace by followed
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            boolean mode_face = extras.getBoolean("org.smartregister.bidan_cloudant.face.face_mode");
            String base_id = extras.getString("org.smartregister.bidan_cloudant.face.base_id");
            double proc_time = extras.getDouble("org.smartregister.bidan_cloudant.face.proc_time");

            if (mode_face){
                nf.setCriteria(base_id);
                mBaseFragment = new NativeKISmartRegisterFragment();

                Log.e(TAG, "onCreate: id " + base_id);

                showToast("id "+base_id);
                AlertDialog.Builder builder= new AlertDialog.Builder(this);
                builder.setTitle("Is it Right Person ?");

                // TODO : get name by base_id

                builder.setNegativeButton("CANCEL", listener);
                builder.setPositiveButton("YES", listener);
                builder.show();
            }
        } else {
            mBaseFragment = new NativeKISmartRegisterFragment();
        }

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

//        ziggyService = context().ziggyService();

        saveService = new SaveService(context().ziggyFileLoader(), context().formDataRepository(),
                context().formSubmissionRouter());
    }

    public void onPageChanged(int page){
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LoginActivity.setLanguage();
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {return null;}

    @Override
    protected void setupViews() {
    }

    @Override
    protected void onResumption(){}

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {return null;}

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {return null;}

    @Override
    protected void onInitialization() {}

    @Override
    public void startRegistration() {
    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_register_fp_form), "kohort_kb_pelayanan", formController),
                new OpenFormOption(getString(R.string.str_register_anc_form), "kartu_anc_registration", formController),
                new OpenFormOption(getString(R.string.str_register_child_form), AllConstantsINA.FormNames.ANAK_BAYI_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_close_ki_form), AllConstantsINA.FormNames.KARTU_IBU_CLOSE, formController),
        };


    }

    @Override
    public void OnLocationSelected(String locationJSONString) {
        Log.d(TAG, "OnLocationSelected: "+ locationJSONString );

        if (BuildConfig.SYNC_WAIT){
            if(Support.ONSYNC) {
                Toast.makeText(this,"Data still Synchronizing, please wait",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        JSONObject combined = null;

        try {

            combined = new JSONObject(locationJSONString);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (combined != null) {

            FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());

            startFormActivity(AllConstantsINA.FormNames.KARTU_IBU_REGISTRATION, null, fieldOverrides.getJSONString());
        }
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        //  FlurryFacade.logEvent(formName);
        if (BuildConfig.SYNC_WAIT){
            if(Support.ONSYNC) {
                Toast.makeText(this,"Data still Synchronizing, please wait",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String start = timer.format(new Date());
        Map<String, String> FS = new HashMap<String, String>();
        FS.put("start", start);
        FlurryAgent.logEvent(formName,FS, true );
//        Log.v("fieldoverride", metaData);

        // Call Form here
        try {
            int formIndex = VaksinatorFormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
            if (entityId != null || metaData != null){
                String data;
                //check if there is previously saved data for the form
                data = getPreviouslySavedDataForForm(formName, metaData, entityId);
                if (data == null){
                    data = VaksinatorFormUtils.getInstance(getApplicationContext()).
                            generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                }

                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
                Log.e(TAG, "activatingForm: data="+data);

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

    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        Log.d("fieldoverride", fieldOverrides.toString());
        Log.e("fieldoverride", fieldOverrides.toString());

        // save the form
        try {
            VaksinatorFormUtils formUtils = VaksinatorFormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);
            saveService.saveForm(getParams(submission), submission.instance());
            ClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);

            if(formName.equals("kartu_ibu_registration")){
              //  saveuniqueid();
            }
            //switch to forms list fragment
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

        } catch (Exception e){
            // TODO: show error dialog on the formfragment if the submission fails
            DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(currentPage);
            if (displayFormFragment != null) {
                displayFormFragment.hideTranslucentProgressDialog();
            }
            e.printStackTrace();
        }
        //end capture flurry log for FS
        String end = timer.format(new Date());
        Map<String, String> FS = new HashMap<String, String>();
        FS.put("end", end);
        FlurryAgent.logEvent(formName,FS, true);
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
    protected void onPause() {
        super.onPause();

        retrieveAndSaveUnsubmittedFormData();
        String KIEnd = timer.format(new Date());
        Map<String, String> KI = new HashMap< >();
        KI.put("end", KIEnd);
        FlurryAgent.logEvent("KI_dashboard",KI, true );
    }

    @Override
    public void onBackPressed() {
        nf.setCriteria("");
        Log.e(TAG, "onBackPressed: "+currentPage );

        if (currentPage != 0) {
            switchToBaseFragment(null);
        } else {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    private String[] buildFormNameList(){
        List<String> formNames = new ArrayList<>();
        formNames.add(AllConstantsINA.FormNames.KARTU_IBU_REGISTRATION);
        formNames.add(AllConstantsINA.FormNames.KOHORT_KB_PELAYANAN);
        formNames.add(AllConstantsINA.FormNames.KARTU_IBU_ANC_REGISTRATION);
        formNames.add(AllConstantsINA.FormNames.ANAK_BAYI_REGISTRATION);
        formNames.add(AllConstantsINA.FormNames.KARTU_IBU_CLOSE);

        return formNames.toArray(new String[formNames.size()]);
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
            String face_end = timer.format(new Date());
            FS.put("face_end", face_end);

            if (which == -1 ){
                nf.setCriteria("!");
                currentPage = 0;
                Log.e(TAG, "onClick: YES ");
                FlurryAgent.logEvent(TAG + "search_by_face OK", FS, true);

            } else {
                nf.setCriteria("");
                Log.e(TAG, "onClick: NO ");
                FlurryAgent.logEvent(TAG + "search_by_face NOK", FS, true);
            }

        }
    };
}
