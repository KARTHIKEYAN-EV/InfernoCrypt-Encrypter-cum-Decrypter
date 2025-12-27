package com.inferno.crypto.algorithm;

import com.inferno.crypto.mode.EncryptionMode;
import java.security.SecureRandom;
import java.util.Arrays;

public abstract class SymmetricCipher implements CipherAlgorithm {

    protected byte[] key;
    protected EncryptionMode mode;

    public void setKey(byte[] key) {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("Symmetric key must not be null/empty");
        }
        this.key = Arrays.copyOf(key, key.length);
    }

    public void setMode(EncryptionMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Encryption mode must not be null");
        }
        this.mode = mode;
    }

    public byte[] generateKey(int keySizeBytes) {
        SecureRandom random = new SecureRandom();
        byte[] newKey = new byte[keySizeBytes];
        random.nextBytes(newKey);
        setKey(newKey);
        return newKey;
    }
    public abstract boolean supportsMode(com.inferno.crypto.mode.EncryptionMode mode);
    public abstract String getAlgorithmName();
    public abstract int getKeySize();
}
