package org.smartregister.bidan.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by sid-tech on 11/14/17.
 */

public class Config {

    public String getCredential(String acc, Context context) throws IOException {
        Properties prop = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        prop.load(inputStream);
        return prop.getProperty(acc);
    }
}
