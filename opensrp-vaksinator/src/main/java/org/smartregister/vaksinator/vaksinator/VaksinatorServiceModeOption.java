package org.smartregister.vaksinator.vaksinator;

import android.view.View;

import org.smartregister.Context;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.vaksinator.R;
import org.smartregister.view.contract.ANCSmartRegisterClient;
import org.smartregister.view.contract.ChildSmartRegisterClient;
import org.smartregister.view.contract.FPSmartRegisterClient;
import org.smartregister.view.contract.pnc.PNCSmartRegisterClient;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.viewholder.NativeANCSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativeChildSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativeFPSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativePNCSmartRegisterViewHolder;

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

    @Override
    public void setupListView(ChildSmartRegisterClient client,
                              NativeChildSmartRegisterViewHolder viewHolder,
                              View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(ANCSmartRegisterClient client, NativeANCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }


}
