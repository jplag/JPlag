package de.jplag.semantics;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This record contains semantic information about a code snippet, in our case either a token or a line of code.
 * @param keep Whether the code snippet must be kept or if it may be removed.
 * @param ordering In which way the ordering of the code snippet relative to other code snippets of the same type is
 * relevant. For the possible options see {@link Ordering}.
 * @param bidirectionalBlockRelation Which relation the code snippet has to bidirectional block, meaning a block where
 * any statement within it may be executed after any other. This will typically be a loop. For the possible options see
 * {@link BlockRelation}.
 * @param reads A set of the variables which were (potentially) read from in the code snippet.
 * @param writes A set of the variables which were (potentially) written to in the code snippet.
 */
public record CodeSemantics(boolean keep, Ordering ordering, BlockRelation bidirectionalBlockRelation, Set<Variable> reads, Set<Variable> writes) {

    private CodeSemantics(boolean keep, Ordering ordering, BlockRelation bidirectionalBlockRelation) {
        this(keep, ordering, bidirectionalBlockRelation, new HashSet<>(), new HashSet<>());
    }

    /**
     * Creates new semantics with the following meaning: The token may be removed, and its order relative to other tokens
     * may change. Example: An assignment to a local variable.
     */
    public CodeSemantics() {
        this(false, Ordering.NONE, BlockRelation.NONE);
    }

    /**
     * @return new semantics with the following meaning: The token may not be removed, and its order relative to other
     * tokens may change. Example: An attribute declaration.
     */
    public static CodeSemantics createKeep() {
        return new CodeSemantics(true, Ordering.NONE, BlockRelation.NONE);
    }

    /**
     * @return new semantics with the following meaning: The token may not be removed, and its order must stay invariant to
     * other tokens of the same type. Example: A method call which is guaranteed to not result in an exception.
     */
    public static CodeSemantics createCritical() {
        return new CodeSemantics(true, Ordering.PARTIAL, BlockRelation.NONE);
    }

    /**
     * @return new semantics with the following meaning: The token may not be removed, and its order must stay invariant to
     * all other tokens. Example: A return statement.
     */
    public static CodeSemantics createControl() {
        return new CodeSemantics(true, Ordering.FULL, BlockRelation.NONE);
    }

    /**
     * @return new semantics with the following meaning: The token may not be removed, and its order must stay invariant to
     * all other tokens, which also begins a bidirectional block. Example: The beginning of a while loop.
     */
    public static CodeSemantics createLoopBegin() {
        return new CodeSemantics(true, Ordering.FULL, BlockRelation.BEGINS_BLOCK);
    }

    /**
     * @return new semantics with the following meaning: The token may not be removed, and its order must stay invariant to
     * all other tokens, which also ends a bidirectional block. Example: The end of a while loop.
     */
    public static CodeSemantics createLoopEnd() {
        return new CodeSemantics(true, Ordering.FULL, BlockRelation.ENDS_BLOCK);
    }

    /**
     * Add a variable to the set of variables which were (potentially) read from in this code snippet.
     * @param variable The variable which is added.
     */
    public void addRead(Variable variable) {
        reads.add(variable);
    }

    /**
     * Add a variable to the set of variables which were (potentially) written to in this code snippet.
     * @param variable The variable which is added.
     */
    public void addWrite(Variable variable) {
        writes.add(variable);
    }

    /**
     * @return an unmodifiable set of the variables which were (potentially) read from in this code snippet.
     */
    public Set<Variable> reads() {
        return Collections.unmodifiableSet(reads);
    }

    /**
     * @return an unmodifiable set of the variables which were (potentially) written to in this code snippet.
     */
    public Set<Variable> writes() {
        return Collections.unmodifiableSet(writes);
    }

    /**
     * Create new joint semantics by joining a number of existing ones. It has the following properties:
     * <ul>
     * <li>keep is the disjunction of all keeps</li>
     * <li>ordering is the strongest ordering out of all orderings</li>
     * <li>bidirectionalBlockRelation is the one that is not NONE out of all bidirectionalBlockRelations if it exists. It's
     * assumed that there is at most one. If there isn't one bidirectionalBlockRelation is NONE.</li>
     * <li>reads is the union of all reads</li>
     * <li>writes is the union of all writes</li>
     * </ul>
     * @param semanticsList A list of the semantics which should be joined.
     * @return New semantics which were created by joining the elements in the semanticsList.
     */
    public static CodeSemantics join(List<CodeSemantics> semanticsList) {
        boolean keep = false;
        Ordering ordering = Ordering.NONE;
        BlockRelation bidirectionalBlockRelation = BlockRelation.NONE;
        Set<Variable> reads = new HashSet<>();
        Set<Variable> writes = new HashSet<>();
        for (CodeSemantics semantics : semanticsList) {
            keep = keep || semantics.keep();
            if (semantics.ordering.isStrongerThan(ordering)) {
                ordering = semantics.ordering;
            }
            if (semantics.bidirectionalBlockRelation != BlockRelation.NONE) {
                assert bidirectionalBlockRelation == BlockRelation.NONE;  // only one block begin/end per line
                bidirectionalBlockRelation = semantics.bidirectionalBlockRelation;
            }
            reads.addAll(semantics.reads);
            writes.addAll(semantics.writes);
        }
        return new CodeSemantics(keep, ordering, bidirectionalBlockRelation, reads, writes);
    }

    @Override
    public String toString() {
        List<String> properties = new LinkedList<>();
        if (keep)
            properties.add("keep");
        if (ordering != Ordering.NONE)
            properties.add(ordering.name().toLowerCase() + " ordering");
        if (bidirectionalBlockRelation != BlockRelation.NONE) {
            String keyword = bidirectionalBlockRelation.name().toLowerCase().split("_")[0];
            properties.add(keyword + " bidirectional block");
        }
        if (!reads.isEmpty())
            properties.add("read " + String.join(" ", reads.stream().map(Variable::toString).toList()));
        if (!writes.isEmpty())
            properties.add("write " + String.join(" ", writes.stream().map(Variable::toString).toList()));
        return String.join(", ", properties);
    }
}