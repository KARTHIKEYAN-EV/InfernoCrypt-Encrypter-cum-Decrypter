package com.inferno.crypto.cli;

import java.util.Map;

public interface Command {

    void execute(Map<String, String> args);

    String getName();

    String getDescription();

    String getUsage();

    boolean validate(Map<String, String> args);
}
