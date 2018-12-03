package org.smartregister.bidan.activity.shadow;

import android.os.Bundle;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowActivity;
import org.smartregister.view.activity.SecuredActivity;

/**
 * Created by sid-tech on 4/25/18
 */
@Implements(SecuredActivity.class)
public class SecuredActivityShadow extends ShadowActivity {

    @Implementation
    protected void onCreate(Bundle savedInstanceState) {
        // do nothing
    }
}
