package com.inferno.crypto.mode;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ECB implements EncryptionMode {

    public ECB() {}

    @Override
    public byte[] encryptBlock(byte[] block, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        return cipher.doFinal(block);
    }

    @Override
    public byte[] decryptBlock(byte[] block, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
        return cipher.doFinal(block);
    }

    @Override
    public boolean requiresIV() {
        return false;
    }

    @Override
    public String getName() {
        return "ECB";
    }
}
