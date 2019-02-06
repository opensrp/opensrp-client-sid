package org.smartregister.gizi.option;

import org.smartregister.cursoradapter.CursorFilterOption;
import org.smartregister.view.contract.SmartRegisterClient;

public class KICommonObjectFilterOption implements CursorFilterOption {
    public final String criteria;
    private final String filterOptionName;

    public KICommonObjectFilterOption(String criteria, String fieldname, String filteroptionname) {
        this.criteria = criteria;
//        String fieldName = fieldname;
        this.filterOptionName = filteroptionname;
    }

    @Override
    public String filter() {
        return "AND ec_anak.relational_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '" + criteria + "')";
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
