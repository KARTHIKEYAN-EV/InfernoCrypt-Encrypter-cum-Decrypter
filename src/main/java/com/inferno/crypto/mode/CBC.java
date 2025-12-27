package com.inferno.crypto.mode;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CBC implements EncryptionMode {

    private byte[] iv;

    public CBC(byte[] iv) {
        this.iv = iv;
    }

    public void setIV(byte[] iv) {
        this.iv = iv;
    }

    @Override
    public byte[] encryptBlock(byte[] block, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(
            Cipher.ENCRYPT_MODE,
            new SecretKeySpec(key, "AES"),
            new IvParameterSpec(iv)
        );
        return cipher.doFinal(block);
    }

    @Override
    public byte[] decryptBlock(byte[] block, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(
            Cipher.DECRYPT_MODE,
            new SecretKeySpec(key, "AES"),
            new IvParameterSpec(iv)
        );
        return cipher.doFinal(block);
    }

    @Override
    public boolean requiresIV() {
        return true;
    }

    @Override
    public String getName() {
        return "CBC";
    }
}
