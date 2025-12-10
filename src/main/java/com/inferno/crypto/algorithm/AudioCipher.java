package com.inferno.crypto.algorithm;

import com.inferno.crypto.exception.CryptoException;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class AudioCipher implements CipherAlgorithm {
    
    // Implementation of CipherAlgorithm interface
    
    @Override
    public byte[] encrypt(byte[] plaintext, String key) throws CryptoException {
        validateKey(key);
        return encryptAudioData(plaintext, key);
    }
    
    @Override
    public byte[] decrypt(byte[] ciphertext, String key) throws CryptoException {
        validateKey(key);
        return decryptAudioData(ciphertext, key);
    }
    
    @Override
    public String getName() {
        return "AudioCipher";
    }
    
    @Override
    public String getDescription() {
        return "Audio encryption algorithm using XOR, arithmetic, and permutation techniques";
    }
    
    @Override
    public int getKeySize() {
        return 256;
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
        if (key == null || key.trim().isEmpty()) {
            throw new CryptoException("Encryption key cannot be null or empty");
        }
        if (key.length() < 4) {
            throw new CryptoException("Key must be at least 4 characters long");
        }
    }
    
    @Override
    public String[] getSupportedModes() {
        return new String[]{"ECB"};
    }
    
    // === Enhanced Audio Methods with proper MP3 handling ===
    
    public static void encryptAudio(String inputPath, String outputPath, String password) {
        try {
            AudioCipher cipher = new AudioCipher();
            cipher.validateKey(password);
            
            String extension = getFileExtension(inputPath).toLowerCase();
            
            // Check if it's an MP3 file
            if (extension.equals("mp3")) {
                System.out.println("Warning: MP3 format detected. Java cannot natively process MP3 files.");
                System.out.println("Please convert your MP3 to WAV format first, or use the built-in test WAV file.");
                System.out.println("Skipping MP3 encryption...");
                return;
            }
            
            // Check if file exists
            File inputFile = new File(inputPath);
            if (!inputFile.exists()) {
                System.out.println("Error: Input file not found: " + inputPath);
                return;
            }
            
            // Check if it's a supported audio format
            if (!isSupportedAudioFormat(extension)) {
                System.out.println("Error: Unsupported audio format: " + extension);
                System.out.println("Supported formats: WAV, AU, AIFF");
                return;
            }
            
            // Read audio file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputFile);
            AudioFormat format = audioStream.getFormat();
            
            // Convert to byte array
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = audioStream.read(buffer)) != -1) {
                byteStream.write(buffer, 0, bytesRead);
            }
            
            byte[] audioData = byteStream.toByteArray();
            audioStream.close();
            
            // Encrypt audio data
            byte[] encryptedData = cipher.encrypt(audioData, password);
            
            // Create new audio stream with encrypted data
            ByteArrayInputStream encryptedStream = new ByteArrayInputStream(encryptedData);
            AudioInputStream encryptedAudioStream = new AudioInputStream(
                encryptedStream, format, encryptedData.length / format.getFrameSize());
            
            // Save encrypted audio (always as WAV for encrypted files)
            String outputFilePath = outputPath;
            if (!outputPath.toLowerCase().endsWith(".wav")) {
                outputFilePath = outputPath.replaceAll("\\.[^.]+$", "") + "_encrypted.wav";
            }
            
            AudioSystem.write(encryptedAudioStream, AudioFileFormat.Type.WAVE, new File(outputFilePath));
            System.out.println(" Audio encryption completed! Saved as: " + outputFilePath);
            
            encryptedAudioStream.close();
            
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Error: Unsupported audio format. Please use WAV, AU, or AIFF format.");
        } catch (IOException e) {
            System.out.println("Error reading/writing file: " + e.getMessage());
        } catch (CryptoException e) {
            System.out.println("Crypto error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
    
    public static void decryptAudio(String inputPath, String outputPath, String password) {
        try {
            AudioCipher cipher = new AudioCipher();
            cipher.validateKey(password);
            
            // Check if file exists
            File inputFile = new File(inputPath);
            if (!inputFile.exists()) {
                System.out.println("Error: Input file not found: " + inputPath);
                return;
            }
            
            // Read encrypted audio file (should be WAV format)
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputFile);
            AudioFormat format = audioStream.getFormat();
            
            // Convert to byte array
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = audioStream.read(buffer)) != -1) {
                byteStream.write(buffer, 0, bytesRead);
            }
            
            byte[] encryptedData = byteStream.toByteArray();
            audioStream.close();
            
            // Decrypt audio data
            byte[] decryptedData = cipher.decrypt(encryptedData, password);
            
            // Create new audio stream with decrypted data
            ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decryptedData);
            AudioInputStream decryptedAudioStream = new AudioInputStream(
                decryptedStream, format, decryptedData.length / format.getFrameSize());
            
            // Save decrypted audio
            String extension = getFileExtension(outputPath);
            AudioFileFormat.Type fileType = getAudioFileType(extension);
            
            String outputFilePath = outputPath;
            if (fileType == null) {
                fileType = AudioFileFormat.Type.WAVE;
                outputFilePath = outputPath.replaceAll("\\.[^.]+$", "") + ".wav";
            }
            
            AudioSystem.write(decryptedAudioStream, fileType, new File(outputFilePath));
            System.out.println(" Audio decryption completed! Saved as: " + outputFilePath);
            
            decryptedAudioStream.close();
            
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Error: Unsupported audio format. Please use WAV, AU, or AIFF format.");
        } catch (IOException e) {
            System.out.println("Error reading/writing file: " + e.getMessage());
        } catch (CryptoException e) {
            System.out.println("Crypto error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
    
    // === Core Encryption/Decryption Methods ===
    
    private byte[] encryptAudioData(byte[] audioData, String password) {
        byte[] result = new byte[audioData.length];
        Random random = new Random(password.hashCode());
        
        // Generate permutation mapping for byte scrambling
        int[] mapping = new int[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            mapping[i] = i;
        }
        shuffleArray(mapping, new Random(password.hashCode() + 1));
        
        // First, apply XOR and arithmetic encryption
        for (int i = 0; i < audioData.length; i++) {
            int byteValue = audioData[i] & 0xFF;
            
            int key1 = random.nextInt(256);
            int key2 = (password.charAt(i % password.length()) & 0xFF);
            int key3 = (i + password.length()) % 256;
            
            // XOR encryption
            byteValue = (byteValue ^ key1 ^ key2 ^ key3) & 0xFF;
            
            // Arithmetic encryption
            byteValue = (byteValue + key1 + key2 + key3) & 0xFF;
            
            // Apply permutation
            int newPosition = mapping[i];
            result[newPosition] = (byte) byteValue;
        }
        
        // Second pass: additional XOR with position-dependent key
        for (int i = 0; i < result.length; i++) {
            int byteValue = result[i] & 0xFF;
            int posKey = (i * password.length() + password.hashCode()) & 0xFF;
            byteValue = (byteValue ^ posKey) & 0xFF;
            result[i] = (byte) byteValue;
        }
        
        return result;
    }
    
    private byte[] decryptAudioData(byte[] encryptedData, String password) {
        byte[] intermediate = new byte[encryptedData.length];
        byte[] result = new byte[encryptedData.length];
        Random random = new Random(password.hashCode());
        
        // First pass: reverse position-dependent XOR
        for (int i = 0; i < encryptedData.length; i++) {
            int byteValue = encryptedData[i] & 0xFF;
            int posKey = (i * password.length() + password.hashCode()) & 0xFF;
            byteValue = (byteValue ^ posKey) & 0xFF;
            intermediate[i] = (byte) byteValue;
        }
        
        // Generate reverse permutation mapping
        int[] mapping = new int[intermediate.length];
        for (int i = 0; i < intermediate.length; i++) {
            mapping[i] = i;
        }
        shuffleArray(mapping, new Random(password.hashCode() + 1));
        
        // Reverse permutation
        for (int i = 0; i < intermediate.length; i++) {
            int originalPosition = findOriginalPosition(mapping, i);
            if (originalPosition != -1) {
                result[originalPosition] = intermediate[i];
            }
        }
        
        // Reverse XOR and arithmetic encryption
        for (int i = 0; i < result.length; i++) {
            int byteValue = result[i] & 0xFF;
            
            int key1 = random.nextInt(256);
            int key2 = (password.charAt(i % password.length()) & 0xFF);
            int key3 = (i + password.length()) % 256;
            
            // Reverse arithmetic encryption
            byteValue = (byteValue - key1 - key2 - key3 + 768) & 0xFF;
            
            // Reverse XOR encryption
            byteValue = (byteValue ^ key1 ^ key2 ^ key3) & 0xFF;
            
            result[i] = (byte) byteValue;
        }
        
        return result;
    }
    
    // === Helper Methods ===
    
    private int findOriginalPosition(int[] mapping, int scrambledPosition) {
        for (int i = 0; i < mapping.length; i++) {
            if (mapping[i] == scrambledPosition) {
                return i;
            }
        }
        return -1;
    }
    
    private void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
    
    private static String getFileExtension(String filePath) {
        if (filePath.contains(".")) {
            return filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }
    
    private static AudioFileFormat.Type getAudioFileType(String extension) {
        switch (extension.toLowerCase()) {
            case "wav":
                return AudioFileFormat.Type.WAVE;
            case "au":
                return AudioFileFormat.Type.AU;
            case "aiff":
            case "aif":
                return AudioFileFormat.Type.AIFF;
            default:
                return null;
        }
    }
    
    private static boolean isSupportedAudioFormat(String extension) {
        String[] supported = {"wav", "wave", "au", "aiff", "aif", "snd"};
        for (String ext : supported) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    // === Public Methods ===
    
    public static void convertToWav(String inputPath, String outputPath) {
        try {
            String extension = getFileExtension(inputPath);
            
            if (extension.equals("mp3")) {
                System.out.println("MP3 to WAV conversion requires additional libraries.");
                System.out.println("Please use online converters or audio software to convert MP3 to WAV first.");
                return;
            }
            
            if (!isSupportedAudioFormat(extension)) {
                System.out.println("Unsupported format for conversion: " + extension);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(inputPath));
            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, new File(outputPath));
            System.out.println(" Audio converted to WAV: " + outputPath);
            audioStream.close();
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Error: Unsupported audio format: " + getFileExtension(inputPath));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public static String[] getSupportedFormats() {
        return new String[]{"wav", "wave", "au", "aiff", "aif", "snd"};
    }
}