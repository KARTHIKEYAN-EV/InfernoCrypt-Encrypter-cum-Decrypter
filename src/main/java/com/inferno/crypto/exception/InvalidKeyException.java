package com.inferno.crypto.exception;

import java.security.Key;

/**
 * Thrown when an invalid key is provided for cryptographic operations.
 */
public class InvalidKeyException extends CryptoException {
    
    private final Key key;
    private final String algorithm;
    private final int expectedSize;
    private final int actualSize;
    
    public InvalidKeyException(String message) {
        super(message, ErrorCode.INVALID_KEY, "Key Validation");
        this.key = null;
        this.algorithm = null;
        this.expectedSize = -1;
        this.actualSize = -1;
    }
    
    public InvalidKeyException(String message, Throwable cause) {
        super(message, cause, ErrorCode.INVALID_KEY, "Key Validation");
        this.key = null;
        this.algorithm = null;
        this.expectedSize = -1;
        this.actualSize = -1;
    }
    
    public InvalidKeyException(String message, Key key, String algorithm) {
        super(message, ErrorCode.INVALID_KEY, "Key Validation");
        this.key = key;
        this.algorithm = algorithm;
        this.expectedSize = -1;
        this.actualSize = -1;
    }
    
    public InvalidKeyException(String message, int expectedSize, int actualSize, String algorithm) {
        super(message, ErrorCode.INVALID_KEY, "Key Size Validation");
        this.key = null;
        this.algorithm = algorithm;
        this.expectedSize = expectedSize;
        this.actualSize = actualSize;
    }
    
    public InvalidKeyException(String message, Throwable cause, Key key, String algorithm) {
        super(message, cause, ErrorCode.INVALID_KEY, "Key Validation");
        this.key = key;
        this.algorithm = algorithm;
        this.expectedSize = -1;
        this.actualSize = -1;
    }
    
    public Key getKey() {
        return key;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public int getExpectedSize() {
        return expectedSize;
    }
    
    public int getActualSize() {
        return actualSize;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        
        if (algorithm != null) {
            sb.append(" [Algorithm: ").append(algorithm).append("]");
        }
        
        if (expectedSize != -1 && actualSize != -1) {
            sb.append(" [Expected: ").append(expectedSize)
              .append(" bits, Actual: ").append(actualSize).append(" bits]");
        }
        
        if (key != null) {
            sb.append(" [Key Type: ").append(key.getAlgorithm())
              .append(", Format: ").append(key.getFormat()).append("]");
        }
        
        return sb.toString();
    }
    
    /**
     * Creates an InvalidKeyException for an expired key
     */
    public static InvalidKeyException createExpiredKeyException(Key key, long expirationTime) {
        String message = String.format(
            "Key has expired. Expiration time: %d, Current time: %d",
            expirationTime, System.currentTimeMillis()
        );
        return new InvalidKeyException(message, key, "Expired Key Check");
    }
    
    /**
     * Creates an InvalidKeyException for an unsupported key type
     */
    public static InvalidKeyException createUnsupportedKeyTypeException(
            Key key, String expectedType) {
        String message = String.format(
            "Unsupported key type. Expected: %s, Actual: %s",
            expectedType, key.getClass().getSimpleName()
        );
        return new InvalidKeyException(message, key, "Key Type Validation");
    }
}