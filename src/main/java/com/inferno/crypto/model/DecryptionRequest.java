package com.inferno.crypto.model;

import java.io.File;
import java.security.Key;

public class DecryptionRequest {

    private File inputFile;
    private File outputFile;
    private String algorithm;
    private Key key;
    private String mode;
    private byte[] iv;

    public DecryptionRequest() {}

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File file) {
        this.inputFile = file;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File file) {
        this.outputFile = file;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public byte[] getIV() {
        return iv;
    }

    public void setIV(byte[] iv) {
        this.iv = iv;
    }
}
