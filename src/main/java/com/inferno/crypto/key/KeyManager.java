package com.inferno.crypto.key;

import java.io.*;
import java.security.Key;
import java.util.*;

public class KeyManager {
    private final Map<String, Key> keyStore = new HashMap<>();

    public void storeKey(String alias, Key key) {
        keyStore.put(alias, key);
    }

    public Key retrieveKey(String alias) {
        return keyStore.get(alias);
    }

    public boolean deleteKey(String alias) {
        return keyStore.remove(alias) != null;
    }

    public List<String> listKeys() {
        return new ArrayList<>(keyStore.keySet());
    }

    public void exportKey(String alias, File file) throws IOException {
        Key key = retrieveKey(alias);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(key);
        }
    }

    public Key importKey(File file, String alias) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Key key = (Key) ois.readObject();
            storeKey(alias, key);
            return key;
        }
    }
}
