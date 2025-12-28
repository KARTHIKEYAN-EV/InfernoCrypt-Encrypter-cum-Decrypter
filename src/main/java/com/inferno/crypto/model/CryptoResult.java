package com.inferno.crypto.model;

import java.io.File;

public class CryptoResult {

    private boolean success;
    private String message;
    private File outputFile;
    private long timeTaken;
    private String algorithm;
    private int dataSize;

    public CryptoResult() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File file) {
        this.outputFile = file;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long time) {
        this.timeTaken = time;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int size) {
        this.dataSize = size;
    }
}
