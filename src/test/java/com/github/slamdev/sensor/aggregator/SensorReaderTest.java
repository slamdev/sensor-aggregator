package com.github.slamdev.sensor.aggregator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.github.slamdev.sensor.aggregator.SensorReader.INVALID_FILE_CONTENT_CODE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SensorReaderTest {

    private static final List<Path> TEMP_FILES = new ArrayList<>();

    private final SensorReader reader = new SensorReader();

    @AfterAll
    static void cleanUpTempFiles() {
        TEMP_FILES.forEach(path -> {
            try {
                Files.delete(path);
            } catch (IOException ignore) {
            }
        });
        TEMP_FILES.clear();
    }

    @Test
    void shouldThrowExceptionWhenFileContentIsNotValidJson() {
        Path file = createFile("invalid data");
        assertThatThrownBy(() -> reader.read(file))
                .isInstanceOf(UserInputException.class)
                .hasMessage("File should contain valid JSON array")
                .hasFieldOrPropertyWithValue("code", INVALID_FILE_CONTENT_CODE);
    }

    @Test
    void shouldConvertJsonArrayToListOfSensors() {
        String id = "a";
        long timestamp = 1509493641L;
        double temperature = 3.53D;
        Path file = createFile(String.format("[{\"id\": \"%s\",\"timestamp\": %d,\"temperature\": %f}]",
                id, timestamp, temperature));
        List<Sensor> sensors = reader.read(file);
        Sensor sensor = new Sensor(id, timestamp, temperature);
        assertThat(sensors).containsOnly(sensor);
    }

    private Path createFile(String content) {
        try {
            Path file = Files.createTempFile(null, null);
            Files.write(file, content.getBytes(UTF_8));
            TEMP_FILES.add(file);
            return file;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
