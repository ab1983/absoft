package com.algoboss.integration.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
 
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
 
 
public class SSLSocketFactoryGenerator {
     
     
    private String alias = null;
    private String keyStore = null;
    private String trustStore = null;
     
    public SSLSocketFactoryGenerator (String alias, String keyStore, String trustStore) {
        if (alias == null)
            throw new IllegalArgumentException("The alias may not be null");
        this.alias = alias;
        this.keyStore = keyStore;
        this.trustStore = trustStore;
 
    }
         
 
    public SSLSocketFactory getSSLSocketFactory() throws IOException, GeneralSecurityException {
     
        KeyManager[] keyManagers = getKeyManagers();
        TrustManager[] trustManagers =getTrustManagers();
     
     
        //For each key manager, check if it is a X509KeyManager (because we will override its       //functionality
        for (int i=0; i<keyManagers.length; i++) {
            if (keyManagers[i] instanceof X509KeyManager) {
                keyManagers[i]=new AliasSelectorKeyManager((X509KeyManager)keyManagers[i], alias);
            }
            }
     
 
        SSLContext context=SSLContext.getInstance("SSL");
        context.init(keyManagers, trustManagers, null);
 
     
        SSLSocketFactory ssf=context.getSocketFactory();
            return ssf;
    }   
         
     
    public String getKeyStorePassword() {
        return "keyStorePassword";
    }
     
    public String getTrustStorePassword() {
        return "trustStorePassword";
    }
 
 
    public String getKeyStore() {
        return keyStore;
    }
 
    public String getTrustStore() {
         
        return trustStore;
    }
 
 
    private KeyManager[] getKeyManagers()
    throws IOException, GeneralSecurityException
    {
         
        //Init a key store with the given file.
         
        String alg=KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmFact=KeyManagerFactory.getInstance(alg);
 
         
        FileInputStream fis=new FileInputStream(getKeyStore());
        KeyStore ks=KeyStore.getInstance("jks");
        ks.load(fis, getKeyStorePassword().toCharArray());
        fis.close();
 
        //Init the key manager factory with the loaded key store
        kmFact.init(ks,  getKeyStorePassword().toCharArray());
 
 
         
        KeyManager[] kms=kmFact.getKeyManagers();
        return kms;
    }
 
     
    protected TrustManager[] getTrustManagers() throws IOException, GeneralSecurityException
    {
     
        String alg=TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmFact=TrustManagerFactory.getInstance(alg);
     
     
        FileInputStream fis=new FileInputStream(getTrustStore());
        KeyStore ks=KeyStore.getInstance("jks");
        ks.load(fis, getTrustStorePassword().toCharArray());
        fis.close();
 
     
        tmFact.init(ks);
 
     
        TrustManager[] tms=tmFact.getTrustManagers();
        return tms;
    }
}
