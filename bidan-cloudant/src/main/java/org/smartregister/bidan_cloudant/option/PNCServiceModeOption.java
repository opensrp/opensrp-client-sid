package org.smartregister.bidan_cloudant.option;

import org.smartregister.Context;
import org.smartregister.bidan_cloudant.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.dialog.ServiceModeOption;

import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class PNCServiceModeOption extends ServiceModeOption {

    public PNCServiceModeOption(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.vaksinator);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() { return 7; }

            @Override
            public int weightSum() {
                return 1000;
            }

            @Override
            public int[] weights() {
                return new int[]{244, 75, 125, 145, 125, 175, 122};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_name, R.string.header_id,
                        R.string.header_rencana,
                        R.string.header_komplikasi,
                        R.string.header_pnc_visits,
                        R.string.header_tanda_vital,
                        R.string.header_edit
                };
            }
        };
    }


}
