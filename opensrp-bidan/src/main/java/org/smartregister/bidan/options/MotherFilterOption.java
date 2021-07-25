package org.smartregister.bidan.options;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.cursoradapter.CursorFilterOption;
import org.smartregister.view.contract.SmartRegisterClient;

public class MotherFilterOption extends BaseFilterOption {
    private final String filterOptionName;

    public MotherFilterOption(String criteria, String fieldname, String filteroptionname, String tablename) {
        super(criteria, fieldname, tablename);
        this.filterOptionName = filteroptionname;
    }

    @Override
    public String filter() {
        if (StringUtils.isNotBlank(fieldname) && "location_name".equals(fieldname)) {
            return "AND " + tablename + ".base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE key MATCH 'location_name' INTERSECT SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '" + criteria + "') ";
        } else
            return super.filter();
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
