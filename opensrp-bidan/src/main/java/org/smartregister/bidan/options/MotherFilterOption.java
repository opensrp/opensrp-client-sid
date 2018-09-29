package org.smartregister.bidan.options;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.cursoradapter.CursorFilterOption;
import org.smartregister.view.contract.SmartRegisterClient;

public class MotherFilterOption implements CursorFilterOption {
    public final String criteria;
    private final String fieldName;
    private final String filterOptionName;
    private final String tableName;

    public MotherFilterOption(String criteria, String fieldname, String filteroptionname, String tablename) {
        this.criteria = criteria;
        this.fieldName = fieldname;
        this.filterOptionName = filteroptionname;
        this.tableName = tablename;
    }

    @Override
    public String filter() {
        if (StringUtils.isNotBlank(fieldName) && "location_name".equals(fieldName)) {
            return "AND " + tableName + ".base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE key MATCH 'location_name' INTERSECT SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '" + criteria + "') ";
        } else if (StringUtils.isNotBlank(fieldName)) {
            return "AND " + tableName + ".base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE key LIKE '" + fieldName + "' AND value LIKE '" + criteria + "') ";
        } else {
            return "AND " + tableName + ".base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '" + criteria + "') ";
        }
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
