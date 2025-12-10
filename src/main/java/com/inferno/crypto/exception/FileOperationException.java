package com.inferno.crypto.exception;

import java.nio.file.Path;

/**
 * Thrown when file operations fail during encryption/decryption.
 */
public class FileOperationException extends CryptoException {
    
    private final String filePath;
    private final FileOperationType operationType;
    private final long fileSize;
    private final String fileType;
    
    public FileOperationException(String message, String filePath) {
        super(message, ErrorCode.GENERAL_ERROR, "File Operation");
        this.filePath = filePath;
        this.operationType = FileOperationType.UNKNOWN;
        this.fileSize = -1;
        this.fileType = null;
    }
    
    public FileOperationException(String message, String filePath, 
                                 FileOperationType operationType) {
        super(message, ErrorCode.GENERAL_ERROR, "File Operation: " + operationType);
        this.filePath = filePath;
        this.operationType = operationType;
        this.fileSize = -1;
        this.fileType = null;
    }
    
    public FileOperationException(String message, Throwable cause, 
                                 String filePath, FileOperationType operationType) {
        super(message, cause, ErrorCode.GENERAL_ERROR, "File Operation: " + operationType);
        this.filePath = filePath;
        this.operationType = operationType;
        this.fileSize = -1;
        this.fileType = null;
    }
    
    public FileOperationException(String message, String filePath, 
                                 FileOperationType operationType,
                                 long fileSize, String fileType) {
        super(message, ErrorCode.GENERAL_ERROR, "File Operation: " + operationType);
        this.filePath = filePath;
        this.operationType = operationType;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public Path getPath() {
        return filePath != null ? java.nio.file.Paths.get(filePath) : null;
    }
    
    public FileOperationType getOperationType() {
        return operationType;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        
        sb.append(" [File: ").append(filePath != null ? filePath : "null").append("]");
        sb.append(" [Operation: ").append(operationType).append("]");
        
        if (fileSize > 0) {
            sb.append(" [Size: ").append(formatFileSize(fileSize)).append("]");
        }
        
        if (fileType != null) {
            sb.append(" [Type: ").append(fileType).append("]");
        }
        
        return sb.toString();
    }

    private static String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
    
    /**
     * File operation types
     */
    public enum FileOperationType {
        READ("Read file"),
        WRITE("Write file"),
        DELETE("Delete file"),
        CREATE("Create file"),
        MOVE("Move file"),
        COPY("Copy file"),
        ENCRYPT("Encrypt file"),
        DECRYPT("Decrypt file"),
        VALIDATE("Validate file"),
        PERMISSION_CHECK("Check permissions"),
        SIZE_CHECK("Check file size"),
        TYPE_CHECK("Check file type"),
        UNKNOWN("Unknown operation");
        
        private final String description;
        
        FileOperationType(String description) {
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
     * Creates an exception for file not found
     */
    public static FileOperationException createFileNotFoundException(String filePath) {
        String message = String.format("File not found: %s", filePath);
        return new FileOperationException(
            message, filePath, FileOperationType.READ
        );
    }
    
    /**
     * Creates an exception for permission denied
     */
    public static FileOperationException createPermissionDeniedException(
            String filePath, FileOperationType operationType) {
        String message = String.format(
            "Permission denied for %s operation on file: %s",
            operationType, filePath
        );
        return new FileOperationException(
            message, filePath, operationType
        );
    }
    
    /**
     * Creates an exception for file too large
     */
    public static FileOperationException createFileTooLargeException(
            String filePath, long maxSize, long actualSize) {
        String message = String.format(
            "File too large. Maximum allowed: %s, Actual: %s",
            formatFileSize(maxSize), formatFileSize(actualSize)
        );
        return new FileOperationException(
            message, filePath, FileOperationType.SIZE_CHECK, 
            actualSize, null
        );
    }
}