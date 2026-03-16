package edu.kit.informatik;

public final class Main {

    private static int result;
    private static int result2;
    private static int result3;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int z = 500;
        x = Math.abs(x);
        int y = 100;

        while (y < z) {
            x++;
            y++;
        }

        result = z;
        result2 = y;
        result3 = x;
    }
}
