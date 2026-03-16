package edu.kit.informatik;

import java.util.HashMap;
import java.util.Map;

public final class Main {

    Map<String, Integer> map2 = new HashMap<>();
    private int result;
    private int result2;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();

        map.put("a", 1);
        map2.put("b", 2);

        result = map.get("a");
        result2 = map2.get("b");
    }

}
