package org.smartregister.vaksinator.vaksinator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.json.XML;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.service.ZiggyService;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.FormUtils;
import org.smartregister.vaksinator.activity.LoginActivity;
import org.smartregister.vaksinator.R;
import org.smartregister.vaksinator.fragment.VaksinatorSmartRegisterFragment;
import org.smartregister.vaksinator.pageradapter.BaseRegisterActivityPagerAdapter;
import org.smartregister.vaksinator.service.SaveService;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.dialog.OpenFormOption;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;
import org.smartregister.view.viewpager.OpenSRPViewPager;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.formula.Support;
import util.VaksinatorFormUtils;

//import org.smartregister.test.fragment.HouseHoldSmartRegisterFragment;

public class VaksinatorSmartRegisterActivity extends SecuredNativeSmartRegisterActivity implements
        LocationSelectorDialogFragment.OnLocationSelectedListener, DisplayFormListener {
    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");

    public static final String TAG = "Vaksinator";
    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;

    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;


    ZiggyService ziggyService;
    SaveService saveService;

    VaksinatorSmartRegisterFragment nf = new VaksinatorSmartRegisterFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        String VakStart = timer.format(new Date());
        Map<String, String> Vaksinator = new HashMap<String, String>();
        Vaksinator.put("start", VakStart);
        FlurryAgent.logEvent("vaksinator_dashboard", Vaksinator, true);

       // FlurryFacade.logEvent("anc_dashboard");
        formNames = this.buildFormNameList();

        //        WD
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            boolean mode_face = extras.getBoolean("org.smartregister.indonesia.face.face_mode");
            String base_id = extras.getString("org.smartregister.indonesia.face.base_id");
            double proc_time = extras.getDouble("org.smartregister.indonesia.face.proc_time");
//            Log.e(TAG, "onCreate: "+proc_time );

            if (mode_face){
                nf.setCriteria(base_id);
                mBaseFragment = new VaksinatorSmartRegisterFragment();

                Log.e(TAG, "onCreate: id " + base_id);
//                showToast("id "+base_id);
                AlertDialog.Builder builder= new AlertDialog.Builder(this);
                builder.setTitle("Is it Right Person ?");
//                builder.setTitle("Is it Right Clients ?" + base_id);
//                builder.setTitle("Is it Right Clients ?"+ pc.getName());

                // TODO : get name by base_id
//                builder.setMessage("Process Time : " + proc_time + " s");

                builder.setNegativeButton("CANCEL", listener);
                builder.setPositiveButton("YES", listener);
                builder.show();
            }
        } else {
            mBaseFragment = new VaksinatorSmartRegisterFragment();
        }


        mBaseFragment = new VaksinatorSmartRegisterFragment();

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

//        if(LoginActivity.generator.uniqueIdController().needToRefillUniqueId(LoginActivity.generator.UNIQUE_ID_LIMIT)) {
//            String toastMessage = "need to refill unique id, its only "+
//                                  LoginActivity.generator.uniqueIdController().countRemainingUniqueId()+
//                                  " remaining";
//            Toast.makeText(context().applicationContext(), toastMessage,Toast.LENGTH_LONG).show();
//        }

        ziggyService = context().ziggyService();
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
    protected void setupViews() {}

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
                new OpenFormOption(context().getStringResource(R.string.str_child_immunizations), "kohort_bayi_immunization", formController),
                new OpenFormOption(context().getStringResource(R.string.str_child_close), "close_form", formController)

        };
    }

    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        Log.d(TAG, "saveFormSubmission: saving form");
        try{
            VaksinatorFormUtils formUtils = VaksinatorFormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);
            saveService.saveForm(getParams(submission), submission.instance());
            ClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);
            //switch to forms list fragment

