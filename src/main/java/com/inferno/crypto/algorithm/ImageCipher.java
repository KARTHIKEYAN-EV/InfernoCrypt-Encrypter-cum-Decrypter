package com.inferno.crypto.algorithm;

import com.inferno.crypto.exception.CryptoException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ImageCipher implements CipherAlgorithm {
    
    // --- Implemented CipherAlgorithm methods ---
    
    @Override
    public byte[] encrypt(byte[] plaintext, String key) throws CryptoException {
        // These are NEW methods that work with byte arrays
        // They don't interfere with the original file-based methods
        try {
            validateKey(key);
            ByteArrayInputStream bis = new ByteArrayInputStream(plaintext);
            BufferedImage image = ImageIO.read(bis);
            
            if (image == null) {
                throw new CryptoException("Invalid image data");
            }
            
            BufferedImage encrypted = encryptPixels(image, key);
            BufferedImage scrambled = scramblePixels(encrypted, key);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(scrambled, "png", baos);
            return baos.toByteArray();
            
        } catch (IOException e) {
            throw new CryptoException("Encryption failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] decrypt(byte[] ciphertext, String key) throws CryptoException {
        try {
            validateKey(key);
            ByteArrayInputStream bis = new ByteArrayInputStream(ciphertext);
            BufferedImage image = ImageIO.read(bis);
            
            if (image == null) {
                throw new CryptoException("Invalid image data");
            }
            
            BufferedImage unscrambled = unscramblePixels(image, key);
            BufferedImage decrypted = decryptPixels(unscrambled, key);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Use PNG format for decrypted output in byte array mode
            ImageIO.write(decrypted, "png", baos);
            return baos.toByteArray();
            
        } catch (IOException e) {
            throw new CryptoException("Decryption failed: " + e.getMessage(), e);
        }
    }
    
    // Helper methods for the interface implementation
    private BufferedImage encryptPixels(BufferedImage image, String password) {
        return ImageCipherHelper.encryptPixels(image, password);
    }
    
    private BufferedImage decryptPixels(BufferedImage image, String password) {
        return ImageCipherHelper.decryptPixels(image, password);
    }
    
    private BufferedImage scramblePixels(BufferedImage image, String password) {
        return ImageCipherHelper.scramblePixels(image, password);
    }
    
    private BufferedImage unscramblePixels(BufferedImage image, String password) {
        return ImageCipherHelper.unscramblePixels(image, password);
    }
    
    // --- CipherAlgorithm Interface Methods ---
    
    @Override
    public String getName() {
        return "ImageCipher";
    }
    
    @Override
    public String getDescription() {
        return "Image encryption algorithm using pixel scrambling and XOR-based encryption";
    }
    
    @Override
    public int getKeySize() {
        return -1; // Variable key size
    }
    
    @Override
    public boolean requiresIV() {
        return false;
    }
    
    @Override
    public boolean isSymmetric() {
        return true;
    }
    
    @Override
    public void validateKey(String key) throws CryptoException {
        if (key == null || key.isEmpty()) {
            throw new CryptoException("Password cannot be null or empty");
        }
        // Minimum length check
        if (key.length() < 1) {
            throw new CryptoException("Password must be at least 1 character long");
        }
    }
    
    @Override
    public String[] getSupportedModes() {
        return new String[]{"ECB"};
    }
    
    // Note: The default encryptToBase64/decryptFromBase64 work with text,
    // but for images you'd want specialized methods
}

// --- ORIGINAL FUNCTIONALITY PRESERVED AS STATIC METHODS ---

class ImageCipherHelper {
    
    // Convert JPEG to PNG before encryption (for images only)
    public static void convertJpegToPng(String jpegPath, String pngPath) {
        try {
            BufferedImage image = ImageIO.read(new File(jpegPath));
            ImageIO.write(image, "png", new File(pngPath));
            System.out.println(" JPEG converted to PNG: " + pngPath);
        } catch (IOException e) {
            System.out.println(" Conversion error: " + e.getMessage());
        }
    }
    
    // Original Image encryption (file-based)
    public static void encryptImage(String inputPath, String outputPath, String password) {
        try {
            BufferedImage image = ImageIO.read(new File(inputPath));
            if (image == null) {
                System.out.println("Error: Could not read image. Ensure path is correct.");
                return;
            }

            BufferedImage encryptedPixels = encryptPixels(image, password);
            BufferedImage scrambledImage = scramblePixels(encryptedPixels, password);
            
            // Always save as PNG to preserve encryption
            ImageIO.write(scrambledImage, "png", new File(outputPath));
            System.out.println(" Image encryption completed!");
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // Original Image decryption (file-based)
    public static void decryptImage(String inputPath, String outputPath, String password) {
        try {
            BufferedImage image = ImageIO.read(new File(inputPath));
            if (image == null) {
                System.out.println("Error: Could not read image.");
                return;
            }
            
            BufferedImage unscrambledImage = unscramblePixels(image, password);
            BufferedImage decryptedImage = decryptPixels(unscrambledImage, password);
            
            // Save decrypted image (can be JPEG now)
            String extension = "png"; // Default to png
            if(outputPath.contains(".")) {
                extension = outputPath.substring(outputPath.lastIndexOf(".") + 1);
            }
            ImageIO.write(decryptedImage, extension, new File(outputPath));
            System.out.println(" Image decryption completed!");
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- Original Private Helper Methods (now package-private for access) ---

    static BufferedImage encryptPixels(BufferedImage image, String password) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Random random = new Random(password.hashCode());
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                
                int red = (rgb >> 16) & 0xff;
                int green = (rgb >> 8) & 0xff;
                int blue = rgb & 0xff;
                
                int key1 = random.nextInt(256);
                int key2 = (password.charAt((x + y) % password.length()) & 0xFF);
                int key3 = (x * y + password.length()) % 256;
                
                red = (red ^ key1 ^ key2 ^ key3) % 256;
                green = (green ^ key2 ^ key3 ^ key1) % 256;
                blue = (blue ^ key3 ^ key1 ^ key2) % 256;
                
                red = (red + key1) % 256;
                green = (green + key2) % 256;
                blue = (blue + key3) % 256;
                
                int newRGB = (red << 16) | (green << 8) | blue;
                result.setRGB(x, y, newRGB);
            }
        }
        return result;
    }
    
    static BufferedImage decryptPixels(BufferedImage image, String password) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Random random = new Random(password.hashCode());
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                
                int red = (rgb >> 16) & 0xff;
                int green = (rgb >> 8) & 0xff;
                int blue = rgb & 0xff;
                
                int key1 = random.nextInt(256);
                int key2 = (password.charAt((x + y) % password.length()) & 0xFF);
                int key3 = (x * y + password.length()) % 256;
                
                red = (red - key1 + 256) % 256;
                green = (green - key2 + 256) % 256;
                blue = (blue - key3 + 256) % 256;
                
                red = (red ^ key1 ^ key2 ^ key3) % 256;
                green = (green ^ key2 ^ key3 ^ key1) % 256;
                blue = (blue ^ key3 ^ key1 ^ key2) % 256;
                
                int newRGB = (red << 16) | (green << 8) | blue;
                result.setRGB(x, y, newRGB);
            }
        }
        return result;
    }
    
    static BufferedImage scramblePixels(BufferedImage image, String password) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Random random = new Random(password.hashCode() + 1);
        
        int[] xMap = new int[width];
        int[] yMap = new int[height];
        for (int i = 0; i < width; i++) xMap[i] = i;
        for (int i = 0; i < height; i++) yMap[i] = i;
        
        shuffleArray(xMap, random);
        shuffleArray(yMap, random);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                result.setRGB(xMap[x], yMap[y], rgb);
            }
        }
        return result;
    }
    
    static BufferedImage unscramblePixels(BufferedImage image, String password) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Random random = new Random(password.hashCode() + 1);
        
        int[] xMap = new int[width];
        int[] yMap = new int[height];
        for (int i = 0; i < width; i++) xMap[i] = i;
        for (int i = 0; i < height; i++) yMap[i] = i;
        
        shuffleArray(xMap, random);
        shuffleArray(yMap, random);
        
        int[] xMapReverse = new int[width];
        int[] yMapReverse = new int[height];
        for (int i = 0; i < width; i++) xMapReverse[xMap[i]] = i;
        for (int i = 0; i < height; i++) yMapReverse[yMap[i]] = i;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                result.setRGB(xMapReverse[x], yMapReverse[y], rgb);
            }
        }
        return result;
    }
    
    private static void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}