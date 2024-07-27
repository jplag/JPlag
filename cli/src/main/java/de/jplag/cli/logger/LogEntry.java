package de.jplag.cli.logger;

import java.util.Date;

import org.slf4j.event.Level;

/**
 * Holds a log entry for later usage.
 * @param message The message of the log
 * @param cause The cause of the log
 * @param timeOfLog The time of the log
 * @param logLevel The level of the log entry
 */
public record LogEntry(String message, Throwable cause, Date timeOfLog, Level logLevel) {
}
