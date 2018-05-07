package org.smartregister.bidan.options;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.cursoradapter.CursorFilterOption;
import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by Iq on 06/02/17
 */
public class ChildFilterOption implements CursorFilterOption {
    private final String criteria;
    private final String fieldname;
    private final String filterOptionName;
    private final String tablename;

    public ChildFilterOption(String criteria, String fieldname, String filteroptionname, String tablename) {
        this.criteria = criteria;
        this.fieldname = fieldname;
        this.filterOptionName = filteroptionname;
        this.tablename = tablename;
    }

    @Override
    public String filter() {
        if (StringUtils.isNotBlank(fieldname) && !"location_name".equals(fieldname)) {
            return "AND " + tablename + ".base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE key MATCH '" + fieldname + "' INTERSECT SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '" + criteria + "') ";
        } else {
            return "AND " + tablename + ".base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '" + criteria + "' ) ";
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
