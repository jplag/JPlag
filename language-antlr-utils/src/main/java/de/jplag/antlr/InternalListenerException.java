package de.jplag.antlr;

/**
 * Exception type used internally within the antlr utils. Has to be a {@link RuntimeException}, because it is thrown
 * within the antlr listener methods. Should not be thrown outside the antlr utils.
 */
public class InternalListenerException extends RuntimeException {
    /**
     * New instance
     * @param message The message of the exception
     */
    public InternalListenerException(String message) {
        super(message);
    }
}
