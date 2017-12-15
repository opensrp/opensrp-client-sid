package org.smartregister.bidan.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.BaseRegisterActivity;
import org.smartregister.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by sid-tech on 11/29/17.
 */

public class BaseSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {
    protected final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(final CharSequence cs, int start, int before, int count) {
            filter(cs.toString(), "", "");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private String criteria;

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
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
    protected void startRegistration() {

    }

    @Override
    protected void onCreation() {

    }

    protected void filter(String filterString, String joinTableString, String mainConditionString) {
        filters = filterString;
        joinTable = joinTableString;
        mainCondition = mainConditionString;
        getSearchCancelView().setVisibility(isEmpty(filterString) ? INVISIBLE : VISIBLE);
        CountExecute();
        filterandSortExecute();
    }

    @Override
    public void setupViews(View view) {

        super.setupViews(view);

        getDefaultOptionsProvider();

        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(View.GONE);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
        initializeQueries(getCriteria());
    }

    public void initializeQueries(String s) {
    }

    @Override
    protected void onResumption() {
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries("");
        }
//        try {
//            LoginActivity.setLanguage();
//        } catch (Exception ignored) {
//
//        }

    }

    public String getCriteria() {
        return criteria;
    }

    protected DialogOption[] getEditOptions() {
        return ((BaseRegisterActivity) getActivity()).getEditOptions();
    }

}
