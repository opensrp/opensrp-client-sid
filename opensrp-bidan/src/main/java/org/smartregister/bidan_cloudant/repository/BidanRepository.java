package org.smartregister.bidan_cloudant.repository;

import android.content.Context;

import org.smartregister.AllConstants;
import org.smartregister.bidan_cloudant.AllConstantsINA;
import org.smartregister.bidan_cloudant.application.BidanApplication;
import org.smartregister.repository.DrishtiRepository;
import org.smartregister.repository.Repository;
import org.smartregister.util.Session;

/**
 * Created by sid-tech on 11/22/17.
 */

public class BidanRepository extends Repository {

    private final Context context;

    public BidanRepository(Context context, org.smartregister.Context opensrpContext) {
        super(context, AllConstants.DATABASE_NAME, AllConstants.DATABASE_VERSION,
                opensrpContext.session(), BidanApplication.createCommonFtsObject(),
                opensrpContext.sharedRepositoriesArray());
        this.context = context;
    }
}
