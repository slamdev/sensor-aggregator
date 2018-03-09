package com.github.slamdev.sensor.aggregator;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;

public class Metric {
    private final String id;
    private final double average;
    private final double median;
    private final List<Double> mode;

    public Metric(String id, double average, double median, List<Double> mode) {
        this.id = id;
        this.average = average;
        this.median = median;
        this.mode = unmodifiableList(mode);
    }

    public String getId() {
        return id;
    }

    public double getAverage() {
        return average;
    }

    public double getMedian() {
        return median;
    }

    public List<Double> getMode() {
        return mode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Metric that = (Metric) o;
        return Double.compare(that.average, average) == 0
                && Double.compare(that.median, median) == 0
                && Objects.equals(id, that.id)
                && Objects.equals(mode, that.mode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, average, median, mode);
    }

    @Override
    public String toString() {
        return "Metric{"
                + "id='" + id + '\''
                + ", average=" + average
                + ", median=" + median
                + ", mode=" + mode
                + '}';
    }
}
