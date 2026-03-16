package edu.kit.informatik;

public final class Main {

    private int result;
    private int result2;
    private int result3;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        int z = 50;
        int y = 100;
        int x = Integer.parseInt(args[0]);

        result = helper(x);
        result2 = helper(y);
        result3 = helper(z);
    }

    private int helper(int x) {
        while (x > 50) {
            return x * 2;
        }
        return x / 2;
    }

}
