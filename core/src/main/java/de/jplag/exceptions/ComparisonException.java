package de.jplag.exceptions;

/**
 * Exceptions for problems during the core comparison algorithm.
 */
public class ComparisonException extends ExitException {

    private static final long serialVersionUID = 7780533681049152452L; // generated

    public ComparisonException(String message) {
        super(message);
    }

    public ComparisonException(String message, Throwable cause) {
        super(message, cause);
    }

}
