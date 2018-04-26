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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Implements(OpensrpSSLHelper.class)
public class ShadowOpensrpSSLHelper extends Shadow {

    private KeyStore systemCAKS;
    private File file;

    public void __constructor__(Context context_, DristhiConfiguration configuration_) {

    }


    public SocketFactory getSslSocketFactoryWithOpenSrpCertificate() throws KeyStoreException, CertificateException,
            IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource("sid.jks");
        file = new File(resource.getPath());
        InputStream inputStream = new FileInputStream(file);

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(inputStream, "Satu2345".toCharArray());
//        SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
        SSLSocketFactory socketFactory = new SSLSocketFactory(getSystemCAKS());
        inputStream.close();
        return socketFactory;
    }

    public KeyStore getSystemCAKS() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        KeyStore keyStore = KeyStore.getInstance("AndroidKeystore");

        keyStore.load(new FileInputStream(System.getProperty("java.home") + "/lib/security/cacerts"), "android".toCharArray());
        return systemCAKS;
    }
}
