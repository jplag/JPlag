package de.jplag.frequency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link WindowIterator} with various list sizes and window lengths.
 */
class WindowIteratorTest {

    @Test
    @DisplayName("Yields all fixed-size windows within the list")
    void yieldsAllWindows() {
        WindowIterator<Integer> it = new WindowIterator<>(List.of(1, 2, 3, 4), 2);

        assertEquals(List.of(1, 2), it.next());
        assertEquals(List.of(2, 3), it.next());
        assertEquals(List.of(3, 4), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    @DisplayName("Yields a single window when window length equals list size")
    void windowEqualToSize() {
        WindowIterator<String> it = new WindowIterator<>(List.of("a", "b"), 2);

        assertEquals(List.of("a", "b"), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    @DisplayName("Yields nothing when the list is empty")
    void emptyList() {
        WindowIterator<Integer> it = new WindowIterator<>(List.of(), 2);

        assertFalse(it.hasNext());
    }

    @Test
    @DisplayName("Yields nothing when the window is larger than the list")
    void windowLargerThanList() {
        WindowIterator<Integer> it = new WindowIterator<>(List.of(1, 2), 3);

        assertFalse(it.hasNext());
    }

    @Test
    @DisplayName("Yields a single-element window for a single-element list")
    void singleElementList() {
        WindowIterator<Integer> it = new WindowIterator<>(List.of(5), 1);

        assertEquals(List.of(5), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    @DisplayName("Throws NoSuchElementException when next is called after exhaustion")
    void throwsAfterExhaustion() {
        WindowIterator<Integer> it = new WindowIterator<>(List.of(1), 1);
        it.next();

        assertThrows(NoSuchElementException.class, it::next);
    }
}
