package org.smartregister.bidan.fragment;

import android.view.View;

import org.smartregister.bidan.R;
import org.smartregister.bidan.options.DateSort;
import org.smartregister.bidan.options.StatusSort;
import org.smartregister.bidan.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.bidan.view.LocationPickerView;
import org.smartregister.cursoradapter.CursorCommonObjectSort;
import org.smartregister.domain.FetchStatus;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;

/**
 * Created by sid-tech on 11/14/17.
 */

public class IbuSmartRegisterFragment extends BaseSmartRegisterFragment implements SyncStatusBroadcastReceiver.SyncStatusListener {

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

    }

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
                        new DateSort("Age", "dob"),
                        new StatusSort("Due Status"),
                        new CursorCommonObjectSort(getResources().getString(R.string.id_sort), "zeir_id")
                };
            }

            @Override
            public String searchHint() {
                return context().getStringResource(R.string.str_search_hint);
            }
        };
    }

}
