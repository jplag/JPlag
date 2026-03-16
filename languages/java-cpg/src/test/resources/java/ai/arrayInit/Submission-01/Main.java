package edu.kit.informatik;

public final class Main {

    private static int result;
    private static int result2;
    private int[] array;
    private int[] array3 = {1};
    private int[] array2 = new int[10];
    private int[] array4 = new int[]{1, 2, 3, 4};

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int z = 500;
        int y = 5;

        array = new int[x];

        array[0] = z;
        array2[y] = 24;

        result = array[0];
        result2 = array2[5];
    }

}
