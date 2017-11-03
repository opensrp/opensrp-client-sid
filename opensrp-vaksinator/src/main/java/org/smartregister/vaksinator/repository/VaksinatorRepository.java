package org.smartregister.vaksinator.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.vaksinator.application.VaksinatorApplication;
import org.smartregister.vaksinator.util.VaksinatorConstants;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

/**
 * Created by sid on 10/12/17.
 */

public class VaksinatorRepository extends Repository {

    public VaksinatorRepository(Context context, org.smartregister.Context opensrpContext) {
        super(context, VaksinatorConstants.DATABASE_NAME, VaksinatorConstants.DATABASE_VERSION, opensrpContext.session(), VaksinatorApplication.createCommonFtsObject(), opensrpContext.sharedRepositoriesArray());
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
        onUpgrade(database, 1, VaksinatorConstants.DATABASE_VERSION);

    }



}
