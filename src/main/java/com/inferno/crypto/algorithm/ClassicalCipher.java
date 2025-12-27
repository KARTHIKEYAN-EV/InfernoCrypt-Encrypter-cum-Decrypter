package com.inferno.crypto.algorithm;

public abstract class ClassicalCipher implements CipherAlgorithm {

    protected String key;

    public void setKey(String key) {
        if (!validateKey(key)) {
            throw new IllegalArgumentException("Invalid key for classical cipher");
        }
        this.key = key;
    }

    public abstract boolean validateKey(String key);
    public abstract boolean supportsMode(com.inferno.crypto.mode.EncryptionMode mode);
    public abstract String getAlgorithmName();
    public abstract int getKeySize();
}
