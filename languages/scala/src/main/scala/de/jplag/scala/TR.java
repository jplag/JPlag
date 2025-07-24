package de.jplag.scala;

import java.util.Optional;
import java.util.function.BiConsumer;

import scala.collection.immutable.List;
import scala.meta.Tree;

/**
 * A token record, that contains information about the tokens to be added for a specific ast node. The name is kept
 * short to make the code more readable. Also contains information about how to traverse the node
 * @param before Token before the node
 * @param after Token after the ode
 * @param traverse The traverse function
 */
public record TR(Optional<ScalaTokenType> before, Optional<ScalaTokenType> after, BiConsumer<Tree, ScalaParser> traverse) {
    /**
     * Initializes the token record with no tokens and a traverse function that visits all children
     */
    public TR() {
        this(Optional.empty(), Optional.empty(), (tree, parser) -> {
            List<Tree> children = tree.children();
            for (int i = 0; i < children.size(); i++) {
                parser.visit(children.apply(i));
            }
        });
    }

    /**
     * Same as {@link #TR()}, but with tokens
     * @param before Token before the node
     * @param after Token after the node
     */
    public TR(ScalaTokenType before, ScalaTokenType after) {
        this(Optional.of(before), Optional.of(after), (tree, parser) -> {
            List<Tree> children = tree.children();
            for (int i = 0; i < children.size(); i++) {
                parser.visit(children.apply(i));
            }
        });
    }

    /**
     * Same as {@link #TR()}, but with a token before
     * @param before Token before the node
     */
    public TR(ScalaTokenType before) {
        this(Optional.of(before), Optional.empty(), (tree, parser) -> {
            List<Tree> children = tree.children();
            for (int i = 0; i < children.size(); i++) {
                parser.visit(children.apply(i));
            }
        });
    }

    /**
     * Returns a copy with a different token before the node
     * @param tokenType Token before the node
     * @return The copy
     */
    public TR before(ScalaTokenType tokenType) {
        return new TR(Optional.of(tokenType), after, traverse);
    }

    /**
     * Returns a copy with a different token after the node
     * @param tokenType Token after the node
     * @return The copy
     */
    public TR after(ScalaTokenType tokenType) {
        return new TR(before, Optional.of(tokenType), traverse);
    }

    /**
     * Returns a copy with a different traverse function
     * @param traverse The new traverse function
     * @return The copy
     */
    public TR traverse(Runnable traverse) {
        return new TR(before, after, (tree, parser) -> traverse.run());
    }
}
