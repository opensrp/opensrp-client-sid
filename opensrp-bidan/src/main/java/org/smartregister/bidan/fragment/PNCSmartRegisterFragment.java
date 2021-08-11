package org.smartregister.bidan.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;

import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.DetailPNCActivity;
import org.smartregister.bidan.activity.PNCSmartRegisterActivity;
import org.smartregister.bidan.options.KIPNCOverviewServiceMode;
import org.smartregister.bidan.options.MotherFilterOption;
import org.smartregister.bidan.provider.PNCClientsProvider;
import org.smartregister.bidan.utils.AllConstantsINA;
import org.smartregister.bidan.utils.BidanSmartRegisterQueryBuilder;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.CursorCommonObjectFilterOption;
import org.smartregister.cursoradapter.CursorCommonObjectSort;
import org.smartregister.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.util.StringUtil;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.AllClientsFilter;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.NameSort;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class PNCSmartRegisterFragment extends BaseSmartRegisterFragment {
    private static final String TAG = PNCSmartRegisterFragment.class.getName();
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
//    public static String criteria;

//    public static String getCriteria() {
//        return criteria;
//    }

//    public void setCriteria(String criteria) {
//        PNCSmartRegisterFragment.criteria = criteria;
//    }

//    @Override
//    protected void onCreation() {
//        //
//    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new KIPNCOverviewServiceMode(clientsProvider());
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
                return Context.getInstance().getStringResource(R.string.pnc_register_title_in_short);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                //FlurryFacade.logEvent("click_filter_option_on_kohort_pnc_dashboard");
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
                //FlurryFacade.logEvent("click_sorting_option_on_kohort_pnc_dashboard");
                return new DialogOption[]{
//                        new HouseholdCensusDueDateSort(),

                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label), kiSortByNameAZ()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label_reverse), kiSortByNameZA()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_wife_age_label), kiSortByAge()),
                        //    new CursorCommonObjectSort(getResources().getString(R.string.sort_by_edd_label),kiSortByEdd()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_no_ibu_label), kiSortByNoIbu()),
                };
            }

            @Override
            public String searchHint() {
                return getResources().getString(R.string.hh_search_hint);
            }
        };
    }

//    @Override
//    protected SmartRegisterClientsProvider clientsProvider() {
//        return null;
//    }


    @Override
    public void setupViews(View view) {
        getDefaultOptionsProvider();

        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.register_client).setVisibility(GONE);
        view.findViewById(R.id.service_mode_selection).setVisibility(GONE);
        view.findViewById(R.id.register_client).setVisibility(GONE);
        clientsView.setVisibility(VISIBLE);
        clientsProgressView.setVisibility(INVISIBLE);
//        list.setBackgroundColor(Color.RED);
        initializeQueries();
    }

    private String filterStringForAll() {
        return "";
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initializeQueries() {
//        Log.d(TAG, "initializeQueries: key " + s);
        try {
//            PNCClientsProvider kiscp = new PNCClientsProvider(getActivity(), clientActionHandler, context().alertService());
            String tableEcPnc = "ec_pnc";
            clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, new PNCClientsProvider(getActivity(), clientActionHandler, context().alertService()), new CommonRepository(tableEcPnc, new String[]{"ec_kartu_ibu.namalengkap", "ec_kartu_ibu.namaSuami"}));
            clientsView.setAdapter(clientAdapter);

            setTablename("ec_pnc");
            SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
            countqueryBUilder.SelectInitiateMainTableCounts(tableEcPnc);
            countqueryBUilder.customJoin("LEFT JOIN ec_kartu_ibu on ec_kartu_ibu.id = ec_pnc.id");

            mainCondition = "is_closed = 0 AND (keadaanIbu ='hidup' OR keadaanIbu IS NULL) AND namalengkap != '' AND namalengkap IS NOT NULL";

            joinTable = "";
            countSelect = countqueryBUilder.mainCondition("ec_pnc." + mainCondition);
            super.CountExecute();

            SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
            queryBUilder.SelectInitiateMainTable(tableEcPnc, new String[]{"ec_pnc.relationalid", "ec_pnc.details", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.namaSuami", "imagelist.imageid"});
            queryBUilder.customJoin("LEFT JOIN ec_kartu_ibu on ec_kartu_ibu.id = ec_pnc.id LEFT JOIN ImageList imagelist ON ec_pnc.id=imagelist.entityID");
            mainSelect = queryBUilder.mainCondition("ec_pnc.is_closed = 0 and (keadaanIbu ='hidup' OR keadaanIbu IS NULL) ");

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

    private static final String COUNT = "count_execute";

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case LOADER_ID:
                // Returns a new CursorLoader
                return new CursorLoader(getActivity()) {
                    @Override
                    public Cursor loadInBackground() {
                        // Count query
                        if (args != null && args.getBoolean(COUNT)) {
                            CountExecute();
                        }

                        // Select register query
                        String query = filterandSortQuery();
                        return commonRepository().rawCustomQueryForAdapter(query);
                    }
                };
            default:
                // An invalid id was passed in
                return null;
        }

    }

    private String filterandSortQuery() {
        BidanSmartRegisterQueryBuilder sqb = new BidanSmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb
                        .searchQueryFts(tablename, joinTable, mainCondition, filters, Sortqueries,
                                currentlimit, currentoffset);
                List<String> ids = commonRepository().findSearchIds(sql);
                query = sqb.toStringFts(ids, tablename, CommonRepository.ID_COLUMN,
                        Sortqueries);

                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(sqb.addlimitandOffset(query, currentlimit, currentoffset));

            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString(), e);
        }

        return query;
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

    @Override
    protected void onResumption() {
//        super.onResumption();
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries();
        }
        updateSearchView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeQueries();
    }

    private void updateSearchView() {
        textWatcher(AllConstantsINA.Register.PNC);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
//                    //FlurryFacade.logEvent("click_detail_view_on_kohort_pnc_dashboard");
                    DetailPNCActivity.pncclient = (CommonPersonObjectClient) view.getTag();
                    Intent intent = new Intent(getActivity(), DetailPNCActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;
                case R.id.ib_pnc_edit:
//                    //FlurryFacade.logEvent("click_visit_button_on_kohort_pnc_dashboard");
//                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    showFragmentDialog(((PNCSmartRegisterActivity) getActivity()).new EditDialogOptionModelNew(), view.getTag());
                    break;

                default:
                    break;
            }
        }

    }

}