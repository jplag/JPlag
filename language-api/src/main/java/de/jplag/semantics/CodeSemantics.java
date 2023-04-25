package de.jplag.semantics;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Contains semantic information about a code snippet, in our case either a token or a statement.
 */
public class CodeSemantics {

    private boolean keep;
    private PositionSignificance positionSignificance;
    private final int bidirectionalBlockDepthChange;
    private Set<Variable> reads;
    private Set<Variable> writes;

    /**
     * Creates new semantics. reads and writes, which each contain the variables which were (potentially) read from/written
     * to in this code snippet, are created empty.
     * @param keep Whether the code snippet must be kept or if it may be removed.
     * @param positionSignificance In which way the position of the code snippet relative to other code snippets of the same
     * type is significant. For the possible options see {@link PositionSignificance}.
     * @param bidirectionalBlockDepthChange How the code snippet affects the depth of bidirectional blocks, meaning blocks
     * where any statement within it may be executed after any other. This will typically be a loop.
     * @param reads A set of the variables which were (potentially) read from in the code snippet.
     * @param writes A set of the variables which were (potentially) written to in the code snippet.
     */
    private CodeSemantics(boolean keep, PositionSignificance positionSignificance, int bidirectionalBlockDepthChange, Set<Variable> reads,
            Set<Variable> writes) {
        this.keep = keep;
        this.positionSignificance = positionSignificance;
        this.bidirectionalBlockDepthChange = bidirectionalBlockDepthChange;
        this.reads = reads;
        this.writes = writes;
    }

    private CodeSemantics(boolean keep, PositionSignificance positionSignificance, int bidirectionalBlockDepthChange) {
        this(keep, positionSignificance, bidirectionalBlockDepthChange, new HashSet<>(), new HashSet<>());
    }

    /**
     * Creates new semantics with the following meaning: The code snippet may be removed, and its position relative to other
     * code snippets may change. Example: An assignment to a local variable.
     */
    public CodeSemantics() {
        this(false, PositionSignificance.NONE, 0);
    }

    /**
     * @return new semantics with the following meaning: The code snippet may not be removed, and its position relative to
     * other code snippets may change. Example: An attribute declaration.
     */
    public static CodeSemantics createKeep() {
        return new CodeSemantics(true, PositionSignificance.NONE, 0);
    }

    /**
     * @return new semantics with the following meaning: The code snippet may not be removed, and its position must stay
     * invariant to other code snippets of the same type. Example: A method call which is guaranteed to not result in an
     * exception.
     */
    public static CodeSemantics createCritical() {
        return new CodeSemantics(true, PositionSignificance.PARTIAL, 0);
    }

    /**
     * @return new semantics with the following meaning: The code snippet may not be removed, and its position must stay
     * invariant to all other code snippets. Example: A return statement.
     */
    public static CodeSemantics createControl() {
        return new CodeSemantics(true, PositionSignificance.FULL, 0);
    }

    /**
     * @return new semantics with the following meaning: The code snippet may not be removed, and its position must stay
     * invariant to all other code snippets, which also begins a bidirectional block. Example: The beginning of a while
     * loop.
     */
    public static CodeSemantics createLoopBegin() {
        return new CodeSemantics(true, PositionSignificance.FULL, 1);
    }

    /**
     * @return new semantics with the following meaning: The code snippet may not be removed, and its position must stay
     * invariant to all other code snippets, which also ends a bidirectional block. Example: The end of a while loop.
     */
    public static CodeSemantics createLoopEnd() {
        return new CodeSemantics(true, PositionSignificance.FULL, -1);
    }

    /**
     * @return whether this code snippet must be kept.
     */
    public boolean keep() {
        return keep;
    }

    /**
     * Mark this code snippet as having to be kept.
     */
    public void markKeep() {
        keep = true;
    }

    /**
     * @return the change this code snippet causes in the depth of bidirectional loops.
     */
    public int bidirectionalBlockDepthChange() {
        return bidirectionalBlockDepthChange;
    }

    /**
     * @return whether this code snippet has partial position significance.
     */
    public boolean hasPartialPositionSignificance() {
        return positionSignificance == PositionSignificance.PARTIAL;
    }

    /**
     * @return whether this code snippet has full position significance.
     */
    public boolean hasFullPositionSignificance() {
        return positionSignificance == PositionSignificance.FULL;
    }

    /**
     * Mark this code snippet as having full position significance.
     */
    public void markFullPositionSignificance() {
        positionSignificance = PositionSignificance.FULL;
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
     * Create new joint semantics by joining a number of existing ones. It has the following properties:
     * <ul>
     * <li>keep is the disjunction of all keeps</li>
     * <li>position significance is the most significant</li>
     * <li>bidirectionalBlockDepthChange is the sum of all bidirectionalBlockDepthChanges</li>
     * <li>reads is the union of all reads</li>
     * <li>writes is the union of all writes</li>
     * </ul>
     * @param semanticsList A list of the semantics which should be joined.
     * @return New semantics which were created by joining the elements in the semanticsList.
     */
    public static CodeSemantics join(List<CodeSemantics> semanticsList) {
        boolean keep = false;
        PositionSignificance positionSignificance = PositionSignificance.NONE;
        int bidirectionalBlockDepthChange = 0;
        Set<Variable> reads = new HashSet<>();
        Set<Variable> writes = new HashSet<>();
        for (CodeSemantics semantics : semanticsList) {
            keep = keep || semantics.keep;
            if (semantics.positionSignificance.compareTo(positionSignificance) > 0) {
                positionSignificance = semantics.positionSignificance;
            }
            bidirectionalBlockDepthChange += semantics.bidirectionalBlockDepthChange();
            reads.addAll(semantics.reads);
            writes.addAll(semantics.writes);
        }
        return new CodeSemantics(keep, positionSignificance, bidirectionalBlockDepthChange, reads, writes);
    }

    @Override
    public String toString() {
        List<String> properties = new LinkedList<>();
        if (keep)
            properties.add("keep");
        if (positionSignificance != PositionSignificance.NONE)
            properties.add(positionSignificance.name().toLowerCase() + " position significance");
        if (bidirectionalBlockDepthChange != 0)
            properties.add("change bidirectional block depth by " + bidirectionalBlockDepthChange);
        if (!reads.isEmpty())
            properties.add("read " + String.join(" ", reads.stream().map(Variable::toString).toList()));
        if (!writes.isEmpty())
            properties.add("write " + String.join(" ", writes.stream().map(Variable::toString).toList()));
        return String.join(", ", properties);
    }
}