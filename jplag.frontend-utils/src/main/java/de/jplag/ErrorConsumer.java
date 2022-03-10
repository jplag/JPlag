package de.jplag;

/**
 * Consumes errors from a language parser.
 */
public interface ErrorConsumer {

    /**
     * Adds an error to the error stack. The error is only printed directly when using the corresponding verbosity option.
     * @param errorMessage is the error message.
     */
    void addError(String errorMessage);

    /**
     * Prints a message depending on the verbosity.
     * @param message is the message for the normal mode.
     * @param longMessage is the message for verbose mode.
     */
    void print(String message, String longMessage);
}
