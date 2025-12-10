package com.inferno.crypto.exception;

/**
 * Thrown when decryption operation fails.
 */
public class DecryptionFailedException extends CryptoException {
    
    private final String algorithm;
    private final String mode;
    private final DecryptionFailureReason failureReason;
    private final boolean isTampered;
    
    public DecryptionFailedException(String message) {
        super(message, ErrorCode.DECRYPTION_FAILED, "Decryption");
        this.algorithm = null;
        this.mode = null;
        this.failureReason = DecryptionFailureReason.UNKNOWN;
        this.isTampered = false;
    }
    
    public DecryptionFailedException(String message, Throwable cause) {
        super(message, cause, ErrorCode.DECRYPTION_FAILED, "Decryption");
        this.algorithm = null;
        this.mode = null;
        this.failureReason = DecryptionFailureReason.UNKNOWN;
        this.isTampered = false;
    }
    
    public DecryptionFailedException(String message, String algorithm, 
                                    String mode, DecryptionFailureReason reason) {
        super(message, ErrorCode.DECRYPTION_FAILED, "Decryption");
        this.algorithm = algorithm;
        this.mode = mode;
        this.failureReason = reason;
        this.isTampered = (reason == DecryptionFailureReason.TAMPERED_DATA ||
                          reason == DecryptionFailureReason.AUTHENTICATION_FAILED);
    }
    
    public DecryptionFailedException(String message, Throwable cause, 
                                    String algorithm, String mode, 
                                    DecryptionFailureReason reason) {
        super(message, cause, ErrorCode.DECRYPTION_FAILED, "Decryption");
        this.algorithm = algorithm;
        this.mode = mode;
        this.failureReason = reason;
        this.isTampered = (reason == DecryptionFailureReason.TAMPERED_DATA ||
                          reason == DecryptionFailureReason.AUTHENTICATION_FAILED);
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public String getMode() {
        return mode;
    }
    
    public DecryptionFailureReason getFailureReason() {
        return failureReason;
    }
    
    public boolean isTampered() {
        return isTampered;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        
        if (algorithm != null) {
            sb.append(" [Algorithm: ").append(algorithm);
            if (mode != null) {
                sb.append("/").append(mode);
            }
            sb.append("]");
        }
        
        sb.append(" [Reason: ").append(failureReason.getDescription()).append("]");
        
        if (isTampered) {
            sb.append(" [WARNING: Data may have been tampered with!]");
        }
        
        return sb.toString();
    }
    
    /**
     * Decryption failure reasons
     */
    public enum DecryptionFailureReason {
        INCORRECT_KEY("Incorrect decryption key"),
        INCORRECT_IV("Incorrect initialization vector"),
        CORRUPTED_DATA("Corrupted or malformed data"),
        TAMPERED_DATA("Data has been tampered with"),
        AUTHENTICATION_FAILED("Authentication tag verification failed"),
        PADDING_ERROR("Padding error - possible incorrect key"),
        EXPIRED_DATA("Data has expired"),
        FORMAT_ERROR("Invalid data format"),
        SIZE_ERROR("Invalid data size"),
        VERSION_MISMATCH("Version mismatch"),
        UNKNOWN("Unknown decryption failure");
        
        private final String description;
        
        DecryptionFailureReason(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
    
    /**
     * Creates an exception for incorrect key
     */
    public static DecryptionFailedException createIncorrectKeyException(
            String algorithm, String mode) {
        String message = "Incorrect decryption key provided";
        return new DecryptionFailedException(
            message, algorithm, mode, DecryptionFailureReason.INCORRECT_KEY
        );
    }
    
    /**
     * Creates an exception for tampered data
     */
    public static DecryptionFailedException createTamperedDataException(
            String algorithm, String mode) {
        String message = "Data integrity check failed - data may have been tampered with";
        return new DecryptionFailedException(
            message, algorithm, mode, DecryptionFailureReason.TAMPERED_DATA
        );
    }
    
    /**
     * Creates an exception for authentication failure (GCM mode)
     */
    public static DecryptionFailedException createAuthenticationFailedException(
            String algorithm, String mode) {
        String message = "Authentication tag verification failed";
        return new DecryptionFailedException(
            message, algorithm, mode, DecryptionFailureReason.AUTHENTICATION_FAILED
        );
    }
}