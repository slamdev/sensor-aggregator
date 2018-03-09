package com.github.slamdev.sensor.aggregator;

public class UserInputException extends IllegalArgumentException {

    private final int code;

    public UserInputException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public UserInputException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
