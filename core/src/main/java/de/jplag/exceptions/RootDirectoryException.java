package de.jplag.exceptions;

import java.io.Serial;

/**
 * Exceptions for problems with the root directory that lead to an preemptive exit.
 */
public class RootDirectoryException extends ExitException {

    @Serial
    private static final long serialVersionUID = 3134534079325843267L; // generated

    /**
     * Constructs a new root-directory-related exception with the specified detail message.
     * @param message the detail message.
     */
    public RootDirectoryException(String message) {
        super(message);
    }

    /**
     * Constructs a new root-directory-related exception with the specified detail message and cause.
     * @param message the detail message.
     * @param cause is the cause of the exception.
     */
    public RootDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
