package org.smartregister.gizi.repository;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.gizi.application.GiziApplication;
import org.smartregister.gizi.facial.repository.ImageRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

import util.GiziConstants;

public class GiziRepository extends Repository {

    private static final String TAG = GiziRepository.class.getCanonicalName();
    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;

    public GiziRepository(Context context, org.smartregister.Context opensrpContext) {
        super(context, GiziConstants.DATABASE_NAME, GiziConstants.DATABASE_VERSION, opensrpContext.session(), GiziApplication.createCommonFtsObject(), opensrpContext.sharedRepositoriesArray());
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository.createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
//        EventClientRepository.createTable(database, EventClientRepository.Table.address, EventClientRepository.address_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());
//        EventClientRepository.createTable(database, EventClientRepository.Table.obs, EventClientRepository.obs_column.values());
        ImageRepository.createTable(database);
        onUpgrade(database, 1, GiziConstants.DATABASE_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(GiziRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");


    }


    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(GiziApplication.getInstance().getPassword());
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(GiziApplication.getInstance().getPassword());
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
