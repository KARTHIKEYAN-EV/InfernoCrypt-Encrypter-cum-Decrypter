package com.inferno.crypto.algorithm;

import java.util.ArrayList;
import java.util.List;

public class CaesarCipher extends ClassicalCipher {

    private int shift;

    public CaesarCipher(int shift) {
        this.shift = shift % 26;
    }

    @Override
    public boolean validateKey(String key) {
        return key.matches("\\d+");
    }

    @Override
    public byte[] encrypt(byte[] data, java.security.Key ignored) {
        String text = new String(data);
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append((char) ((c + shift - 32) % 95 + 32));
        }
        return result.toString().getBytes();
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
        return "Caesar";
    }

    @Override
    public int getKeySize() {
        return shift;
    }
}