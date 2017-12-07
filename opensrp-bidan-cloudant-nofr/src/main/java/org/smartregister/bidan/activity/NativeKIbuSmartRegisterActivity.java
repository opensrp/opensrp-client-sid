package org.smartregister.bidan.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.bidan.R;
import org.smartregister.bidan.fragment.KISmartRegisterFragment;
import org.smartregister.bidan.pageradapter.BaseRegisterActivityPagerAdapter;
import org.smartregister.bidan.sync.ClientProcessor;
import org.smartregister.bidan.utils.BidanFormUtils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.ZiggyService;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.DialogOptionModel;
import org.smartregister.view.dialog.EditOption;
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

import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.ANAK_BAYI_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_ANC_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_CLOSE;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_EDIT;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KARTU_IBU_REGISTRATION;
import static org.smartregister.bidan.utils.AllConstantsINA.FormNames.KOHORT_KB_PELAYANAN;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by sid-tech on 11/28/17.
 */

public class NativeKIbuSmartRegisterActivity extends SecuredNativeSmartRegisterActivity implements
        LocationSelectorDialogFragment.OnLocationSelectedListener, DisplayFormListener {

    public static final String TAG = NativeKIbuSmartRegisterActivity.class.getSimpleName();
    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    ZiggyService ziggyService;
    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        formNames = this.buildFormNameList();
        mBaseFragment = new KISmartRegisterFragment();

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

    @Override
    protected void onPause() {
        super.onPause();

        retrieveAndSaveUnsubmittedFormData();
        String KIEnd = timer.format(new Date());
        Map<String, String> KI = new HashMap<>();
        KI.put("end", KIEnd);
    }

    public void retrieveAndSaveUnsubmittedFormData() {
        if (currentActivityIsShowingForm()) {
            DisplayFormFragment formFragment = getDisplayFormFragmentAtIndex(currentPage);
            formFragment.saveCurrentFormData();
        }
    }


    private boolean currentActivityIsShowingForm() {
        return currentPage != 0;
    }

    public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return (DisplayFormFragment) findFragmentByPosition(index);
    }

    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }


    public DialogOption[] getEditOptions() {

        return new DialogOption[]{

                new OpenFormOption(getString(R.string.str_edit_ec_form), KARTU_IBU_EDIT, formController),
                new OpenFormOption(getString(R.string.str_register_fp_form), KOHORT_KB_PELAYANAN, formController),
                new OpenFormOption(getString(R.string.str_register_anc_form), KARTU_IBU_ANC_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_register_child_form), ANAK_BAYI_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_close_ki_form), KARTU_IBU_CLOSE, formController),
        };
    }

    public void onPageChanged(int page) {
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        LoginActivity.setLanguage();
    }

    private String[] buildFormNameList() {
        List<String> formNames = new ArrayList<>();
        formNames.add(KARTU_IBU_REGISTRATION);
        formNames.add(KOHORT_KB_PELAYANAN);
        formNames.add(KARTU_IBU_ANC_REGISTRATION);
        formNames.add(ANAK_BAYI_REGISTRATION);
        formNames.add(KARTU_IBU_CLOSE);

        return formNames.toArray(new String[formNames.size()]);
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

    }

    @Override
    public void startRegistration() {

    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void onResumption() {
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed: "+currentPage );

        if (currentPage != 0) {
            switchToBaseFragment(null);
        } else {
            super.onBackPressed(); // allow back key only if we are
        }
    }


    /**
     * Method post excecute KISmartRegisterFragment: startRegistration
     * @param locationJSONString
     */
    @Override
    public void OnLocationSelected(String locationJSONString) {
//        if(Support.ONSYNC) {
//            Toast.makeText(this,"Data still Synchronizing, please wait",Toast.LENGTH_SHORT).show();
//            return;
//        }
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
            Log.e(TAG, "OnLocationSelected: "+ combined.toString() );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (combined != null) {
            FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());
            Log.e(TAG, "OnLocationSelected: combined" );

            startFormActivity(KARTU_IBU_REGISTRATION, null, fieldOverrides.getJSONString());
        }
    }

    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        try {
            BidanFormUtils formUtils = BidanFormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);
            ziggyService.saveForm(getParams(submission), submission.instance());
            ClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);
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
//        FlurryAgent.logEvent(formName,FS, true);
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

    /**
     * Methid post execute NativeKIbuSmartRegisterActivity: OnLocationSelected
     * @param formName
     * @param entityId
     * @param metaData
     */
    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        // FlurryFacade.logEvent(formName);
