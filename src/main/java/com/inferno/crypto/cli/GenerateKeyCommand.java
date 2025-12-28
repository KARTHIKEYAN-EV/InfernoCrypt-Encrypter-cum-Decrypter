package com.inferno.crypto.cli;

import com.inferno.crypto.service.KeyManagementService;

import java.util.Map;

public class GenerateKeyCommand implements Command {

    private final KeyManagementService keyService;

    public GenerateKeyCommand(KeyManagementService service) {
        this.keyService = service;
    }

    @Override
    public void execute(Map<String, String> args) {
        keyService.generateKey(args);
        System.out.println("Key generated successfully.");
    }

    @Override
    public String getName() {
        return "genkey";
    }

    @Override
    public String getDescription() {
        return "Generate cryptographic key";
    }

    @Override
    public String getUsage() {
        return "genkey algo=AES size=256";
    }

    @Override
    public boolean validate(Map<String, String> args) {
        return args.containsKey("algo");
    }
}
