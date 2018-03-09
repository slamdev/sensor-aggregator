package com.github.slamdev.sensor.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private final CommandLineParser commandLineParser;

    private final SensorReader sensorReader;

    private final MetricAggregator metricAggregator;

    private final MetricWriter metricWriter;

    public static void main(String[] args) {
        CommandLineParser commandLineParser = new CommandLineParser();
        SensorReader sensorReader = new SensorReader();
        MetricAggregator metricAggregator = new MetricAggregator();
        MetricWriter metricWriter = new MetricWriter();
        new Application(commandLineParser, sensorReader, metricAggregator, metricWriter)
                .run(args);
    }

    private Application(CommandLineParser commandLineParser, SensorReader sensorReader,
                        MetricAggregator metricAggregator, MetricWriter metricWriter) {
        this.commandLineParser = commandLineParser;
        this.sensorReader = sensorReader;
        this.metricAggregator = metricAggregator;
        this.metricWriter = metricWriter;
    }

    public void run(String... args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> handleException(e));
        CommandLineOptions options = commandLineParser.parse(args);
        List<Sensor> sensors = sensorReader.read(options.getFile());
        List<Metric> metrics = metricAggregator.aggregate(sensors);
        metricWriter.write(System.out, metrics);
    }

    private static void handleException(Throwable throwable) {
        if (throwable instanceof UserInputException) {
            UserInputException exception = (UserInputException) throwable;
            System.err.println(exception.getMessage());
            System.exit(exception.getCode());
        }
        LOGGER.error("", throwable);
    }
}
