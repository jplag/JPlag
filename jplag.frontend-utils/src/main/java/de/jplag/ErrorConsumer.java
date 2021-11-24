package de.jplag;

public interface ErrorConsumer {

    /**
     * Print and store an error.
     * @param errorMessage is the error message.
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
