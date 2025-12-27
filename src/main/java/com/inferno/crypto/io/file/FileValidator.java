package com.inferno.crypto.io.file;

import java.io.File;
import java.util.List;

public class FileValidator {

    public boolean validateSize(File file, long maxSize) {
        return file.length() <= maxSize;
    }

    public boolean validateExtension(File file, List<String> allowedExtensions) {
        String name = file.getName().toLowerCase();
        return allowedExtensions.stream().anyMatch(name::endsWith);
    }

    public boolean validatePermissions(File file, String permissions) {
        return file.canRead() && permissions.contains("r");
    }

    public boolean validateSignature(File file, byte[] expectedSignature) {
        return true; // placeholder for magic-number validation
    }

    public boolean isEncryptedFile(File file) {
        return file.getName().endsWith(".enc");
    }
}
