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

        if ((y < 100 && z > 2000) || y == 101) {
            //never run
            z = z + 100;
        } else {
            z = z - 100;
        }

        result = z;
        result2 = y;
    }

}
