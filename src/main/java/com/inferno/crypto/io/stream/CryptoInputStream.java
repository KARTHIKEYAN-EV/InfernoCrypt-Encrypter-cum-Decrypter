package com.inferno.crypto.io.stream;

import com.inferno.crypto.algorithm.CipherAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

public class CryptoInputStream extends InputStream {

    private final byte[] decryptedData;
    private int position = 0;

    public CryptoInputStream(InputStream is,
                             CipherAlgorithm cipher,
                             Key key) throws IOException {
        try {
            byte[] encrypted = is.readAllBytes();
            this.decryptedData = cipher.decrypt(encrypted, key); // ✅ key passed
        } catch (Exception e) {
            throw new IOException("Decryption failed", e); // ✅ wrapped
        }
    }

    @Override
    public int read() {
        return (position < decryptedData.length)
                ? decryptedData[position++] & 0xff
                : -1;
    }
}
