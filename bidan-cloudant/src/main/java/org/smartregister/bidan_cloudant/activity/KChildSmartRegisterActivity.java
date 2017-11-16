package org.smartregister.bidan_cloudant.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.flurry.android.FlurryAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.bidan_cloudant.R;
import org.smartregister.bidan_cloudant.fragment.ChildSmartRegisterFragment;
import org.smartregister.bidan_cloudant.pageradapter.BaseRegisterActivityPagerAdapter;
import org.smartregister.bidan_cloudant.service.SaveService;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.sync.ClientProcessor;
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
import util.BidanFormUtils;

import static org.smartregister.bidan_cloudant.utils.AllConstantsINA.FormNames.KOHORT_CHILD_REGISTRATION;
import static org.smartregister.bidan_cloudant.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_CLOSE;
import static org.smartregister.bidan_cloudant.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_RENCANA_PERSALINAN;
import static org.smartregister.bidan_cloudant.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT;
import static org.smartregister.bidan_cloudant.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT_INTEGRASI;
import static org.smartregister.bidan_cloudant.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT_LABTEST;
import static org.smartregister.bidan_cloudant.utils.AllConstantsINA.FormNames.KOHORT_PNC_REGISTRATION;

public class KChildSmartRegisterActivity extends SecuredNativeSmartRegisterActivity implements
        LocationSelectorDialogFragment.OnLocationSelectedListener, DisplayFormListener{

    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");

    public static final String TAG = KChildSmartRegisterActivity.class.getSimpleName();
    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;
    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;

    SaveService saveService;

    ChildSmartRegisterFragment nf = new ChildSmartRegisterFragment();

    Map<String, String> FS = new HashMap<>();

    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        String VaksinatorStart = timer.format(new Date());
        Map<String, String> Vaksinator = new HashMap<>();
        Vaksinator.put("start", VaksinatorStart);

        formNames = this.buildFormNameList();

        extras = getIntent().getExtras();

        if (extras != null) {
            boolean mode_face = extras.getBoolean("org.ei.opensrp.indonesia.face.face_mode");
            String base_id = extras.getString("org.ei.opensrp.indonesia.face.base_id");
            double proc_time = extras.getDouble("org.ei.opensrp.indonesia.face.proc_time");

            if (mode_face) {
                nf.setCriteria(base_id);
                mBaseFragment = new ChildSmartRegisterFragment();

                Log.e(TAG, "onCreate: id " + base_id);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Is it Right Person ?");

                // TODO : get name by base_id
                builder.setNegativeButton("CANCEL", listener );
                builder.setPositiveButton("YES", listener );
                builder.show();
            }

        } else {
            mBaseFragment = new ChildSmartRegisterFragment();
        }

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

       /* if(LoginActivity.generator.uniqueIdController().needToRefillUniqueId(LoginActivity.generator.UNIQUE_ID_LIMIT)) {
            String toastMessage =  String.format("need to refill unique id, its only %s remaining",
                                   LoginActivity.generator.uniqueIdController().countRemainingUniqueId());
            Toast.makeText(context().applicationContext(), toastMessage, Toast.LENGTH_LONG).show();
        }*/
        ziggyService = context().ziggyService();
        saveService = new SaveService(context().ziggyFileLoader(), context().formDataRepository(),
                context().formSubmissionRouter());
    }
    public void onPageChanged(int page){
        setRequestedOrientation(page == 0
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
                new OpenFormOption("Kunjungan Per Bulan ", "kohort_bayi_immunization", formController),
                new OpenFormOption("Close Form","close_form",formController)
            };
    }


    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try{
            BidanFormUtils formUtils = BidanFormUtils.getInstance(getApplicationContext());
          //  FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);
            saveService.saveForm(getParams(submission), submission.instance());
            ClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

            //end capture flurry log for FS
            String end = timer.format(new Date());
            Map<String, String> FS = new HashMap<String, String>();
            FS.put("end", end);
            FlurryAgent.logEvent(formName, FS, true);
        }catch (Exception e){
            // TODO: show error dialog on the formfragment if the submission fails
            DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(currentPage);
            if (displayFormFragment != null) {
                displayFormFragment.hideTranslucentProgressDialog();
            }
            e.printStackTrace();
        }

       

    }

    public void activatingOtherForm(final String formName, final String entityId, final String metaData) {
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
                activatingForm(formName, entityId, metaData);
            }
        });
    }

        @Override
    public void OnLocationSelected(String locationJSONString) {
        JSONObject combined = null;

        try {
            JSONObject locationJSON = new JSONObject(locationJSONString);
           // JSONObject uniqueId = new JSONObject(LoginActivity.generator.uniqueIdController().getUniqueIdJson());

            combined = locationJSON;
          //  Iterator<String> iter = uniqueId.keys();

           /* while (iter.hasNext()) {
                String key = iter.next();
                combined.put(key, uniqueId.get(key));
            }*/

            System.out.println("injection string: " + combined.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (combined != null) {
            FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());
            startFormActivity(KOHORT_CHILD_REGISTRATION, null, fieldOverrides.getJSONString());
        }
    }

   /* public void saveuniqueid() {
        try {
            JSONObject uniqueId = new JSONObject(LoginActivity.generator.uniqueIdController().getUniqueIdJson());
            String uniq = uniqueId.getString("unique_id");
            LoginActivity.generator.uniqueIdController().updateCurrentUniqueId(uniq);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public static boolean out;

    @Override
    public void startFormActivity(final String formName, final String entityId, final String metaData) {
       /* if(Support.ONSYNC) {
            Toast.makeText(this, "Data still Synchronizing, please wait", Toast.LENGTH_SHORT).show();
            return;
        }*/

        String start = timer.format(new Date());
        Map<String, String> FS = new HashMap<String, String>();
        FS.put("start", start);
        FlurryAgent.logEvent(formName, FS, true);

            activatingForm(formName,entityId,metaData);

    }

    private void activatingForm(String formName, String entityId, String metaData){
        try {
            int formIndex = BidanFormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
            if (entityId != null || metaData != null){
                String data = null;
                //check if there is previously saved data for the form
                data = getPreviouslySavedDataForForm(formName, metaData, entityId);
                if (data == null){
                    data = BidanFormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
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

    /**
     * Get 3 children name, 1 determined and 2 random. the determined one will be generated based on
     * @entityId and stored to index @choice of char sequence array.
     * @param choice
     * @param entityId
     * @return
     */
    private CharSequence[] selections(int choice, String entityId){
        String name = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak").findByCaseID(entityId).getColumnmaps().get("namaBayi");
        System.out.println("start form activity / nama = " + name);
        CharSequence selections[] = new CharSequence[]{name, name, name};

        selections[choice] = (CharSequence) name;

        String query = "SELECT namaBayi FROM ec_anak where ec_anak.is_closed = 0";
        Cursor cursor = context().commonrepository("ec_anak").rawCustomQueryForAdapter(query);
        cursor.moveToFirst();

        for (int i = 0; i < selections.length; i++) {
            if (i != choice) {
                cursor.move(new java.util.Random().nextInt(cursor.getCount()));
                String temp = cursor.getString(cursor.getColumnIndex("namaBayi"));
                if(temp==null)
                    i--;
                else if (temp.equals(name))
                    i--;
                else
                    selections[i] = (CharSequence) temp;
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
        Log.e(TAG, "onBackPressed: "+currentPage );

        if (currentPage != 0) {
            switchToBaseFragment(null);
        } else {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    private String[] buildFormNameList(){
        List<String> formNames = new ArrayList<String>();
        formNames.add(KARTU_IBU_ANC_VISIT);
        formNames.add(KARTU_IBU_ANC_VISIT_INTEGRASI);
        formNames.add(KARTU_IBU_ANC_VISIT_LABTEST);
        formNames.add(KARTU_IBU_ANC_RENCANA_PERSALINAN);
        formNames.add(KOHORT_PNC_REGISTRATION);
        formNames.add(KARTU_IBU_ANC_CLOSE);
        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        retrieveAndSaveUnsubmittedFormData();
        String VaksinatorEnd = timer.format(new Date());
        Map<String, String> Vaksinator = new HashMap<String, String>();
        Vaksinator.put("end", VaksinatorEnd);
        FlurryAgent.logEvent("Vaksinator_dashboard",Vaksinator, true );
    }

    public void retrieveAndSaveUnsubmittedFormData(){
        if (currentActivityIsShowingForm()){
            DisplayFormFragment formFragment = getDisplayFormFragmentAtIndex(currentPage);
            formFragment.saveCurrentFormData();
        }
    }

    private int getNumOfChild(){
        Cursor childcountcursor = context().commonrepository("ec_anak").rawCustomQueryForAdapter(new SmartRegisterQueryBuilder().queryForCountOnRegisters("ec_anak_search", "ec_anak_search.is_closed=0"));
        childcountcursor.moveToFirst();
        int childcount= childcountcursor.getInt(0);
        childcountcursor.close();
        return childcount;
    }

    private boolean currentActivityIsShowingForm(){
        return currentPage != 0;
    }

    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String face_end = timer.format(new Date());
            FS.put("face_end", face_end);

            Log.e(TAG, "onClick: which "+ which );
            nf.setCriteria("!");

            if (which == -1 ){
                currentPage = 0;
                Log.e(TAG, "onClick: YES ");
                FlurryAgent.logEvent(TAG+"search_by_face OK", true);

            } else {
                Log.e(TAG, "onClick: NO "+currentPage);
                FlurryAgent.logEvent(TAG+"search_by_face NOK", true);

                onBackPressed();

                Intent intent= new Intent(KChildSmartRegisterActivity.this, KChildSmartRegisterActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }


        }
    };


}
