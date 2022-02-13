package de.jplag.exceptions;

/**
 * Exceptions for problems with the root directory that lead to an preemptive exit.
 */
public class RootDirectoryException extends ExitException {

    private static final long serialVersionUID = 3134534079325843267L; // generated

    public RootDirectoryException(String message) {
        super(message);
    }

    public RootDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
