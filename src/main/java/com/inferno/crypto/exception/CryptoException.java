package com.inferno.crypto.exception;

/**
 * Base exception for all cryptographic operations.
 */
public class CryptoException extends Exception {
    
    private final ErrorCode errorCode;
    private final String operation;
    
    public CryptoException(String message) {
        super(message);
        this.errorCode = ErrorCode.GENERAL_ERROR;
        this.operation = "Unknown";
    }
    
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.GENERAL_ERROR;
        this.operation = "Unknown";
    }
    
    public CryptoException(String message, Throwable cause, ErrorCode errorCode, String operation) {
        super(message, cause);
        this.errorCode = errorCode;
        this.operation = operation;
    }
    
    public CryptoException(String message, ErrorCode errorCode, String operation) {
        super(message);
        this.errorCode = errorCode;
        this.operation = operation;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getOperation() {
        return operation;
    }
    
    @Override
    public String getMessage() {
        return String.format("[%s] Operation: %s - %s", 
            errorCode, operation, super.getMessage());
    }
    
    /**
     * Error codes for better exception categorization
     */
    public enum ErrorCode {
        GENERAL_ERROR("ERR-000", "General cryptographic error"),
        INVALID_KEY("ERR-001", "Invalid or corrupted key"),
        INVALID_ALGORITHM("ERR-002", "Unsupported or invalid algorithm"),
        INVALID_PARAMETERS("ERR-003", "Invalid cryptographic parameters"),
        ENCRYPTION_FAILED("ERR-004", "Encryption operation failed"),
        DECRYPTION_FAILED("ERR-005", "Decryption operation failed"),
        KEY_GENERATION_FAILED("ERR-006", "Key generation failed"),
        AUTHENTICATION_FAILED("ERR-007", "Authentication/verification failed"),
        PADDING_ERROR("ERR-008", "Padding error"),
        BLOCK_SIZE_ERROR("ERR-009", "Invalid block size"),
        INITIALIZATION_ERROR("ERR-010", "Cryptographic initialization failed"),
        SECURITY_ERROR("ERR-011", "Security violation detected"),
        KEY_STORE_ERROR("ERR-012", "Key store operation failed"),
        PASSWORD_ERROR("ERR-013", "Password-related error"),
        SALT_ERROR("ERR-014", "Salt generation/usage error"),
        IV_ERROR("ERR-015", "Initialization Vector error"),
        MODE_ERROR("ERR-016", "Unsupported encryption mode"),
        HASH_ERROR("ERR-017", "Hashing operation failed");
        
        private final String code;
        private final String description;
        
        ErrorCode(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        @Override
        public String toString() {
            return code + ": " + description;
        }
    }
}