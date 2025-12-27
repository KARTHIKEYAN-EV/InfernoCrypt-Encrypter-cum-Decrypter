package com.inferno.crypto.io.stream;

import com.inferno.crypto.algorithm.CipherAlgorithm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;

public class CryptoOutputStream extends OutputStream {

    private final OutputStream outputStream;
    private final CipherAlgorithm cipher;
    private Key key;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public CryptoOutputStream(OutputStream os, CipherAlgorithm cipher, Key key) {
        this.outputStream = os;
        this.cipher = cipher;
        this.key = key;
    }

    @Override
    public void write(int b) {
        buffer.write(b);
    }

    @Override
    public void close() throws IOException {
        try {
            byte[] encrypted = cipher.encrypt(buffer.toByteArray(), key);
            outputStream.write(encrypted);
            outputStream.close();
        } catch (Exception e) {
            throw new IOException("Encryption failed", e);
        }
    }

}
