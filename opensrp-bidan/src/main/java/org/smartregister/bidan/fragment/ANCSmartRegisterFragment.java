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
import org.smartregister.bidan.activity.ANCSmartRegisterActivity;
import org.smartregister.bidan.activity.DetailANCActivity;
import org.smartregister.bidan.options.KIANCOverviewServiceMode;
import org.smartregister.bidan.options.MotherFilterOption;
import org.smartregister.bidan.provider.ANCClientsProvider;
import org.smartregister.bidan.utils.AllConstantsINA;
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
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ANCSmartRegisterFragment extends BaseSmartRegisterFragment {

    private static final String TAG = ANCSmartRegisterFragment.class.getName();
    //    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
//    private ANCClientsProvider kiSCP = new ANCClientsProvider(getActivity(), new ClientActionHandler(), context().alertService());
    private String ecKiTableName = "ec_kartu_ibu";

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

                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_all_label), ""));

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

//    @Override
//    protected SmartRegisterClientsProvider clientsProvider() {
//        return null;
//    }

    @Override
    public void setupViews(View view) {
        getDefaultOptionsProvider();
        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(GONE);
        view.findViewById(R.id.register_client).setVisibility(GONE);
        clientsView.setVisibility(VISIBLE);
        clientsProgressView.setVisibility(INVISIBLE);
        initializeQueries();
        Log.e(TAG, "setupViews: " + getResources().getConfiguration().locale);
    }

    public void initializeQueries() {
        Log.e(TAG, "initializeQueries: ");
        String tableEcIbu = "ec_ibu";

        try {

            clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null,
                    new ANCClientsProvider(getActivity(), new ClientActionHandler(), context().alertService()),
                    new CommonRepository(tableEcIbu, new String[]{"ec_ibu.is_closed", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.namaSuami"}));
            clientsView.setAdapter(clientAdapter);

            setTablename("ec_kartu_ibu");
            SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTableCounts(tableEcIbu);
            countQueryBuilder.customJoin("LEFT JOIN ec_kartu_ibu on ec_kartu_ibu.id = ec_ibu.id");

//            mainCondition = "is_closed = 0";
            mainCondition = "is_closed = 0 AND namalengkap != '' AND namalengkap IS NOT NULL";

            joinTable = "";
            countSelect = countQueryBuilder.mainCondition(mainCondition);
            super.CountExecute();

            SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
            queryBuilder.SelectInitiateMainTable(tableEcIbu, new String[]{"ec_ibu.relationalid", "ec_ibu.is_closed", "ec_ibu.details", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.namaSuami", "imagelist.imageid"});
            queryBuilder.customJoin("LEFT JOIN ec_kartu_ibu on ec_kartu_ibu.id = ec_ibu.id LEFT JOIN ImageList imagelist ON ec_ibu.id=imagelist.entityID");
            mainSelect = queryBuilder.mainCondition("ec_kartu_ibu.is_closed = 0 AND namalengkap != '' AND namalengkap IS NOT NULL");

            Sortqueries = kiSortByNameAZ();

            currentlimit = 20;
            currentoffset = 0;

            super.filterandSortInInitializeQueries();
            CountExecute();
            updateSearchView();
            refresh();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "initializeQueries: " + e.getCause());
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
        Log.e(TAG, "onResumption: " + getResources().getConfiguration().locale);
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeQueries();
    }

    private void updateSearchView() {
        textWatcher(AllConstantsINA.Register.ANC);
    }

    public void addChildToList(ArrayList<DialogOption> dialogOptionslist, Map<String, TreeNode<String, Location>> locationMap) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if (entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist, entry.getValue().getChildren());

            } else {
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                dialogOptionslist.add(new MotherFilterOption(name, "location_name", name, ecKiTableName));

            }
        }
    }

    public class ClientActionHandler implements View.OnClickListener {
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

                case R.id.btn_anc_edit:
                    DetailANCActivity.ancClient = (CommonPersonObjectClient) view.getTag();
//                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    showFragmentDialog(((ANCSmartRegisterActivity) getActivity()).new EditDialogOptionModelNew(), view.getTag());
                    break;
                default:
                    break;
            }
        }

    }

}