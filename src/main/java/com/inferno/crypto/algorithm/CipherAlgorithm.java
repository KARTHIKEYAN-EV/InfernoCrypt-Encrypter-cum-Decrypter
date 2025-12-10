package com.inferno.crypto.algorithm;

import com.inferno.crypto.exception.CryptoException;

public interface CipherAlgorithm {
    // Core encryption methods
    byte[] encrypt(byte[] plaintext, String key) throws CryptoException;
    byte[] decrypt(byte[] ciphertext, String key) throws CryptoException;
    
    // Algorithm metadata
    String getName();
    String getDescription();
    int getKeySize();
    boolean requiresIV();
    boolean isSymmetric();
    
    // Key validation
    void validateKey(String key) throws CryptoException;
    
    // Mode support
    String[] getSupportedModes();
    
    // Utility methods
    default String encryptToBase64(String plaintext, String key) throws CryptoException {
        byte[] encrypted = encrypt(plaintext.getBytes(), key);
        return java.util.Base64.getEncoder().encodeToString(encrypted);
    }
    
    default String decryptFromBase64(String base64Text, String key) throws CryptoException {
        byte[] decoded = java.util.Base64.getDecoder().decode(base64Text);
        byte[] decrypted = decrypt(decoded, key);
        return new String(decrypted);
    }
}