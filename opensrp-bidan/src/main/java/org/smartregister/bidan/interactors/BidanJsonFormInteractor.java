package org.smartregister.bidan.interactors;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.bidan.widgets.PathDatePickerFactory;
import org.smartregister.bidan.widgets.PathEditTextFactory;

public class BidanJsonFormInteractor extends JsonFormInteractor {

    private static final JsonFormInteractor INSTANCE = new BidanJsonFormInteractor();

    private BidanJsonFormInteractor() {
        super();
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new PathEditTextFactory());
        map.put(JsonFormConstants.DATE_PICKER, new PathDatePickerFactory());
//        map.put(JsonFormConstants.LABEL, new PathCalculateLabelFactory());
    }

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }
}
