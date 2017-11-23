package org.smartregister.bidan2.repository;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.bidan2.application.BidanApplication;
import org.smartregister.bidan2.utils.BidanConstants;
import org.smartregister.repository.Repository;

/**
 * Created by sid-tech on 11/23/17.
 */

public class BidanRepository extends Repository {

    private final Context context;

    public BidanRepository(Context context, org.smartregister.Context opensrpContext) {
        super(context, BidanConstants.DATABASE_NAME, BidanConstants.DATABASE_VERSION, opensrpContext.session(),
                BidanApplication.createCommonFtsObject(), opensrpContext.sharedRepositoriesArray());
        this.context = context;
    }


}
