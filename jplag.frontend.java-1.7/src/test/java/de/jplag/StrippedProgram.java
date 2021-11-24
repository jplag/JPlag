package de.jplag;

/**
 * This is a stripped version of the JPlag main class ErrorConsumer to be used during front end development only.
 */
public class StrippedProgram implements ErrorConsumer {
    @Override
    public void addError(String errorMsg) {
        System.err.println(errorMsg);
    }

    @Override
    public void print(String normalMsg, String longMsg) {
        if (longMsg != null) {
            System.out.println(longMsg);
        } else if (normalMsg != null) {
            System.out.println(normalMsg);
        } else {
            System.out.println("Somebody messed up - no message given");
        }
    }
}
