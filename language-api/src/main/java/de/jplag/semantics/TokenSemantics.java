package de.jplag.semantics;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This record contains semantic information about the token.
 * @param critical Whether the token is critical, e.g. whether it (potentially) has any non-local effects.
 * @param control Whether the token controls the program flow.
 * @param loopBegin Whether the token marks the beginning of a loop.
 * @param loopEnd Whether the token marks the end of a loop
 * @param reads A set of the variables which were (potentially) read from in this token.
 * @param writes A set of the variables which were (potentially) written to in this token.
 */
public record TokenSemantics(boolean critical, boolean control, boolean loopBegin, boolean loopEnd, Set<Variable> reads, Set<Variable> writes) {

    public void addRead(Variable read) {
        reads.add(read);
    }

    public void addWrite(Variable write) {
        writes.add(write);
    }

    /**
     * @return an unmodifiable set of the variables which were (potentially) read from in this token.
     */
    public Set<Variable> reads() {
        return Collections.unmodifiableSet(reads);
    }

    /**
     * @return an unmodifiable set of the variables which were (potentially) written to in this token.
     */
    public Set<Variable> writes() {
        return Collections.unmodifiableSet(writes);
    }

    @Override
    public String toString() {
        List<String> properties = new LinkedList<>();
        if (critical)
            properties.add("critical");
        if (control)
            properties.add("control");
        if (loopBegin)
            properties.add("loop begin");
        if (loopEnd)
            properties.add("loop end");
        if (!reads.isEmpty())
            properties.add("read " + String.join(" ", reads.stream().map(Variable::toString).toList()));
        if (!writes.isEmpty())
            properties.add("write " + String.join(" ", writes.stream().map(Variable::toString).toList()));
        return String.join(", ", properties);
    }
}