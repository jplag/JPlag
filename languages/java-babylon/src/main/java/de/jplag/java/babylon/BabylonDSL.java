package de.jplag.java.babylon;

import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeElement;
import jdk.incubator.code.Op;
import jdk.incubator.code.Value;
import jdk.incubator.code.dialect.core.CoreOp;

import java.util.SequencedCollection;

import static jdk.incubator.code.dialect.java.JavaType.VOID;

/**
 * A set of utility methods for working with Babylon code models.
 * Designed to be used by "implementing" this interface (inspired by ASM).
 */
public interface BabylonDSL {
    /**
     * Copy the contents of a {@link Body} to a {@link Block.Builder}.
     *
     * @param from the source body
     * @param to the target block builder
     */
    default void copy(Body from, Block.Builder to) {
        // this implicitly clears out the parameters which we depend on
        for (Op op1 : requireSingle(from.blocks()).ops()) {
            if (isLocationMarker(op1)) continue;
            to.add(op1);
        }
    }

    /**
     * Creates a simple location marker.
     * 
     * @param location the location to mark
     * @return the new op
     * @see #isLocationMarker(Op) 
     */
    default Op locationMarker(Op.Location location) {
        var op = CoreOp.constant(VOID, null);
        op.setLocation(location);
        return op;
    }

    /**
     * Whether the given {@link Op} is a simple location marker.
     * 
     * @param op the op
     * @return true if it is a location marker
     * @see #locationMarker(Op.Location)
     */
    default boolean isLocationMarker(Op op) {
        if (!(op instanceof CoreOp.ConstantOp cnst)) return false;
        if (cnst.resultType() != VOID) return false;
        if (cnst.value() != null) return false;
        return true;
    }

    /**
     * Obtain the declaring location of a body.
     * 
     * @param body the body
     * @return the location of the body
     */
    default Op.Location location(Body body) {
        if (body == null) return null;
        for (Block block : body.blocks()) {
            var location = location(block);
            if (location != null) return location;
        }
        return null;
    }

    /**
     * Obtain the declaring location of a block.
     * 
     * @param block the block
     * @return the location of the block
     */
    default Op.Location location(Block block) {
        for (Op op : block.ops()) {
            var location = op.location();
            if (location != null) return location;
        }
        return null;
    }

    /**
     * Obtain the declaring location of a code element.
     *
     * @param ce the code element
     * @return the location of the code element
     */
    default Op.Location location(CodeElement<?, ?> ce) {
        return switch (ce) {
            case Block block -> location(block);
            case Body body -> location(body);
            case Op op -> op.location();
        };
    }

    /**
     * Obtain the declaring location of a value.
     *
     * @param value the value
     * @return the location of the value
     */
    default Op.Location location(Value value) {
        return location(value.declaringElement());
    }

    /**
     * Place an {@link Op} into a {@link Block.Builder} and set its {@link Op.Location}.
     *
     * @param bd builder to add the {@link Op} to
     * @param location location of the {@link Op}
     * @param op operation to add
     * @return operation result of the appended operation
     */
    default Op.Result place(Block.Builder bd, Op.Location location, Op op) {
        op.setLocation(location);
        return bd.add(op);
    }

    /**
     * Obtains the single element of a {@link SequencedCollection} and ensures no others exist.
     *
     * @param collection the collection to take the element from
     * @param <T> type of elements in the collection
     * @return the single element
     * @throws IllegalStateException if there is not exactly one element
     */
    default <T> T requireSingle(SequencedCollection<T> collection) {
        if (collection.size() != 1) {
            throw new IllegalStateException("Expected exactly one element, but found: " + collection.size());
        }
        return collection.getFirst();
    }
}
