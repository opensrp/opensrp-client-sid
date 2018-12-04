package org.smartregister.vaksinator.option;

import org.smartregister.Context;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.vaksinator.R;
import org.smartregister.view.dialog.ServiceModeOption;

import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class TTServiceModeOption extends ServiceModeOption {

    public TTServiceModeOption(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
       return Context.getInstance().getStringResource(R.string.registerTT);
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
                        R.string.tt_status, R.string.header_edit
                        };
            }
        };
    }


}
