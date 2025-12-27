package com.inferno.crypto.algorithm;
import com.inferno.crypto.mode.EncryptionMode;
import java.security.Key;

public interface CipherAlgorithm {
    byte[] encrypt(byte[] data, Key key) throws Exception;
    byte[] decrypt(byte[] data, Key key) throws Exception;
    String getAlgorithmName();
    int getKeySize();
    boolean supportsMode(EncryptionMode mode);
}
