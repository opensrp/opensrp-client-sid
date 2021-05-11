package org.smartregister.bidan.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import org.smartregister.bidan.utils.AllConstantsINA;
import org.smartregister.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by sid-tech on 11/29/17
 */

public class BaseSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {
    private static final String TAG = BaseSmartRegisterFragment.class.getName();
    private String customMainCondition;

    protected final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // do nothing
        }

        @Override
        public void onTextChanged(final CharSequence cs, int start, int before, int count) {
//            filter(cs.toString(), "", "");
            filters = cs.toString();
            joinTable = "";
//            Log.e(TAG, "onTextChanged: " + customMainCondition);
            mainCondition = customMainCondition;

            getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
            CountExecute();
            filterandSortExecute();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // do nothing
        }
    };

    protected final void textWatcher(int i) {
        switch (i) {
            case AllConstantsINA.Register.KI:
                customMainCondition = "is_closed = 0 AND namalengkap IS NOT NULL AND namalengkap != ''";
                break;
            case AllConstantsINA.Register.FP:
                customMainCondition = "is_closed = 0 AND namalengkap IS NOT NULL AND namalengkap != '' AND jenisKontrasepsi !='0'";
                break;
            case AllConstantsINA.Register.ANC:
                customMainCondition = "is_closed = 0 AND namalengkap IS NOT NULL AND namalengkap != ''";
                break;
            case AllConstantsINA.Register.PNC:
                customMainCondition = "is_closed = 0 AND namalengkap IS NOT NULL AND namalengkap != '' AND (keadaanIbu ='hidup' OR keadaanIbu IS NULL)";
                break;
            case AllConstantsINA.Register.CHILD:
                customMainCondition = "is_closed = 0 AND relational_id != '' ";
                break;
            default:
                break;
        }

        getSearchView().removeTextChangedListener(textWatcher);
        getSearchView().addTextChangedListener(textWatcher);
    }

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
        // do nothing
    }

    @Override
    protected void startRegistration() {
        // do nothing
    }

    @Override
    protected void onCreation() {
        // do nothing

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
    protected void onResumption() {
        super.onResumption();
//        Log.e(TAG, "onResumption: " + getResources().getConfiguration().locale);
//        LoginActivity.setLanguage();
    }

    protected String kiSortByNameAZ() {
        return "namalengkap ASC";
    }

    protected String kiSortByNameZA() {
        return "namalengkap DESC";
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

}
