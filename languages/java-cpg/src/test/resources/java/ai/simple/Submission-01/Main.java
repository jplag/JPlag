package edu.kit.informatik;

/**
 * @author ujiqk
 * @version 1.0
 */
public final class Main {

    private static final String ERROR_ARGUMENTS_NOT_SUPPORTED = "Error: Commandline arguments not supported";
    private static int result;
    private static int result2;

    private Main() {
        throw new IllegalStateException();
    }

    /**
     * @param args Kommandozeilenparameter mit Wert von x.
     */
    public static void main(String[] args) {

        System.out.print("1");

        int x = Integer.parseInt(args[0]);
        int z = 500;
        x = Math.abs(x);
        int y = 100;

        if (x + y < 100) {
            System.out.print("2");
            z = z + 100;
        } else {
            System.out.print("3");
            z = z - 100;
        }

        result = z;
        result2 = y;
    }

}
