package de.jplag.exceptions;

import java.io.Serial;

/**
 * Exceptions for problems with the basecode submission that lead to an preemptive exit.
 */
public class BasecodeException extends ExitException {

    @Serial
    private static final long serialVersionUID = -3911476090624995247L; // generated

    /**
     * Constructs a new basecode-related exception with the specified detail message.
     * @param message the detail message.
     */
    public BasecodeException(String message) {
        super(message);
    }

    /**
     * Constructs a new basecode-related exception with the specified detail message and cause.
     * @param message the detail message.
     * @param cause is the cause of the exception.
     */
    public BasecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
