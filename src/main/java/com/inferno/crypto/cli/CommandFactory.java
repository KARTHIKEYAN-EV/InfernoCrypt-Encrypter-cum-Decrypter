package com.inferno.crypto.cli;

import java.util.*;

public class CommandFactory {

    private final Map<String, Command> commandMap = new HashMap<>();

    public CommandFactory() {}

    public void registerCommand(String name, Command command) {
        commandMap.put(name.toLowerCase(), command);
    }

    public Command getCommand(String name) {
        return commandMap.get(name.toLowerCase());
    }

    public List<Command> getAllCommands() {
        return new ArrayList<>(commandMap.values());
    }

    public Command createCommand(String name) {
        return getCommand(name);
    }
}
