package com.inferno.crypto.cli;

import java.util.*;

public class CommandParser {

    private String command;
    private Map<String, String> arguments = new HashMap<>();
    private List<String> options = new ArrayList<>();

    public CommandParser() {}

    public Command parse(String input) {
        String[] tokens = input.trim().split("\\s+");
        command = tokens[0];

        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].startsWith("--")) {
                options.add(tokens[i].substring(2));
            } else if (tokens[i].contains("=")) {
                String[] kv = tokens[i].split("=", 2);
                arguments.put(kv[0], kv[1]);
            }
        }
        return null; // actual command resolved by factory
    }

    public String getCommandName() {
        return command;
    }

    public String getArgument(String name) {
        return arguments.get(name);
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean hasOption(String option) {
        return options.contains(option);
    }
}
