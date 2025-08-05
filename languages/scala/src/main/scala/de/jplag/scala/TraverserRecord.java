package de.jplag.scala;

import java.util.Optional;
import java.util.function.BiConsumer;

import scala.collection.immutable.List;
import scala.meta.Tree;

/**
 * A traverser record, that contains information about the tokens to be added for a specific ast node. The name is kept
 * short to make the code more readable. Also contains information about how to traverse the node
 * @param before Token before the node
 * @param after Token after the node
 * @param traverse The traverse function
 */
public record TraverserRecord(Optional<ScalaTokenType> before, Optional<ScalaTokenType> after, BiConsumer<Tree, ScalaParser> traverse) {
    /**
     * Initializes the token record with no tokens and a traverse function that visits all children.
     */
    public TraverserRecord() {
        this(Optional.empty(), Optional.empty(), (tree, parser) -> {
            List<Tree> children = tree.children();
            for (int i = 0; i < children.size(); i++) {
                parser.visit(children.apply(i));
            }
        });
    }

    /**
     * Same as {@link #TraverserRecord()}, but with tokens.
     * @param before Token before the node
     * @param after Token after the node
     */
    public TraverserRecord(ScalaTokenType before, ScalaTokenType after) {
        this(Optional.of(before), Optional.of(after), (tree, parser) -> {
            List<Tree> children = tree.children();
            for (int i = 0; i < children.size(); i++) {
                parser.visit(children.apply(i));
            }
        });
    }

    /**
     * Same as {@link #TraverserRecord()}, but with a token before.
     * @param before Token before the node
     */
    public TraverserRecord(ScalaTokenType before) {
        this(Optional.of(before), Optional.empty(), (tree, parser) -> {
            List<Tree> children = tree.children();
            for (int i = 0; i < children.size(); i++) {
                parser.visit(children.apply(i));
            }
        });
    }

    /**
     * Returns a copy with a different token before the node.
     * @param tokenType Token before the node
     * @return The copy
     */
    public TraverserRecord before(ScalaTokenType tokenType) {
        return new TraverserRecord(Optional.of(tokenType), after, traverse);
    }

    /**
     * Returns a copy with a different token after the node.
     * @param tokenType Token after the node
     * @return The copy
     */
    public TraverserRecord after(ScalaTokenType tokenType) {
        return new TraverserRecord(before, Optional.of(tokenType), traverse);
    }

    /**
     * Returns a copy with a different traverse function.
     * @param traverse The new traverse function
     * @return The copy
     */
    public TraverserRecord traverse(Runnable traverse) {
        return new TraverserRecord(before, after, (tree, parser) -> traverse.run());
    }
}
