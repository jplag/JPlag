package de.jplag.exceptions;

import java.io.Serial;

/**
 * Exceptions for problems with the report generation that lead to an preemptive exit.
 */
public class ReportGenerationException extends ExitException {

    @Serial
    private static final long serialVersionUID = 628097693006117102L; // generated

    public ReportGenerationException(String message) {
        super(message);
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

}
