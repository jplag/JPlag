package de.jplag.exceptions;

import java.io.Serial;

/**
 * Exceptions used if configuration is wrong.
 */
public class ConfigurationException extends ExitException {
    @Serial
    private static final long serialVersionUID = 4625302641982932127L; // generated

    public ConfigurationException(String message) {
        super(message);
    }
}
