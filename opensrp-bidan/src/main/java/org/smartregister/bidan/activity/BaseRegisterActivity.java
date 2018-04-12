package org.smartregister.bidan.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.adapter.SmartRegisterPaginatedAdapter;
import org.smartregister.bidan.R;
//import org.smartregister.bidan.sync.BidanClientProcessor;
//import org.smartregister.bidan.utils.EnketoFormUtils;
import org.smartregister.bidan.sync.ClientProcessor;
import org.smartregister.bidan.utils.BidanFormUtils;

import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.enketo.adapter.pager.EnketoRegisterPagerAdapter;
import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.util.Log;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.DialogOptionModel;
import org.smartregister.view.dialog.EditOption;
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.smartregister.util.Utils.getValue;

//import org.smartregister.bidan.utils.BidanFormUtils;
//import android.content.res.Configuration;
//import org.smartregister.Context;
//import org.smartregister.repository.AllSharedPreferences;
//import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by SID
 */

public class BaseRegisterActivity extends SecuredNativeSmartRegisterActivity implements DisplayFormListener {

    private final String TAG = BaseRegisterActivity.class.getName();
    protected List<String> formNames;
    @Bind(R.id.view_pager)
    protected OpenSRPViewPager mPager;
    protected int currentPage;
    //    private String[] formNames = new String[]{};
    protected FragmentPagerAdapter mPagerAdapter;
    protected DisplayFormFragment displayFormFragment;
    protected DisplayFormFragment formFragment;
    //    protected SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    private int style = DateFormat.MEDIUM;
    //Also try with style = DateFormat.FULL and DateFormat.SHORT
    private Date date = new Date();
    private DateFormat timer = DateFormat.getDateInstance(style, Locale.US);
    private Map<String, String> formTime = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use core layout
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        formNames = this.buildFormNameList();

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new EnketoRegisterPagerAdapter(getSupportFragmentManager(), formNames.toArray(new String[formNames.size()]), mBaseFragment());
        mPager.setOffscreenPageLimit(formNames.size());
        mPager.setAdapter(mPagerAdapter);
//        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                currentPage = position;
//                onPageChanged(position);
//            }
//        });
//
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // This Space for book :)
                android.util.Log.i(TAG, "onPageScrolled: ");
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // This Space for book :)
                android.util.Log.i(TAG, "onPageScrollStateChanged: ");

            }
        });

    }

    @Override
    protected void setupViews() {
        android.util.Log.d(TAG, "setupViews: Initialize NavBar");

    }

    protected List<String> buildFormNameList() {
        return new ArrayList<>();
    }

    protected Fragment mBaseFragment() {
        return null;
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {
        android.util.Log.e(TAG, "onInitialization: ");
    }

    public void startRegistration() {
        android.util.Log.d(TAG, "startRegistration: ");
    }

    @Override
    protected void onResumption() {
        android.util.Log.e(TAG, "onResumption: ");
        LoginActivity.setLanguage();

    }

    @Override
    protected void onPause() {
        super.onPause();
        retrieveAndSaveUnsubmittedFormData();

//        String KIEnd = timer.format(new Date());
//        Map<String, String> KI = new HashMap<>();
//        KI.put("end", KIEnd);
    }

    @Override
    public void onBackPressed() {
//        nf.setCriteria("");
//        Log.e(TAG, "onBackPressed: "+currentPage );
        // TODO: Set Language from Enketo
//        android.util.Log.e(TAG, "getView: lang 2 "+ getApplicationContext().getResources().getConfiguration().locale );
//        android.util.Log.e(TAG, "onBackPressed: ");
//        if (Locale.US.equals(getApplicationContext().getResources().getConfiguration().locale)){
//            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(Context.getInstance().applicationContext()));
//            android.util.Log.e(TAG, "onBackPressed:change Language " + allSharedPreferences.fetchLanguagePreference() );
//            Configuration cfg = new Configuration();
//            cfg.locale = new Locale("in");
//            getApplicationContext().getResources().updateConfiguration(cfg, null);
////            LoginActivity.switchLanguagePreference();
//        } else {
//            android.util.Log.e(TAG, "onBackPressed: onLang " );
//        }

        if (currentPage != 0) {
            switchToBaseFragment(null);
        } else {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    public void switchToBaseFragment(final String data) {
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
                displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.hideTranslucentProgressDialog();
                    displayFormFragment.setFormData(null);
                    displayFormFragment.setRecordId(null);

                }
            }
        });

    }

    public void onPageChanged(int page) {
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private boolean currentActivityIsShowingForm() {
        return currentPage != 0;
    }

    public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return (DisplayFormFragment) findFragmentByPosition(index);
    }

    public void retrieveAndSaveUnsubmittedFormData() {
        if (currentActivityIsShowingForm()) {
            formFragment = getDisplayFormFragmentAtIndex(currentPage);
            formFragment.saveCurrentFormData();
        }
    }

    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

