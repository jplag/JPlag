package edu.kit.informatik;

import java.util.ArrayList;
import java.util.List;

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

        List<String> values = new ArrayList<>();

        values.add("Test");
        values.add("x: " + x);

        z = values.stream().map(String::length).max(Integer::compareTo).get();

        result = z;
        result2 = y;
    }

}
