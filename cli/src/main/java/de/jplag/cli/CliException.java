package de.jplag.cli;

import de.jplag.exceptions.ExitException;

/**
 * Exception for invalid CLI usage.
 */
public class CliException extends ExitException {
    /**
     * Constructs a new CLI-related exception with the specified detail message.
     * @param message the detail message.
     */
    public CliException(String message) {
        super(message);
    }

    /**
     * Constructs a new CLI-related exception with the specified detail message and cause.
     * @param message the detail message.
     * @param cause is the cause of the exception.
     */
    public CliException(String message, Throwable cause) {
        super(message, cause);
    }
}
