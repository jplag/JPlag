package de.jplag.cli;

import de.jplag.exceptions.ExitException;

public class CliException extends ExitException {
    public CliException(String message) {
        super(message);
    }

    public CliException(String message, Throwable cause) {
        super(message, cause);
    }
}
