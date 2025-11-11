package de.jplag.testutils;

import java.util.Collection;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;

public class AssertionUtils {
    public static <T> void assertAll(String heading, Collection<T> items, Consumer<T> check) {
        Assertions.assertAll(heading, items.stream().map(item -> () -> check.accept(item)));
    }

    public static <T> void assertAll(Collection<T> items, Consumer<T> check) {
        Assertions.assertAll(items.stream().map(item -> () -> check.accept(item)));
    }
}
