package de.jplag;

public interface ErrorConsumer {

    /**
     * Adds an error to the error stack. The error is only printed directly when using the corresponding verbosity option.
     * @param errorMessage is the error message.
     * @see JPlagOptions.getVerbosity()
     */
    public void addError(String errorMessage);

    /**
     * Prints a message depending on the verbosity.
     * @param message is the message for the normal mode.
     * @param longMessage is the message for verbose mode.
     * @see JPlagOptions.getVerbosity()
     */
    public void print(String message, String longMessage);
}
