package de.jplag.cli.logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.MessageFormatter;

/**
 * A logger implementation that prints all errors during finalization. Handles the enabled log levels for SLF4J.
 */
public class CollectedLogger extends AbstractLogger {

    private static final String JPLAG_LOGGER_PREFIX = "de.jplag.";
    private static final Level LOG_LEVEL_FOR_EXTERNAL_LIBRARIES = Level.ERROR;
    private static final int MAXIMUM_MESSAGE_LENGTH = 32;
    private static Level currentLogLevel = Level.INFO;

    private final transient SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss_SSS");
    private final ConcurrentLinkedDeque<LogEntry> allErrors = new ConcurrentLinkedDeque<>();

    /**
     * Indicator whether finalization is in progress.
     * @see #printAllErrorsForLogger()
     */
    private transient boolean isFinalizing = false;

    /**
     * Creates a logger with a specific name and level.
     * @param name is the name of the logger.
     */
    CollectedLogger(String name) {
        this.name = name;
    }

    @Override
    public boolean isTraceEnabled() {
        return isLogLevelEnabled(Level.TRACE);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return isLogLevelEnabled(Level.DEBUG);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return isLogLevelEnabled(Level.INFO);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return isLogLevelEnabled(Level.WARN);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return isLogLevelEnabled(Level.ERROR);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    @Override
    protected String getFullyQualifiedCallerName() {
        return null; // does not seem to be used by anything, but is required by SLF4J
    }

    @Override
    protected void handleNormalizedLoggingCall(Level level, Marker marker, String format, Object[] args, Throwable cause) {
        String logMessage = prepareFormattedMessage(format, args);
        LogEntry entry = new LogEntry(logMessage, cause, new Date(), level);

        if (level == Level.ERROR && !isFinalizing) {
            allErrors.add(entry);
        } else {
            printLogEntry(entry);
        }
    }

    void printAllErrorsForLogger() {
        isFinalizing = true;
        ArrayList<LogEntry> errors = new ArrayList<>(allErrors);
        if (!errors.isEmpty()) {
            info("Summary of all errors:");
            allErrors.clear();
            errors.forEach(this::printLogEntry);
        }
        isFinalizing = false;
    }

    private String computeShortName() {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    private boolean isJPlagLog() {
        return name.startsWith(JPLAG_LOGGER_PREFIX);
    }

    private boolean isLogLevelEnabled(Level logLevel) {
        return logLevel.toInt() >= (isJPlagLog() ? currentLogLevel.toInt() : LOG_LEVEL_FOR_EXTERNAL_LIBRARIES.toInt());
    }

    private String prepareFormattedMessage(String format, Object[] args) {
        if (args == null) {
            return format;
        }

        return MessageFormatter.arrayFormat(format, args).getMessage();
    }

    private StringBuilder prepareLogOutput(LogEntry entry) {
        StringBuilder outputBuilder = new StringBuilder(MAXIMUM_MESSAGE_LENGTH);
        outputBuilder.append(dateFormat.format(entry.timeOfLog())).append(' ');
        outputBuilder.append('[').append(entry.logLevel().name()).append("] ");
        outputBuilder.append(computeShortName()).append(" - ");
        outputBuilder.append(entry.message());
        return outputBuilder;
    }

    private void printLogEntry(LogEntry entry) {
        StringBuilder output = prepareLogOutput(entry);
        DelayablePrinter.getInstance().println(output.toString());
        if (entry.cause() != null) {
            this.printStackTrace(entry.cause());
        }
    }

    /**
     * @return the log level.
     */
    public static Level getLogLevel() {
        return currentLogLevel;
    }

    /**
     * Sets the log level to a specified value.
     * @param logLevel is the specified value.
     */
    public static void setLogLevel(Level logLevel) {
        currentLogLevel = logLevel;
    }

    private void printStackTrace(Throwable error) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        error.printStackTrace(new PrintStream(outputStream));
        String stackTrace = outputStream.toString();
        DelayablePrinter.getInstance().println(stackTrace);
    }
}
