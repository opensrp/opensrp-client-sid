package org.smartregister.bidan.fragment.mock;

import android.content.Context;

import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.smartregister.DristhiConfiguration;
import org.smartregister.ssl.OpensrpSSLHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

@Implements(OpensrpSSLHelper.class)
public class ShadowOpensrpSSLHelper extends Shadow {

    public void __constructor__(Context context_, DristhiConfiguration configuration_) {

    }

    public SocketFactory getSslSocketFactoryWithOpenSrpCertificate() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource("sid.jks");
        File file = new File(resource.getPath());
        InputStream inputStream = new FileInputStream(file);

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(inputStream,"Satu2345".toCharArray());
        SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
//        inputStream.close();
        return socketFactory;
    }
}
