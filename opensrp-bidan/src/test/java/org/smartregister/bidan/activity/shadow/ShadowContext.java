package org.smartregister.bidan.activity.shadow;

import org.mockito.Mock;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.smartregister.Context;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.repository.Repository;
import org.smartregister.service.PendingFormSubmissionService;
import org.smartregister.view.controller.ANMController;
import org.smartregister.view.controller.ANMLocationController;

/**
 * Created by sid-tech on 4/24/18
 */

@Implements(Context.class)
public class ShadowContext extends Shadow {

    @Mock
    Repository repository;
    @Mock
    ANMController anmController;
    @Mock
    AllEligibleCouples allEligibleCouples;
    @Mock
    AllBeneficiaries allBeneficiaries;
    @Mock
    ANMLocationController anmLocationController;
    @Mock
    private PendingFormSubmissionService pendingFormSubmissionService;

    @Implementation
    public Boolean IsUserLoggedOut() {
        return false;
    }

}
