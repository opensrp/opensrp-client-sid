package org.smartregister.bidan.options;

import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.ServiceModeOption;

import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class AnakOverviewServiceMode extends ServiceModeOption {

    public AnakOverviewServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.child_title);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new SecuredNativeSmartRegisterActivity.ClientsHeaderProvider() {
            @Override
            public int count() {
                return 6;
            }

            @Override
            public int weightSum() {
                return 100;
            }

            @Override
            public int[] weights() {
                return new int[]{26, 16, 16, 16, 18, 8};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_name,
                        R.string.header_dok_persalinan,
                        R.string.header_neonatal,
                        R.string.header_immunizations,
                        R.string.header_birth_status,
                        R.string.header_edit};
            }
        };
    }

}
