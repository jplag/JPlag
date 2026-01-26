package de.jplag.exceptions;

/**
 * Exceptions for problems during the core comparison algorithm.
 */
public class ComparisonException extends ExitException {

    private static final long serialVersionUID = 7780533681049152452L; // generated

    /**
     * Constructs a new comparison-related exception with the specified detail message.
     * @param message the detail message.
     */
    public ComparisonException(String message) {
        super(message);
    }

    /**
     * Constructs a new comparison-related exception with the specified detail message and cause.
     * @param message the detail message.
     * @param cause is the cause of the exception.
     */
    public ComparisonException(String message, Throwable cause) {
        super(message, cause);
    }

}
