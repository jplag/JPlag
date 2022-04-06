package de.jplag.exceptions;

import java.io.Serial;

/**
 * Exceptions for problems with the regular submissions that lead to an preemptive exit.
 */
public class SubmissionException extends ExitException {

    @Serial
    private static final long serialVersionUID = 794916053362767596L; // generated

    public SubmissionException(String message) {
        super(message);
    }

    public SubmissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
