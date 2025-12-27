package com.inferno.crypto.key;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.SecureRandom;

public class KeyGenerator {

    public SecretKey generateSymmetricKey(String algorithm, int size) {
        try {
            javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance(algorithm);
            keyGen.init(size, new SecureRandom());
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Symmetric key gen failed", e);
        }
    }

    public KeyPair generateAsymmetricKeyPair(String algorithm, int size) {
        try {
            java.security.KeyPairGenerator gen = java.security.KeyPairGenerator.getInstance(algorithm);
            gen.initialize(size, new SecureRandom());
            return gen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Asymmetric key pair gen failed", e);
        }
    }

    public byte[] generateIV(int length) {
        byte[] iv = new byte[length];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public byte[] generateSalt(int length) {
        return generateIV(length);
    }

    public byte[] generateRandomBytes(int length) {
        return generateIV(length);
    }
}
