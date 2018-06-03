package org.smartregister.gizi.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by samuelgithengi on 1/18/18.
 */

public class GiziRepositoryShadow extends GiziRepository {

    private String password = "Sample PASS";

    public GiziRepositoryShadow(Context context, org.smartregister.Context openSRPContext) {
        super(context, openSRPContext);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(password);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(password);
    }
}
