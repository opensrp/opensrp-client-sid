package org.smartregister.vaksinator.option;

import org.smartregister.cursoradapter.CursorFilterOption;
import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by Dani on 22/11/2017.
 */
public class TTCommonObjectFilterOption implements CursorFilterOption {
    public final String criteria;
    public final String fieldname;
    private final String filterOptionName;

    @Override
    public String filter() {


        return " and ec_ibu.base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '"+criteria+"')";

    }



    public TTCommonObjectFilterOption(String criteria, String fieldname, String filteroptionname) {
        this.criteria = criteria;
        this.fieldname = fieldname;
        this.filterOptionName = filteroptionname;
    }

    @Override
    public String name() {
        return filterOptionName;
    }

    @Override
    public boolean filter(SmartRegisterClient client) {

        return false;
    }
}
