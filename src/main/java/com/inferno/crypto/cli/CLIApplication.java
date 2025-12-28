package com.inferno.crypto.cli;

import java.util.Scanner;

public class CLIApplication {

    private final CommandParser parser;
    private final CommandFactory factory;
    private boolean running;

    public CLIApplication(CommandFactory factory) {
        this.parser = new CommandParser();
        this.factory = factory;
        this.running = true;
    }

    public void run(String[] args) {
        if (args.length > 0) {
            executeCommand(String.join(" ", args));
        } else {
            startInteractive();
        }
    }

    public void startInteractive() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Inferno CipherGuard CLI started. Type 'help'.");

        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine();
            executeCommand(input);
        }
    }

    public void executeCommand(String input) {
        parser.parse(input);
        Command command = factory.getCommand(parser.getCommandName());

        if (command == null) {
            System.out.println("Unknown command. Type 'help'.");
            return;
        }

        if (!command.validate(parser.getArguments())) {
            System.out.println("Usage: " + command.getUsage());
            return;
        }

        command.execute(parser.getArguments());
    }

    public void stop() {
        running = false;
    }

    public void printHelp() {
        factory.getAllCommands().forEach(cmd ->
            System.out.println(cmd.getName() + " - " + cmd.getDescription())
        );
    }
}
