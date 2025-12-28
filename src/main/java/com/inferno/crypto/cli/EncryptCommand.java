package com.inferno.crypto.cli;

import com.inferno.crypto.service.EncryptionService;

import java.util.Map;

public class EncryptCommand implements Command {

    private final EncryptionService encryptionService;

    public EncryptCommand(EncryptionService service) {
        this.encryptionService = service;
    }

    @Override
    public void execute(Map<String, String> args) {
        encryptionService.encrypt(args);
        System.out.println("Encryption completed.");
    }

    @Override
    public String getName() {
        return "encrypt";
    }

    @Override
    public String getDescription() {
        return "Encrypt a file";
    }

    @Override
    public String getUsage() {
        return "encrypt input=<file> output=<file> algo=<AES>";
    }

    @Override
    public boolean validate(Map<String, String> args) {
        return args.containsKey("input") && args.containsKey("output");
    }
}