//            if(formName.equals("registrasi_jurim")){
//                saveuniqueid();
//            }

            if(formName=="registrasi_jurim"){
                Log.d(TAG, "saveFormSubmission: it was registrasi_jurim form");
                fieldOverrides.put("ibuCaseId",submission.entityId());
                FieldOverrides fo = new FieldOverrides(fieldOverrides.toString());
                activatingOtherForm("registrasi_anak", null, fo.getJSONString());
            }else{
                switchToBaseFragment(formSubmission); // Unnecessary!! passing on data
            }

        }catch (Exception e){
            // TODO: show error dialog on the formfragment if the submission fails
            Log.d(TAG, "saveFormSubmission: error saving form");
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
        FlurryAgent.logEvent(formName, FS, true);
    }

    @Override
    public void OnLocationSelected(String locationJSONString) {
        JSONObject combined = null;

        try {
            JSONObject locationJSON = new JSONObject(locationJSONString);
//            JSONObject uniqueId = new JSONObject(LoginActivity.generator.uniqueIdController().getUniqueIdJson());

            combined = locationJSON;
//            Iterator<String> iter = uniqueId.keys();

//            while (iter.hasNext()) {
//                String key = iter.next();
//                combined.put(key, uniqueId.get(key));
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (combined != null) {
            FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());
            startFormActivity("registrasi_jurim", null, fieldOverrides.getJSONString());
        }
    }
    public void saveuniqueid() {
        try {
            JSONObject uniqueId = new JSONObject(LoginActivity.generator.uniqueIdController().getUniqueIdJson());
            String uniq = uniqueId.getString("unique_id");
            LoginActivity.generator.uniqueIdController().updateCurrentUniqueId(uniq);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startFormActivity(final String formName, final String entityId, final String metaData) {
        if(Support.ONSYNC) {
            Toast.makeText(this,"Data still Synchronizing, please wait",Toast.LENGTH_SHORT).show();
            return;
        }

        String start = timer.format(new Date());
        Map<String, String> FS = new HashMap<String, String>();
        FS.put("start", start);
        FlurryAgent.logEvent(formName, FS, true);

        if(formName.equals("kohort_bayi_immunization")) {
            if(numOfRecord()<4)
                activatingForm(formName,entityId,metaData);
            else {
                final int choice = new java.util.Random().nextInt(3);
                CharSequence[] selections = selections(choice, entityId);

                final AlertDialog.Builder builder = new AlertDialog.Builder(VaksinatorSmartRegisterActivity.this);
                builder.setTitle(context().getStringResource(R.string.reconfirmChildName));
                builder.setItems(selections, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        if (which == choice) {
                            activatingForm(formName, entityId, metaData);
                        }
                    }
                });
                builder.show();
            }
        }
        else{
            activatingForm(formName,entityId,metaData);
        }

    }

    private void activatingForm(String formName, String entityId, String metaData){
        try {
            int formIndex = VaksinatorFormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
            if (entityId != null || metaData != null){
                String data = null;
                //check if there is previously saved data for the form
                data = getPreviouslySavedDataForForm(formName, metaData, entityId);
                if (data == null){
                    data = VaksinatorFormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                }

                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.setFormData(data);
                    displayFormFragment.setRecordId(entityId);
                    displayFormFragment.setListener(this);
                    displayFormFragment.setFieldOverides(metaData);
                }
            }

            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private CharSequence[] selections(int choice, String entityId){
        String name = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak").findByCaseID(entityId).getColumnmaps().get("namaBayi");
        CharSequence selections[] = new CharSequence[]{name, name, name};

        selections[choice] = (CharSequence) name;

        String query = "SELECT namaBayi FROM ec_anak where ec_anak.is_closed = 0";
        Cursor cursor = context().commonrepository("ec_anak").rawCustomQueryForAdapter(query);
        cursor.moveToFirst();

        System.out.println(cursor.getCount());

        for (int i = 0; i < selections.length; i++) {
            if (i != choice) {
                cursor.move(new java.util.Random().nextInt(cursor.getCount()));
                String temp = cursor.getString(cursor.getColumnIndex("namaBayi"));
                System.out.println("start form activity / temp = " + temp);
                if(temp==null)
                    i--;
                else if (temp.equals(name)) {
                    System.out.println("equals");
                    i--;
                } else {
                    selections[i] = (CharSequence) temp;
                    System.out.println("char sequence of temp = " + selections[i]);
                }
                cursor.moveToFirst();
            }
        }
        cursor.close();

        return selections;
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

    public void activatingOtherForm(final String formName, final String entityId, final String metaData){
        final int prevPageIndex = currentPage;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //hack reset the form
                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.hideTranslucentProgressDialog();
                    displayFormFragment.setFormData(null);

                }

                displayFormFragment.setRecordId(null);
                activatingForm(formName,entityId,metaData);
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
        nf.setCriteria("!");
        if (currentPage != 0) {
            switchToBaseFragment(null);
        } else if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    private String[] buildFormNameList(){
        List<String> formNames = new ArrayList<String>();
        formNames.add("registrasi_jurim");
        formNames.add("registrasi_anak");
       formNames.add("close_form");
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
        String VakEnd = timer.format(new Date());
        Map<String, String> Vaksinator = new HashMap<String, String>();
        Vaksinator.put("end", VakEnd);
        FlurryAgent.logEvent("vaksinator_dashboard", Vaksinator, true);
    }

    public void retrieveAndSaveUnsubmittedFormData(){
        if (currentActivityIsShowingForm()){
            DisplayFormFragment formFragment = getDisplayFormFragmentAtIndex(currentPage);
            formFragment.saveCurrentFormData();
        }
    }

    private int numOfRecord(){
        Cursor childcountcursor = context().commonrepository("ec_anak").rawCustomQueryForAdapter(new SmartRegisterQueryBuilder().queryForCountOnRegisters("ec_anak_search", "ec_anak_search.is_closed=0"));
        childcountcursor.moveToFirst();
        int childcount = childcountcursor.getInt(0);
        childcountcursor.close();
        return childcount;
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
                Log.e(TAG, "onClick: YES "+currentPage);
                FlurryAgent.logEvent(TAG + "search_by_face OK", true);

            } else {
                nf.setCriteria("!");
                Log.e(TAG, "onClick: NO "+currentPage);
                FlurryAgent.logEvent(TAG + "search_by_face NOK", true);

                onBackPressed();

                Intent intent= new Intent(VaksinatorSmartRegisterActivity.this, VaksinatorSmartRegisterActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }


        }
    };

}
