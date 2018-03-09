package com.github.slamdev.sensor.aggregator;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MetricWriter {

    private static final Gson GSON = new Gson();

    public void write(OutputStream stream, List<Metric> metrics) {
        String json = GSON.toJson(metrics);
        try {
            stream.write(json.getBytes(UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
