package org.smartregister.bidan.options;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.cursoradapter.CursorFilterOption;

public abstract class BaseFilterOption implements CursorFilterOption {
    protected final String criteria;
    protected final String fieldname;
    protected final String tablename;

    protected BaseFilterOption(String criteria, String fieldname, String tablename) {
        this.criteria = criteria;
        this.fieldname = fieldname;
        this.tablename = tablename;
    }

    @Override
    public String filter() {
        if (StringUtils.isNotBlank(fieldname)) {
            return "AND " + tablename + ".base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE key LIKE '" + fieldname + "' AND value LIKE '" + criteria + "') ";
        } else {
            return "AND " + tablename + ".base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '" + criteria + "') ";
        }
    }
}
