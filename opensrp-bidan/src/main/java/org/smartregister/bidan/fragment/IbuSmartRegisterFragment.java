package org.smartregister.bidan.fragment;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.Circle;

import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.KIbuSmartRegisterActivity;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.domain.RegisterClickables;
import org.smartregister.bidan.options.DateSort;
import org.smartregister.bidan.options.StatusSort;
import org.smartregister.bidan.provider.IbuSmartClientsProvider;
import org.smartregister.bidan.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.bidan.utils.BidanConstants;
import org.smartregister.bidan.view.LocationPickerView;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.CursorCommonObjectSort;
import org.smartregister.cursoradapter.CursorSortOption;
import org.smartregister.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.FetchStatus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sid-tech on 11/14/17.
 */

public class IbuSmartRegisterFragment extends BaseSmartRegisterFragment implements SyncStatusBroadcastReceiver.SyncStatusListener {

    private static final String TAG = IbuSmartRegisterFragment.class.getName();

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    private TextView filterCount;
    private View filterSection;
    private ImageView backButton;
    private TextView nameInitials;
    private LinearLayout btnBackToHome;
    private ProgressBar syncProgressBar;
    private int dueOverdueCount = 0;
    public LocationPickerView getLocationPickerView() {
        return getClinicSelection();
    }

    @Override
    public void onSyncStart() {

    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {

    }

    @Override
    protected void setupViews(View view) {
        super.setupViews(view);

        view.findViewById(R.id.btn_report_month).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(View.INVISIBLE);

        // Set Your customs field Id here
//        filterSection = view.findViewById(R.id.filter_selection);
//        filterSection.setOnClickListener(clientActionHandler);

//        filterCount = (TextView) view.findViewById(R.id.filter_count);
//        filterCount.setVisibility(View.GONE);
//        filterCount.setClickable(false);
//        filterCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (view.isClickable()) {
//                    filterSection.performClick();
//                }
//            }
//        });

        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
        setServiceModeViewDrawableRight(null);
        initializeQueries();
        updateSearchView();
        populateClientListHeaderView(view);


//        backButton = (ImageView) view.findViewById(R.id.back_button);
//        nameInitials = (TextView) view.findViewById(R.id.name_inits);
//        btnBackToHome = (LinearLayout) view.findViewById(R.id.btn_back_to_home);
//        syncProgressBar = (ProgressBar) view.findViewById(R.id.sync_progress_bar);
        Circle circle = new Circle();
//        syncProgressBar.setIndeterminateDrawable(circle);

        AllSharedPreferences allSharedPreferences = context().allSharedPreferences();
        String preferredName = allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM());
        if (!preferredName.isEmpty()) {
            String[] preferredNameArray = preferredName.split(" ");
            String initials = "";
            if (preferredNameArray.length > 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0)) + String.valueOf(preferredNameArray[1].charAt(0));
            } else if (preferredNameArray.length == 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0));
            }
//            nameInitials.setText(initials);
        }

        View globalSearchButton = mView.findViewById(R.id.global_search);
        globalSearchButton.setOnClickListener(clientActionHandler);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Changed the layout below to use customs layout
        View view = inflater.inflate(R.layout.smart_register_activity, container, false);
        mView = view;
//        onInitialization();
        setupViews(view);
//        onResumption();
        return view;
    }
    /**
     * Navigation Bar Options parameters
     * @return
     */
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new CursorCommonObjectSort(getResources().getString(R.string.woman_alphabetical_sort), "first_name"),
                        new DateSort(getResources().getString(R.string.woman_age_sort), "dob"),
                        new StatusSort(getResources().getString(R.string.woman_duestatus_sort)),
                        new CursorCommonObjectSort(getResources().getString(R.string.id_sort), "zeir_id")
                };
            }

            @Override
            public String searchHint() {
                return context().getStringResource(R.string.str_search_hint);
            }
        };
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        Log.e(TAG, "onResumption: " );
//        getDefaultOptionsProvider();
//        if (isPausedOrRefreshList()) initializeQueries();
    }

    private void initializeQueries() {
        String tableName = BidanConstants.MOTHER_TABLE_NAME;

        IbuSmartClientsProvider ibuscp = new IbuSmartClientsProvider(getActivity(),
                clientActionHandler, context().alertService(),
                context().commonrepository(tableName));

        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, ibuscp, context().commonrepository(tableName));
        clientsView.setAdapter(clientAdapter);

        setTablename(tableName);
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts(tableName);
        mainCondition = "is_closed = 0 and namalengkap != '' ";
        countSelect = countqueryBUilder.mainCondition(mainCondition);
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{
                tableName + ".relationalid",
                tableName + ".is_closed",
                tableName + ".details",
                tableName + ".isOutOfArea",
                tableName + ".namalengkap",
                tableName + ".umur",
                tableName + ".namaSuami",
                "noIbu"
        });
        mainSelect = queryBUilder.mainCondition(mainCondition);
//        Sortqueries = ((CursorSortOption) getDefaultOptionsProvider().sortOption()).sort();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

        updateSearchView();
        refresh();
    }

    private void updateSearchView() {
        getSearchView().removeTextChangedListener(textWatcher);
        getSearchView().addTextChangedListener(textWatcher);
    }

    private void populateClientListHeaderView(View view) {
        LinearLayout clientsHeaderLayout = (LinearLayout) view.findViewById(org.smartregister.R.id.clients_header_layout);
        clientsHeaderLayout.setVisibility(View.VISIBLE);

//        LinearLayout headerLayout = (LinearLayout) getLayoutInflater(null).inflate(R.layout.smart_register_child_header, null);
//        clientsView.addHeaderView(headerLayout);
        clientsView.setEmptyView(getActivity().findViewById(R.id.empty_view));

    }

    private String filterSelectionCondition(boolean urgentOnly) {
        String mainCondition = " (inactive != 'true' and lost_to_follow_up != 'true') AND ( ";

        if (urgentOnly) {
            return mainCondition + " ) ";
        }

        mainCondition += " or ";

        return mainCondition + " ) ";
    }

    // INNER CLASS
    private class ClientActionHandler implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            CommonPersonObjectClient client = null;
            if (v.getTag() != null && v.getTag() instanceof CommonPersonObjectClient) {
                client = (CommonPersonObjectClient) v.getTag();
            }
            RegisterClickables registerClickables = new RegisterClickables();

            switch (v.getId()) {
//                case R.id.child_profile_info_layout:
//                    ChildImmunizationActivity.launchActivity(getActivity(), client, null);
//                    break;
//                case R.id.record_weight:
//                    registerClickables.setRecordWeight(true);
//                    ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
//                    break;
//
//                case R.id.record_vaccination:
//                    registerClickables.setRecordAll(true);
//                    ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
//                    break;
//                case R.id.filter_selection:
//                    toggleFilterSelection();
//                    break;
//
//                case R.id.global_search:
//                    ((ChildSmartRegisterActivity) getActivity()).startAdvancedSearch();
//                    break;
//
//                case R.id.scan_qr_code:
//                    ((ChildSmartRegisterActivity) getActivity()).startQrCodeScanner();
//                    break;
                default:
                    break;
            }
        }
    }
}
