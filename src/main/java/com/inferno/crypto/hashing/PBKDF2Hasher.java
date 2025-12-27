package com.inferno.crypto.hashing;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PBKDF2Hasher implements HashAlgorithm {
    private final int iterations;
    private final int keyLength;

    public PBKDF2Hasher(int iterations, int keyLength) {
        this.iterations = iterations;
        this.keyLength = keyLength;
    }

    @Override
    public byte[] hash(byte[] data) {
        throw new UnsupportedOperationException("Use hash(String password, byte[] salt)");
    }

    public byte[] hash(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("PBKDF2 hashing failed", e);
        }
    }

    public byte[] generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

    @Override
    public String hash(String data) {
        throw new UnsupportedOperationException("Use hash(password, salt)");
    }

    @Override
    public String getAlgorithmName() {
        return "PBKDF2WithHmacSHA256";
    }
}
