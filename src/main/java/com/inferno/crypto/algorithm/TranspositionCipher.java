package com.inferno.crypto.algorithm;

public class TranspositionCipher extends ClassicalCipher {

    private int rails;

    public TranspositionCipher(int rails) {
        this.rails = rails;
    }

    @Override
    public boolean validateKey(String key) {
        return true;
    }

    @Override
    public byte[] encrypt(byte[] data, java.security.Key ignored) {
        String text = new String(data);
        StringBuilder[] fence = new StringBuilder[rails];
        for (int i = 0; i < rails; i++) fence[i] = new StringBuilder();
        int row=0, dir=1;
        for (char c : text.toCharArray()) {
            fence[row].append(c);
            if (row == 0) dir = 1;
            if (row == rails-1) dir = -1;
            row += dir;
        }
        StringBuilder out = new StringBuilder();
        for (StringBuilder sb : fence) out.append(sb);
        return out.toString().getBytes();
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
        return "Transposition";
    }

    @Override
    public int getKeySize() {
        return rails;
    }
}