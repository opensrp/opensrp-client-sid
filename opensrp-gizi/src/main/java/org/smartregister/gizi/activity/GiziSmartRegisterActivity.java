package org.smartregister.gizi.activity;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.flurry.android.FlurryAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.gizi.R;
import org.smartregister.gizi.fragment.GiziSmartRegisterFragment;
import org.smartregister.gizi.pageradapter.BaseRegisterActivityPagerAdapter;
import org.smartregister.gizi.sync.GiziClientProcessor;
import org.smartregister.gizi.utils.KmsHandler;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.repository.DetailsRepository;
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
import util.EnketoFormUtils;

import static org.smartregister.util.Utils.getValue;

public class GiziSmartRegisterActivity extends SecuredNativeSmartRegisterActivity implements
        LocationSelectorDialogFragment.OnLocationSelectedListener, DisplayFormListener{

    private SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");

    public static final String TAG = GiziSmartRegisterActivity.class.getSimpleName();
    @Bind(R.id.view_pager) public OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;
    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;

    private GiziSmartRegisterFragment nf = new GiziSmartRegisterFragment();

//    private Map<String, String> FS = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        String GiziStart = timer.format(new Date());
        Map<String, String> Gizi = new HashMap<>();
        Gizi.put("start", GiziStart);

        formNames = this.buildFormNameList();

//        Bundle extras = getIntent().getExtras();

//        if (extras != null) {
//            boolean mode_face = extras.getBoolean("org.ei.opensrp.indonesia.face.face_mode");
//            String base_id = extras.getString("org.ei.opensrp.indonesia.face.base_id");
//            double proc_time = extras.getDouble("org.ei.opensrp.indonesia.face.proc_time");
//
//            if (mode_face) {
//                nf.setCriteria(base_id);
//                mBaseFragment = new GiziSmartRegisterFragment();
//
//                Log.e(TAG, "onCreate: id " + base_id);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Is it Right Person ?");
//
//                // TODO : get name by base_id
//                builder.setNegativeButton("CANCEL", listener );
//                builder.setPositiveButton("YES", listener );
//                builder.show();
//            }
//
//        } else {
            mBaseFragment = new GiziSmartRegisterFragment();
//        }

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
//        ZiggyService ziggyService = context().ziggyService();
    }

    public void onPageChanged(int page){
        setRequestedOrientation(page == 0
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LoginActivity.setLanguage();
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() { return null; }

    @Override
    protected void setupViews() {
        // do nothing
    }

    @Override
    protected void onResumption(){
        // do nothing
    }

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
        //do nothing
    }

    public DialogOption[] getEditOptions() {
            return new DialogOption[]{
                new OpenFormOption("Kunjungan Per Bulan ", "kunjungan_gizi", formController),
                    new OpenFormOption("Edit Anak ", "child_edit", formController),
                new OpenFormOption("Close Form","close_form",formController)
            };
    }


    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try{

            EnketoFormUtils formUtils = EnketoFormUtils.getInstance(getApplicationContext());
          //  FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            GiziClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

            if(formName.equals("registrasi_ibu")) {
                Log.d(TAG, "saveFormSubmission: it was registrasi_ibu form");
                //  FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());

                fieldOverrides.put("ibuCaseId",submission.entityId());
                FieldOverrides fo = new FieldOverrides(fieldOverrides.toString());

                activatingOtherForm("registrasi_anak", null, fo.getJSONString());
        ///        activatingForm("registrasi_anak", null, fo.getJSONString());
            }

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
            startFormActivity("registrasi_ibu", null, fieldOverrides.getJSONString());
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
            int formIndex = EnketoFormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
            if (entityId != null || metaData != null){
                String data = null;
                //check if there is previously saved data for the form
                data = getPreviouslySavedDataForForm(formName, metaData, entityId);
                if (data == null){
                    data = EnketoFormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
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

        selections[choice] = name;

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
                    selections[i] = temp;
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

                assert displayFormFragment != null;
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
        formNames.add("registrasi_gizi");
        formNames.add("registrasi_ibu");
        formNames.add("registrasi_anak");
        formNames.add("kunjungan_gizi");
        formNames.add("close_form");
        formNames.add("child_edit");
        formNames.add("kartu_ibu_registration");
        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        retrieveAndSaveUnsubmittedFormData();
        String GiziEnd = timer.format(new Date());
        Map<String, String> Gizi = new HashMap<String, String>();
        Gizi.put("end", GiziEnd);
        FlurryAgent.logEvent("Gizi_dashboard",Gizi, true );
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

//    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            String face_end = timer.format(new Date());
//            FS.put("face_end", face_end);
//
//            Log.e(TAG, "onClick: which "+ which );
//            nf.setCriteria("!");
//
//            if (which == -1 ){
//                currentPage = 0;
//                Log.e(TAG, "onClick: YES ");
//                FlurryAgent.logEvent(TAG+"search_by_face OK", true);
//
//            } else {
//                Log.e(TAG, "onClick: NO "+currentPage);
//                FlurryAgent.logEvent(TAG+"search_by_face NOK", true);
//
//                onBackPressed();
//
//                Intent intent= new Intent(GiziSmartRegisterActivity.this, GiziSmartRegisterActivity.class);
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
//            }
//
//
//        }
//    };

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
            JSONObject fieldOverrides = new JSONObject();
            try {
                fieldOverrides.put("Province", pc.getDetails().get("stateProvince"));
                fieldOverrides.put("District", pc.getDetails().get("countyDistrict"));
                fieldOverrides.put("Sub-district", pc.getDetails().get("address2"));
                fieldOverrides.put("Village", pc.getDetails().get("cityVillage"));
                fieldOverrides.put("Sub-village", pc.getDetails().get("address1"));
                fieldOverrides.put("ibuCaseId", ibuCaseId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            FieldOverrides fo = new FieldOverrides(fieldOverrides.toString());
            onEditSelectionWithMetadata((EditOption) option, (SmartRegisterClient) tag, fo.getJSONString());
        }
    }
}
