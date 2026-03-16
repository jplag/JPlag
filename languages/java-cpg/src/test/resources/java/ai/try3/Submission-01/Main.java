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

        try {
            z = z - 100;
            if (z != 400) {
                throw new Exception();
            }
            y = y + 100;
        } catch (Exception e) {
            y++;
        }

        result = z;
        result2 = y;
    }

}
