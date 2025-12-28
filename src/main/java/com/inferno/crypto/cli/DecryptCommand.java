package com.inferno.crypto.cli;

import com.inferno.crypto.service.DecryptionService;

import java.util.Map;

public class DecryptCommand implements Command {

    private final DecryptionService decryptionService;

    public DecryptCommand(DecryptionService service) {
        this.decryptionService = service;
    }

    @Override
    public void execute(Map<String, String> args) {
        decryptionService.decrypt(args);
        System.out.println("Decryption completed.");
    }

    @Override
    public String getName() {
        return "decrypt";
    }

    @Override
    public String getDescription() {
        return "Decrypt a file";
    }

    @Override
    public String getUsage() {
        return "decrypt input=<file> output=<file>";
    }

    @Override
    public boolean validate(Map<String, String> args) {
        return args.containsKey("input") && args.containsKey("output");
    }
}
