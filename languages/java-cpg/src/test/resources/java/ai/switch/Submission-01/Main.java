package edu.kit.informatik;

public final class Main {

    private int result;
    private int result2;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {

        int x = Integer.parseInt(args[0]);
        int z = 500;
        int y = 100;

        switch (x + x < 100) {
            case true:
                z++;
                break;
            default:
                z--;
        }

        result = z;
        result2 = y;
    }

}
