package com.inferno.crypto.mode;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class GCM implements EncryptionMode {

    private byte[] iv;
    private byte[] aad;
    private byte[] tag;

    public GCM(byte[] iv, byte[] aad) {
        this.iv = iv;
        this.aad = aad;
    }

    public void setAAD(byte[] aad) {
        this.aad = aad;
    }

    public byte[] getTag() {
        return tag;
    }

    @Override
    public byte[] encryptBlock(byte[] block, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(
            Cipher.ENCRYPT_MODE,
            new SecretKeySpec(key, "AES"),
            spec
        );

        if (aad != null) {
            cipher.updateAAD(aad);
        }

        byte[] encrypted = cipher.doFinal(block);
        tag = cipher.getIV(); // Stored for reference
        return encrypted;
    }

    @Override
    public byte[] decryptBlock(byte[] block, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(
            Cipher.DECRYPT_MODE,
            new SecretKeySpec(key, "AES"),
            spec
        );

        if (aad != null) {
            cipher.updateAAD(aad);
        }

        return cipher.doFinal(block);
    }

    @Override
    public boolean requiresIV() {
        return true;
    }

    @Override
    public String getName() {
        return "GCM";
    }
}
