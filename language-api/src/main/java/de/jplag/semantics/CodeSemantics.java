package de.jplag.semantics;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Contains semantic information about a code fragment, in our case either a token or a statement.
 */
public class CodeSemantics {

    private boolean critical;
    private PositionSignificance positionSignificance;
    private final int bidirectionalBlockDepthChange;
    private final Set<Variable> reads;
    private final Set<Variable> writes;

    /**
     * Creates new semantics. reads and writes, which each contain the variables which were (potentially) read from/written
     * to in this code fragment, are created empty.
     * @param critical Whether the code fragment must be kept as it affects the program behavior or if it may be removed.
     * @param positionSignificance In which way the position of the code fragment relative to other tokens of the same type
     * is significant. For the possible options see {@link PositionSignificance}.
     * @param bidirectionalBlockDepthChange How the code fragment affects the depth of bidirectional blocks, meaning blocks
     * where any statement within it may be executed after any other. This will typically be a loop.
     * @param reads A set of the variables which were (potentially) read from in the code fragment.
     * @param writes A set of the variables which were (potentially) written to in the code fragment.
     */
    private CodeSemantics(boolean critical, PositionSignificance positionSignificance, int bidirectionalBlockDepthChange, Set<Variable> reads,
            Set<Variable> writes) {
        this.critical = critical;
        this.positionSignificance = positionSignificance;
        this.bidirectionalBlockDepthChange = bidirectionalBlockDepthChange;
        this.reads = reads;
        this.writes = writes;
    }

    private CodeSemantics(boolean critical, PositionSignificance positionSignificance, int bidirectionalBlockDepthChange) {
        this(critical, positionSignificance, bidirectionalBlockDepthChange, new HashSet<>(), new HashSet<>());
    }

    /**
     * Creates new semantics with the following meaning: The code fragment may be removed, and its position relative to
     * other code fragments may change. Example: An assignment to a local variable.
     */
    public CodeSemantics() {
        this(false, PositionSignificance.NONE, 0);
    }

    /**
     * @return new semantics with the following meaning: The code fragment may not be removed, and its position relative to
     * other code fragments may change. Example: An attribute declaration.
     */
    public static CodeSemantics createKeep() {
        return new CodeSemantics(true, PositionSignificance.NONE, 0);
    }

    /**
     * @return new semantics with the following meaning: The code fragment may not be removed, and its position must stay
     * invariant to other code fragments of the same type. Example: A method call which is guaranteed to not result in an
     * exception.
     */
    public static CodeSemantics createCritical() {
        return new CodeSemantics(true, PositionSignificance.PARTIAL, 0);
    }

    /**
     * @return new semantics with the following meaning: The code fragment may not be removed, and its position must stay
     * invariant to all other code fragments. Example: A return statement.
     */
    public static CodeSemantics createControl() {
        return new CodeSemantics(true, PositionSignificance.FULL, 0);
    }

    /**
     * @return new semantics with the following meaning: The code fragment may not be removed, and its position must stay
     * invariant to all other code fragments, which also begins a bidirectional block. Example: The beginning of a while
     * loop.
     */
    public static CodeSemantics createLoopBegin() {
        return new CodeSemantics(true, PositionSignificance.FULL, 1);
    }

    /**
     * @return new semantics with the following meaning: The code fragment may not be removed, and its position must stay
     * invariant to all other code fragments, which also ends a bidirectional block. Example: The end of a while loop.
     */
    public static CodeSemantics createLoopEnd() {
        return new CodeSemantics(true, PositionSignificance.FULL, -1);
    }

    /**
     * @return whether this token is critical to the program behavior.
     */
    public boolean isCritical() {
        return critical;
    }

    /**
     * Mark this token as critical to the program behavior.
     */
    public void markAsCritical() {
        critical = true;
    }

    /**
     * @return the change this code fragment causes in the depth of bidirectional loops.
     */
    public int bidirectionalBlockDepthChange() {
        return bidirectionalBlockDepthChange;
    }

    /**
     * @return whether this code fragment has partial position significance.
     */
    public boolean hasPartialPositionSignificance() {
        return positionSignificance == PositionSignificance.PARTIAL;
    }

    /**
     * @return whether this code fragment has full position significance.
     */
    public boolean hasFullPositionSignificance() {
        return positionSignificance == PositionSignificance.FULL;
    }

    /**
     * Mark this code fragment as having full position significance.
     */
    public void markFullPositionSignificance() {
        positionSignificance = PositionSignificance.FULL;
    }

    /**
     * @return an unmodifiable set of the variables which were (potentially) read from in this code fragment.
     */
    public Set<Variable> reads() {
        return Collections.unmodifiableSet(reads);
    }

    /**
     * @return an unmodifiable set of the variables which were (potentially) written to in this code fragment.
     */
    public Set<Variable> writes() {
        return Collections.unmodifiableSet(writes);
    }

    /**
     * Add a variable to the set of variables which were (potentially) read from in this code fragment.
     * @param variable The variable which is added.
     */
    public void addRead(Variable variable) {
        reads.add(variable);
    }

    /**
     * Add a variable to the set of variables which were (potentially) written to in this code fragment.
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
            keep = keep || semantics.critical;
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
        if (critical) {
            properties.add("keep");
        }
        if (positionSignificance != PositionSignificance.NONE) {
            properties.add(positionSignificance.name().toLowerCase() + " position significance");
        }
        if (bidirectionalBlockDepthChange != 0) {
            properties.add("change bidirectional block depth by " + bidirectionalBlockDepthChange);
        }
        if (!reads.isEmpty()) {
            properties.add("read " + String.join(" ", reads.stream().map(Variable::toString).toList()));
        }
        if (!writes.isEmpty()) {
            properties.add("write " + String.join(" ", writes.stream().map(Variable::toString).toList()));
        }
        return String.join(", ", properties);
    }
}