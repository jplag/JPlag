package de.jplag.java.babylon;

import java.util.List;
import java.util.stream.Stream;

import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.SimpleTreeVisitor;

/**
 * A simple {@link TreeVisitor} that broadcasts all visits to a list of other visitors.<br>
 * Deliberately does not support passing or returning data.
 */
public class MulticastTreeVisitor extends SimpleTreeVisitor<Void, Void> {
    protected final List<TreeVisitor<?, ?>> targets;

    /**
     * Create a new {@link MulticastTreeVisitor}.
     * @param targets the targets to which to broadcast visits
     */
    public MulticastTreeVisitor(List<? extends TreeVisitor<?, ?>> targets) {
        // Copy the target list and replace multicast visitors with their constituent targets for efficiency
        this.targets = targets.stream().flatMap(s -> s instanceof MulticastTreeVisitor mv ? mv.targets.stream() : Stream.of(s)).toList();
    }

    @Override
    protected Void defaultAction(Tree node, Void unused) {
        for (TreeVisitor<?, ?> target : targets) {
            node.accept(target, null);
        }
        return null;
    }
}
