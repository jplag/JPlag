package de.jplag.semantics;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This record contains semantic information about the token.
 * @param critical Whether the token is critical, e.g. whether it (potentially) has any non-local effects.
 * @param control Whether the token controls the program flow.
 * @param blockBegin Whether the token marks the beginning of a block.
 * @param blockEnd Whether the token marks the end of a block.
 * @param blockIsLoop Whether the block is a loop (ignored if blockBegin is false).
 * @param reads A set of the variables which were (potentially) read from in this token.
 * @param writes A set of the variables which were (potentially) written to in this token.
 */
public record TokenSemantics(boolean critical, boolean control,
        boolean blockBegin, boolean blockEnd, boolean blockIsLoop,
        Set<Variable> reads, Set<Variable> writes) {

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

    public static TokenSemantics join(List<TokenSemantics> semanticsList) {
        Set<Variable> reads = new HashSet<>();
        Set<Variable> writes = new HashSet<>();
        TokenSemanticsBuilder semanticsBuilder = new TokenSemanticsBuilder();
        for (TokenSemantics semantics : semanticsList) {
            if (semantics.critical)
                semanticsBuilder.critical();
            if (semantics.control)
                semanticsBuilder.control();
            if (semantics.blockBegin)
                semanticsBuilder.blockBegin();
            if (semantics.blockEnd)
                semanticsBuilder.blockEnd();
            if (semantics.blockIsLoop)
                semanticsBuilder.blockIsLoop();
            reads.addAll(semantics.reads);
            writes.addAll(semantics.writes);
        }
        TokenSemantics semantics = semanticsBuilder.build();
        for (Variable r : reads)
            semantics.addRead(r);
        for (Variable w : writes)
            semantics.addWrite(w);
        return semantics;
    }

    @Override
    public String toString() {
        List<String> properties = new LinkedList<>();
        if (critical)
            properties.add("critical");
        if (control)
            properties.add("control");
        if (blockBegin)
            properties.add("block begin");
        if (blockBegin)
            properties.add("block end");
        if (blockIsLoop)
            properties.add("block is loop");
        if (!reads.isEmpty())
            properties.add("read " + String.join(" ", reads.stream().map(Variable::toString).toList()));
        if (!writes.isEmpty())
            properties.add("write " + String.join(" ", writes.stream().map(Variable::toString).toList()));
        return String.join(", ", properties);
    }
}