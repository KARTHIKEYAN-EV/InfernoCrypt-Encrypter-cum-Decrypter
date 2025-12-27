package com.inferno.crypto.factory;

import com.inferno.crypto.mode.*;

import java.util.Arrays;
import java.util.List;

public class AlgorithmFactory {

    public static EncryptionMode getEncryptionMode(String modeName, byte[] iv, byte[] aad) {
        switch (modeName.toUpperCase()) {
            case "ECB":
                return new ECB();
            case "CBC":
                return new CBC(iv);
            case "GCM":
                return new GCM(iv, aad);
            default:
                throw new IllegalArgumentException("Unsupported mode: " + modeName);
        }
    }

    public static List<String> getSupportedModes() {
        return Arrays.asList("ECB", "CBC", "GCM");
    }

    public static String getDefaultAlgorithm() {
        return "AES";
    }
}
