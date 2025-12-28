package com.inferno.crypto.cli;

import java.util.Map;

public class HelpCommand implements Command {

    private final Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(Map<String, String> args) {
        commands.values().forEach(cmd ->
            System.out.println(cmd.getUsage() + " - " + cmd.getDescription())
        );
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Show help";
    }

    @Override
    public String getUsage() {
        return "help";
    }

    @Override
    public boolean validate(Map<String, String> args) {
        return true;
    }
}
