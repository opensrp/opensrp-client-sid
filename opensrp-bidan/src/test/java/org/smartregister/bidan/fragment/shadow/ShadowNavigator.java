package org.smartregister.bidan.fragment.shadow;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.bidan.fragment.BaseSmartRegisterFragment;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


/**
 * Created by sid-tech on 4/25/18
 */

@Implements(BaseSmartRegisterFragment.class)
@SuppressWarnings("WeakerAccess")
public class ShadowNavigator {
    @Implementation
    @SuppressWarnings("unused")
    public static KeyStore getSystemCAKeyStore() throws
            KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream(System.getProperty("java.home") + "/lib/security/cacerts"), null);
        return keyStore;
    }
}