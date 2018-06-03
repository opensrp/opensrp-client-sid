package org.smartregister.bidan.shadow;

import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.smartregister.bidan.fragment.HomeFragment;

/**
 * Created by ndegwamartin on 13/11/2017.
 */

@Implements(HomeFragment.class)
public class RegisterFragmentShadow extends Shadow {
}
