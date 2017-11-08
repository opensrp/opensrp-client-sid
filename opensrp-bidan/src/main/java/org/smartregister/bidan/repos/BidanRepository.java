package org.smartregister.bidan.repos;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.repository.UniqueIdRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

import util.BidanConstants;

public class BidanRepository extends Repository {

    private static final String TAG = BidanRepository.class.getCanonicalName();
    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;
    private final Context context;


    public BidanRepository(Context context, org.smartregister.Context opensrpContext) {
        super(context, BidanConstants.DATABASE_NAME, BidanConstants.DATABASE_VERSION, opensrpContext.session(), BidanApplication.createCommonFtsObject(), opensrpContext.sharedRepositoriesArray());
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository.createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.address, EventClientRepository.address_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());
//        EventClientRepository.createTable(database, EventClientRepository.Table.obs, EventClientRepository.obs_column.values());
        UniqueIdBidanRepository.createTable(database);

        onUpgrade(database, 1, BidanConstants.DATABASE_VERSION);

    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(BidanApplication.getInstance().getPassword());
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(BidanApplication.getInstance().getPassword());
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Log.e(TAG, "Database Error. " + e.getMessage());
            return null;
        }

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }

}
