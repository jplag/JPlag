package de.jplag.semantics;

import java.util.Collections;
import java.util.Set;

/**
 * This record contains semantic information about the token.
 * @param critical Whether the token is critical, e.g. whether it (potentially) has any non-local effects.
 * @param control Whether the token controls the program flow.
 * @param loopBegin Whether the token marks the beginning of a loop.
 * @param loopEnd Whether the token marks the end of a loop
 * @param writes A set of the variable names which were (potentially) written to in this token.
 * @param reads A set of the variable names which were (potentially) read from in this token.
 */
public record TokenSemantics(boolean critical, boolean control, boolean loopBegin, boolean loopEnd, Set<String> writes, Set<String> reads) {

    public void addWrite(String write) {
        writes.add(write);
    }

    public void addRead(String read) {
        reads.add(read);
    }

    /**
     * @return an unmodifiable set of the variable names which were (potentially) written to in this token.
     */
    public Set<String> writes() {
        return Collections.unmodifiableSet(writes);
    }

    /**
     * @return an unmodifiable set of the variable names which were (potentially) read from in this token.
     */
    public Set<String> reads() {
        return Collections.unmodifiableSet(reads);
    }
}