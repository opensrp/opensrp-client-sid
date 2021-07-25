package org.smartregister.bidan.options;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.cursoradapter.CursorFilterOption;
import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by Iq on 06/02/17
 */
public class ChildFilterOption extends BaseFilterOption {
    private final String filterOptionName;

    public ChildFilterOption(String criteria, String fieldname, String filteroptionname, String tablename) {
        super(criteria, fieldname, tablename);
        this.filterOptionName = filteroptionname;
    }

    @Override
    public String filter() {
        if (StringUtils.isNotBlank(fieldname) && "location_name".equals(fieldname)) {
            return "AND " + tablename + ".base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE key LIKE '" + fieldname + "' AND value LIKE '" + criteria + "') ";
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
