package com.inferno.crypto.io.format;

import com.inferno.crypto.algorithm.CipherAlgorithm;
import com.inferno.crypto.algorithm.ImageCipher;
import com.inferno.crypto.exception.CryptoException;
import com.inferno.crypto.exception.UnsupportedFileException;
import com.inferno.crypto.io.file.FileHandler;
import com.inferno.crypto.io.file.FileValidator;
import com.inferno.crypto.model.CryptoResult;
import com.inferno.crypto.utils.FileUtils;
import com.inferno.crypto.utils.ValidationUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

/**
 * ImageEncryptor - Handles encryption and decryption of various image formats.
 * Supports: PNG, JPEG, JPG, BMP, GIF, TIFF
 */
public class ImageEncryptor implements FileHandler {
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
        "png", "jpg", "jpeg", "bmp", "gif", "tiff", "tif"
    );
    
    private static final List<String> LOSSLESS_FORMATS = Arrays.asList(
        "png", "bmp", "tiff", "tif"
    );
    
    private static final List<String> LOSSY_FORMATS = Arrays.asList(
        "jpg", "jpeg"
    );
    
    private final FileValidator fileValidator;
    
    public ImageEncryptor() {
        this.fileValidator = new FileValidator();
    }
    
    @Override
    public CryptoResult encrypt(File inputFile, File outputFile, String password, 
                                CipherAlgorithm algorithm, String mode) throws CryptoException {
        
        try {
            // Validate input
            validateInput(inputFile, password);
            
            // Check if output directory exists
            FileUtils.ensureDirectoryExists(outputFile.getParentFile());
            
            // Get file extension
            String inputExtension = FileUtils.getFileExtension(inputFile);
            String outputExtension = FileUtils.getFileExtension(outputFile);
            
            // Validate formats
            if (!isSupportedFormat(inputExtension)) {
                throw new UnsupportedFileException("Unsupported image format: " + inputExtension);
            }
            
            // Handle JPEG separately (convert to PNG for encryption)
            if (LOSSY_FORMATS.contains(inputExtension.toLowerCase())) {
                return handleLossyImageEncryption(inputFile, outputFile, password, algorithm);
            } else {
                return handleLosslessImageEncryption(inputFile, outputFile, password, algorithm, inputExtension);
            }
            
        } catch (IOException e) {
            throw new CryptoException("Image encryption failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CryptoResult decrypt(File inputFile, File outputFile, String password, 
                                CipherAlgorithm algorithm, String mode) throws CryptoException {
        
        try {
            // Validate input
            validateInput(inputFile, password);
            
            // Check if output directory exists
            FileUtils.ensureDirectoryExists(outputFile.getParentFile());
            
            // Get file extension
            String inputExtension = FileUtils.getFileExtension(inputFile);
            String outputExtension = FileUtils.getFileExtension(outputFile);
            
            // Validate input format
            if (!isSupportedFormat(inputExtension)) {
                throw new UnsupportedFileException("Unsupported image format: " + inputExtension);
            }
            
            // For decryption, we need to check if it's an encrypted image
            if (!isLikelyEncryptedImage(inputFile)) {
                System.out.println("Warning: The image might not be encrypted or was encrypted with different parameters.");
            }
            
            // Perform decryption
            return performImageDecryption(inputFile, outputFile, password, algorithm, outputExtension);
            
        } catch (IOException e) {
            throw new CryptoException("Image decryption failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean supportsFileType(File file) {
        String extension = FileUtils.getFileExtension(file);
        return isSupportedFormat(extension);
    }
    
    @Override
    public List<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }
    
    /**
     * Handles encryption of lossy images (JPEG/JPG)
     */
    private CryptoResult handleLossyImageEncryption(File inputFile, File outputFile, 
                                                    String password, CipherAlgorithm algorithm) throws IOException {
        
        // Create a temporary PNG file
        File tempPngFile = createTempFile("temp_encrypt_", ".png");
        
        try {
            // Convert JPEG to PNG first
            BufferedImage image = ImageIO.read(inputFile);
            if (image == null) {
                throw new IOException("Could not read image file");
            }
            
            ImageIO.write(image, "png", tempPngFile);
            
            // Encrypt the PNG file
            File encryptedTempFile = createTempFile("encrypted_", ".png");
            
            // Use ImageCipher for encryption
            ImageCipher.encryptImage(tempPngFile.getAbsolutePath(), 
                                    encryptedTempFile.getAbsolutePath(), 
                                    password);
            
            // If output is specified as JPEG, we need to convert back
            String outputExtension = FileUtils.getFileExtension(outputFile);
            if (LOSSY_FORMATS.contains(outputExtension.toLowerCase())) {
                BufferedImage encryptedImage = ImageIO.read(encryptedTempFile);
                ImageIO.write(encryptedImage, "jpg", outputFile);
            } else {
                // Copy the encrypted PNG to output
                Files.copy(encryptedTempFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Clean up temp files
            tempPngFile.delete();
            encryptedTempFile.delete();
            
            return CryptoResult.success("Image encrypted successfully", 
                                       outputFile.getAbsolutePath(),
                                       Files.size(outputFile.toPath()));
            
        } finally {
            // Ensure temp file is deleted even if an exception occurs
            if (tempPngFile.exists()) {
                tempPngFile.delete();
            }
        }
    }
    
    /**
     * Handles encryption of lossless images
     */
    private CryptoResult handleLosslessImageEncryption(File inputFile, File outputFile, 
                                                       String password, CipherAlgorithm algorithm,
                                                       String originalExtension) throws IOException {
        
        // For lossless formats, we can encrypt directly
        File tempEncryptedFile = createTempFile("encrypted_", ".png");
        
        try {
            // Use ImageCipher for encryption
            ImageCipher.encryptImage(inputFile.getAbsolutePath(), 
                                    tempEncryptedFile.getAbsolutePath(), 
                                    password);
            
            // Copy to output with appropriate extension
            String outputExtension = FileUtils.getFileExtension(outputFile);
            if (!outputExtension.equalsIgnoreCase("png")) {
                // Convert to desired format
                BufferedImage encryptedImage = ImageIO.read(tempEncryptedFile);
                ImageIO.write(encryptedImage, outputExtension, outputFile);
            } else {
                Files.copy(tempEncryptedFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            tempEncryptedFile.delete();
            
            return CryptoResult.success("Image encrypted successfully", 
                                       outputFile.getAbsolutePath(),
                                       Files.size(outputFile.toPath()));
            
        } finally {
            if (tempEncryptedFile.exists()) {
                tempEncryptedFile.delete();
            }
        }
    }
    
    /**
     * Performs image decryption
     */
    private CryptoResult performImageDecryption(File inputFile, File outputFile, 
                                                String password, CipherAlgorithm algorithm,
                                                String outputExtension) throws IOException {
        
        File tempDecryptedFile = createTempFile("decrypted_", ".png");
        
        try {
            // Use ImageCipher for decryption
            ImageCipher.decryptImage(inputFile.getAbsolutePath(), 
                                    tempDecryptedFile.getAbsolutePath(), 
                                    password);
            
            // Handle output format
            if (!outputExtension.equalsIgnoreCase("png")) {
                BufferedImage decryptedImage = ImageIO.read(tempDecryptedFile);
                ImageIO.write(decryptedImage, outputExtension, outputFile);
            } else {
                Files.copy(tempDecryptedFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            tempDecryptedFile.delete();
            
            return CryptoResult.success("Image decrypted successfully", 
                                       outputFile.getAbsolutePath(),
                                       Files.size(outputFile.toPath()));
            
        } finally {
            if (tempDecryptedFile.exists()) {
                tempDecryptedFile.delete();
            }
        }
    }
    
    /**
     * Validates input file and password
     */
    private void validateInput(File inputFile, String password) throws CryptoException {
        if (!fileValidator.isValidFile(inputFile)) {
            throw new CryptoException("Invalid input file");
        }
        
        if (!ValidationUtils.isValidPassword(password)) {
            throw new CryptoException("Invalid password. Password must be at least 8 characters long");
        }
    }
    
    /**
     * Checks if the file format is supported
     */
    private boolean isSupportedFormat(String extension) {
        if (extension == null) return false;
        return SUPPORTED_EXTENSIONS.contains(extension.toLowerCase());
    }
    
    /**
     * Creates a temporary file
     */
    private File createTempFile(String prefix, String suffix) throws IOException {
        return File.createTempFile(prefix, suffix);
    }
    
    /**
     * Checks if an image is likely encrypted by this system
     */
    private boolean isLikelyEncryptedImage(File file) throws IOException {
        // Simple heuristic: check file extension and size
        String extension = FileUtils.getFileExtension(file);
        long fileSize = Files.size(file.toPath());
        
        // Encrypted images are always saved as PNG
        if (!extension.equalsIgnoreCase("png")) {
            return false;
        }
        
        // Check if file has reasonable size for an image
        return fileSize > 100; // At least 100 bytes
    }
    
    /**
     * Gets image metadata
     */
    public ImageMetadata getImageMetadata(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        if (image == null) {
            throw new IOException("Cannot read image file");
        }
        
        return new ImageMetadata(
            image.getWidth(),
            image.getHeight(),
            image.getType(),
            FileUtils.getFileExtension(imageFile),
            Files.size(imageFile.toPath())
        );
    }
    
    /**
     * Inner class for image metadata
     */
    public static class ImageMetadata {
        private final int width;
        private final int height;
        private final int imageType;
        private final String format;
        private final long fileSize;
        
        public ImageMetadata(int width, int height, int imageType, String format, long fileSize) {
            this.width = width;
            this.height = height;
            this.imageType = imageType;
            this.format = format;
            this.fileSize = fileSize;
        }
        
        // Getters
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public int getImageType() { return imageType; }
        public String getFormat() { return format; }
        public long getFileSize() { return fileSize; }
        
        @Override
        public String toString() {
            return String.format("Image: %dx%d, Format: %s, Size: %d bytes, Type: %d", 
                                width, height, format, fileSize, imageType);
        }
    }
}