package com.inferno.crypto.algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SubstitutionCipher extends ClassicalCipher {

    private final Map<Character,Character> map;

    public SubstitutionCipher(Map<Character, Character> map) {
        this.map = map;
    }

    @Override
    public boolean validateKey(String key) {
        return true;
    }

    @Override
    public byte[] encrypt(byte[] data, java.security.Key ignored) {
        String text = new String(data);
        StringBuilder out = new StringBuilder();
        for (char c : text.toCharArray())
            out.append(map.getOrDefault(c,c));
        return out.toString().getBytes();
    }

    @Override
    public byte[] decrypt(byte[] data, java.security.Key ignored) {
        Map<Character,Character> inv = new HashMap<>();
        map.forEach((k,v)->inv.put(v,k));
        String text = new String(data);
        StringBuilder out = new StringBuilder();
        for (char c : text.toCharArray())
            out.append(inv.getOrDefault(c,c));
        return out.toString().getBytes();
    }

    public static Map<Character,Character> generateRandomMap() {
        Map<Character,Character> map = new HashMap<>();
        Random rnd = new Random();
        for (char c = 32; c < 127; c++)
            map.put(c,(char)(rnd.nextInt(95)+32));
        return map;
    }

    @Override
    public boolean supportsMode(com.inferno.crypto.mode.EncryptionMode mode) {
        return false;
    }

    @Override
    public String getAlgorithmName() {
        return "Substitution";
    }

    @Override
    public int getKeySize() {
        return map.size();
    }
}