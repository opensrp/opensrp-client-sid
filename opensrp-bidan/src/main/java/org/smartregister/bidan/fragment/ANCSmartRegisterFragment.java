package org.smartregister.bidan.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.BaseRegisterActivity;
import org.smartregister.bidan.activity.DetailANCActivity;
import org.smartregister.bidan.activity.DetailMotherActivity;
import org.smartregister.bidan.activity.NativeKIANCSmartRegisterActivity;
import org.smartregister.bidan.activity.NativeKIbuSmartRegisterActivity;
import org.smartregister.bidan.options.KIANCOverviewServiceMode;
import org.smartregister.bidan.options.MotherFilterOption;
import org.smartregister.bidan.provider.KIANCClientsProvider;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.CursorCommonObjectFilterOption;
import org.smartregister.cursoradapter.CursorCommonObjectSort;
import org.smartregister.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.util.StringUtil;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.AllClientsFilter;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.DialogOptionModel;
import org.smartregister.view.dialog.EditOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.NameSort;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.INVISIBLE;

/**
 * Created by sid-tech on 11/29/17.
 */

public class ANCSmartRegisterFragment extends BaseSmartRegisterFragment {

    private static final String TAG = ANCSmartRegisterFragment.class.getName();
    //    WD
    public static String criteria;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    Date date = new Date();
    SimpleDateFormat sdf;
    Map<String, String> FS = new HashMap<>();

    public void setCriteria(String criteria) {
        ANCSmartRegisterFragment.criteria = criteria;
    }

