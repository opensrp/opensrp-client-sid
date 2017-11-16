package org.smartregister.gizi.giziIbu;

import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.gizi.R;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.FilterOption;

public class IbuSearchOption implements FilterOption {
    private final String criteria;

    public IbuSearchOption(String criteria) {
        this.criteria = criteria;
    }

    @Override
    public String name() {
        return Context.getInstance().applicationContext().getResources().getString(R.string.hh_search_hint);
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        boolean result = false;
        CommonPersonObjectClient currentclient = (CommonPersonObjectClient) client;
//        AllCommonsRepository allElcoRepository = new AllCommonsRepository("elco");
        if(!result) {
            if(currentclient.getDetails().get("namaLengkap") != null) {
                if (currentclient.getDetails().get("namaLengkap").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }

        return result;
    }
}
