package com.github.slamdev.sensor.aggregator.asserts;

import org.assertj.core.api.AbstractAssert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.Permission;
import java.util.Objects;

/**
 * Assertion methods for System.exit code and message.
 * <p>
 * To create an instance of this class, invoke <code>{@link SystemExitAssert#assertThat(Runnable)}</code>
 * </p>
 */
public final class SystemExitAssert extends AbstractAssert<SystemExitAssert, SystemExitAssert.ExitData> {

    private SystemExitAssert(Runnable runnable) {
        super(execute(runnable), SystemExitAssert.class);
    }

    public static SystemExitAssert assertThat(Runnable runnable) {
        return new SystemExitAssert(runnable);
    }

    private static String removeNewLines(String string) {
        return string.replace("\r\n", "\n")
                .replace("\n", "");
    }

    /**
     * Java SecurityManager should be mocked to prevent exiting via System.exit
     */
    private static ExitData execute(Runnable runnable) {
        NoOpSecurityManager noOpSecurityManager = new NoOpSecurityManager();
        SecurityManager original = System.getSecurityManager();
        System.setSecurityManager(noOpSecurityManager);
        String output = captureSystemErrorStream(() -> executeInSeparateThread(runnable));
        System.setSecurityManager(original);
        ExitData exitData = new ExitData();
        exitData.code = noOpSecurityManager.exitCode;
        exitData.message = output;
        return exitData;
    }

    /**
     * In order to catch error stream it needs to be replaced by custom one
     */
    private static String captureSystemErrorStream(Runnable runnable) {
        ByteArrayOutputStream newStream = new ByteArrayOutputStream();
        PrintStream originalStream = System.err;
        System.setErr(new PrintStream(newStream));
        runnable.run();
        System.setErr(originalStream);
        return newStream.toString();
    }

    /**
     * Exceptions thrown from the main thread are caught by Junit automatically
     * and there is no way to get the exception data, that's why the code is executed in separate thread
     */
    private static void executeInSeparateThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join(100);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public SystemExitAssert hasExitCode(int code) {
        isNotNull();
        if (code != actual.code) {
            failWithMessage("Expected exit code to be <%s> but was <%s>", code, actual.code);
        }
        return this;
    }

    public SystemExitAssert hasExitMessage(String message) {
        isNotNull();
        if (!Objects.equals(message, removeNewLines(actual.message))) {
            failWithMessage("Expected exit message to be <%s> but was <%s>", message, actual.message);
        }
        return this;
    }

    static class ExitData {
        int code;
        String message;
    }

    private static class NoOpSecurityManager extends SecurityManager {

        int exitCode;

        @Override
        public void checkPermission(Permission perm) {
            // no-op
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
            // no-op
        }

        @Override
        public void checkExit(int status) {
            exitCode = status;
            throw new IllegalStateException();
        }
    }
}
