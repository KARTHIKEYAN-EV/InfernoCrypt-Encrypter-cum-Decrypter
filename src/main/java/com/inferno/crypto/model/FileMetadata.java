package com.inferno.crypto.model;

import java.util.Date;

public class FileMetadata {

    private String fileName;
    private long size;
    private Date lastModified;
    private String extension;
    private String mimeType;
    private boolean encrypted;
    private String encryptionAlgorithm;

    public FileMetadata() {}

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }
}
