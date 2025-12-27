package com.inferno.crypto.algorithm;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class AsymmetricCipher implements CipherAlgorithm {

    protected PublicKey publicKey;
    protected PrivateKey privateKey;

    public KeyPair generateKeyPair(int keySize) throws Exception {
        if (keySize < 1024) {
            throw new IllegalArgumentException("Key size too small for secure asymmetric encryption");
        }
        return doGenerateKeyPair(keySize);
    }

    protected abstract KeyPair doGenerateKeyPair(int keySize) throws Exception;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
    
    public abstract boolean supportsMode(com.inferno.crypto.mode.EncryptionMode mode);
    public abstract String getAlgorithmName();
    public abstract int getKeySize();
}
