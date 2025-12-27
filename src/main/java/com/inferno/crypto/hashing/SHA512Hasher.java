package com.inferno.crypto.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SHA512Hasher implements HashAlgorithm {
    public SHA512Hasher() {}

    @Override
    public byte[] hash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 not supported", e);
        }
    }

    @Override
    public String hash(String data) {
        return Base64.getEncoder().encodeToString(hash(data.getBytes()));
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-512";
    }
}
