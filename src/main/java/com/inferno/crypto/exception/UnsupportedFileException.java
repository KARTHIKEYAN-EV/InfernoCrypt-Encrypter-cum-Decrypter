package com.inferno.crypto.exception;

import java.nio.file.Path;

/**
 * Thrown when an unsupported file type is encountered.
 */
public class UnsupportedFileException extends CryptoException {
    
    private final String filePath;
    private final String fileExtension;
    private final String mimeType;
    private final String[] supportedExtensions;
    private final long fileSize;
    
    public UnsupportedFileException(String message, String filePath) {
        super(message, ErrorCode.GENERAL_ERROR, "File Type Validation");
        this.filePath = filePath;
        this.fileExtension = extractExtension(filePath);
        this.mimeType = null;
        this.supportedExtensions = null;
        this.fileSize = -1;
    }
    
    public UnsupportedFileException(String message, String filePath, 
                                   String[] supportedExtensions) {
        super(message, ErrorCode.GENERAL_ERROR, "File Type Validation");
        this.filePath = filePath;
        this.fileExtension = extractExtension(filePath);
        this.mimeType = null;
        this.supportedExtensions = supportedExtensions;
        this.fileSize = -1;
    }
    
    public UnsupportedFileException(String message, String filePath, 
                                   String mimeType, String[] supportedExtensions) {
        super(message, ErrorCode.GENERAL_ERROR, "File Type Validation");
        this.filePath = filePath;
        this.fileExtension = extractExtension(filePath);
        this.mimeType = mimeType;
        this.supportedExtensions = supportedExtensions;
        this.fileSize = -1;
    }
    
    public UnsupportedFileException(String message, Path filePath, 
                                   long fileSize, String mimeType) {
        super(message, ErrorCode.GENERAL_ERROR, "File Type Validation");
        this.filePath = filePath.toString();
        this.fileExtension = extractExtension(this.filePath);
        this.mimeType = mimeType;
        this.supportedExtensions = null;
        this.fileSize = fileSize;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public String[] getSupportedExtensions() {
        return supportedExtensions != null ? supportedExtensions.clone() : null;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        
        sb.append(" [File: ").append(filePath != null ? filePath : "null").append("]");
        
        if (fileExtension != null) {
            sb.append(" [Extension: .").append(fileExtension).append("]");
        }
        
        if (mimeType != null) {
            sb.append(" [MIME Type: ").append(mimeType).append("]");
        }
        
        if (fileSize > 0) {
            sb.append(" [Size: ").append(formatFileSize(fileSize)).append("]");
        }
        
        if (supportedExtensions != null && supportedExtensions.length > 0) {
            sb.append(" [Supported: ");
            for (int i = 0; i < supportedExtensions.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(".").append(supportedExtensions[i]);
            }
            sb.append("]");
        }
        
        return sb.toString();
    }
    
    private static String extractExtension(String filePath) {
        if (filePath == null) return null;
        int dotIndex = filePath.lastIndexOf('.');
        return (dotIndex == -1) ? null : filePath.substring(dotIndex + 1);
    }
    
    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
    
    /**
     * Creates an exception for unsupported file extension
     */
    public static UnsupportedFileException createForExtension(
            String filePath, String[] supportedExtensions) {
        String ext = extractExtension(filePath);
        String message = String.format(
            "Unsupported file extension: %s", 
            ext != null ? "." + ext : "(no extension)"
        );
        return new UnsupportedFileException(message, filePath, supportedExtensions);
    }
    
    /**
     * Creates an exception for unsupported MIME type
     */
    public static UnsupportedFileException createForMimeType(
            String filePath, String mimeType, String[] supportedMimeTypes) {
        String message = String.format(
            "Unsupported file MIME type: %s", mimeType
        );
        return new UnsupportedFileException(message, filePath, mimeType, null);
    }
    
    /**
     * Creates an exception for encrypted file
     */
    public static UnsupportedFileException createForEncryptedFile(String filePath) {
        String message = "File appears to be already encrypted";
        return new UnsupportedFileException(message, filePath);
    }
    
    /**
     * Creates an exception for system/protected file
     */
    public static UnsupportedFileException createForSystemFile(String filePath) {
        String message = "System or protected file cannot be processed";
        return new UnsupportedFileException(message, filePath);
    }
}