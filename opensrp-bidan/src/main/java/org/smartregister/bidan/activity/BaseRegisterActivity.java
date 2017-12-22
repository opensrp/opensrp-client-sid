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
import org.smartregister.bidan.R;
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
import org.smartregister.util.Log;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.DialogOptionModel;
import org.smartregister.view.dialog.EditOption;
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.smartregister.util.Utils.getValue;

/**
 * Created by sid-tech on 12/7/17.
 */

public class BaseRegisterActivity extends SecuredNativeSmartRegisterActivity implements DisplayFormListener {

    protected SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");

    @Bind(R.id.view_pager)
    protected OpenSRPViewPager mPager;

    protected int currentPage;
    protected FragmentPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use core layout
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames(), mBaseFragment());

        int formLength = ((formNames() == null) ? 1 : formNames().length);
        mPager.setOffscreenPageLimit(formLength);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }
        });

//        ziggyService = context().ziggyService();

    }

    protected String[] formNames() {

        return null;
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

    }

    @Override
    public void startRegistration() {

    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void onResumption() {

        formNames();
    }

    @Override
    protected void onPause() {
        super.onPause();
        retrieveAndSaveUnsubmittedFormData();

        String KIEnd = timer.format(new Date());
        Map<String, String> KI = new HashMap<>();
        KI.put("end", KIEnd);
    }

    @Override
    public void onBackPressed() {
//        nf.setCriteria("");
//        Log.e(TAG, "onBackPressed: "+currentPage );

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
                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.hideTranslucentProgressDialog();
                    displayFormFragment.setFormData(null);

                }

                displayFormFragment.setRecordId(null);
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
        return (DisplayFormFragment) findFragmentByPosition(3);
    }

    public void retrieveAndSaveUnsubmittedFormData() {
        if (currentActivityIsShowingForm()) {
            DisplayFormFragment formFragment = getDisplayFormFragmentAtIndex(currentPage);
            formFragment.saveCurrentFormData();
        }
    }

    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    public void saveuniqueid() {
//        Log.e(TAG, "saveuniqueid: saved" );
//        try {
//            JSONObject uniqueId = new JSONObject(LoginActivity.generator.uniqueIdController().getUniqueIdJson());
//            String uniq = uniqueId.getString("unique_id");
//            LoginActivity.generator.uniqueIdController().updateCurrentUniqueId(uniq);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        //  FlurryFacade.logEvent(formName);
//        if(Support.ONSYNC) {
//            Toast.makeText(this,"Data still Synchronizing, please wait",Toast.LENGTH_SHORT).show();
//            return;
//        }
        String start = timer.format(new Date());
        Map<String, String> FS = new HashMap<>();
        FS.put("start", start);
//        FlurryAgent.logEvent(formName,FS, true );
//        Log.v("fieldoverride", metaData);
        try {
            int formIndex = BidanFormUtils.getIndexForFormName(formName, formNames()) + 1; // add the offset
            if (entityId != null || metaData != null) {
                String data = null;
                //check if there is previously saved data for the form
                data = getPreviouslySavedDataForForm(formName, metaData, entityId);
                if (data == null) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides) {
        // save the form
        try {
            BidanFormUtils formUtils = BidanFormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            ClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);

            if (formName.equals("kartu_ibu_registration")) {
                saveuniqueid();
            }
            //switch to forms list fragment
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

        } catch (Exception e) {
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

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{};
    }

    private String getDetailsPc(Object tag, String key) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) tag;

        return pc.getDetails().get(key);
    }

    public class EditDialogOptionModel implements DialogOptionModel {

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            CommonPersonObjectClient pc = (CommonPersonObjectClient) tag;
            DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
            detailsRepository.updateDetails(pc);
            String ibuCaseId = getValue(pc.getColumnmaps(), "relational_id", true).toLowerCase();
            Log.logError(TAG, "onDialogOptionSelection: " + pc.getDetails());
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
            onEditSelectionWithMetadata((EditOption) option, (SmartRegisterClient) tag, fo.getJSONString());
        }
    }

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
         * @param option
         * @param tag
         */
        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
//            android.util.Log.e(TAG, "onDialogOptionSelection: "+ option.name() );
            CommonPersonObjectClient pc = (CommonPersonObjectClient) tag;

            DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();

            if (option.name().equalsIgnoreCase(getString(R.string.str_edit_ki_form))) {
                // Edit Form Ibu
                Log.logError(TAG, "update_ibu_form");
//                CommonPersonObjectClient pc = (CommonPersonObjectClient) tag;
//                DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
                detailsRepository.updateDetails(pc);
                String ibuCaseId = getValue(pc.getColumnmaps(), "relational_id", true).toLowerCase();
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
                onEditSelectionWithMetadata((EditOption) option, (SmartRegisterClient) tag, fo.getJSONString());

            } else {
//                DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
//                detailsRepository.updateDetails(motherClient);

                if (option.name().equalsIgnoreCase(getString(R.string.str_register_fp_form))) {
//                     pc = KIDetailActivity.fpClient;
                    pc = DetailMotherActivity.motherClient;

//                    CommonPersonObjectClient pc = motherClient;

//                    Log.logError(TAG, String.valueOf(motherClient));

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
//                    CommonPersonObjectClient pc = motherClient;
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
