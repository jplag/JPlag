package de.jplag.cli.logger;

import java.io.PrintStream;
import java.io.Serial;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * This logger is able to collect errors and print them at the end. Mainly adopted from org.slf4j.impl.SimpleLogger
 * @author Dominik Fuchss
 */
public final class CollectedLogger extends MarkerIgnoringBase {

    @Serial
    private static final long serialVersionUID = -1278670638921140275L;

    private static final int LOG_LEVEL_TRACE = LocationAwareLogger.TRACE_INT;
    private static final int LOG_LEVEL_DEBUG = LocationAwareLogger.DEBUG_INT;
    private static final int LOG_LEVEL_INFO = LocationAwareLogger.INFO_INT;
    private static final int LOG_LEVEL_WARN = LocationAwareLogger.WARN_INT;
    private static final int LOG_LEVEL_ERROR = LocationAwareLogger.ERROR_INT;

    /**
     * The default log level that shall be used for external libraries (like Stanford Core NLP)
     */
    private static final int LOG_LEVEL_FOR_EXTERNAL_LIBRARIES = LOG_LEVEL_ERROR;

    private static final int CURRENT_LOG_LEVEL = LOG_LEVEL_INFO;

    /**
     * The short name of this simple log instance
     */
    private transient String shortLogName = null;

    /**
     * Indicator whether finalization is in progress.
     * @see #printAllErrorsForLogger()
     */
    private transient boolean isFinalizing = false;

    private final transient SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss_SSS");

    private final ConcurrentLinkedDeque<Triple<String, Throwable, Date>> allErrors = new ConcurrentLinkedDeque<>();

    CollectedLogger(String name) {
        this.name = name;
    }

    private void log(int level, String message, Throwable throwable) {
        log(level, message, throwable, null);
    }

    private void log(int level, String message, Throwable throwable, Date timeOfError) {
        if (!isLevelEnabled(level)) {
            return;
        }

        if (level == LOG_LEVEL_ERROR && !isFinalizing) {
            // Buffer errors for the final output
            allErrors.add(new Triple<>(message, throwable, new Date()));
            return;
        }

        StringBuilder builder = new StringBuilder(32);

        // Append date-time
        builder.append(dateFormat.format(timeOfError == null ? new Date() : timeOfError)).append(' ');

        // Append current thread name
        builder.append('[').append(Thread.currentThread().getName()).append("] ");
        // Append current Level
        builder.append('[').append(renderLevel(level)).append(']').append(' ');

        // Append the name of the log instance
        if (shortLogName == null)
            shortLogName = computeShortName();
        builder.append(shortLogName).append(" - ");
        // Append the message
        builder.append(message);

        write(builder, throwable);
    }

    void printAllErrorsForLogger() {
        this.isFinalizing = true;
        // Copy errors to prevent infinite recursion
        var errors = new ArrayList<>(this.allErrors);
        if (errors.isEmpty())
            return;

        this.allErrors.removeAll(errors);

        info("Summary of all Errors:");
        errors.forEach(error -> log(LOG_LEVEL_ERROR, error.first(), error.second(), error.third()));
        isFinalizing = false;
    }

    @SuppressWarnings("java:S106")
    void write(StringBuilder buf, Throwable throwable) {
        PrintStream targetStream = System.out;

        targetStream.println(buf.toString());
        writeThrowable(throwable, targetStream);
        targetStream.flush();
    }

    private void writeThrowable(Throwable throwable, PrintStream targetStream) {
        if (throwable != null) {
            throwable.printStackTrace(targetStream);
        }
    }

