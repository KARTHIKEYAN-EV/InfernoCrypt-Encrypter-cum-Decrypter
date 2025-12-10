package com.inferno.crypto.io.file;

import com.inferno.crypto.exception.FileOperationException;
import com.inferno.crypto.exception.FileOperationException.FileOperationType;
import com.inferno.crypto.model.FileMetadata;
import com.inferno.crypto.algorithm.CipherAlgorithm;
import com.inferno.crypto.model.CryptoResult;

import java.io.*;
import java.nio.file.*;
import java.security.Key;
import java.util.concurrent.locks.ReentrantLock;

public class FileHandler {
    private static final int BUFFER_SIZE = 8192;
    private static final long MAX_FILE_SIZE = 1024L * 1024L * 1024L; // 1GB
    
    // For thread-safe operations on same files
    private final ReentrantLock fileLock = new ReentrantLock();
    
    public byte[] readFile(String filePath) throws FileOperationException {
        validateFilePath(filePath);
        
        try {
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                throw FileOperationException.createFileNotFoundException(filePath);
            }
            
            long fileSize = Files.size(path);
            if (fileSize > MAX_FILE_SIZE) {
                throw FileOperationException.createFileTooLargeException(
                    filePath, MAX_FILE_SIZE, fileSize
                );
            }
            
            // Use try-with-resources for auto-close
            try (InputStream is = Files.newInputStream(path)) {
                return is.readAllBytes(); // Java 9+ alternative
            }
            
        } catch (SecurityException e) {
            throw FileOperationException.createPermissionDeniedException(
                filePath, FileOperationType.READ
            );
        } catch (IOException e) {
            throw new FileOperationException(
                "Failed to read file", e, filePath, FileOperationType.READ
            );
        }
    }
    
    public byte[] readFileChunked(String filePath, long offset, int size) 
            throws FileOperationException {
        validateFilePath(filePath);
        
        fileLock.lock();
        try {
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                throw FileOperationException.createFileNotFoundException(filePath);
            }
            
            long fileSize = Files.size(path);
            if (offset >= fileSize) {
                throw new FileOperationException(
                    "Offset exceeds file size", filePath, 
                    FileOperationType.READ, fileSize, null
                );
            }
            
            int bytesToRead = (int) Math.min(size, fileSize - offset);
            byte[] buffer = new byte[bytesToRead];
            
            try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
                raf.seek(offset);
                raf.readFully(buffer);
            }
            
            return buffer;
        } catch (IOException e) {
            throw new FileOperationException(
                "Failed to read file chunk", e, filePath, FileOperationType.READ
            );
        } finally {
            fileLock.unlock();
        }
    }
    
    public void writeFile(String filePath, byte[] data, boolean atomic) 
            throws FileOperationException {
        validateFilePath(filePath);
        
        fileLock.lock();
        try {
            Path path = Paths.get(filePath);
            Path parentDir = path.getParent();
            
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            
            if (atomic) {
                writeFileAtomic(path, data);
            } else {
                Files.write(path, data, StandardOpenOption.CREATE, 
                           StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (SecurityException e) {
            throw FileOperationException.createPermissionDeniedException(
                filePath, FileOperationType.WRITE
            );
        } catch (IOException e) {
            throw new FileOperationException(
                "Failed to write file", e, filePath, FileOperationType.WRITE
            );
        } finally {
            fileLock.unlock();
        }
    }
    
    private void writeFileAtomic(Path path, byte[] data) throws IOException {
        Path tempFile = Files.createTempFile(
            path.getParent(), 
            "temp_" + System.currentTimeMillis(), 
            ".tmp"
        );
        
        try {
            Files.write(tempFile, data);
            Files.move(
                tempFile, 
                path, 
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            );
        } finally {
            // Clean up temp file if move failed
            if (Files.exists(tempFile)) {
                Files.deleteIfExists(tempFile);
            }
        }
    }
    
    public void encryptFile(String inputPath, String outputPath,
                           CipherAlgorithm algorithm,
                           Object key) throws FileOperationException {
        
        validateFilePath(inputPath);
        validateFilePath(outputPath);
        
        fileLock.lock();
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputPath));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(outputPath))) {
            
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            
            while ((bytesRead = in.read(buffer)) != -1) {
                // No need to copy buffer - process in place if possible
                byte[] encryptedChunk;
                
                if (key instanceof Key) {
                    CryptoResult result = algorithm.encrypt(buffer, 0, bytesRead, (Key) key);
                    encryptedChunk = result.getData();
                } else if (key instanceof String) {
                    CryptoResult result = algorithm.encrypt(buffer, 0, bytesRead, (String) key);
                    encryptedChunk = result.getData();
                } else {
                    throw new FileOperationException(
                        "Unsupported key type: " + key.getClass().getName(),
                        inputPath, FileOperationType.ENCRYPT
                    );
                }
                
                out.write(encryptedChunk);
            }
            
            out.flush();
            
        } catch (FileNotFoundException e) {
            throw FileOperationException.createFileNotFoundException(inputPath);
        } catch (SecurityException e) {
            throw FileOperationException.createPermissionDeniedException(
                inputPath, FileOperationType.ENCRYPT
            );
        } catch (IOException e) {
            throw new FileOperationException(
                "File encryption failed", e, inputPath, FileOperationType.ENCRYPT
            );
        } finally {
            fileLock.unlock();
        }
    }
    
    public FileMetadata getFileMetadata(String filePath) throws FileOperationException {
        validateFilePath(filePath);
        
        try {
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                throw FileOperationException.createFileNotFoundException(filePath);
            }
            
            BasicFileAttributes attrs = Files.readAttributes(
                path, BasicFileAttributes.class
            );
            
            String contentType = Files.probeContentType(path);
            boolean isReadable = Files.isReadable(path);
            boolean isWritable = Files.isWritable(path);
            boolean isExecutable = Files.isExecutable(path);
            boolean isHidden = Files.isHidden(path);
            
            return new FileMetadata(
                path.getFileName().toString(),
                path.toAbsolutePath().toString(),
                attrs.size(),
                attrs.creationTime().toMillis(),
                attrs.lastModifiedTime().toMillis(),
                attrs.lastAccessTime().toMillis(),
                contentType != null ? contentType : "application/octet-stream",
                isReadable,
                isWritable,
                isExecutable,
                isHidden,
                Files.isSymbolicLink(path),
                attrs.isDirectory()
            );
            
        } catch (SecurityException e) {
            throw FileOperationException.createPermissionDeniedException(
                filePath, FileOperationType.VALIDATE
            );
        } catch (IOException e) {
            throw new FileOperationException(
                "Failed to get file metadata", e, filePath, FileOperationType.VALIDATE
            );
        }
    }
    
    private void validateFilePath(String filePath) throws FileOperationException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new FileOperationException(
                "File path cannot be null or empty",
                filePath, FileOperationType.VALIDATE
            );
        }
        
        // Better path traversal detection
        Path normalizedPath;
        try {
            normalizedPath = Paths.get(filePath).normalize();
            Path absolutePath = normalizedPath.toAbsolutePath();
            
            // Check for directory traversal
            if (!normalizedPath.equals(absolutePath.normalize())) {
                throw new FileOperationException(
                    "Path contains directory traversal", 
                    filePath, FileOperationType.VALIDATE
                );
            }
            
            // Check for invalid characters (OS-specific)
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows-specific validation
                if (filePath.contains("<") || filePath.contains(">") || 
                    filePath.contains(":") || filePath.contains("\"") || 
                    filePath.contains("|") || filePath.contains("?") || 
                    filePath.contains("*")) {
                    throw new FileOperationException(
                        "Invalid characters in file path", 
                        filePath, FileOperationType.VALIDATE
                    );
                }
            }
            
        } catch (InvalidPathException e) {
            throw new FileOperationException(
                "Invalid file path format", e, filePath, FileOperationType.VALIDATE
            );
        }
    }
    
    // Additional utility methods
    public boolean deleteFile(String filePath) throws FileOperationException {
        validateFilePath(filePath);
        
        fileLock.lock();
        try {
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                return false;
            }
            
            Files.delete(path);
            return true;
            
        } catch (SecurityException e) {
            throw FileOperationException.createPermissionDeniedException(
                filePath, FileOperationType.DELETE
            );
        } catch (IOException e) {
            throw new FileOperationException(
                "Failed to delete file", e, filePath, FileOperationType.DELETE
            );
        } finally {
            fileLock.unlock();
        }
    }
    
    public void copyFile(String sourcePath, String destPath) throws FileOperationException {
        validateFilePath(sourcePath);
        validateFilePath(destPath);
        
        fileLock.lock();
        try {
            Path source = Paths.get(sourcePath);
            Path dest = Paths.get(destPath);
            
            if (!Files.exists(source)) {
                throw FileOperationException.createFileNotFoundException(sourcePath);
            }
            
            Files.copy(
                source, 
                dest, 
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
            );
            
        } catch (SecurityException e) {
            throw FileOperationException.createPermissionDeniedException(
                sourcePath, FileOperationType.COPY
            );
        } catch (IOException e) {
            throw new FileOperationException(
                "Failed to copy file", e, sourcePath, FileOperationType.COPY
            );
        } finally {
            fileLock.unlock();
        }
    }
}