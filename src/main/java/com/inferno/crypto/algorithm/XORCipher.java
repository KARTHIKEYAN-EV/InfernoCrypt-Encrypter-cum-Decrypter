package com.inferno.crypto.algorithm;

import java.util.Random;

public class XORCipher extends ClassicalCipher {

    private byte[] xorKey;

    public XORCipher(byte[] key) { this.xorKey = key; }

    @Override
    public boolean validateKey(String key) { return false; }

    public byte[] generateRandomKey(int length) {
        byte[] out = new byte[length];
        new Random().nextBytes(out);
        return out;
    }

    @Override
    public byte[] encrypt(byte[] data, java.security.Key ignored) {
        byte[] out = new byte[data.length];
        for (int i = 0; i < data.length; i++)
            out[i] = (byte)(data[i] ^ xorKey[i % xorKey.length]);
        return out;
    }

    @Override
    public byte[] decrypt(byte[] data, java.security.Key ignored) {
        return encrypt(data, ignored);
    }

    @Override
    public boolean supportsMode(com.inferno.crypto.mode.EncryptionMode mode) {
        return false;
    }

    @Override
    public String getAlgorithmName() {
        return "XOR";
    }

    @Override
    public int getKeySize() {
        return xorKey.length;
    }
}