package com.inferno.crypto.model;

import java.util.Date;

public class KeyMetadata {

    private String keyId;
    private String algorithm;
    private int keySize;
    private Date creationDate;
    private Date expirationDate;
    private String owner;

    public KeyMetadata() {}

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String id) {
        this.keyId = id;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int size) {
        this.keySize = size;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date date) {
        this.expirationDate = date;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
