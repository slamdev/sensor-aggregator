package com.github.slamdev.sensor.aggregator;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class MetricWriterTest {

    private final MetricWriter writer = new MetricWriter();

    @Test
    void shouldWriteEmptyArrayWhenNoMetricsProvided() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        writer.write(stream, emptyList());
        assertThat(stream.toString()).isEqualTo("[]");
    }

    @Test
    void shouldWriteMetricsAsJsonArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        writer.write(stream, singletonList(new Metric("id1", 1D, 2D, singletonList(3D))));
        assertThat(stream.toString()).isEqualTo("[{\"id\":\"id1\",\"average\":1.0,\"median\":2.0,\"mode\":[3.0]}]");
    }
}