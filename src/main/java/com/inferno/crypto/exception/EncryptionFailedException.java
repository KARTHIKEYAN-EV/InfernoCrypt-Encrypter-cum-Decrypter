package com.inferno.crypto.exception;

/**
 * Thrown when encryption operation fails.
 */
public class EncryptionFailedException extends CryptoException {
    
    private final String algorithm;
    private final String mode;
    private final EncryptionFailureReason failureReason;
    private final long dataSize;
    
    public EncryptionFailedException(String message) {
        super(message, ErrorCode.ENCRYPTION_FAILED, "Encryption");
        this.algorithm = null;
        this.mode = null;
        this.failureReason = EncryptionFailureReason.UNKNOWN;
        this.dataSize = -1;
    }
    
    public EncryptionFailedException(String message, Throwable cause) {
        super(message, cause, ErrorCode.ENCRYPTION_FAILED, "Encryption");
        this.algorithm = null;
        this.mode = null;
        this.failureReason = EncryptionFailureReason.UNKNOWN;
        this.dataSize = -1;
    }
    
    public EncryptionFailedException(String message, String algorithm, 
                                    String mode, EncryptionFailureReason reason) {
        super(message, ErrorCode.ENCRYPTION_FAILED, "Encryption");
        this.algorithm = algorithm;
        this.mode = mode;
        this.failureReason = reason;
        this.dataSize = -1;
    }
    
    public EncryptionFailedException(String message, String algorithm, 
                                    String mode, EncryptionFailureReason reason,
                                    long dataSize) {
        super(message, ErrorCode.ENCRYPTION_FAILED, "Encryption");
        this.algorithm = algorithm;
        this.mode = mode;
        this.failureReason = reason;
        this.dataSize = dataSize;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public String getMode() {
        return mode;
    }
    
    public EncryptionFailureReason getFailureReason() {
        return failureReason;
    }
    
    public long getDataSize() {
        return dataSize;
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
        
        if (dataSize > 0) {
            sb.append(" [Data size: ").append(formatDataSize(dataSize)).append("]");
        }
        
        return sb.toString();
    }
    
    private static String formatDataSize(long size) {
        if (size < 1024) return size + " bytes";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
    
    /**
     * Encryption failure reasons
     */
    public enum EncryptionFailureReason {
        INVALID_DATA("Invalid input data"),
        DATA_TOO_LARGE("Input data too large"),
        MEMORY_ERROR("Insufficient memory"),
        KEY_ERROR("Key-related error"),
        IV_ERROR("Initialization vector error"),
        MODE_ERROR("Unsupported encryption mode"),
        PADDING_ERROR("Padding error"),
        BLOCK_SIZE_ERROR("Block size error"),
        STREAM_ERROR("Stream processing error"),
        BUFFER_OVERFLOW("Buffer overflow"),
        UNKNOWN("Unknown encryption failure");
        
        private final String description;
        
        EncryptionFailureReason(String description) {
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
     * Creates an exception for data too large
     */
    public static EncryptionFailedException createDataTooLargeException(
            String algorithm, long maxSize, long actualSize) {
        String message = String.format(
            "Data too large for encryption. Maximum: %s, Actual: %s",
            formatDataSize(maxSize), formatDataSize(actualSize)
        );
        return new EncryptionFailedException(
            message, algorithm, null, 
            EncryptionFailureReason.DATA_TOO_LARGE, actualSize
        );
    }
    
    /**
     * Creates an exception for memory error
     */
    public static EncryptionFailedException createMemoryErrorException(
            String algorithm, long dataSize, long availableMemory) {
        String message = String.format(
            "Insufficient memory for encryption. Required: ~%s, Available: %s",
            formatDataSize(dataSize * 2), // Rough estimate
            formatDataSize(availableMemory)
        );
        return new EncryptionFailedException(
            message, algorithm, null, 
            EncryptionFailureReason.MEMORY_ERROR, dataSize
        );
    }
}