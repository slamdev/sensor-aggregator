package com.github.slamdev.sensor.aggregator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SensorReader {

    static final int INVALID_FILE_CONTENT_CODE = 3;

    private static final Gson GSON = new Gson();

    private static final Type LIST_TYPE = new TypeToken<List<Sensor>>() {
    }.getType();

    public List<Sensor> read(Path file) {
        String content = readFile(file);
        return convertJsonToSensors(content);
    }

    private List<Sensor> convertJsonToSensors(String content) {
        try {
            return GSON.fromJson(content, LIST_TYPE);
        } catch (JsonSyntaxException e) {
            throw new UserInputException(INVALID_FILE_CONTENT_CODE, "File should contain valid JSON array", e);
        }
    }

    private String readFile(Path file) {
        try {
            byte[] bytes = Files.readAllBytes(file);
            return new String(bytes, UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
