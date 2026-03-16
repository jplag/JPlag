package edu.kit.informatik;

public final class Main {
    private static int result;

    public static void main(String[] args) {
        result = 1;
    }
}

class DeadClass {
    public void deadMethod() {
        System.out.println("I am dead");
    }
}
