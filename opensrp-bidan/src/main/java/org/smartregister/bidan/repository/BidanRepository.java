package org.smartregister.bidan.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.util.BidanConstants;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

/**
 * Created by sid on 10/12/17.
 */

public class BidanRepository extends Repository {
    public BidanRepository(Context context, org.smartregister.Context opensrpContext) {
        super(context, BidanConstants.DATABASE_NAME, BidanConstants.DATABASE_VERSION, opensrpContext.session(), BidanApplication.createCommonFtsObject(), opensrpContext.sharedRepositoriesArray());
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository.createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.address, EventClientRepository.address_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.obs, EventClientRepository.obs_column.values());
        UniqueIdRepository.createTable(database);
//        WeightRepository.createTable(database);
//        VaccineRepository.createTable(database);
        onUpgrade(database, 1, BidanConstants.DATABASE_VERSION);

    }

}
