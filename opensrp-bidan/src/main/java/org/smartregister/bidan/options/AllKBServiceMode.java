package org.smartregister.bidan.options;

import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.ServiceModeOption;

/**
 * Created by sid-tech on 11/30/17.
 */

public class AllKBServiceMode extends ServiceModeOption {

    public AllKBServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.kb_selection);
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
                return 989;
            }

            @Override
            public int[] weights() {
                return new int[]{244, 75, 110, 140, 170, 120, 120};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_nama, R.string.header_id, R.string.header_obsetri,
                        R.string.header_kb_method, R.string.header_risk_factors, R.string.header_update_refill,
                        R.string.header_edit};
            }
        };
    }
}
