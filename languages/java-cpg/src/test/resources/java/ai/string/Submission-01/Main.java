package edu.kit.informatik;

public final class Main {

    private String result;
    private String result2;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        String name = "John Doe";
        String greeting = "Hello";

        if ((name.length() > 5 && greeting.startsWith("H")) || name.equals("Jane")) {
            result = greeting + ", " + name + "!";
        } else {
            result = "Welcome, " + name;
        }

        result2 = name.toUpperCase();

        System.out.println(result);
        System.out.println(result2);
    }

}
