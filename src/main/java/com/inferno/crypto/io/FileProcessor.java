package com.inferno.crypto.io;

import com.inferno.crypto.algorithm.CipherAlgorithm;
import com.inferno.crypto.io.file.FileHandler;

import java.io.*;
import java.security.Key;

public class FileProcessor {

    private FileHandler fileHandler;
    private CipherAlgorithm cipher;
    private double progress;
    private Key key; // store encryption key

    public FileProcessor(FileHandler handler, CipherAlgorithm cipher, Key key) {
        this.fileHandler = handler;
        this.cipher = cipher;
        this.key = key;
    }


    public void processFile(File input, File output, boolean encrypt) throws Exception {
        byte[] data = fileHandler.read(input);
        byte[] result = encrypt ? cipher.encrypt(data, key) : cipher.decrypt(data, key);
        fileHandler.write(output, result);
        progress = 1.0;
    }

    public void processStream(InputStream in, OutputStream out, boolean encrypt) throws Exception {
        byte[] buffer = in.readAllBytes();
        byte[] result = encrypt ? cipher.encrypt(buffer, key) : cipher.decrypt(buffer, key);
        out.write(result);
        progress = 1.0;
    }

    public void setFileHandler(FileHandler handler) {
        this.fileHandler = handler;
    }

    public void setCipherAlgorithm(CipherAlgorithm cipher) {
        this.cipher = cipher;
    }

    public double getProgress() {
        return progress;
    }
}
