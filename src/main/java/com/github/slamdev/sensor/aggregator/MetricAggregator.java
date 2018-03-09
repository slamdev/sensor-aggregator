package com.github.slamdev.sensor.aggregator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingDouble;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class MetricAggregator {

    public List<Metric> aggregate(List<Sensor> sensors) {
        return sensors.stream()
                .collect(groupingBy(Sensor::getId))
                .entrySet().stream()
                .map(e -> calculateMetric(e.getValue()))
                .sorted(comparingDouble(Metric::getAverage))
                .collect(toList());
    }

    private Metric calculateMetric(List<Sensor> sensors) {
        List<Double> temperatures = sensors.stream()
                .map(Sensor::getTemperature)
                .sorted().collect(toList());
        double average = calculateAverage(temperatures);
        double median = calculateMedian(temperatures);
        List<Double> mode = calculateMode(temperatures);
        return new Metric(sensors.get(0).getId(), average, median, mode);
    }

    /**
     * sum of the values divided by the number of items
     */
    private double calculateAverage(List<Double> list) {
        return round(stream(list).average().orElse(Double.NaN));
    }

    /**
     * middle value in a sorted list of items
     * for even list it is an average between two middle values
     */
    private double calculateMedian(List<Double> list) {
        OptionalDouble median;
        if (list.size() % 2 == 0) {
            median = stream(list).skip(list.size() / 2 - 1).limit(2).average();
        } else {
            median = stream(list).skip(list.size() / 2).findFirst();
        }
        return round(median.orElse(Double.NaN));
    }

    /**
     * most frequently occurring values in a list
     * can be empty if all values occurring the same amount of times
     */
    private List<Double> calculateMode(List<Double> list) {
        Map<Double, Long> countFrequencies = list.stream().collect(groupingBy(identity(), counting()));
        if (countFrequencies.values().stream().distinct().count() == 1) {
            return emptyList();
        }
        long maxFrequency = countFrequencies.values().stream()
                .mapToLong(count -> count)
                .max().orElse(-1);
        return countFrequencies.entrySet().stream()
                .filter(e -> e.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .map(this::round)
                .sorted()
                .collect(toList());
    }

    private double round(double value) {
        return new BigDecimal(String.valueOf(value))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private DoubleStream stream(List<Double> list) {
        return list.stream().mapToDouble(Double::doubleValue);
    }
}
