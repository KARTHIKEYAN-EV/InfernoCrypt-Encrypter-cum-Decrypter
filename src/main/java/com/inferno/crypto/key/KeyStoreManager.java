package com.inferno.crypto.key;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;

public class KeyStoreManager {
    private KeyStore keyStore;
    private char[] password;

    public void loadKeyStore(File file, char[] password) throws Exception {
        this.password = password;
        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fis = new FileInputStream(file)) {
            keyStore.load(fis, password);
        }
    }

    public void saveKeyStore(File file) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            keyStore.store(fos, password);
        }
    }

    public void addKey(String alias, java.security.Key key) throws Exception {
        keyStore.setKeyEntry(alias, key, password, null);
    }

    public java.security.Key getKey(String alias) throws Exception {
        return keyStore.getKey(alias, password);
    }

    public boolean containsAlias(String alias) throws Exception {
        return keyStore.containsAlias(alias);
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}
