package com.inferno.crypto.algorithm;

import com.inferno.crypto.exception.CryptoException;
import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;

public class VideoCipher implements CipherAlgorithm {
    
    public static final int STRATEGY_FULL_FILE = 1;
    public static final int STRATEGY_SELECTIVE_FRAMES = 2;
    public static final int STRATEGY_HYBRID = 3;
    
    private static final String[] SUPPORTED_FORMATS = {
        "mp4", "avi", "mov", "mkv", "wmv", "flv", "webm", "m4v", "mpg", "mpeg"
    };
    
    private static final byte[] MAGIC_BYTES = "VDOENC".getBytes();
    private static final int HEADER_SIZE = 128;
    private static final int CHUNK_SIZE = 65536;
    
    // Video header sizes (varies by format)
    private static final int MP4_HEADER_SIZE = 1024 * 1024; // 1MB for MP4
    private static final int AVI_HEADER_SIZE = 512 * 1024;  // 512KB for AVI
    private static final int DEFAULT_HEADER_SIZE = 1024 * 1024; // 1MB default
    
    private final SecureRandom secureRandom;
    
    public VideoCipher() {
        this.secureRandom = new SecureRandom();
    }
    
    // === CipherAlgorithm Interface ===
    
    @Override
    public byte[] encrypt(byte[] plaintext, String key) throws CryptoException {
        try {
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] iv = generateIV();
            return xorEncrypt(plaintext, keyBytes, iv);
        } catch (Exception e) {
            throw new CryptoException("Encryption failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] decrypt(byte[] ciphertext, String key) throws CryptoException {
        try {
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] iv = generateIV();
            return xorDecrypt(ciphertext, keyBytes, iv);
        } catch (Exception e) {
            throw new CryptoException("Decryption failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getName() { return "VideoCipher"; }
    
    @Override
    public String getDescription() { 
        return "Advanced video encryption algorithm supporting multiple strategies"; 
    }
    
    @Override
    public int getKeySize() { return 256; }
    
    @Override
    public boolean requiresIV() { return true; }
    
    @Override
    public boolean isSymmetric() { return true; }
    
    @Override
    public void validateKey(String key) throws CryptoException {
        if (key == null || key.length() < 8) {
            throw new CryptoException("Key must be at least 8 characters long");
        }
    }
    
    @Override
    public String[] getSupportedModes() {
        return new String[]{"FULL_FILE", "SELECTIVE", "HYBRID"};
    }
    
    // === Public Video Methods ===
    
    public void encryptVideo(String inputPath, String outputPath, String password, int strategy) 
            throws CryptoException {
        try {
            validateInputFile(inputPath);
            validateOutputPath(outputPath);
            
            File inputFile = new File(inputPath);
            File outputFile = new File(outputPath);
            String extension = getFileExtension(inputFile);
            
            switch (strategy) {
                case STRATEGY_FULL_FILE:
                    encryptWithHeaderPreservation(inputFile, outputFile, password, extension);
                    break;
                case STRATEGY_SELECTIVE_FRAMES:
                    encryptSelective(inputFile, outputFile, password, extension);
                    break;
                case STRATEGY_HYBRID:
                    encryptHybrid(inputFile, outputFile, password, extension);
                    break;
                default:
                    throw new CryptoException("Unsupported encryption strategy: " + strategy);
            }
        } catch (Exception e) {
            throw new CryptoException("Video encryption failed: " + e.getMessage(), e);
        }
    }
    
    public void decryptVideo(String inputPath, String outputPath, String password) 
            throws CryptoException {
        try {
            validateInputFile(inputPath);
            validateOutputPath(outputPath);
            
            File inputFile = new File(inputPath);
            File outputFile = new File(outputPath);
            
            if (!isEncryptedFile(inputFile)) {
                throw new CryptoException("File is not encrypted or was encrypted with different parameters");
            }
            
            // Read strategy from header
            int strategy = readEncryptionStrategy(inputFile);
            String originalExtension = readOriginalExtension(inputFile);
            
            switch (strategy) {
                case STRATEGY_FULL_FILE:
                    decryptWithHeaderPreservation(inputFile, outputFile, password, originalExtension);
                    break;
                case STRATEGY_SELECTIVE_FRAMES:
                    decryptSelective(inputFile, outputFile, password, originalExtension);
                    break;
                case STRATEGY_HYBRID:
                    decryptHybrid(inputFile, outputFile, password, originalExtension);
                    break;
                default:
                    throw new CryptoException("Unknown encryption strategy: " + strategy);
            }
        } catch (Exception e) {
            throw new CryptoException("Video decryption failed: " + e.getMessage(), e);
        }
    }
    
    // === Encryption Methods ===
    
    private void encryptWithHeaderPreservation(File inputFile, File outputFile, 
                                             String password, String extension) 
            throws IOException {
        int headerSize = getHeaderSizeForFormat(extension);
        
        try (RandomAccessFile raf = new RandomAccessFile(inputFile, "r");
             FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            writeEncryptionHeader(bos, STRATEGY_FULL_FILE, extension, headerSize);
            
            byte[] key = password.getBytes("UTF-8");
            byte[] iv = generateIV();
            
            // 1. Preserve video header (unencrypted)
            raf.seek(0);
            byte[] header = new byte[headerSize];
            int headerBytes = raf.read(header);
            bos.write(header, 0, headerBytes);
            
            // 2. Encrypt the rest
            encryptRemaining(raf, bos, key, iv, headerSize);
        }
    }
    
    private void encryptSelective(File inputFile, File outputFile, 
                                 String password, String extension) 
            throws IOException {
        int headerSize = getHeaderSizeForFormat(extension);
        int skipSize = headerSize * 2; // Skip more for selective
        
        try (RandomAccessFile raf = new RandomAccessFile(inputFile, "r");
             FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            writeEncryptionHeader(bos, STRATEGY_SELECTIVE_FRAMES, extension, skipSize);
            
            byte[] key = password.getBytes("UTF-8");
            byte[] iv = generateIV();
            
            // 1. Copy unencrypted portions
            raf.seek(0);
            long fileSize = inputFile.length();
            long position = 0;
            byte[] buffer = new byte[CHUNK_SIZE];
            
            while (position < fileSize) {
                int toRead = (int) Math.min(buffer.length, fileSize - position);
                int bytesRead = raf.read(buffer, 0, toRead);
                
                if (bytesRead <= 0) break;
                
                if (position < skipSize || position % (skipSize * 2) < skipSize) {
                    // Unencrypted section
                    bos.write(buffer, 0, bytesRead);
                } else {
                    // Encrypted section
                    byte[] encrypted = xorEncrypt(Arrays.copyOf(buffer, bytesRead), key, iv);
                    bos.write(encrypted);
                    updateIV(iv, encrypted);
                }
                
                position += bytesRead;
            }
        }
    }
    
    private void encryptHybrid(File inputFile, File outputFile, 
                              String password, String extension) 
            throws IOException {
        int headerSize = getHeaderSizeForFormat(extension);
        
        try (RandomAccessFile raf = new RandomAccessFile(inputFile, "r");
             FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            writeEncryptionHeader(bos, STRATEGY_HYBRID, extension, headerSize);
            
            byte[] strongKey = (password + "STRONG").getBytes("UTF-8");
            byte[] lightKey = (password + "LIGHT").getBytes("UTF-8");
            byte[] iv = generateIV();
            
            // 1. Preserve header
            raf.seek(0);
            byte[] header = new byte[headerSize];
            int headerBytes = raf.read(header);
            bos.write(header, 0, headerBytes);
            
            // 2. Encrypt first part with strong key
            long fileSize = inputFile.length();
            long strongPartSize = Math.min(10 * 1024 * 1024, (fileSize - headerSize) / 2);
            
            encryptPart(raf, bos, strongKey, iv, headerSize, strongPartSize);
            
            // 3. Encrypt rest with light key
            encryptPart(raf, bos, lightKey, iv, headerSize + strongPartSize, 
                       fileSize - headerSize - strongPartSize);
        }
    }
    
    // === Decryption Methods ===
    
    private void decryptWithHeaderPreservation(File inputFile, File outputFile, 
                                             String password, String extension) 
            throws IOException, CryptoException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            EncryptionHeader header = readEncryptionHeader(bis);
            if (header.strategy != STRATEGY_FULL_FILE) {
                throw new CryptoException("Strategy mismatch");
            }
            
            byte[] key = password.getBytes("UTF-8");
            byte[] iv = generateIV();
            
            // 1. Copy preserved header
            byte[] headerBytes = new byte[header.preservedHeaderSize];
            bis.read(headerBytes);
            bos.write(headerBytes);
            
            // 2. Decrypt the rest
            decryptRemaining(bis, bos, key, iv);
        }
    }
    
    private void decryptSelective(File inputFile, File outputFile, 
                                 String password, String extension) 
            throws IOException, CryptoException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            EncryptionHeader header = readEncryptionHeader(bis);
            if (header.strategy != STRATEGY_SELECTIVE_FRAMES) {
                throw new CryptoException("Strategy mismatch");
            }
            
            byte[] key = password.getBytes("UTF-8");
            byte[] iv = generateIV();
            
            long position = 0;
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            
            while ((bytesRead = bis.read(buffer)) != -1) {
                if (position < header.preservedHeaderSize || 
                    position % (header.preservedHeaderSize * 2) < header.preservedHeaderSize) {
                    // Unencrypted section
                    bos.write(buffer, 0, bytesRead);
                } else {
                    // Decrypt section
                    byte[] decrypted = xorDecrypt(Arrays.copyOf(buffer, bytesRead), key, iv);
                    bos.write(decrypted, 0, decrypted.length);
                    updateIV(iv, buffer);
                }
                position += bytesRead;
            }
        }
    }
    
    private void decryptHybrid(File inputFile, File outputFile, 
                              String password, String extension) 
            throws IOException, CryptoException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            EncryptionHeader header = readEncryptionHeader(bis);
            if (header.strategy != STRATEGY_HYBRID) {
                throw new CryptoException("Strategy mismatch");
            }
            
            byte[] strongKey = (password + "STRONG").getBytes("UTF-8");
            byte[] lightKey = (password + "LIGHT").getBytes("UTF-8");
            byte[] iv = generateIV();
            
            // 1. Copy preserved header
            byte[] headerBytes = new byte[header.preservedHeaderSize];
            bis.read(headerBytes);
            bos.write(headerBytes);
            
            // 2. Decrypt first part with strong key
            decryptPart(bis, bos, strongKey, iv, header.preservedHeaderSize);
            
            // 3. Decrypt rest with light key
            decryptRemaining(bis, bos, lightKey, iv);
        }
    }
    
    // === Core Cryptographic Operations ===
    
    private byte[] xorEncrypt(byte[] data, byte[] key, byte[] iv) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            int keyIndex = i % key.length;
            int ivIndex = i % iv.length;
            result[i] = (byte) (data[i] ^ key[keyIndex] ^ iv[ivIndex] ^ (i & 0xFF));
        }
        return result;
    }
    
    private byte[] xorDecrypt(byte[] data, byte[] key, byte[] iv) {
        return xorEncrypt(data, key, iv); // XOR is symmetric
    }
    
    private void updateIV(byte[] iv, byte[] data) {
        for (int i = 0; i < iv.length; i++) {
            iv[i] ^= data[i % data.length];
        }
    }
    
    private byte[] generateIV() {
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        return iv;
    }
    
    // === File Operations ===
    
    private void encryptRemaining(RandomAccessFile raf, OutputStream os, 
                                 byte[] key, byte[] iv, long startPos) 
            throws IOException {
        raf.seek(startPos);
        byte[] buffer = new byte[CHUNK_SIZE];
        int bytesRead;
        
        while ((bytesRead = raf.read(buffer)) != -1) {
            byte[] encrypted = xorEncrypt(Arrays.copyOf(buffer, bytesRead), key, iv);
            os.write(encrypted);
            updateIV(iv, encrypted);
        }
    }
    
    private void decryptRemaining(InputStream is, OutputStream os, 
                                 byte[] key, byte[] iv) throws IOException {
        byte[] buffer = new byte[CHUNK_SIZE];
        int bytesRead;
        
        while ((bytesRead = is.read(buffer)) != -1) {
            byte[] decrypted = xorDecrypt(Arrays.copyOf(buffer, bytesRead), key, iv);
            os.write(decrypted);
            updateIV(iv, buffer);
        }
    }
    
    private void encryptPart(RandomAccessFile raf, OutputStream os, byte[] key, byte[] iv,
                            long startPos, long size) throws IOException {
        raf.seek(startPos);
        long remaining = size;
        byte[] buffer = new byte[CHUNK_SIZE];
        
        while (remaining > 0) {
            int toRead = (int) Math.min(buffer.length, remaining);
            int bytesRead = raf.read(buffer, 0, toRead);
            if (bytesRead <= 0) break;
            
            byte[] encrypted = xorEncrypt(Arrays.copyOf(buffer, bytesRead), key, iv);
            os.write(encrypted);
            updateIV(iv, encrypted);
            
            remaining -= bytesRead;
        }
    }
    
    private void decryptPart(InputStream is, OutputStream os, byte[] key, byte[] iv,
                            long bytesToDecrypt) throws IOException {
        long remaining = bytesToDecrypt;
        byte[] buffer = new byte[CHUNK_SIZE];
        
        while (remaining > 0) {
            int toRead = (int) Math.min(buffer.length, remaining);
            int bytesRead = is.read(buffer, 0, toRead);
            if (bytesRead <= 0) break;
            
            byte[] decrypted = xorDecrypt(Arrays.copyOf(buffer, bytesRead), key, iv);
            os.write(decrypted);
            updateIV(iv, buffer);
            
            remaining -= bytesRead;
        }
    }
    
    // === Header Operations ===
    
    private void writeEncryptionHeader(OutputStream os, int strategy, 
                                      String extension, int preservedHeaderSize) 
            throws IOException {
        os.write(MAGIC_BYTES);
        os.write(strategy);
        
        byte[] extBytes = extension.getBytes("UTF-8");
        os.write(extBytes.length);
        os.write(extBytes);
        
        // Write preserved header size
        for (int i = 3; i >= 0; i--) {
            os.write((preservedHeaderSize >>> (8 * i)) & 0xFF);
        }
        
        // Fill remaining
        int bytesWritten = MAGIC_BYTES.length + 1 + 1 + extBytes.length + 4;
        for (int i = bytesWritten; i < HEADER_SIZE; i++) {
            os.write(0);
        }
    }
    
    private EncryptionHeader readEncryptionHeader(InputStream is) 
            throws IOException, CryptoException {
        EncryptionHeader header = new EncryptionHeader();
        
        header.magicBytes = new byte[MAGIC_BYTES.length];
        int read = is.read(header.magicBytes);
        if (read != MAGIC_BYTES.length || !Arrays.equals(header.magicBytes, MAGIC_BYTES)) {
            throw new CryptoException("Invalid encrypted file");
        }
        
        header.strategy = is.read();
        if (header.strategy < 1 || header.strategy > 3) {
            throw new CryptoException("Invalid encryption strategy");
        }
        
        int extLength = is.read();
        byte[] extBytes = new byte[extLength];
        is.read(extBytes);
        header.originalExtension = new String(extBytes, "UTF-8");
        
        // Read preserved header size
        header.preservedHeaderSize = 0;
        for (int i = 0; i < 4; i++) {
            header.preservedHeaderSize = (header.preservedHeaderSize << 8) | (is.read() & 0xFF);
        }
        
        // Skip remaining
        int bytesRead = MAGIC_BYTES.length + 1 + 1 + extLength + 4;
        is.skip(HEADER_SIZE - bytesRead);
        
        return header;
    }
    
    private int readEncryptionStrategy(File file) throws IOException, CryptoException {
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            EncryptionHeader header = readEncryptionHeader(bis);
            return header.strategy;
        }
    }
    
    private String readOriginalExtension(File file) throws IOException, CryptoException {
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            EncryptionHeader header = readEncryptionHeader(bis);
            return header.originalExtension;
        }
    }
    
    // === Helper Methods ===
    
    private int getHeaderSizeForFormat(String extension) {
        switch (extension.toLowerCase()) {
            case "mp4":
            case "m4v":
                return MP4_HEADER_SIZE;
            case "avi":
                return AVI_HEADER_SIZE;
            default:
                return DEFAULT_HEADER_SIZE;
        }
    }
    
    private void validateInputFile(String filePath) throws CryptoException {
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new CryptoException("Input file does not exist: " + filePath);
        }
        
        if (!file.canRead()) {
            throw new CryptoException("Cannot read input file: " + filePath);
        }
        
        if (file.length() == 0) {
            throw new CryptoException("Input file is empty: " + filePath);
        }
        
        String extension = getFileExtension(file);
        if (!isSupportedFormat(extension)) {
            throw new CryptoException("Unsupported video format: " + extension);
        }
    }
    
    private void validateOutputPath(String filePath) throws CryptoException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new CryptoException("Cannot create output directory: " + parent.getPath());
        }
        
        if (file.exists() && !file.canWrite()) {
            throw new CryptoException("Cannot write to output file: " + filePath);
        }
    }
    
    private boolean isEncryptedFile(File file) throws IOException {
        if (file.length() < HEADER_SIZE) return false;
        
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] magic = new byte[MAGIC_BYTES.length];
            int read = fis.read(magic);
            return read == MAGIC_BYTES.length && Arrays.equals(magic, MAGIC_BYTES);
        }
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex == -1) ? "" : name.substring(dotIndex + 1).toLowerCase();
    }
    
    private boolean isSupportedFormat(String extension) {
        for (String format : SUPPORTED_FORMATS) {
            if (format.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    
    // === Inner Class ===
    
    private static class EncryptionHeader {
        byte[] magicBytes;
        int strategy;
        String originalExtension;
        int preservedHeaderSize;
    }
    
    // === Public Static Methods ===
    
    public static String[] getSupportedFormats() {
        return SUPPORTED_FORMATS.clone();
    }
    
    public static boolean isVideoFormat(String filename) {
        if (filename == null) return false;
        
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) return false;
        
        String extension = filename.substring(dotIndex + 1).toLowerCase();
        for (String format : SUPPORTED_FORMATS) {
            if (format.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    public static long estimateProcessingTime(long fileSizeBytes) {
        return fileSizeBytes / (100 * 1024 * 1024) * 1000;
    }
}