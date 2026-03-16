package edu.kit.informatik;

public final class Main {

    private int result;
    private int result2;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        int z = 500;
        int y = 100;

        for (String input : args) {
            z++;
        }

        result = z;
        result2 = y;
    }

}
