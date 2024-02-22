package de.jplag.cli.logger;

import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CollectedLogger extends AbstractLogger {
    private static final Level LOG_LEVEL_TRACE = Level.TRACE;
    private static final Level LOG_LEVEL_DEBUG = Level.DEBUG;
    private static final Level LOG_LEVEL_INFO = Level.INFO;
    private static final Level LOG_LEVEL_WARN = Level.WARN;
    private static final Level LOG_LEVEL_ERROR = Level.ERROR;

    /**
     * The default log level that shall be used for external libraries (like Stanford Core NLP)
     */
    private static final Level LOG_LEVEL_FOR_EXTERNAL_LIBRARIES = LOG_LEVEL_ERROR;

    private static final Level CURRENT_LOG_LEVEL = LOG_LEVEL_INFO;

    private static final int MAXIMUM_MESSAGE_LENGTH = 32;

    private static final PrintStream TARGET_STREAM = System.out;

    /**
     * Indicator whether finalization is in progress.
     *
     * @see #printAllErrorsForLogger()
     */
    private transient boolean isFinalizing = false;

    private final transient SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss_SSS");

    private final ConcurrentLinkedDeque<LogEntry> allErrors = new ConcurrentLinkedDeque<>();

    public CollectedLogger(String name) {
        this.name = name;
    }

    @Override
    protected void handleNormalizedLoggingCall(Level level, Marker marker, String format, Object[] args, Throwable cause) {
        String logMessage = prepareFormattedMessage(format, args);
        LogEntry entry = new LogEntry(logMessage, cause, new Date(), level);

        if (level == LOG_LEVEL_ERROR && !isFinalizing) {
            allErrors.add(entry);
        } else {
            printLogEntry(entry);
        }
    }

    private String prepareFormattedMessage(String format, Object[] args) {
        if (args == null) {
            return format;
        }

        return MessageFormatter.arrayFormat(format, args).getMessage();
    }

    private void printLogEntry(LogEntry entry) {
        StringBuilder output = prepareLogOutput(entry);

        TARGET_STREAM.println(output);
        if (entry.cause() != null) {
            entry.cause().printStackTrace(TARGET_STREAM);
        }
        TARGET_STREAM.flush();
    }

    private StringBuilder prepareLogOutput(LogEntry entry) {
        StringBuilder outputBuilder = new StringBuilder(MAXIMUM_MESSAGE_LENGTH);
        outputBuilder.append(dateFormat.format(entry.timeOfLog())).append(' ');
        outputBuilder.append('[').append(entry.logLevel().name()).append("] ");
        outputBuilder.append(computeShortName()).append(" - ");
        outputBuilder.append(entry.message());
        return outputBuilder;
    }

    void printAllErrorsForLogger() {
        this.isFinalizing = true;
        ArrayList<LogEntry> errors = new ArrayList<>(this.allErrors);

        if(!errors.isEmpty()) {
            info("Summary of all errors:");
            this.allErrors.removeAll(errors);
            for (LogEntry errorEntry : errors) {
                printLogEntry(errorEntry);
            }
        }

        this.isFinalizing = false;
    }

    @Override
    public boolean isTraceEnabled() {
        return isLogLevelEnabled(LOG_LEVEL_TRACE);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return isLogLevelEnabled(LOG_LEVEL_DEBUG);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return isLogLevelEnabled(LOG_LEVEL_INFO);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return isLogLevelEnabled(LOG_LEVEL_WARN);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return isLogLevelEnabled(LOG_LEVEL_ERROR);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    private boolean isLogLevelEnabled(Level logLevel) {
        return logLevel.toInt() >= (isJPlagLog() ? CURRENT_LOG_LEVEL.toInt() : LOG_LEVEL_FOR_EXTERNAL_LIBRARIES.toInt());
    }

    private boolean isJPlagLog() {
        return this.name.startsWith("de.jplag.");
    }

    private String computeShortName() {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    @Override
    protected String getFullyQualifiedCallerName() {
        return null; //does not seem to be used by anything, but is required by SLF4J
    }
}
