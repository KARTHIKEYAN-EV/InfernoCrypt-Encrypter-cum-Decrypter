package com.inferno.crypto.exception;

import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

/**
 * Thrown when insufficient permissions are detected for file operations.
 */
public class InsufficientPermissionException extends CryptoException {
    
    private final String filePath;
    private final String operation;
    private final String requiredPermission;
    private final String currentPermission;
    private final Set<PosixFilePermission> missingPermissions;
    private final boolean isReadOnly;
    
    public InsufficientPermissionException(String message, String filePath, String operation) {
        super(message, ErrorCode.SECURITY_ERROR, "Permission Check");
        this.filePath = filePath;
        this.operation = operation;
        this.requiredPermission = null;
        this.currentPermission = null;
        this.missingPermissions = null;
        this.isReadOnly = false;
    }
    
    public InsufficientPermissionException(String message, String filePath, 
                                         String operation, String requiredPermission,
                                         String currentPermission) {
        super(message, ErrorCode.SECURITY_ERROR, "Permission Check");
        this.filePath = filePath;
        this.operation = operation;
        this.requiredPermission = requiredPermission;
        this.currentPermission = currentPermission;
        this.missingPermissions = null;
        this.isReadOnly = "read".equalsIgnoreCase(currentPermission) && 
                         "write".equalsIgnoreCase(requiredPermission);
    }
    
    public InsufficientPermissionException(String message, String filePath,
                                         String operation, 
                                         Set<PosixFilePermission> missingPermissions) {
        super(message, ErrorCode.SECURITY_ERROR, "POSIX Permission Check");
        this.filePath = filePath;
        this.operation = operation;
        this.requiredPermission = null;
        this.currentPermission = null;
        this.missingPermissions = missingPermissions;
        this.isReadOnly = missingPermissions.contains(PosixFilePermission.OWNER_WRITE) ||
                         missingPermissions.contains(PosixFilePermission.GROUP_WRITE) ||
                         missingPermissions.contains(PosixFilePermission.OTHERS_WRITE);
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getRequiredPermission() {
        return requiredPermission;
    }
    
    public String getCurrentPermission() {
        return currentPermission;
    }
    
    public Set<PosixFilePermission> getMissingPermissions() {
        return missingPermissions;
    }
    
    public boolean isReadOnly() {
        return isReadOnly;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        
        sb.append(" [File: ").append(filePath != null ? filePath : "null").append("]");
        sb.append(" [Operation: ").append(operation != null ? operation : "null").append("]");
        
        if (requiredPermission != null && currentPermission != null) {
            sb.append(" [Required: ").append(requiredPermission)
              .append(", Current: ").append(currentPermission).append("]");
        }
        
        if (missingPermissions != null && !missingPermissions.isEmpty()) {
            sb.append(" [Missing permissions: ");
            boolean first = true;
            for (PosixFilePermission perm : missingPermissions) {
                if (!first) sb.append(", ");
                sb.append(perm.name());
                first = false;
            }
            sb.append("]");
        }
        
        if (isReadOnly) {
            sb.append(" [File is read-only]");
        }
        
        return sb.toString();
    }
    
    /**
     * Creates an exception for read permission
     */
    public static InsufficientPermissionException createReadPermissionException(
            String filePath) {
        String message = String.format(
            "Insufficient read permission for file: %s", filePath
        );
        return new InsufficientPermissionException(
            message, filePath, "read", "read", "none"
        );
    }
    
    /**
     * Creates an exception for write permission
     */
    public static InsufficientPermissionException createWritePermissionException(
            String filePath) {
        String message = String.format(
            "Insufficient write permission for file: %s", filePath
        );
        return new InsufficientPermissionException(
            message, filePath, "write", "write", "read"
        );
    }
    
    /**
     * Creates an exception for execute permission
     */
    public static InsufficientPermissionException createExecutePermissionException(
            String filePath) {
        String message = String.format(
            "Insufficient execute permission for file: %s", filePath
        );
        return new InsufficientPermissionException(
            message, filePath, "execute", "execute", "none"
        );
    }
    
    /**
     * Creates an exception for read-only file system
     */
    public static InsufficientPermissionException createReadOnlyFileSystemException(
            String filePath) {
        String message = String.format(
            "File system is read-only for file: %s", filePath
        );
        InsufficientPermissionException ex = new InsufficientPermissionException(
            message, filePath, "write"
        );
        return ex;
    }
}