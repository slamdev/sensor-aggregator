package com.github.slamdev.sensor.aggregator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class MetricAggregatorTest {

    private final MetricAggregator aggregator = new MetricAggregator();

    @Test
    void shouldReturnEmptyListForEmptySensors() {
        List<Metric> metrics = aggregator.aggregate(emptyList());
        assertThat(metrics).isEmpty();
    }

    @Test
    void shouldReturnSingleMetricWithSameIdAndTemperatureForSingleSensor() {
        String id = "id";
        double temperature = 1D;
        Sensor sensor = new Sensor(id, 1, temperature);
        List<Metric> metrics = aggregator.aggregate(singletonList(sensor));
        Metric metric = new Metric(id, temperature, temperature, emptyList());
        assertThat(metrics).containsOnly(metric);
    }

    @Test
    void shouldGroupSensorsById() {
        String id1 = "id1";
        String id2 = "id2";
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor(id1, 0, 0),
                new Sensor(id2, 1, 1),
                new Sensor(id1, 2, 2)
        ));
        assertThat(metrics)
                .extracting("id")
                .containsExactlyInAnyOrder(id1, id2);
    }

    @Test
    void shouldOrderSensorsByAverage() {
        String id1 = "id1";
        String id2 = "id2";
        String id3 = "id3";
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor(id1, 3, 3),
                new Sensor(id2, 1, 1),
                new Sensor(id3, 2, 2)
        ));
        assertThat(metrics)
                .extracting("id")
                .containsExactly(id2, id3, id1);
    }

    @Test
    void shouldCountAverageMetricForNonZeroTemperature() {
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor("id", 1, 1),
                new Sensor("id", 1, 2),
                new Sensor("id", 1, 3)
        ));
        assertThat(metrics)
                .extracting("average")
                .containsExactly(2D);
    }

    @Test
    void shouldCountAverageMetricForZeroTemperature() {
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor("id", 1, 0),
                new Sensor("id", 1, 0),
                new Sensor("id", 1, 0)
        ));
        assertThat(metrics)
                .extracting("average")
                .containsExactly(0D);
    }

    @Test
    void shouldCountMedianMetricForNonZeroOddTemperature() {
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor("id", 1, 1),
                new Sensor("id", 1, 2),
                new Sensor("id", 1, 3)
        ));
        assertThat(metrics)
                .extracting("median")
                .containsExactly(2D);
    }

    @Test
    void shouldCountMedianMetricForNonZeroEvenTemperature() {
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor("id", 1, 1),
                new Sensor("id", 1, 2),
                new Sensor("id", 1, 3),
                new Sensor("id", 1, 4)
        ));
        assertThat(metrics)
                .extracting("median")
                .containsExactly(2.5D);
    }

    @Test
    void shouldCountMedianMetricForZeroTemperature() {
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor("id", 1, 0),
                new Sensor("id", 1, 0),
                new Sensor("id", 1, 0)
        ));
        assertThat(metrics)
                .extracting("median")
                .containsExactly(0D);
    }

    @Test
    void shouldCountModeMetricForNonZeroNonRepeatTemperature() {
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor("id", 1, 1),
                new Sensor("id", 1, 2),
                new Sensor("id", 1, 3)
        ));
        assertThat(metrics)
                .extracting("mode")
                .containsExactly(emptyList());
    }

    @Test
    void shouldCountModeMetricForNonZeroSingleRepeatTemperature() {
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor("id", 1, 1),
                new Sensor("id", 1, 2),
                new Sensor("id", 1, 2)
        ));
        assertThat(metrics)
                .extracting("mode")
                .containsExactly(singletonList(2D));
    }

    @Test
    void shouldCountModeMetricForNonZeroMultiRepeatTemperature() {
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor("id", 1, 1),
                new Sensor("id", 1, 1),
                new Sensor("id", 1, 2),
                new Sensor("id", 1, 2),
                new Sensor("id", 1, 3)
        ));
        assertThat(metrics)
                .extracting("mode")
                .containsExactly(asList(1D, 2D));
    }

    @Test
    void shouldOrderModeMetric() {
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor("id", 1, 2),
                new Sensor("id", 1, 1),
                new Sensor("id", 1, 2),
                new Sensor("id", 1, 1),
                new Sensor("id", 1, 3)
        ));
        assertThat(metrics)
                .extracting("mode")
                .containsExactly(asList(1D, 2D));
    }

    @Test
    void shouldCountModeMetricForZeroTemperature() {
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor("id", 1, 0),
                new Sensor("id", 1, 0),
                new Sensor("id", 1, 0)
        ));
        assertThat(metrics)
                .extracting("mode")
                .containsExactly(emptyList());
    }

    @Test
    void shouldRoundMetricsTo2DecimalPoints() {
        List<Metric> metrics = aggregator.aggregate(asList(
                new Sensor("id", 1, 1.155D),
                new Sensor("id", 1, 1.155D)
        ));
        double expected = 1.16D;
        assertThat(metrics).extracting("average").containsExactly(expected);
        assertThat(metrics).extracting("median").containsExactly(expected);
    }
}
