package edu.kit.informatik;

public final class Main {
    private static int result;

    public Main(int x) {
        if (x > 10) {
            result = 1;
        } else {
            result = 2;
        }
    }

    public static void main(String[] args) {
        Main m = new Main(15);
        // result should be 1
    }
}

class DeadClass {
    static {
        System.out.println("Static init");
    }

    {
        System.out.println("Instance init");
    }

    public DeadClass() {
        System.out.println("Constructor");
    }
}
