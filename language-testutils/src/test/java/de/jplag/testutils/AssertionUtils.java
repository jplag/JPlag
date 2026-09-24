package de.jplag.testutils;

import java.util.Collection;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;

/**
 * Utility functions for assertions.
 */
public class AssertionUtils {
    /**
     * Runs a function on all elements and fails if any of the functions fail, returning all errors to the user.
     * @param items The list of items
     * @param check The check to perform
     * @param <T> The type of element
     */
    public static <T> void assertAll(Collection<T> items, Consumer<T> check) {
        Assertions.assertAll(items.stream().map(item -> () -> check.accept(item)));
    }
}