//    public void saveuniqueid() {
//        Log.e(TAG, "saveuniqueid: saved" );
//        try {
//            JSONObject uniqueId = new JSONObject(LoginActivity.generator.uniqueIdController().getUniqueIdJson());
//            String uniq = uniqueId.getString("unique_id");
//            LoginActivity.generator.uniqueIdController().updateCurrentUniqueId(uniq);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        //  FlurryFacade.logEvent(formName);
//        if(Support.ONSYNC) {
//            Toast.makeText(this,"Data still Synchronizing, please wait",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        android.util.Log.e(TAG, "startFormActivity: timer " + timer.format(date));
        formTime.put("start", timer.format(date));

//        FlurryAgent.logEvent(formName,FS, true );
//        Log.v("fieldoverride", metaData);
        String data = "";

        try {
            int formIndex = formNames.indexOf(formName) + 1;// add the offset
            if (entityId != null || metaData != null) {
                //check if there is previously saved data for the form
                data = getPreviouslySavedDataForForm(formName, metaData, entityId);

                if (data == null) {
//                    data = EnketoFormUtils.getInstance(getApplicationContext())
//                            .generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                    data = BidanFormUtils.getInstance(getApplicationContext())
                            .generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                }

                displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.setFormData(data);
                    displayFormFragment.setRecordId(entityId);
                    displayFormFragment.setFieldOverides(metaData);
                    displayFormFragment.setListener(this);
                }
            }

            android.util.Log.e(TAG, "startFormActivity: formName " + formName);
            android.util.Log.e(TAG, "startFormActivity: displayForm " + data);

            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears

        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "startFormActivity: " + e.getMessage());
        }

    }

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides) {
        android.util.Log.e("fieldoverride", fieldOverrides.toString());
        // save the form
        try {

            //  FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
//            EnketoFormUtils formUtils = EnketoFormUtils.getInstance(getApplicationContext());
            BidanFormUtils formUtils = BidanFormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

//            BidanClientProcessor.getInstance(getApplicationContext()).processClient();
            ClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

            if ("registrasi_ibu".equals(formName)) {
                android.util.Log.d(TAG, "saveFormSubmission: it was registrasi_ibu form");
                //  FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());

                fieldOverrides.put("ibuCaseId", submission.entityId());
//                FieldOverrides fo = new FieldOverrides(fieldOverrides.toString());
//                activatingOtherForm("registrasi_anak", null, fo.getJSONString());
            }

            //end capture flurry log for FS
//            String end = timer.format(new Date());
//            Map<String, String> FS = new HashMap<>();
//            FS.put("end", end);
//            FlurryAgent.logEvent(formName, FS, true);
        } catch (Exception e) {
            // TODO: show error dialog on the formfragment if the submission fails
            DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(currentPage);
            if (displayFormFragment != null) {
                displayFormFragment.hideTranslucentProgressDialog();
            }
            e.printStackTrace();
        }

    }


//    public void activatingOtherForm(final String formName, final String entityId, final String metaData){
//        final int prevPageIndex = currentPage;
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //hack reset the form
//                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
//                if (displayFormFragment != null) {
//                    displayFormFragment.hideTranslucentProgressDialog();
//                    displayFormFragment.setFormData(null);
//
//                }
//
//                displayFormFragment.setRecordId(null);
////                activatingForm(formName, entityId, metaData);
//            }
//        });
//
//    }

