package org.smartregister.bidan.activity.shadow;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.smartregister.Context;
import org.smartregister.view.fragment.SecuredFragment;

/**
 * Created by sid-tech on 4/25/18
 */

@Implements(SecuredFragment.class)
public class SecuredFragmentShadow extends Shadow {
    public static Context mContext;

    @Implementation
    protected Context context()
    {
        return mContext;
    }
}
