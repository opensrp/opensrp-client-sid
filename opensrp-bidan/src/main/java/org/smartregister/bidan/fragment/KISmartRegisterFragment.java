package org.smartregister.bidan.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.BaseRegisterActivity;
import org.smartregister.bidan.activity.DetailMotherActivity;
import org.smartregister.bidan.options.AllKartuIbuServiceMode;
import org.smartregister.bidan.options.MotherFilterOption;
import org.smartregister.bidan.provider.KIClientsProvider;
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
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.dialog.NameSort;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class KISmartRegisterFragment extends BaseSmartRegisterFragment {

    private static final String TAG = KISmartRegisterFragment.class.getName();
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new AllKartuIbuServiceMode(clientsProvider());
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
                return Context.getInstance().getStringResource(R.string.ki_register_title_in_short);
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

                String locationjson = context().anmLocationController().get();
                LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);

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

    @Override
    public void startRegistration() {

//        String uniqueIdJson = LoginActivity.generator.uniqueIdController().getUniqueIdJson();
//        if (uniqueIdJson == null || uniqueIdJson.isEmpty()) {
//            Toast.makeText(getActivity(), "no unique id", Toast.LENGTH_LONG).show();
//            return;
//        }

        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        String locationDialogTAG = "locationDialogTAG";
        Fragment prev = getActivity().getFragmentManager().findFragmentByTag(locationDialogTAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        LocationSelectorDialogFragment
                .newInstance((BaseRegisterActivity) getActivity(),
                        ((BaseRegisterActivity) getActivity()).new EditDialogOptionModelNew(),
                        context().anmLocationController().get(),
                        "kartu_ibu_registration")
                .show(ft, locationDialogTAG);
    }

    @Override
    public void setupViews(View view) {
        getDefaultOptionsProvider();
        super.setupViews(view);
        Log.d(TAG, "setupViews: Init");

        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(GONE);
        clientsView.setVisibility(VISIBLE);
        clientsProgressView.setVisibility(INVISIBLE);
        initializeQueries();

//        LoginActivity.setLanguage();
    }

    @Override
    protected void onResumption() {
        getDefaultOptionsProvider();

        if (isPausedOrRefreshList()) {
            initializeQueries();
        }

//        try {
//            LoginActivity.setLanguage();
//        } catch (Exception ignored) {
//
//        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initializeQueries();
    }

    protected String filterStringForAll() {
        return "";
    }

    public void initializeQueries() {

        try {
            KIClientsProvider kiscp = new KIClientsProvider(getActivity(), clientActionHandler, context().alertService());

            clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, kiscp,
                    new CommonRepository("ec_kartu_ibu", new String[]{"ec_kartu_ibu.is_closed", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.umur", "ec_kartu_ibu.namaSuami", "noIbu"}));
            clientsView.setAdapter(clientAdapter);

            setTablename("ec_kartu_ibu");
            SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
            countqueryBUilder.SelectInitiateMainTableCounts("ec_kartu_ibu");

            mainCondition = "is_closed = 0 AND namalengkap IS NOT NULL AND namalengkap != '' ";

            joinTable = "";
            countSelect = countqueryBUilder.mainCondition(mainCondition);
            super.CountExecute();

            SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();

            queryBUilder.SelectInitiateMainTable("ec_kartu_ibu", new String[]{"ec_kartu_ibu.relationalid", "ec_kartu_ibu.is_closed", "ec_kartu_ibu.details", "ec_kartu_ibu.isOutOfArea", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.umur", "ec_kartu_ibu.namaSuami", "noIbu"});
            //   queryBUilder.customJoin("LEFT JOIN ec_anak ON ec_kartu_ibu.id = ec_anak.relational_id ");
            mainSelect = queryBUilder.mainCondition(mainCondition);
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

    public void updateSearchView() {
        textWatcher(AllConstantsINA.Register.KI);
    }

    public void addChildToList(ArrayList<DialogOption> dialogOptionslist, Map<String, TreeNode<String, Location>> locationMap) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if (entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist, entry.getValue().getChildren());

            } else {
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                dialogOptionslist.add(new MotherFilterOption(name, "address1", name, "ec_kartu_ibu"));

            }
        }
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
//                    FlurryFacade.logEvent("click_detail_view_on_kohort_ibu_dashboard");
                    DetailMotherActivity.motherClient = (CommonPersonObjectClient) view.getTag();
                    Intent intent = new Intent(getActivity(), DetailMotherActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;

                case R.id.ib_cm_edit:
                    showFragmentDialog(((BaseRegisterActivity) getActivity()).new EditDialogOptionModelNew(), view.getTag());
                    break;

                default:
                    break;
            }
        }

    }

}