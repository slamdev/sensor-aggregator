package com.github.slamdev.sensor.aggregator;

import java.util.Objects;

public class Sensor {
    private final String id;
    private final long timestamp;
    private final double temperature;

    public Sensor(String id, long timestamp, double temperature) {
        this.id = id;
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getTemperature() {
        return temperature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sensor sensor = (Sensor) o;
        return timestamp == sensor.timestamp
                && Double.compare(sensor.temperature, temperature) == 0
                && Objects.equals(id, sensor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, temperature);
    }

    @Override
    public String toString() {
        return "Sensor{"
                + "id='" + id + '\''
                + ", timestamp=" + timestamp
                + ", temperature=" + temperature
                + '}';
    }
}
