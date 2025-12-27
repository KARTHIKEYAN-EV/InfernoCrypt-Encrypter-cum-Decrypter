package com.inferno.crypto.hashing;

public interface HashAlgorithm {
    byte[] hash(byte[] data) throws Exception;
    String hash(String data) throws Exception;
    //boolean verify(byte[] data, byte[] expectedHash) throws Exception;
    String getAlgorithmName();
}
