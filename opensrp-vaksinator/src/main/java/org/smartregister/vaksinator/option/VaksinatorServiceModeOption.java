package org.smartregister.vaksinator.option;

import org.smartregister.Context;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.vaksinator.R;
import org.smartregister.view.dialog.ServiceModeOption;

import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class VaksinatorServiceModeOption extends ServiceModeOption {

    public VaksinatorServiceModeOption(SmartRegisterClientsProvider provider) {
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
            public int count() { return 9; }

            @Override
            public int weightSum() {
                return 176;
            }

            @Override
            public int[] weights() {
                return new int[]{18,32,17,16,20,20,25,20,22};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.space,
                        R.string.hh_profile,
                        R.string.hb1,
                        R.string.polio1,
                        R.string.polio2,
                        R.string.polio3,
                        R.string.polio4,
                        R.string.measles,
                        R.string.space
                };
            }
        };
    }


}
