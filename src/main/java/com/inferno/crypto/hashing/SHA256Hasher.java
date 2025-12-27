package com.inferno.crypto.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SHA256Hasher implements HashAlgorithm {
    private final int iterations;

    public SHA256Hasher() {
        this.iterations = 1;
    }

    @Override
    public byte[] hash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    @Override
    public String hash(String data) {
        return Base64.getEncoder().encodeToString(hash(data.getBytes()));
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-256";
    }
}
