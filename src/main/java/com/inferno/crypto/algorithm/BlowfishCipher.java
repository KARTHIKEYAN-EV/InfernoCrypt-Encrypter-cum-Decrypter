package com.inferno.crypto.algorithm;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class BlowfishCipher extends SymmetricCipher {

    private int rounds = 16;

    public BlowfishCipher() {}

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    @Override
    public byte[] encrypt(byte[] data, Key ignored) throws Exception {
        SecretKeySpec skey = new SecretKeySpec(key, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, skey);
        return cipher.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] data, Key ignored) throws Exception {
        SecretKeySpec skey = new SecretKeySpec(key, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, skey);
        return cipher.doFinal(data);
    }

    @Override
    public String getAlgorithmName() {
        return "Blowfish";
    }

    @Override
    public int getKeySize() {
        return key.length * 8;
    }

    @Override
    public boolean supportsMode(com.inferno.crypto.mode.EncryptionMode mode) {
        return false;
    }
}
