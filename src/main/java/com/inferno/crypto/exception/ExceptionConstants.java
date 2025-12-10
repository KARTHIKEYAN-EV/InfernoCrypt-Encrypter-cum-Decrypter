package com.inferno.crypto.exception;

/**
 * Constants for exception handling and error messages.
 */
public final class ExceptionConstants {
    
    private ExceptionConstants() {
        // Constants class - no instantiation
    }
    
    // Error message templates
    public static final String MSG_INVALID_KEY = "Invalid key for algorithm: %s";
    public static final String MSG_KEY_SIZE_MISMATCH = 
        "Key size mismatch. Expected: %d bits, Actual: %d bits";
    public static final String MSG_UNSUPPORTED_ALGORITHM = 
        "Unsupported algorithm: %s. Supported algorithms: %s";
    public static final String MSG_FILE_NOT_FOUND = "File not found: %s";
    public static final String MSG_PERMISSION_DENIED = 
        "Permission denied for %s operation on file: %s";
    public static final String MSG_DECRYPTION_FAILED = 
        "Decryption failed for algorithm: %s/%s. Reason: %s";
    public static final String MSG_ENCRYPTION_FAILED = 
        "Encryption failed for algorithm: %s/%s. Reason: %s";
    public static final String MSG_UNSUPPORTED_FILE_TYPE = 
        "Unsupported file type: %s. Supported types: %s";
    public static final String MSG_INSUFFICIENT_MEMORY = 
        "Insufficient memory. Required: %s, Available: %s";
    public static final String MSG_DATA_TOO_LARGE = 
        "Data too large. Maximum: %s, Actual: %s";
    public static final String MSG_CORRUPTED_DATA = 
        "Data appears to be corrupted or tampered with";
    public static final String MSG_AUTHENTICATION_FAILED = 
        "Data authentication failed - possible tampering detected";
    
    // Recovery suggestions
    public static final String SUGGESTION_CHECK_KEY = 
        "Please verify that you're using the correct encryption key.";
    public static final String SUGGESTION_CHECK_ALGORITHM = 
        "Please verify that you're using the correct encryption algorithm.";
    public static final String SUGGESTION_CHECK_FILE = 
        "Please verify that the file exists and is accessible.";
    public static final String SUGGESTION_CHECK_PERMISSIONS = 
        "Please check file permissions and try again.";
    public static final String SUGGESTION_REDUCE_DATA_SIZE = 
        "Try breaking the data into smaller chunks.";
    public static final String SUGGESTION_FREE_MEMORY = 
        "Close other applications to free up memory.";
    public static final String SUGGESTION_USE_DIFFERENT_ALGORITHM = 
        "Try using a different encryption algorithm.";
    public static final String SUGGESTION_VERIFY_INTEGRITY = 
        "Verify the integrity of the encrypted data.";
    public static final String SUGGESTION_CONTACT_SUPPORT = 
        "If the problem persists, contact support with the error details.";
    
    // Log messages
    public static final String LOG_SECURITY_ALERT = 
        "SECURITY ALERT - %s";
    public static final String LOG_DECRYPTION_TAMPERED = 
        "Possible data tampering detected during decryption";
    public static final String LOG_INVALID_KEY_ATTEMPT = 
        "Invalid key attempt detected for algorithm: %s";
    public static final String LOG_FILE_OPERATION_FAILED = 
        "File operation failed: %s - %s";
    public static final String LOG_CRYPTO_OPERATION_FAILED = 
        "Cryptographic operation failed: %s - %s";
    
    // Error codes for external systems
    public static final String ERROR_CODE_PREFIX = "INFERNO-CRYPTO-";
    public static final String ERROR_CODE_INVALID_KEY = ERROR_CODE_PREFIX + "001";
    public static final String ERROR_CODE_INVALID_ALGORITHM = ERROR_CODE_PREFIX + "002";
    public static final String ERROR_CODE_FILE_NOT_FOUND = ERROR_CODE_PREFIX + "003";
    public static final String ERROR_CODE_PERMISSION_DENIED = ERROR_CODE_PREFIX + "004";
    public static final String ERROR_CODE_DECRYPTION_FAILED = ERROR_CODE_PREFIX + "005";
    public static final String ERROR_CODE_ENCRYPTION_FAILED = ERROR_CODE_PREFIX + "006";
    public static final String ERROR_CODE_MEMORY_ERROR = ERROR_CODE_PREFIX + "007";
    public static final String ERROR_CODE_DATA_TOO_LARGE = ERROR_CODE_PREFIX + "008";
    public static final String ERROR_CODE_CORRUPTED_DATA = ERROR_CODE_PREFIX + "009";
    public static final String ERROR_CODE_AUTHENTICATION_FAILED = ERROR_CODE_PREFIX + "010";
    public static final String ERROR_CODE_UNSUPPORTED_FILE = ERROR_CODE_PREFIX + "011";
    
    // Maximum values for validation
    public static final long MAX_FILE_SIZE = 1024L * 1024 * 1024 * 10; // 10GB
    public static final long MAX_MEMORY_USAGE = 1024L * 1024 * 1024 * 2; // 2GB
    public static final int MAX_KEY_SIZE = 4096; // bits
    public static final int MIN_KEY_SIZE = 128; // bits
    public static final int MAX_PASSWORD_LENGTH = 256;
    public static final int MIN_PASSWORD_LENGTH = 8;
    
    // Timeout values (milliseconds)
    public static final long FILE_OPERATION_TIMEOUT = 300000; // 5 minutes
    public static final long CRYPTO_OPERATION_TIMEOUT = 60000; // 1 minute
    public static final long KEY_GENERATION_TIMEOUT = 30000; // 30 seconds
    
    /**
     * Gets a formatted error message with the appropriate error code.
     */
    public static String formatErrorMessage(String errorCode, String message, Object... args) {
        String formattedMessage = String.format(message, args);
        return String.format("[%s] %s", errorCode, formattedMessage);
    }
    
    /**
     * Gets a recovery suggestion based on the error code.
     */
    public static String getRecoverySuggestion(String errorCode) {
        switch (errorCode) {
            case ERROR_CODE_INVALID_KEY:
                return SUGGESTION_CHECK_KEY;
                
            case ERROR_CODE_INVALID_ALGORITHM:
                return SUGGESTION_CHECK_ALGORITHM;
                
            case ERROR_CODE_FILE_NOT_FOUND:
                return SUGGESTION_CHECK_FILE;
                
            case ERROR_CODE_PERMISSION_DENIED:
                return SUGGESTION_CHECK_PERMISSIONS;
                
            case ERROR_CODE_MEMORY_ERROR:
                return SUGGESTION_FREE_MEMORY;
                
            case ERROR_CODE_DATA_TOO_LARGE:
                return SUGGESTION_REDUCE_DATA_SIZE;
                
            case ERROR_CODE_CORRUPTED_DATA:
            case ERROR_CODE_AUTHENTICATION_FAILED:
                return SUGGESTION_VERIFY_INTEGRITY;
                
            case ERROR_CODE_UNSUPPORTED_FILE:
                return SUGGESTION_USE_DIFFERENT_ALGORITHM;
                
            default:
                return SUGGESTION_CONTACT_SUPPORT;
        }
    }
}