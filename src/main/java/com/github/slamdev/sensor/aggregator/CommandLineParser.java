package com.github.slamdev.sensor.aggregator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandLineParser {

    static final int INVALID_ARGUMENT_CODE = 2;

    public CommandLineOptions parse(String... args) {
        validateArgs(args);
        Path file = Paths.get(args[0]);
        validateFile(file);
        return new CommandLineOptions(file);
    }

    private void validateArgs(String... args) {
        if (args.length == 0) {
            throw new UserInputException(INVALID_ARGUMENT_CODE, "Path to a file should be provided");
        }
        if (args.length > 1) {
            throw new UserInputException(INVALID_ARGUMENT_CODE, "Only one argument is supported");
        }
    }

    private void validateFile(Path file) {
        if (!Files.isRegularFile(file)) {
            throw new UserInputException(INVALID_ARGUMENT_CODE, "Passed argument is not a file");
        }
    }
}
