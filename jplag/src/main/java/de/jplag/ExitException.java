package de.jplag;

/**
 * Exception to be reached through to the main caller of JPlag to replace the "exit" method
 * @apiNote Created on 05.03.2005 Author Moritz Kroll, Emeric Kwemou.
 */
public class ExitException extends Exception {
    public static final int COMPARE_SOURCE_DONE = 300;
    public static final int UNKNOWN_ERROR_OCCURRED = 400;
    public static final int BAD_LANGUAGE_ERROR = 401;
    public static final int NOT_ENOUGH_SUBMISSIONS_ERROR = 402;
    public static final int BAD_PARAMETER = 403;
    public static final int BAD_SENSITIVITY_OF_COMPARISON = 404;
    public static final int SUBMISSION_ABORTED = 405;

    private static final long serialVersionUID = 1L;
    private final int errorCode;
    private final String message;

    public ExitException(String message) {
        super(message);
        errorCode = UNKNOWN_ERROR_OCCURRED;
        this.message = message;
    }

    public ExitException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public ExitException(String message, Throwable cause) {
        super(message, cause);
        errorCode = UNKNOWN_ERROR_OCCURRED;
        this.message = message;
    }

    public int getState() {
        return errorCode;
    }

    public String getReport() {
        return message;
    }
}
