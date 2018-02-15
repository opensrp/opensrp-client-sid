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

import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.BaseRegisterActivity;
import org.smartregister.bidan.activity.DetailPNCActivity;
import org.smartregister.bidan.activity.PNCSmartRegisterActivity;
import org.smartregister.bidan.options.KIPNCOverviewServiceMode;
import org.smartregister.bidan.options.MotherFilterOption;
import org.smartregister.bidan.provider.PNCClientsProvider;
import org.smartregister.bidan.utils.AllConstantsINA;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.CursorCommonObjectFilterOption;
import org.smartregister.cursoradapter.CursorCommonObjectSort;
import org.smartregister.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.util.StringUtil;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.ECClient;
import org.smartregister.view.controller.VillageController;
import org.smartregister.view.dialog.AllClientsFilter;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.DialogOptionMapper;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.NameSort;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.view.View.INVISIBLE;

/**
 * Created by sid-tech on 11/30/17.
 */

public class PNCSmartRegisterFragment extends BaseSmartRegisterFragment {
    private static final String TAG = PNCSmartRegisterFragment.class.getName();
    //    WD
    public static String criteria;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    String tableName = "ec_kartu_ibu";
    String tableEcPnc = "ec_pnc";

    public static String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    @Override
    protected void onCreation() {
        //
    }

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
                ArrayList<DialogOption> dialogOptionslist = new ArrayList<DialogOption>();

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

                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label), KiSortByNameAZ()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label_reverse), KiSortByNameZA()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_wife_age_label), KiSortByAge()),
                        //    new CursorCommonObjectSort(getResources().getString(R.string.sort_by_edd_label),KiSortByEdd()),
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

    private DialogOption[] getEditOptions() {
        return ((BaseRegisterActivity) getActivity()).getEditOptions();
    }


    @Override
    public void setupViews(View view) {
        getDefaultOptionsProvider();

        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.register_client).setVisibility(View.GONE);
        view.findViewById(R.id.service_mode_selection).setVisibility(View.GONE);
        view.findViewById(R.id.register_client).setVisibility(View.GONE);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
//        list.setBackgroundColor(Color.RED);
        initializeQueries(getCriteria());
    }

    private String filterStringForAll() {
        return "";
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initializeQueries(String s){
        try {
            PNCClientsProvider kiscp = new PNCClientsProvider(getActivity(),clientActionHandler,context().alertService());
            clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, kiscp, new CommonRepository(tableEcPnc,new String []{"ec_kartu_ibu.namalengkap", "ec_kartu_ibu.namaSuami"}));
            clientsView.setAdapter(clientAdapter);

            setTablename(tableEcPnc);
            SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
            countqueryBUilder.SelectInitiateMainTableCounts(tableEcPnc);
            countqueryBUilder.customJoin("LEFT JOIN ec_kartu_ibu on ec_kartu_ibu.id = ec_pnc.id");

            mainCondition = "is_closed = 0 AND (keadaanIbu ='hidup' OR keadaanIbu IS NULL) AND namalengkap != '' AND namalengkap IS NOT NULL";

            joinTable = "";
            countSelect = countqueryBUilder.mainCondition(mainCondition);
            super.CountExecute();

            SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
            queryBUilder.SelectInitiateMainTable(tableEcPnc, new String[]{"ec_pnc.relationalid", "ec_pnc.details",  "ec_kartu_ibu.namalengkap","ec_kartu_ibu.namaSuami","imagelist.imageid"});
            queryBUilder.customJoin("LEFT JOIN ec_kartu_ibu on ec_kartu_ibu.id = ec_pnc.id LEFT JOIN ImageList imagelist ON ec_pnc.id=imagelist.entityID");
            mainSelect = queryBUilder.mainCondition("ec_kartu_ibu.is_closed = 0 and (keadaanIbu ='hidup' OR keadaanIbu IS NULL) ");

            Sortqueries = KiSortByNameAZ();

            currentlimit = 20;
            currentoffset = 0;

            super.filterandSortInInitializeQueries();
            CountExecute();
            updateSearchView();
            refresh();
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
        }

    }

    private String KiSortByNameAZ() {
        return " namalengkap ASC";
    }

    private String KiSortByNameZA() {
        return " namalengkap DESC";
    }

    private String KiSortByAge() {
        return " umur DESC";
    }

    private String KiSortByNoIbu() {
        return " noIbu ASC";
    }

    private String KiSortByEdd() {
        return " htp IS NULL, htp";
    }

    @Override
    protected void onResumption() {
//        super.onResumption();
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries("!");
        }
        updateSearchView();
    }

    private void updateSearchView() {
        textWatcher(AllConstantsINA.Register.PNC);
    }

    public void addChildToList(ArrayList<DialogOption> dialogOptionslist, Map<String, TreeNode<String, Location>> locationMap) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if (entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist, entry.getValue().getChildren());

            } else {
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                dialogOptionslist.add(new MotherFilterOption(name, "location_name", name, tableName));

            }
        }
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
                case R.id.ib_btn_edit:
//                    //FlurryFacade.logEvent("click_visit_button_on_kohort_pnc_dashboard");
//                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    showFragmentDialog(((PNCSmartRegisterActivity) getActivity()).new EditDialogOptionModelNew(), view.getTag());

                    break;
            }
        }

        private void showProfileView(ECClient client) {
            navigationController.startEC(client.entityId());
        }
    }

}