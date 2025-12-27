package com.inferno.crypto.mode;

public interface EncryptionMode {
    byte[] encryptBlock(byte[] block, byte[] key) throws Exception;
    byte[] decryptBlock(byte[] block, byte[] key) throws Exception;
    boolean requiresIV();
    String getName();
}
