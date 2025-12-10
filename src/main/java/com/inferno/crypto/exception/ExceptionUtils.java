package com.inferno.crypto.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtils {
    
    private ExceptionUtils() {
        // Utility class - no instantiation
    }
    
    /**
     * Converts a throwable to a detailed string representation.
     */
    public static String getStackTraceAsString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * Gets the root cause of an exception.
     */
    public static Throwable getRootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
    
    /**
     * Logs an exception to console.
     */
    public static void logToConsole(Throwable throwable, String context) {
        if (throwable == null) {
            return;
        }
        
        if (context != null && !context.isEmpty()) {
            System.err.println("[" + context + "] " + throwable.getClass().getSimpleName() + 
                             ": " + throwable.getMessage());
        } else {
            System.err.println(throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }
        
        // Print full stack trace for debugging
        throwable.printStackTrace();
    }
    
    /**
     * Logs an exception to console (simplified version).
     */
    public static void logToConsole(Throwable throwable) {
        logToConsole(throwable, null);
    }
    
    /**
     * Creates a user-friendly error message.
     */
    public static String createUserFriendlyMessage(Throwable throwable) {
        if (throwable == null) {
            return "An unknown error occurred.";
        }
        
        String message = throwable.getMessage();
        if (message != null && !message.isEmpty()) {
            return message;
        }
        
        return "An error occurred: " + throwable.getClass().getSimpleName();
    }
    
    /**
     * Validates that an object is not null.
     */
    public static <T> T requireNonNull(T obj, String paramName) throws CryptoException {
        if (obj == null) {
            throw new CryptoException(paramName + " cannot be null");
        }
        return obj;
    }
    
    /**
     * Validates that a string is not null or empty.
     */
    public static String requireNonEmpty(String str, String paramName) throws CryptoException {
        requireNonNull(str, paramName);
        if (str.trim().isEmpty()) {
            throw new CryptoException(paramName + " cannot be empty");
        }
        return str;
    }
}