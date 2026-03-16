package edu.kit.informatik;

public final class Main {

    private int result;
    private int result2;
    private int result3;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int a = 5;
        int b = 10;

        result = a > 3 ? b : a;
        result2 = x < 3 ? b : a;
        result3 = x == 6 ? a + b : a - b;
    }

}
