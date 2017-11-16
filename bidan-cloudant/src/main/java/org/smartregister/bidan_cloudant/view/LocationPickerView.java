package org.smartregister.bidan_cloudant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;

/**
 * Created by sid-tech on 11/14/17.
 */

public class LocationPickerView extends CustomFontTextView implements View.OnClickListener {

    private final Context context;

    public static final String PREF_TEAM_LOCATIONS = "PREF_TEAM_LOCATIONS";
    public static final String PREF_VILLAGE_LOCATIONS = "PREF_VILLAGE_LOCATIONS";
    public static final String PREF_TYPE_LOCATIONS = "VILLAGE"; // VILLAGE or TEAM
    public static final ArrayList<String> ALLOWED_LEVELS;
    private static final String DEFAULT_LOCATION_LEVEL = "Health Facility";

    public LocationPickerView(Context context) {
        super(context);
        this.context = context;
    }

    public LocationPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public LocationPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    public void onClick(View v) {

    }

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add("Village");
    }

    public void init(org.smartregister.Context context) {

    }
}
