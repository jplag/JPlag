package edu.kit.informatik;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public final class Main {

    private Set<String> set2 = new TreeSet<>();
    private int result;
    private boolean result2;
    private int result3;
    private String result4;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();

        set.add("a");
        set.add("a"); // duplicate not added
        result = set.size();
        result2 = set.contains("a");

        set2.add("b");
        set2.add("a");
        result3 = set2.size();


        Set<String> sortedEntries = new TreeSet<>();
        sortedEntries.add("apple");
        result4 = sortedEntries.first();    //"apple"
    }

}
