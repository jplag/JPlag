package de.jplag.exceptions;

/**
 * Exceptions related to file handling.
 */
public class FileException extends ExitException {
    private static final long serialVersionUID = 5685703308840622858L; // generated

    /**
     * Constructs a new file-handling-related exception with the specified detail message and cause.
     * @param message the detail message.
     * @param cause is the cause of the exception.
     */
    public FileException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new file-handling-related exception with the specified cause.
     * @param cause is the cause of the exception.
     **/
    public FileException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
