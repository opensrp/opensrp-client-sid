package org.smartregister.vaksinator.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by wildan on 10/30/17.
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
