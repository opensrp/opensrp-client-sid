package org.smartregister.vaksinator.vaksinator;

import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.vaksinator.R;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.FilterOption;

public class VaksinatorSearchOption implements FilterOption {
    private final String criteria;

    public VaksinatorSearchOption(String criteria) {
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
            if(currentclient.getDetails().get("nama_bayi") != null) {
                if (currentclient.getDetails().get("nama_bayi").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }
        if(!result) {
            if(currentclient.getDetails().get("namaIbu") != null) {
                if (currentclient.getDetails().get("namaIbu").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }
        if(!result) {
            if(currentclient.getDetails().get("nama_orang_tua") != null) {
                if (currentclient.getDetails().get("nama_orang_tua").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }
        if(!result) {
            if(currentclient.getDetails().get("village") != null) {
                if (currentclient.getDetails().get("village").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }
        if(!result) {
            if(currentclient.getDetails().get("dusun") != null) {
                if (currentclient.getDetails().get("dusun").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }

        return result;
    }
}
