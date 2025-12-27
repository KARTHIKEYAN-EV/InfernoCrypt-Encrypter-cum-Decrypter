package com.inferno.crypto.algorithm;

import com.inferno.crypto.mode.EncryptionMode;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;


public class AESCipher extends SymmetricCipher {

    private int keySize;
    private String padding = "AES/CBC/PKCS5Padding";

    public AESCipher(int keySize) {
        this.keySize = keySize;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }

    @Override
    public byte[] encrypt(byte[] data, Key ignored) throws Exception {
        SecretKeySpec skey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(padding);

        IvParameterSpec iv = generateIV();
        cipher.init(Cipher.ENCRYPT_MODE, skey, iv);

        return cipher.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] data, Key ignored) throws Exception {
        SecretKeySpec skey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(padding);

        IvParameterSpec iv = generateIV();
        cipher.init(Cipher.DECRYPT_MODE, skey, iv);

        return cipher.doFinal(data);
    }

    private IvParameterSpec generateIV() {
        byte[] iv = new byte[16]; // AES block size
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }


    @Override
    public String getAlgorithmName() {
        return "AES";
    }

    @Override
    public int getKeySize() {
        return keySize;
    }

    @Override
    public boolean supportsMode(EncryptionMode mode) {
        return true; // AES supports all standard modes handled by Cipher
    }
}