//        Log.v("fieldoverride", metaData);
        Log.e(TAG, "startFormActivity: formName "+ formName );
        Log.e(TAG, "startFormActivity: metaData "+ metaData );
        Log.e(TAG, "startFormActivity: entityId "+ entityId );

        try {
            int formIndex = BidanFormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
            if (entityId != null || metaData != null){
                String data;
                //check if there is previously saved data for the form

                data = getPreviouslySavedDataForForm(formName, metaData, entityId);
                Log.e(TAG, "startFormActivity: "+data );
                if (data == null){
                    data = BidanFormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                }

                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.setFormData(data);
                    displayFormFragment.setRecordId(entityId);
                    displayFormFragment.setFieldOverides(metaData);

                    displayFormFragment.setListener(this);
                }
            }

            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Inner class for Edit and Followup
     */
    public class EditDialogOptionModel implements DialogOptionModel {

        @Override
        public DialogOption[] getDialogOptions() {

            Log.e(TAG, "getDialogOptions: " );
            return getEditOptions();
        }

        /**
         * Method
         * @param option
         * @param tag
         */
        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
//            Log.e(TAG, "onDialogOptionSelection: optionName "+ option.name() );

            if (option.name().equalsIgnoreCase(getString(R.string.str_edit_ec_form)))  {
                // Edit Form Ibu
                Log.e(TAG, "onDialogOptionSelection: " );
                CommonPersonObjectClient pc = (CommonPersonObjectClient) tag;
                DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
                detailsRepository.updateDetails(pc);
                String ibuCaseId = getValue(pc.getColumnmaps(), "relational_id", true).toLowerCase();
                Log.d(TAG, "onDialogOptionSelection: "+pc.getDetails());
                JSONObject fieldOverrides = new JSONObject();

                try {
                    fieldOverrides.put("Province",      pc.getDetails().get("stateProvince"));
                    fieldOverrides.put("District",      pc.getDetails().get("countyDistrict"));
                    fieldOverrides.put("Sub-district",  pc.getDetails().get("address2"));
                    fieldOverrides.put("Village",       pc.getDetails().get("cityVillage"));
                    fieldOverrides.put("Sub-village",   pc.getDetails().get("address1"));
                    fieldOverrides.put("jenis_kelamin", pc.getDetails().get("gender"));
                    fieldOverrides.put("ibuCaseId",     ibuCaseId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FieldOverrides fo = new FieldOverrides(fieldOverrides.toString());
                onEditSelectionWithMetadata((EditOption) option, (SmartRegisterClient) tag, fo.getJSONString());

            } else {

                if (option.name().equalsIgnoreCase(getString(R.string.str_register_fp_form))) {
                    Log.e(TAG, "onDialogOptionSelection: FP" );
                    CommonPersonObjectClient pc = DetailMotherActivity.motherClient;

                    if (!StringUtils.isNumeric(pc.getDetails().get("jenisKontrasepsi"))) {
                        Toast.makeText(NativeKIbuSmartRegisterActivity.this, getString(R.string.mother_already_registered_in_fp), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AllCommonsRepository iburep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
                    final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());
                    if (ibuparent != null) {
                        short anc_isclosed = ibuparent.getClosed();
                        if (anc_isclosed == 0) {
                            Toast.makeText(NativeKIbuSmartRegisterActivity.this, getString(R.string.mother_already_registered), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                }

                if (option.name().equalsIgnoreCase(getString(R.string.str_register_anc_form))) {
                    Log.e(TAG, "onDialogOptionSelection: ANC " );
                    CommonPersonObjectClient pc = DetailMotherActivity.motherClient;
                    AllCommonsRepository iburep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
                    final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());
                    if (ibuparent != null) {
                        short anc_isclosed = ibuparent.getClosed();
                        if (anc_isclosed == 0) {
                            Toast.makeText(NativeKIbuSmartRegisterActivity.this, getString(R.string.mother_already_registered), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }


                }
                onEditSelection((EditOption) option, (SmartRegisterClient) tag);

            }

        }
    }

    private String getDetailsPc(Object tag, String key) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) tag;

        return pc.getDetails().get(key);
    }


}
