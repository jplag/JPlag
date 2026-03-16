package edu.kit.informatik;

public final class Main {
    private static int result;

    public static void main(String[] args) {
        result = 1;
    }

    public static void deadMethod() {
        result = 2; // Dead method
    }
}
