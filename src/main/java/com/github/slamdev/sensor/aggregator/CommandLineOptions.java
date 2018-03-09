package com.github.slamdev.sensor.aggregator;

import java.nio.file.Path;

public class CommandLineOptions {

    private final Path file;

    public CommandLineOptions(Path file) {
        this.file = file;
    }

    public Path getFile() {
        return file;
    }
}
