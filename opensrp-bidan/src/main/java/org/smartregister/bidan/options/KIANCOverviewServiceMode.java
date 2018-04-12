package org.smartregister.bidan.options;

import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.dialog.ServiceModeOption;

import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class KIANCOverviewServiceMode extends ServiceModeOption {

    public KIANCOverviewServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.kb_selection);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() {
                return 7;
            }

            @Override
            public int weightSum() {
                return 1000;
            }

            @Override
            public int[] weights() {
                return new int[]{244, 90, 140, 120, 170, 135, 95};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_name, R.string.header_id, R.string.header_klinis,
                        R.string.header_pemeriksaan, R.string.header_history_anc,
                        R.string.header_status, R.string.header_edit};
            }
        };
    }


}
