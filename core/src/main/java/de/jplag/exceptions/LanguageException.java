package de.jplag.exceptions;

/**
 * Exceptions for problems with the language module and its parser.
 */
public class LanguageException extends ExitException {

    private static final long serialVersionUID = 5685703308840622858L; // generated

    public LanguageException(String message, Throwable cause) {
        super(message, cause);
    }
}
