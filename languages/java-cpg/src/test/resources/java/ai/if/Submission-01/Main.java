package edu.kit.informatik;

/**
 * @author ujiqk
 * @version 1.0
 */
public final class Main {

    private static final String ERROR_ARGUMENTS_NOT_SUPPORTED = "Error: Commandline arguments not supported";
    private static final String ERROR_ARGUMENTS_NOT_SUPPORTED2;
    private int result;
    private int result2;

    private Main() {
        throw new IllegalStateException();
    }

    /**
     * @param args Kommandozeilenparameter mit Wert von x.
     */
    public static void main(String[] args) {

        int z = 500;
        int y = 100;

        if (y < 100) {
            //never run
            z = z + 100;
        } else {
            z = z - 100;
        }

        result = z;
        result2 = y;
    }

}