    @Override
    protected void onCreation() {
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new KIANCOverviewServiceMode(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new NameSort();
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.anc_register_title_in_short);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
//                FlurryFacade.logEvent("click_filter_option_on_kohort_ibu_dashboard");
                ArrayList<DialogOption> dialogOptionslist = new ArrayList<>();

                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_all_label), filterStringForAll()));

                String locationJSON = context().anmLocationController().get();
                LocationTree locationTree = EntityUtils.fromJson(locationJSON, LocationTree.class);

                Map<String, TreeNode<String, Location>> locationMap =
                        locationTree.getLocationsHierarchy();

                addChildToList(dialogOptionslist, locationMap);

                DialogOption[] dialogOptions = new DialogOption[dialogOptionslist.size()];
                for (int i = 0; i < dialogOptionslist.size(); i++) {
                    dialogOptions[i] = dialogOptionslist.get(i);
                }

                return dialogOptions;
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
//                FlurryFacade.logEvent("click_sorting_option_on_kohort_ibu_dashboard");
                return new DialogOption[]{
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label), KiSortByNameAZ()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label_reverse), KiSortByNameZA()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_wife_age_label), KiSortByAge()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_edd_label), KiSortByEdd()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_no_ibu_label), KiSortByNoIbu()),
                };
            }

            @Override
            public String searchHint() {
                return getResources().getString(R.string.hh_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {
        //  context.formSubmissionRouter().getHandlerMap().put("census_enrollment_form", new CensusEnrollmentHandler());
    }

    @Override
    public void startRegistration() {
    }

    @Override
    public void setupViews(View view) {
        getDefaultOptionsProvider();

        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(View.GONE);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
//        list.setBackgroundColor(Color.RED);
        initializeQueries(getCriteria());
    }

    private String filterStringForAll() {
        return "";
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initializeQueries(String s) {
        try {

            KIANCClientsProvider kiscp = new KIANCClientsProvider(getActivity(), clientActionHandler, context().alertService());
            clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, kiscp, new CommonRepository("ec_ibu", new String[]{"ec_ibu.is_closed", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.namaSuami"}));
            clientsView.setAdapter(clientAdapter);

            setTablename("ec_ibu");
            SmartRegisterQueryBuilder countqueryBuilder = new SmartRegisterQueryBuilder();
            countqueryBuilder.SelectInitiateMainTableCounts("ec_ibu");

            if (s != null && !s.isEmpty()) {
                Log.e(TAG, "initializeQueries with ID = " + s);
                mainCondition = "is_closed = 0 AND namalengkap != '' AND object_id LIKE '%" + s + "%'";

            } else {
                mainCondition = "is_closed = 0 AND namalengkap != '' ";
                Log.e(TAG, "initializeQueries: Not Initialized");
            }

            joinTable = "";
            countSelect = countqueryBuilder.mainCondition(mainCondition);
            super.CountExecute();

            SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();

            queryBuilder.SelectInitiateMainTable("ec_ibu", new String[]{"ec_ibu.relationalid", "ec_ibu.is_closed", "ec_ibu.details", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.namaSuami", "imagelist.imageid"});
            queryBuilder.customJoin("LEFT JOIN ec_kartu_ibu on ec_kartu_ibu.id = ec_ibu.id LEFT JOIN ImageList imagelist ON ec_ibu.id=imagelist.entityID");
            mainSelect = queryBuilder.mainCondition(mainCondition);
            Sortqueries = KiSortByNameAZ();

            currentlimit = 20;
            currentoffset = 0;

            super.filterandSortInInitializeQueries();
            CountExecute();
            updateSearchView();
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String KiSortByNameAZ() {
        return "namalengkap ASC";
    }

    private String KiSortByNameZA() {
        return "namalengkap DESC";
    }

    private String KiSortByAge() {
        return "umur DESC";
    }

    private String KiSortByNoIbu() {
        return "noIbu ASC";
    }

    private String KiSortByEdd() {
        return "htp IS NULL, htp";
    }

    @Override
    protected void onResumption() {
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries("");
        }
        try {
//            LoginActivity.setLanguage();
        } catch (Exception ignored) {

        }

    }

    private void updateSearchView() {
        getSearchView().removeTextChangedListener(textWatcher);
        getSearchView().addTextChangedListener(textWatcher);
    }

    public void addChildToList(ArrayList<DialogOption> dialogOptionslist, Map<String, TreeNode<String, Location>> locationMap) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if (entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist, entry.getValue().getChildren());

            } else {
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                dialogOptionslist.add(new MotherFilterOption(name, "location_name", name, "ec_kartu_ibu"));

            }
        }
    }

    @Override
    public void setupSearchView(final View view) {
        searchView = (EditText) view.findViewById(org.smartregister.R.id.edt_search);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (SmartShutterActivity.isDevCompat) {
//                    CharSequence selections[] = new CharSequence[]{"Name", "Photo"};
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setTitle("Please Choose one, Search by");
//                    builder.setItems(selections, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int opt) {
//                            if (opt == 0) searchTextChangeListener("");
//                            else getFacialRecord(view);
//                        }
//                    });
//                    builder.show();
//                } else {
                searchTextChangeListener("");
//                }
            }
        });

        searchCancelView = view.findViewById(org.smartregister.R.id.btn_search_cancel);
        searchCancelView.setOnClickListener(searchCancelHandler);
    }

    public void getFacialRecord(View view) {

//        FlurryAgent.logEvent(TAG + "search_by_face", true);
        Log.d(TAG, "getFacialRecord: ");
//        Log.e(TAG, "getFacialRecord: ");

//        sdf = new SimpleDateFormat("hh:mm:ss.SS", Locale.ENGLISH);
//        String face_start = sdf.format(date);
//        FS.put("face_start", face_start);

//        SmartShutterActivity.kidetail = (CommonPersonObjectClient) view.getTag();
//        FlurryAgent.logEvent(TAG + "search_by_face", FS, true);
//        Intent intent = new Intent(getActivity(), SmartShutterActivity.class);
//        intent.putExtra("org.sid.sidface.ImageConfirmation.origin", TAG);
//        intent.putExtra("org.sid.sidface.ImageConfirmation.identify", true);
//        intent.putExtra("org.sid.sidface.ImageConfirmation.kidetail", (Parcelable) SmartShutterActivity.kidetail);
//        startActivityForResult(intent, 2);
    }

    public void searchTextChangeListener(String s) {

        if (s != null) {
            filters = s;
        } else {
            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(final CharSequence cs, int start, int before, int count) {

                    (new AsyncTask() {

                        @Override
                        protected Object doInBackground(Object[] params) {
                            filters = cs.toString();
                            return null;
                        }
                    }).execute();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent myIntent = new Intent(getActivity(), NativeKIbuSmartRegisterActivity.class);
        if (data != null) {
            myIntent.putExtra("org.smartregister.bidan_cloudant.face.face_mode", true);
            myIntent.putExtra("org.smartregister.bidan_cloudant.face.base_id", data.getStringExtra("org.smartregister.bidan_cloudant.face.base_id"));
        }
        getActivity().startActivity(myIntent);

    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
//                    FlurryFacade.logEvent("click_detail_view_on_kohort_ibu_dashboard");
                    DetailANCActivity.ancClient = (CommonPersonObjectClient) view.getTag();
                    Intent intent = new Intent(getActivity(), DetailANCActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;

                case R.id.ib_btn_edit:
                    DetailANCActivity.ancClient = (CommonPersonObjectClient) view.getTag();
//                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    showFragmentDialog(((NativeKIANCSmartRegisterActivity) getActivity()).new EditDialogOptionModel(), view.getTag());

                    break;
            }
        }

    }

    private class EditDialogOptionModelOld implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {

            if (option.name().equalsIgnoreCase(getString(R.string.str_register_anc_form))) {
                CommonPersonObjectClient pc = DetailMotherActivity.motherClient;
                AllCommonsRepository iburep = Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
                final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());
                if (ibuparent != null) {
                    short anc_isclosed = ibuparent.getClosed();
                    if (anc_isclosed == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.mother_already_registered), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            if (option.name().equalsIgnoreCase(getString(R.string.str_register_fp_form))) {
                CommonPersonObjectClient pc = DetailMotherActivity.motherClient;

                if (!StringUtils.isNumeric(pc.getDetails().get("jenisKontrasepsi"))) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.mother_already_registered_in_fp), Toast.LENGTH_SHORT).show();
                    return;
                }

                AllCommonsRepository iburep = Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
                final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());
                if (ibuparent != null) {
                    short anc_isclosed = ibuparent.getClosed();
                    if (anc_isclosed == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.mother_already_registered), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }
}
