package edu.kit.informatik;

public final class Main {

    private int result;
    private int result2;
    private int result3;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        int z = 500;
        int y = 100;
        int x = Integer.parseInt(args[0]);

        result = helper(x);
        result2 = helper(y);
        result3 = z;
    }

    private int helper(int x) {
        if (x > 0) {
            if (x > 90) {
                return x + 600;
            }
            return x * 2;
        } else {
            return x / 2;
        }
    }

}
