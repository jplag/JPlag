package de.jplag.exceptions;

/**
 * Exceptions used if configuration is wrong.
 */
public class ConfigurationException extends ExitException {
    public ConfigurationException(String message) {
        super(message);
    }
}