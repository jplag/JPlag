package de.jplag.java.babylon;

import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.SimpleTreeVisitor;

import java.util.List;
import java.util.stream.Stream;

/**
 * A simple {@link TreeVisitor} that broadcasts all visits to a list of other visitors.
 * Deliberately does not support passing or returning data.
 */
public final class MulticastTreeVisitor extends SimpleTreeVisitor<Void, Void> {
    private final List<TreeVisitor<?, ?>> targets;

    private MulticastTreeVisitor(List<TreeVisitor<?, ?>> targets) {
        this.targets = targets;
    }

    public static MulticastTreeVisitor create(List<TreeVisitor<?, ?>> targets) {
        // Copy the target list and replace multicast visitors with their constituent targets for efficiency
        var flatTargets = targets.stream()
                .flatMap(s -> s instanceof MulticastTreeVisitor mv ? mv.targets.stream() : Stream.of(s))
                .toList();
        return new MulticastTreeVisitor(flatTargets);
    }

    @Override
    protected Void defaultAction(Tree node, Void unused) {
        for (TreeVisitor<?, ?> target : targets) {
            node.accept(target,  null);
        }
        return null;
    }
}
