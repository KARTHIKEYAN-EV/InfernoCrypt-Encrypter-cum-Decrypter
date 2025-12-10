package com.inferno.crypto.main;

import com.inferno.crypto.algorithm.CipherAlgorithm;
import com.inferno.crypto.algorithm.AudioCipher;
import com.inferno.crypto.algorithm.ImageCipher;
import com.inferno.crypto.algorithm.VideoCipher;
import com.inferno.crypto.exception.CryptoException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Testing All Crypto Algorithms ===\n");
        
        // Test all algorithms
        testAudioCipher();
        //testImageCipher();
        //testVideoCipher();
    }
    
    private static void testAudioCipher() {
        try {
            System.out.println("--- Testing AudioCipher ---");
            
            // 1. Create algorithm instance
            CipherAlgorithm cipher = new AudioCipher();
            
            // 2. Display algorithm info
            System.out.println("Algorithm: " + cipher.getName());
            System.out.println("Description: " + cipher.getDescription());
            System.out.println("Key Size: " + cipher.getKeySize() + " bits");
            System.out.println("Symmetric: " + cipher.isSymmetric());
            System.out.println("Requires IV: " + cipher.requiresIV());
            
            // 3. Test byte array encryption/decryption
            System.out.println("\nTesting basic byte array encryption...");
            String testData = "This is test audio data for encryption testing";
            byte[] testBytes = testData.getBytes("UTF-8");
            
            System.out.println("Original: " + testData);
            
            byte[] encryptedBytes = cipher.encrypt(testBytes, "audiopassword123");
            System.out.println("Encrypted byte array length: " + encryptedBytes.length);
            
            byte[] decryptedBytes = cipher.decrypt(encryptedBytes, "audiopassword123");
            String decryptedText = new String(decryptedBytes, "UTF-8");
            System.out.println("Decrypted: " + decryptedText);
            
            System.out.println("Basic encryption test: " + 
                (testData.equals(decryptedText) ? "✓ PASSED" : "✗ FAILED"));
            
            // 4. Test with wrong password
            System.out.println("\nTesting wrong password:");
            try {
                byte[] wrongDecrypt = cipher.decrypt(encryptedBytes, "wrongpassword");
                String wrongResult = new String(wrongDecrypt, "UTF-8");
                System.out.println("Wrong password result (should be garbage): " + 
                    (wrongResult.length() > 20 ? wrongResult.substring(0, 20) + "..." : wrongResult));
            } catch (Exception e) {
                System.out.println("Error with wrong password: " + e.getMessage());
            }
            
            // 5. Test password validation
            System.out.println("\n--- Testing Password Validation ---");
            try {
                cipher.validateKey("abc"); // This should fail (less than 4 chars)
                System.out.println("✗ Password validation test FAILED - should have thrown exception");
            } catch (CryptoException e) {
                System.out.println("✓ Password validation working: " + e.getMessage());
            }
            
            try {
                cipher.validateKey("validpassword");
                System.out.println("✓ Valid password accepted");
            } catch (CryptoException e) {
                System.out.println("✗ Valid password rejected: " + e.getMessage());
            }
            
            // 6. Test file encryption if audio file exists
            System.out.println("\n--- Testing File Encryption ---");
            String testAudioPath = "C:\\Users\\karth\\.vscode\\ev\\audiotest.mp3"; // or "test.mp3", "test.mp4", etc.
            File testAudioFile = new File(testAudioPath);
            
            if (testAudioFile.exists()) {
                System.out.println("Found audio file: " + testAudioPath);
                System.out.println("File size: " + testAudioFile.length() + " bytes");
                
                String encryptedAudioPath = "C:\\Users\\karth\\.vscode\\ev\\test_encrypted.wav";
                String decryptedAudioPath = "C:\\Users\\karth\\.vscode\\ev\\test_decrypted.wav";
                
                // Encrypt using static method
                System.out.println("\nEncrypting audio file...");
                AudioCipher.encryptAudio(testAudioPath, encryptedAudioPath, "mypassword123");
                
                // Decrypt using static method
                System.out.println("Decrypting audio file...");
                AudioCipher.decryptAudio(encryptedAudioPath, decryptedAudioPath, "mypassword123");
                
                // Verify file sizes
                File encryptedFile = new File(encryptedAudioPath);
                File decryptedFile = new File(decryptedAudioPath);
                
                System.out.println("\nFile sizes:");
                System.out.println("Original: " + testAudioFile.length() + " bytes");
                System.out.println("Encrypted: " + encryptedFile.length() + " bytes");
                System.out.println("Decrypted: " + decryptedFile.length() + " bytes");
                
                // Check if decrypted file size matches original
                if (testAudioFile.length() == decryptedFile.length()) {
                    System.out.println("✓ File size test PASSED");
                } else {
                    System.out.println("✗ File size test FAILED");
                }
                
            } else {
                System.out.println("\nTest audio file not found at: " + testAudioPath);
                System.out.println("Creating a test audio file for demonstration...");
                
                // Create a simple test WAV file
                createTestAudioFile("test_audio.wav");
                System.out.println("Created test audio file: test_audio.wav");
                
                // Test with the created file
                if (new File("test_audio.wav").exists()) {
                    System.out.println("\nTesting with created audio file...");
                    AudioCipher.encryptAudio("test_audio.wav", "test_audio_encrypted.wav", "testpass");
                    AudioCipher.decryptAudio("test_audio_encrypted.wav", "test_audio_decrypted.wav", "testpass");
                    
                    File original = new File("test_audio.wav");
                    File decrypted = new File("test_audio_decrypted.wav");
                    
                    if (original.length() == decrypted.length()) {
                        System.out.println("✓ Created audio file test PASSED");
                    } else {
                        System.out.println("✗ Created audio file test FAILED");
                    }
                }
            }
            
            // 7. Test audio format conversion (optional)
            System.out.println("\n--- Testing Audio Format Conversion ---");
            if (new File("test_audio.wav").exists()) {
                AudioCipher.convertToWav("test_audio.wav", "test_converted.wav");
                System.out.println("✓ Audio conversion test completed");
            }
            
            System.out.println("\n✓ AudioCipher test sequence completed!\n");
            
        } catch (CryptoException e) {
            System.out.println("Crypto error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testImageCipher() {
        try {
            System.out.println("--- Testing ImageCipher ---");
            
            // 1. Create algorithm instance
            CipherAlgorithm cipher = new ImageCipher();
            
            // 2. Display algorithm info
            System.out.println("Algorithm: " + cipher.getName());
            System.out.println("Description: " + cipher.getDescription());
            
            // 3. Test encryption
            System.out.println("\nTesting with image file...");
            String imagePath = "image.png";
            File imageFile = new File(imagePath);
            
            if (imageFile.exists()) {
                System.out.println("Reading " + imagePath + "...");
                byte[] original = Files.readAllBytes(Paths.get(imagePath));
                
                System.out.println("Encrypting...");
                byte[] encrypted = cipher.encrypt(original, "mypassword");
                
                System.out.println("Saving encrypted.png...");
                Files.write(Paths.get("encrypted.png"), encrypted);
                
                // 4. Test decryption
                System.out.println("Decrypting...");
                byte[] decrypted = cipher.decrypt(encrypted, "mypassword");
                
                System.out.println("Saving decrypted.png...");
                Files.write(Paths.get("decrypted.png"), decrypted);
                
                System.out.println("✓ ImageCipher test completed successfully!\n");
            } else {
                System.out.println("Image file not found: " + imagePath);
                System.out.println("Skipping image file tests...\n");
            }
            
        } catch (CryptoException e) {
            System.out.println("Crypto error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testVideoCipher() {
        try {
            System.out.println("--- Testing VideoCipher ---");
            
            // 1. Create algorithm instance
            VideoCipher videoCipher = new VideoCipher();
            
            // 2. Display algorithm info
            System.out.println("Algorithm: " + videoCipher.getName());
            System.out.println("Description: " + videoCipher.getDescription());
            System.out.println("Key Size: " + videoCipher.getKeySize() + " bits");
            System.out.println("Symmetric: " + videoCipher.isSymmetric());
            System.out.println("Requires IV: " + videoCipher.requiresIV());
            
            // 3. Display supported modes
            String[] modes = videoCipher.getSupportedModes();
            System.out.print("Supported Modes: ");
            for (String mode : modes) {
                System.out.print(mode + " ");
            }
            System.out.println();
            
            // 4. Test byte array encryption/decryption
            System.out.println("\nTesting basic byte array encryption...");
            String testData = "This is test video data for encryption testing";
            byte[] testBytes = testData.getBytes("UTF-8");
            
            System.out.println("Original: " + testData);
            
            byte[] encryptedBytes = videoCipher.encrypt(testBytes, "videopassword123");
            byte[] decryptedBytes = videoCipher.decrypt(encryptedBytes, "videopassword123");
            
            String decryptedText = new String(decryptedBytes, "UTF-8");
            System.out.println("Decrypted: " + decryptedText);
            System.out.println("Basic encryption test: " + 
                (testData.equals(decryptedText) ? "✓ PASSED" : "✗ FAILED"));
            
            // 5. Test file encryption/decryption
            System.out.println("\n--- Testing File Encryption Strategies ---");
            
            String testVideoPath = "test.mp4";
            File testVideoFile = new File(testVideoPath);
            
            if (testVideoFile.exists()) {
                // Test all three strategies
                for (int strategy = 1; strategy <= 3; strategy++) {
                    String strategyName = getVideoStrategyName(strategy);
                    System.out.println("\nTesting " + strategyName + " strategy:");
                    
                    String encryptedPath = "test_encrypted_" + strategy + ".mp4";
                    String decryptedPath = "test_decrypted_" + strategy + ".mp4";
                    
                    try {
                        videoCipher.encryptVideo(testVideoPath, encryptedPath, "securepass123", strategy);
                        System.out.println("  Encryption completed");
                        
                        videoCipher.decryptVideo(encryptedPath, decryptedPath, "securepass123");
                        System.out.println("  Decryption completed");
                        
                        // Verify file sizes
                        long originalSize = testVideoFile.length();
                        long decryptedSize = new File(decryptedPath).length();
                        
                        if (originalSize == decryptedSize) {
                            System.out.println("  ✓ File sizes match: " + originalSize + " bytes");
                        } else {
                            System.out.println("  ✗ File sizes differ: " + originalSize + " vs " + decryptedSize);
                        }
                        
                    } catch (CryptoException e) {
                        System.out.println("  Error: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("\nTest video file not found at: " + testVideoPath);
                System.out.println("Creating a test video info display...");
                
                // Display supported formats
                String[] supportedFormats = VideoCipher.getSupportedFormats();
                System.out.println("\nVideoCipher Supported Formats:");
                for (String format : supportedFormats) {
                    System.out.println("  - ." + format);
                }
            }
            
            System.out.println("\n✓ VideoCipher test sequence completed!\n");
            
        } catch (CryptoException e) {
            System.out.println("Crypto error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Helper method to create a simple test audio file
    private static void createTestAudioFile(String filename) {
        try {
            // Create a simple sine wave audio
            float sampleRate = 44100;
            double frequency = 440; // A4 note
            double amplitude = 0.5;
            double duration = 2.0; // 2 seconds
            
            int numSamples = (int) (duration * sampleRate);
            byte[] audioData = new byte[numSamples * 2]; // 16-bit = 2 bytes per sample
            
            for (int i = 0; i < numSamples; i++) {
                double time = i / sampleRate;
                double sample = amplitude * Math.sin(2 * Math.PI * frequency * time);
                short shortSample = (short) (sample * Short.MAX_VALUE);
                
                // Little endian
                audioData[2 * i] = (byte) (shortSample & 0xFF);
                audioData[2 * i + 1] = (byte) ((shortSample >> 8) & 0xFF);
            }
            
            // Write as WAV file
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filename)) {
                // WAV header (simplified)
                writeWavHeader(fos, numSamples, sampleRate);
                fos.write(audioData);
            }
            
        } catch (Exception e) {
            System.out.println("Could not create test audio file: " + e.getMessage());
        }
    }
    
    private static void writeWavHeader(java.io.OutputStream os, int numSamples, float sampleRate) 
            throws IOException {
        int numChannels = 1; // Mono
        int bitsPerSample = 16;
        int byteRate = (int) (sampleRate * numChannels * bitsPerSample / 8);
        int blockAlign = numChannels * bitsPerSample / 8;
        int dataSize = numSamples * blockAlign;
        int fileSize = 36 + dataSize;
        
        // RIFF header
        os.write("RIFF".getBytes());
        writeLittleEndianInt(os, fileSize);
        os.write("WAVE".getBytes());
        
        // fmt chunk
        os.write("fmt ".getBytes());
        writeLittleEndianInt(os, 16); // Chunk size
        writeLittleEndianShort(os, 1); // Audio format (PCM)
        writeLittleEndianShort(os, numChannels);
        writeLittleEndianInt(os, (int) sampleRate);
        writeLittleEndianInt(os, byteRate);
        writeLittleEndianShort(os, blockAlign);
        writeLittleEndianShort(os, bitsPerSample);
        
        // data chunk
        os.write("data".getBytes());
        writeLittleEndianInt(os, dataSize);
    }
    
    private static void writeLittleEndianInt(java.io.OutputStream os, int value) throws IOException {
        os.write(value & 0xFF);
        os.write((value >> 8) & 0xFF);
        os.write((value >> 16) & 0xFF);
        os.write((value >> 24) & 0xFF);
    }
    
    private static void writeLittleEndianShort(java.io.OutputStream os, int value) throws IOException {
        os.write(value & 0xFF);
        os.write((value >> 8) & 0xFF);
    }
    
    private static String getVideoStrategyName(int strategy) {
        switch (strategy) {
            case VideoCipher.STRATEGY_FULL_FILE: return "Full File";
            case VideoCipher.STRATEGY_SELECTIVE_FRAMES: return "Selective Frames";
            case VideoCipher.STRATEGY_HYBRID: return "Hybrid";
            default: return "Unknown";
        }
    }
}