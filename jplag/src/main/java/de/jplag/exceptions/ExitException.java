package de.jplag.exceptions;

/**
 * Exceptions for problems during the execution of JPlag that lead to an preemptive exit.
 */
public abstract class ExitException extends Exception {

    private static final long serialVersionUID = 7091658804288889231L; // generated

    public ExitException(String message) {
        super(message);
    }

    public ExitException(String message, Throwable cause) {
        super(message, cause);
    }
}
