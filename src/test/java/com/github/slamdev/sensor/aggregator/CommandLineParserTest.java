package com.github.slamdev.sensor.aggregator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.slamdev.sensor.aggregator.CommandLineParser.INVALID_ARGUMENT_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CommandLineParserTest {

    private static Path file;
    private final CommandLineParser parser = new CommandLineParser();

    @BeforeAll
    static void createTempFile() {
        try {
            file = Files.createTempFile(null, null);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @AfterAll
    static void deleteTempFile() {
        try {
            Files.delete(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void shouldThrowExceptionWhenNoArgumentsPassed() {
        assertThatThrownBy(parser::parse)
                .isInstanceOf(UserInputException.class)
                .hasMessage("Path to a file should be provided")
                .hasFieldOrPropertyWithValue("code", INVALID_ARGUMENT_CODE);
    }

    @Test
    void shouldThrowExceptionWhenMoreThanOneArgumentPassed() {
        assertThatThrownBy(() -> parser.parse("1", "2"))
                .isInstanceOf(UserInputException.class)
                .hasMessage("Only one argument is supported")
                .hasFieldOrPropertyWithValue("code", INVALID_ARGUMENT_CODE);
    }

    @Test
    void shouldThrowExceptionWhenPassedArgumentIsNotAFile() {
        assertThatThrownBy(() -> parser.parse("not-existing-file"))
                .isInstanceOf(UserInputException.class)
                .hasMessage("Passed argument is not a file")
                .hasFieldOrPropertyWithValue("code", INVALID_ARGUMENT_CODE);
    }

    @Test
    void shouldReturnFileByPath() {
        assertThat(parser.parse(file.toString()).getFile())
                .isEqualTo(file);
    }
}
