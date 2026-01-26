package de.jplag.exceptions;

/**
 * Exceptions for problems with the language module and its parser.
 */
public class LanguageException extends ExitException {

    private static final long serialVersionUID = 5685703308840622858L; // generated

    /**
     * Constructs a new language-related exception with the specified detail message and cause.
     * @param message the detail message.
     * @param cause is the cause of the exception.
     */
    public LanguageException(String message, Throwable cause) {
        super(message, cause);
    }
}
