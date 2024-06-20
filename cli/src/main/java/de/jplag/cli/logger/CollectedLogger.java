package de.jplag.cli.logger;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.MessageFormatter;

/**
 * A logger implementation, that prints all errors during finalization.
 */
public class CollectedLogger extends JPlagLoggerBase {
    private static final int MAXIMUM_MESSAGE_LENGTH = 32;

    private static final PrintStream TARGET_STREAM = System.out;

    /**
     * Indicator whether finalization is in progress.
     * @see #printAllErrorsForLogger()
     */
    private transient boolean isFinalizing = false;

    private final transient SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss_SSS");

    private final ConcurrentLinkedDeque<LogEntry> allErrors = new ConcurrentLinkedDeque<>();

    public CollectedLogger(String name) {
        super(name);
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

        if (!errors.isEmpty()) {
            info("Summary of all errors:");
            this.allErrors.removeAll(errors);
            for (LogEntry errorEntry : errors) {
                printLogEntry(errorEntry);
            }
        }

        this.isFinalizing = false;
    }

    private String computeShortName() {
        return name.substring(name.lastIndexOf(".") + 1);
    }
}