//    private void activatingForm(String formName, String entityId, String metaData){
//        try {
//            int formIndex = EnketoFormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
//            if (entityId != null || metaData != null){
//                String data = null;
//                //check if there is previously saved data for the form
//                data = getPreviouslySavedDataForForm(formName, metaData, entityId);
//                if (data == null){
//                    data = EnketoFormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
//                }
//
//                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
//                if (displayFormFragment != null) {
//                    displayFormFragment.setFormData(data);
//                    displayFormFragment.setRecordId(entityId);
//                    displayFormFragment.setListener(this);
//                    displayFormFragment.setFieldOverides(metaData);
//                }
//            }
//
//            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }


//    public void saveFormSubmissionOld(String formSubmission, String id, String formName, JSONObject fieldOverrides) {
//        // save the form
//        try {
//            BidanFormUtils formUtils = BidanFormUtils.getInstance(getApplicationContext());
//            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);
//
////            ClientProcessor mClientProcessor = new ClientProcessor(getApplicationContext());
////            mClientProcessor.getInstance(getApplicationContext()).processClient();
////            ClientProcessor.getInstance(getApplicationContext()).processClient();
//            BidanClientProcessor.getInstance(getApplicationContext()).processClient();
//
//            android.util.Log.e(TAG, "saveFormSubmission:formName " + formName );
//            System.out.println(submission);
//            android.util.Log.e(TAG, "saveFormSubmission:submission " + submission );
//
//            context().formSubmissionService().updateFTSsearch(submission);
//            context().formSubmissionRouter().handleSubmission(submission, formName);
//
//            if ("kartu_ibu_registration".equals(formName)) saveuniqueid();
//
//            //switch to forms list fragment
//            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data
//
//        } catch (Exception e) {
//            // TODO: show error dialog on the formfragment if the submission fails
//            displayFormFragment = getDisplayFormFragmentAtIndex(currentPage);
//            if (displayFormFragment != null) {
//                displayFormFragment.hideTranslucentProgressDialog();
//            }
//            e.printStackTrace();
//            android.util.Log.e(TAG, "saveFormSubmission: " + e.getCause());
//        }
//        //end capture flurry log for FS
//        formTime.put("end", timer.format(new Date()));
////        FlurryAgent.logEvent(formName,FS, true);
//    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{};
    }

