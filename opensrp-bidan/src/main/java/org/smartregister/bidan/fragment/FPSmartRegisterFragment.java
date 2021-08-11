package org.smartregister.bidan.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.DetailFPActivity;
import org.smartregister.bidan.activity.FPSmartRegisterActivity;
import org.smartregister.bidan.options.AllKBServiceMode;
import org.smartregister.bidan.options.MotherFilterOption;
import org.smartregister.bidan.provider.KBClientsProvider;
import org.smartregister.bidan.utils.AllConstantsINA;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.CursorCommonObjectFilterOption;
import org.smartregister.cursoradapter.CursorCommonObjectSort;
import org.smartregister.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.util.StringUtil;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.AllClientsFilter;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.NameSort;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import java.util.ArrayList;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class FPSmartRegisterFragment extends BaseSmartRegisterFragment {

    private static final String TAG = FPSmartRegisterFragment.class.getName();
    public static String criteria;

//    @Override
//    protected SmartRegisterPaginatedAdapter adapter() {
//        return new SmartRegisterPaginatedAdapter(clientsProvider());
//    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new AllKBServiceMode(clientsProvider());
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
                return Context.getInstance().getStringResource(R.string.kb_register_title_in_short);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
//                FlurryFacade.logEvent("click_filter_option_on_kohort_kb_dashboard");
                ArrayList<DialogOption> dialogOptionslist = new ArrayList<>();

                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_all_label), ""));

                String locationJSON = context().anmLocationController().get();
                LocationTree locationTree = EntityUtils.fromJson(locationJSON, LocationTree.class);

                Map<String, TreeNode<String, Location>> locationMap = locationTree.getLocationsHierarchy();
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
//                FlurryFacade.logEvent("click_sorting_option_on_kohort_kb_dashboard");
                return new DialogOption[]{
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label), kiSortByNameAZ()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label_reverse), kiSortByNameZA()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_wife_age_label), kiSortByAge()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_edd_label), kiSortByEdd()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_no_ibu_label), kiSortByNoIbu()),
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
        // do nothing
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        getDefaultOptionsProvider();

        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(GONE);
        view.findViewById(R.id.register_client).setVisibility(GONE);
        clientsView.setVisibility(VISIBLE);
        clientsProgressView.setVisibility(INVISIBLE);
        initializeQueries();
    }

//    private String filterStringForAll() {
//        return "";
//    }

//    public void initializeQueries() {
//        Log.e(TAG, "initializeQueries: " );
//        try {
//            clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null,
//                    new KBClientsProvider(getActivity(), new ClientActionHandler(), context().alertService()),
//                    new CommonRepository(tableName,
//                            new String[]{"ec_kartu_ibu.is_closed", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.umur", "ec_kartu_ibu.namaSuami", "noIbu", "jenisKontrasepsi"}));
//            clientsView.setAdapter(clientAdapter);
//
//            setTablename(tableName);
//            SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
//            countQueryBuilder.SelectInitiateMainTableCounts(tableName);
//
//            mainCondition = "is_closed = 0 AND namalengkap != '' AND jenisKontrasepsi !='0'";
//
//            joinTable = "";
//            countSelect = countQueryBuilder.mainCondition(mainCondition);
//            super.CountExecute();
//
//            SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
//
//            queryBuilder.SelectInitiateMainTable(tableName, new String[]{"ec_kartu_ibu.relationalid", "ec_kartu_ibu.is_closed", "ec_kartu_ibu.details", "ec_kartu_ibu.isOutOfArea", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.umur", "ec_kartu_ibu.namaSuami", "noIbu"});
//            //   queryBuilder.customJoin("LEFT JOIN ec_anak ON ec_kartu_ibu.id = ec_anak.relational_id ");
//            mainSelect = queryBuilder.mainCondition(mainCondition);
//            Sortqueries = kiSortByNameAZ();
//
//            currentlimit = 20;
//            currentoffset = 0;
//
//            super.filterandSortInInitializeQueries();
//            CountExecute();
//            updateSearchView();
//
//            refresh();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    public void initializeQueries() {
        try {
            clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null,
                    new KBClientsProvider(getActivity(), new ClientActionHandler(), context().alertService()),
                    new CommonRepository("ec_kartu_ibu",
                            new String[]{"ec_kartu_ibu.is_closed", "namalengkap", "umur", "namaSuami", "ec_kartu_ibu.isOutOfArea"}));
            clientsView.setAdapter(clientAdapter);

            setTablename("ec_kartu_ibu");
            SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTableCounts("ec_kartu_ibu");
            //countQueryBuilder.customJoin("LEFT JOIN ec_ibu on ec_kartu_ibu.id = ec_ibu.base_entity_id");
            Log.e(TAG, "initializeQueries: " + "Not Initialized");
            mainCondition = "is_closed = 0 AND jenisKontrasepsi !='0' AND namalengkap != '' AND namalengkap IS NOT NULL";
            joinTable = "";
            countSelect = countQueryBuilder.mainCondition(mainCondition);
            Log.e(TAG, "initializeQueries: CountExecute " + countSelect);
            super.CountExecute();

            SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
            queryBuilder.SelectInitiateMainTable("ec_kartu_ibu", new String[]{"ec_kartu_ibu.relationalid", "ec_kartu_ibu.is_closed", "ec_kartu_ibu.details", "ec_kartu_ibu.isOutOfArea", "namalengkap", "umur", "namaSuami", "imagelist.imageid"});
            queryBuilder.customJoin("LEFT JOIN ec_ibu on ec_kartu_ibu.id = ec_ibu.base_entity_id LEFT JOIN ImageList imagelist ON ec_ibu.base_entity_id=imagelist.entityID ");

            mainSelect = queryBuilder.mainCondition("ec_kartu_ibu.is_closed = 0 and jenisKontrasepsi != 0 AND namalengkap != '' AND namalengkap IS NOT NULL");
            Log.e(TAG, "initializeQueries:mainSelect " + mainSelect);
            Sortqueries = kiSortByNameAZ();

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


    protected String kiSortByNameAZ() {
        return "namalengkap COLLATE NOCASE ASC";
    }

    protected String kiSortByNameZA() {
        return "namalengkap COLLATE NOCASE DESC";
    }

    protected String kiSortByAge() {
        return "umur DESC";
    }

    protected String kiSortByNoIbu() {
        return "noIbu ASC";
    }

    protected String kiSortByEdd() {
        return "htp IS NULL, htp";
    }

    @Override
    protected void onResumption() {
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries();
        }
        Log.e(TAG, "onResumption: ");

    }

    @Override
    public void onResume() {
        super.onResume();
        initializeQueries();
    }

    private void updateSearchView() {
        textWatcher(AllConstantsINA.Register.FP);
    }

    private class ClientActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
//                    FlurryFacade.logEvent("click_detail_view_on_kohort_kb_dashboard");
                    DetailFPActivity.kiclient = (CommonPersonObjectClient) view.getTag();
                    Intent intent = new Intent(getActivity(), DetailFPActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;
                case R.id.ib_fp_edit:
//                    FlurryFacade.logEvent("click_visit_button_on_kohort_kb_dashboard");
                    showFragmentDialog(((FPSmartRegisterActivity) getActivity()).new EditDialogOptionModelNew(), view.getTag());
                    break;
                default:
                    Log.d(TAG, "onClick: Default Click");
                    break;
            }
        }

    }

}