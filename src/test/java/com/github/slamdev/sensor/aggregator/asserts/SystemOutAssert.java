package com.github.slamdev.sensor.aggregator.asserts;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.assertj.core.api.StringAssert;
import org.assertj.core.api.WritableAssertionInfo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;

public final class SystemOutAssert extends StringAssert {

    private SystemOutAssert(Runnable runnable) {
        super(captureSystemOutStream(runnable));
    }

    public static SystemOutAssert assertThat(Runnable runnable) {
        return new SystemOutAssert(runnable);
    }

    private static String captureSystemOutStream(Runnable runnable) {
        ByteArrayOutputStream newStream = new ByteArrayOutputStream();
        PrintStream originalStream = System.out;
        System.setOut(new PrintStream(newStream));
        runnable.run();
        System.setOut(originalStream);
        return newStream.toString();
    }

    public SystemOutAssert isJsonEqualTo(String expected) {
        isNotNull();
        JsonParser parser = new JsonParser();
        JsonElement o1 = parser.parse(expected);
        JsonElement o2 = parser.parse(actual);
        if (!Objects.equals(o1, o2)) {
            org.assertj.core.internal.Objects.instance().assertEqual(new WritableAssertionInfo(), actual, expected);
        }
        return this;
    }
}
