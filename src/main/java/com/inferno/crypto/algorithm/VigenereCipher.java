package com.inferno.crypto.algorithm;

import java.util.HashMap;
import java.util.Map;

public class VigenereCipher extends ClassicalCipher {
    public VigenereCipher(String key) { this.key = key; }

    @Override
    public boolean validateKey(String key) {
        return key.chars().allMatch(Character::isLetter);
    }

    @Override
    public byte[] encrypt(byte[] data, java.security.Key ignored) {
        String text = new String(data);
        String res = "";
        for (int i = 0; i < text.length(); i++) {
            res += (char)((text.charAt(i) + key.charAt(i % key.length())) % 256);
        }
        return res.getBytes();
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
        return "Vigenere";
    }

    @Override
    public int getKeySize() {
        return key.length();
    }
}