package com.inferno.crypto.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MD5Hasher implements HashAlgorithm {
    public MD5Hasher() {}

    @Override
    public byte[] hash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
    }

    @Override
    public String hash(String data) {
        return Base64.getEncoder().encodeToString(hash(data.getBytes()));
    }

    @Override
    public String getAlgorithmName() {
        return "MD5";
    }
}
