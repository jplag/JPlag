package de.jplag.exceptions;

import java.io.Serial;

/**
 * Exceptions for problems with the regular submissions that lead to an preemptive exit.
 */
public class SubmissionException extends ExitException {

    @Serial
    private static final long serialVersionUID = 794916053362767596L; // generated

    /**
     * Constructs a new submission-directory-related exception with the specified detail message.
     * @param message the detail message.
     */
    public SubmissionException(String message) {
        super(message);
    }

    /**
     * Constructs a new submission-related exception with the specified detail message and cause.
     * @param message the detail message.
     * @param cause is the cause of the exception.
     */
    public SubmissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
