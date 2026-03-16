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

        int x = Integer.parseInt(args[0]);
        int y = 100;
        result2 = compute(x, y);

        System.out.print("1");
        int y = 100;
        int z = 500;
        x = Math.abs(x);

        if (x + y < 100) {
            //System.out.print("1");
            z = z + 100;
        } else {
            //System.out.print("2");
            z = z - 100;
        }

        result = z;
    }

    private int compute(int x, int y) {
        return x + y;
    }

}
