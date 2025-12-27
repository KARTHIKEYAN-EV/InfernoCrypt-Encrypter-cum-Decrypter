package com.inferno.crypto.algorithm;

import java.security.*;
import javax.crypto.Cipher;

public class RSACipher extends AsymmetricCipher {

    private int keySize;
    private String paddingScheme = "RSA/ECB/PKCS1Padding";

    public RSACipher(int keySize) {
        this.keySize = keySize;
    }

    public void setPaddingScheme(String scheme) {
        this.paddingScheme = scheme;
    }

    @Override
    protected KeyPair doGenerateKeyPair(int keySize) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keySize);
        KeyPair pair = generator.generateKeyPair();
        this.publicKey = pair.getPublic();
        this.privateKey = pair.getPrivate();
        return pair;
    }

    @Override
    public byte[] encrypt(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(paddingScheme);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(paddingScheme);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    @Override
    public boolean supportsMode(com.inferno.crypto.mode.EncryptionMode mode) {
        return false;
    }

    @Override
    public String getAlgorithmName() {
        return "RSA";
    }

    @Override
    public int getKeySize() {
        return keySize;
    }
}