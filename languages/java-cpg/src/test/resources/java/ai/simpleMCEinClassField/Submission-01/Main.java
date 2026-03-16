package edu.kit.informatik;

public final class Main {

    private static int result;
    private static int result2;
    private int x = Integer.parseInt("42");

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        x = x + 1;
    }

}
