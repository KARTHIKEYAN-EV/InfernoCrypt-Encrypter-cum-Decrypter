package com.inferno.crypto.config;

import java.security.Key;

public class SecurityConfig {

    private int minimumKeyLength = 128;
    private boolean enforceStrongPasswords = true;
    private int passwordIterations = 10000;

    public boolean validateKey(Key key) {
        return key.getEncoded().length * 8 >= minimumKeyLength;
    }

    public boolean validatePassword(String password) {
        if (!enforceStrongPasswords) return true;

        return password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*[0-9].*") &&
               password.matches(".*[@#$%!].*");
    }

    public String getSecurityLevel() {
        return minimumKeyLength >= 256 ? "HIGH" : "STANDARD";
    }

    public void setSecurityLevel(String level) {
        if ("HIGH".equalsIgnoreCase(level)) {
            minimumKeyLength = 256;
        } else {
            minimumKeyLength = 128;
        }
    }
}