    private String computeShortName() {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    private boolean isLevelEnabled(int logLevel) {
        return logLevel >= (isJPlagLog() ? CURRENT_LOG_LEVEL : LOG_LEVEL_FOR_EXTERNAL_LIBRARIES);
    }

    private boolean isJPlagLog() {
        return this.name.startsWith("de.jplag.");
    }

    private String renderLevel(int level) {
        return switch (level) {
            case LOG_LEVEL_TRACE -> "TRACE";
            case LOG_LEVEL_DEBUG -> "DEBUG";
            case LOG_LEVEL_INFO -> "INFO";
            case LOG_LEVEL_WARN -> "WARN";
            case LOG_LEVEL_ERROR -> "ERROR";
            default -> throw new IllegalStateException("Unrecognized level [" + level + "]");
        };
    }

    public boolean isTraceEnabled() {
        return isLevelEnabled(LOG_LEVEL_TRACE);
    }

    @Override
    public void trace(String message) {
        log(LOG_LEVEL_TRACE, message, null);
    }

    public void trace(String format, Object param1) {
        formatAndLog(LOG_LEVEL_TRACE, format, param1, null);
    }

    public void trace(String format, Object param1, Object param2) {
        formatAndLog(LOG_LEVEL_TRACE, format, param1, param2);
    }

    public void trace(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_TRACE, format, argArray);
    }

    public void trace(String message, Throwable t) {
        log(LOG_LEVEL_TRACE, message, t);
    }

    public boolean isDebugEnabled() {
        return isLevelEnabled(LOG_LEVEL_DEBUG);
    }

    public void debug(String message) {
        log(LOG_LEVEL_DEBUG, message, null);
    }

    public void debug(String format, Object param1) {
        formatAndLog(LOG_LEVEL_DEBUG, format, param1, null);
    }

    public void debug(String format, Object param1, Object param2) {
        formatAndLog(LOG_LEVEL_DEBUG, format, param1, param2);
    }

    public void debug(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_DEBUG, format, argArray);
    }

    public void debug(String message, Throwable throwable) {
        log(LOG_LEVEL_DEBUG, message, throwable);
    }

    public boolean isInfoEnabled() {
        return isLevelEnabled(LOG_LEVEL_INFO);
    }

    public void info(String message) {
        log(LOG_LEVEL_INFO, message, null);
    }

    public void info(String format, Object arg) {
        formatAndLog(LOG_LEVEL_INFO, format, arg, null);
    }

    public void info(String format, Object arg1, Object arg2) {
        formatAndLog(LOG_LEVEL_INFO, format, arg1, arg2);
    }

    public void info(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_INFO, format, argArray);
    }

    public void info(String message, Throwable throwable) {
        log(LOG_LEVEL_INFO, message, throwable);
    }

    public boolean isWarnEnabled() {
        return isLevelEnabled(LOG_LEVEL_WARN);
    }

    public void warn(String message) {
        log(LOG_LEVEL_WARN, message, null);
    }

    public void warn(String format, Object arg) {
        formatAndLog(LOG_LEVEL_WARN, format, arg, null);
    }

    public void warn(String format, Object arg1, Object arg2) {
        formatAndLog(LOG_LEVEL_WARN, format, arg1, arg2);
    }

    public void warn(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_WARN, format, argArray);
    }

    public void warn(String message, Throwable throwable) {
        log(LOG_LEVEL_WARN, message, throwable);
    }

    public boolean isErrorEnabled() {
        return isLevelEnabled(LOG_LEVEL_ERROR);
    }

    public void error(String message) {
        log(LOG_LEVEL_ERROR, message, null);
    }

    public void error(String format, Object arg) {
        formatAndLog(LOG_LEVEL_ERROR, format, arg, null);
    }

    public void error(String format, Object arg1, Object arg2) {
        formatAndLog(LOG_LEVEL_ERROR, format, arg1, arg2);
    }

    public void error(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_ERROR, format, argArray);
    }

    public void error(String message, Throwable throwable) {
        log(LOG_LEVEL_ERROR, message, throwable);
    }

    private void formatAndLog(int level, String format, Object arg1, Object arg2) {
        if (!isLevelEnabled(level)) {
            return;
        }
        FormattingTuple formattingTuple = MessageFormatter.format(format, arg1, arg2);
        log(level, formattingTuple.getMessage(), formattingTuple.getThrowable());
    }

    private void formatAndLog(int level, String format, Object... arguments) {
        if (!isLevelEnabled(level)) {
            return;
        }
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(format, arguments);
        log(level, formattingTuple.getMessage(), formattingTuple.getThrowable());
    }
}
