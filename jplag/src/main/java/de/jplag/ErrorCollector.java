package de.jplag;

import static de.jplag.options.Verbosity.LONG;
import static de.jplag.options.Verbosity.QUIET;

import java.util.ArrayList;
import java.util.List;

import de.jplag.options.JPlagOptions;
import de.jplag.options.Verbosity;

public class ErrorCollector implements ErrorReporting {

    private final List<String> errorVector; // List of errors that occurred during the execution of the program.
    private final JPlagOptions options;
    private String currentSubmissionName;

    public ErrorCollector(JPlagOptions options) {
        this.options = options;
        errorVector = new ArrayList<>();
        currentSubmissionName = "<Unknown submission>";
    }

    @Override
    public void addError(String errorMessage) {
        errorVector.add("[" + currentSubmissionName + "]\n" + errorMessage);
        print(errorMessage, null);
    }

    @Override
    public void print(String message, String longMessage) {
        Verbosity verbosity = options.getVerbosity();
        if (verbosity != QUIET) {
            if (message != null) {
                System.out.print(message);
            }
            if (longMessage != null && verbosity == LONG) {
                System.out.print(longMessage);
            }
        }
    }

    /**
     * Print all errors from the errorVector.
     */
    public void printErrors() {
        StringBuilder errorStr = new StringBuilder();

        for (String str : errorVector) {
            errorStr.append(str);
            errorStr.append('\n');
        }

        System.out.println(errorStr.toString());
    }

    public void setCurrentSubmissionName(String currentSubmissionName) {
        this.currentSubmissionName = currentSubmissionName;
    }
}
