package org.smartregister.bidan2.servicemode;

import android.view.View;

import org.smartregister.Context;
import org.smartregister.bidan2.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.ServiceModeOption;

/**
 * Created by sid-tech on 11/23/17.
 */

public class MotherServiceMode extends ServiceModeOption {

    public MotherServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.household_entries);
    }

    @Override
    public SecuredNativeSmartRegisterActivity.ClientsHeaderProvider getHeaderProvider() {
        return new SecuredNativeSmartRegisterActivity.ClientsHeaderProvider() {
            @Override
            public int count() {
                return 7;
            }

            @Override
            public int weightSum() {
                return 1004;
            }

            @Override
            public int[] weights() {
                return new int[]{244, 75, 110, 125, 160, 205, 75};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_nama, R.string.header_id, R.string.header_obsetri,
                        R.string.header_edd, R.string.header_child, R.string.header_status_title,
                        R.string.header_edit};
            }
        };
    }

}
