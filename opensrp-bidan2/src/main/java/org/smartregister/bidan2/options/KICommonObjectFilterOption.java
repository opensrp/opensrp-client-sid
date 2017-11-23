package org.smartregister.bidan2.options;

import org.smartregister.cursoradapter.CursorFilterOption;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.DialogOption;

/**
 * Created by sid-tech on 11/23/17.
 */

public class KICommonObjectFilterOption implements CursorFilterOption {

    public final String criteria;
    public final String fieldname;
    private final String filterOptionName;
    private final String tablename;

    public KICommonObjectFilterOption(String criteria, String fieldname,String filteroptionname,String tablename) {
        this.criteria = criteria;
        this.fieldname = fieldname;
        this.filterOptionName = filteroptionname;
        this.tablename = tablename;
    }

    @Override
    public String filter() {
        return null;
    }

    @Override
    public boolean filter(SmartRegisterClient smartRegisterClient) {
        return false;
    }

    @Override
    public String name() {
        return null;
    }
}