//    public class EditDialogOptionModel implements DialogOptionModel {
//
//        @Override
//        public DialogOption[] getDialogOptions() {
//            return getEditOptions();
//        }
//
//        @Override
//        public void onDialogOptionSelection(DialogOption option, Object tag) {
//            android.util.Log.e(TAG, "onDialogOptionSelection: NOEDIT" );
//            CommonPersonObjectClient pc = (CommonPersonObjectClient) tag;
//            DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
//            detailsRepository.updateDetails(pc);
//            String ibuCaseId = getValue(pc.getColumnmaps(), "_id", true).toLowerCase();
//            JSONObject fieldOverrides = new JSONObject();
//            try {
//                fieldOverrides.put("Province", pc.getDetails().get("stateProvince"));
//                fieldOverrides.put("District", pc.getDetails().get("countyDistrict"));
//                fieldOverrides.put("Sub-district", pc.getDetails().get("address2"));
//                fieldOverrides.put("Village", pc.getDetails().get("cityVillage"));
//                fieldOverrides.put("Sub-village", pc.getDetails().get("address1"));
//                fieldOverrides.put("jenis_kelamin", pc.getDetails().get("gender"));
//                fieldOverrides.put("ibuCaseId", ibuCaseId);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            FieldOverrides fo = new FieldOverrides(fieldOverrides.toString());
//            onEditSelectionWithMetadata((EditOption) option, (SmartRegisterClient) tag, fo.getJSONString());
//        }
//    }

    /**
     * Inner class for Edit and Followup
     */
    public class EditDialogOptionModelNew implements DialogOptionModel {

        @Override
        public DialogOption[] getDialogOptions() {

            return getEditOptions();
        }

        /**
         * Method
         *
         * @param option DialogOption
         * @param tag    Object Tag
         */
        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            CommonPersonObjectClient pc = (CommonPersonObjectClient) tag;

//            android.util.Log.e(TAG, "onDialogOptionSelection:columnMap "+ pc.getColumnmaps() );
//            android.util.Log.e(TAG, "onDialogOptionSelection:details "+ pc.getDetails() );
            DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();

            if (option.name().equalsIgnoreCase(getString(R.string.str_edit_ki_form))) {
                // Edit Form Ibu
                detailsRepository.updateDetails(pc);
                String ibuCaseId = getValue(pc.getColumnmaps(), "_id", true).toLowerCase();
                String namaIbu = getValue(pc.getColumnmaps(), "namalengkap", true).toLowerCase();
                JSONObject fieldOverrides = new JSONObject();

                try {
                    fieldOverrides.put("Province", pc.getDetails().get("stateProvince"));
                    fieldOverrides.put("District", pc.getDetails().get("countyDistrict"));
                    fieldOverrides.put("Sub-district", pc.getDetails().get("address2"));
                    fieldOverrides.put("Village", pc.getDetails().get("cityVillage"));
                    fieldOverrides.put("Sub-village", pc.getDetails().get("address1"));
                    fieldOverrides.put("jenis_kelamin", pc.getDetails().get("gender"));
                    fieldOverrides.put("ibuCaseId", ibuCaseId);
                    fieldOverrides.put("namaLengkap", namaIbu);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FieldOverrides fo = new FieldOverrides(fieldOverrides.toString());
                android.util.Log.e(TAG, "onDialogOptionSelection:fo "+ fo.getJSONString() );
                onEditSelectionWithMetadata((EditOption) option, (SmartRegisterClient) tag, fo.getJSONString());

            } else if (option.name().equalsIgnoreCase(getString(R.string.str_anak_edit))) {
                // Edit Form Ibu
                Log.logError(TAG, "kohort_bayi_edit");
                detailsRepository.updateDetails(pc);
                String ibuCaseId = getValue(pc.getColumnmaps(), "_id", true).toLowerCase();
                JSONObject fieldOverrides = new JSONObject();

                try {
                    fieldOverrides.put("Province", pc.getDetails().get("stateProvince"));
                    fieldOverrides.put("District", pc.getDetails().get("countyDistrict"));
                    fieldOverrides.put("Sub-district", pc.getDetails().get("address2"));
                    fieldOverrides.put("Village", pc.getDetails().get("cityVillage"));
                    fieldOverrides.put("Sub-village", pc.getDetails().get("address1"));
                    fieldOverrides.put("jenis_kelamin", pc.getDetails().get("gender"));
                    fieldOverrides.put("ibuCaseId", ibuCaseId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FieldOverrides fo = new FieldOverrides(fieldOverrides.toString());
                android.util.Log.e(TAG, "onDialogOptionSelection:fo.getJSONString() " + fo.getJSONString());
                onEditSelectionWithMetadata((EditOption) option, (SmartRegisterClient) tag, fo.getJSONString());

            } else {

                if (option.name().equalsIgnoreCase(getString(R.string.str_register_fp_form))) {
//                    pc = DetailMotherActivity.motherClient;
//                    android.util.Log.e(TAG, "onDialogOptionSelection: pc " + pc);

                    if (!StringUtils.isNumeric(pc.getDetails().get("jenisKontrasepsi"))) {
                        Toast.makeText(BaseRegisterActivity.this, getString(R.string.mother_already_registered_in_fp), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AllCommonsRepository iburep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
                    final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());
                    if (ibuparent != null) {
                        short anc_isclosed = ibuparent.getClosed();
                        if (anc_isclosed == 0) {
                            Toast.makeText(BaseRegisterActivity.this, getString(R.string.mother_already_registered), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                }

                if (option.name().equalsIgnoreCase(getString(R.string.str_register_anc_form))) {
                    AllCommonsRepository iburep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
                    final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());
                    if (ibuparent != null) {
                        short anc_isclosed = ibuparent.getClosed();
                        if (anc_isclosed == 0) {
                            Toast.makeText(BaseRegisterActivity.this, getString(R.string.mother_already_registered), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                }
                onEditSelection((EditOption) option, (SmartRegisterClient) tag);

            }
            // End Select Edit or Followup


        }
    }

}
