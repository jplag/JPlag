package de.jplag.java.babylon;

import static jdk.incubator.code.dialect.java.JavaType.VOID;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;

import javax.annotation.Nullable;

import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeContext;
import jdk.incubator.code.CodeElement;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.CodeType;
import jdk.incubator.code.Op;
import jdk.incubator.code.Value;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.core.Inliner;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.JavaType;
import jdk.incubator.code.extern.OpWriter;

/**
 * A set of utility methods for working with Babylon code models. Designed to be used by "implementing" this interface
 * (inspired by ASM).
 */
public interface BabylonDSL {
    /**
     * Copy the contents of a {@link Body} to a {@link Block.Builder}.
     * @param from the source body
     * @param to the target block builder
     */
    default void copy(Body from, Block.Builder to) {
        // this implicitly clears out the parameters which we depend on
        for (Op op1 : requireSingle(from.blocks()).ops()) {
            if (isLocationMarker(op1))
                continue;
            to.add(op1);
        }
    }

    /**
     * Creates a simple location marker.
     * @param location the location to mark
     * @return the new op
     * @see #isLocationMarker(Op)
     */
    default Op locationMarker(Op.Location location) {
        CoreOp.ConstantOp op = CoreOp.constant(VOID, null);
        op.setLocation(location);
        return op;
    }

    /**
     * Whether the given {@link Op} is a simple location marker.
     * @param op the op
     * @return true if it is a location marker
     * @see #locationMarker(Op.Location)
     */
    default boolean isLocationMarker(Op op) {
        if (!(op instanceof CoreOp.ConstantOp cnst))
            return false;
        if (cnst.resultType() != VOID)
            return false;
        if (cnst.value() != null)
            return false;
        return true;
    }

    /**
     * Obtain the declaring location of a body.
     * @param body the body
     * @return the location of the body
     */
    default Op.Location location(Body body) {
        if (body == null)
            return null;
        for (Block block : body.blocks()) {
            Op.Location location = location(block);
            if (location != null)
                return location;
        }
        return null;
    }

    /**
     * Obtain the declaring location of a block.
     * @param block the block
     * @return the location of the block
     */
    default Op.Location location(Block block) {
        for (Op op : block.ops()) {
            Op.Location location = op.location();
            if (location != null)
                return location;
        }
        return null;
    }

    /**
     * Obtain the declaring location of a code element.
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
     * @param value the value
     * @return the location of the value
     */
    default Op.Location location(Value value) {
        return location(value.declaringElement());
    }

    /**
     * Place an {@link Op} into a {@link Block.Builder} and set its {@link Op.Location}.
     * @param bd builder to add the {@link Op} to
     * @param location location of the {@link Op}
     * @param op operation to add
     * @return operation result of the appended operation
     */
    default Op.Result place(Block.Builder bd, Op.Location location, Op op) {
        if (location != null) {
            op.setLocation(location);
        }
        return bd.add(op);
    }

    /**
     * Place an {@link Op} into a {@link Block.Builder} <b>without using transform-on-append</b> and set its
     * {@link Op.Location}.
     * @param bd builder to add the {@link Op} to
     * @param location location of the {@link Op}
     * @param op operation to add
     * @return operation result of the appended operation
     */
    default Op.Result placeExact(Block.Builder bd, Op.Location location, Op op) {
        // This logic mirrors the internal logic in builder.add, except for the fact that transformation is delegated to the
        // copying transformer.
        // This allows bodies of this op to have their contents processed normally rather than being handled by the transformer
        // invoking this.
        Op.Result result = place(bd, location,
                op.isPlacedInBlock() || op.isRoot() ? op.transform(bd.context(), CodeTransformer.COPYING_TRANSFORMER) : op);
        if (op.result() != null && bd.context().queryValue(op.result()).isEmpty()) {
            bd.context().mapValue(op.result(), result);
        }
        return result;
    }

    /**
     * Inline an {@link Op.Invokable} into a {@link Block.Builder}, optionally storing the result in a variable.
     * @param bd the block builder
     * @param target the op to inline
     * @param args the arguments to pass to the inlined op
     * @param resultVariable the variable to store the result in
     * @param <O> the type of operation to inline
     */
    default <O extends Op & Op.Invokable> void inline(Block.Builder bd, O target, List<Value> args, @Nullable Value resultVariable) {
        Inliner.inline(bd, target, args, (b, value) -> {
            if (resultVariable != null) {
                b.add(CoreOp.varStore(resultVariable, value));
            }
        });
    }

    /**
     * Obtains the single element of a {@link SequencedCollection} and ensures no others exist.
     * @param collection the collection to take the element from
     * @param <T> type of elements in the collection
     * @return the single element
     * @throws IllegalStateException if there is not exactly one element
     */
    default <T> T requireSingle(SequencedCollection<T> collection) {
        Iterator<T> iterator = collection.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalStateException("Expected exactly one element but got empty");
        }
        T result = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalStateException("Expected exactly one element but found more");
        }
        return result;
    }

    /**
     * Obtains a string representing a value, for use in identifying store operations.
     * @param value the {@link Value} to which something is stored
     * @return a name representing the {@link Value}
     */
    default String name(Value value) {
        return switch (value) {
            case Block.Parameter parameter -> Integer.toString(parameter.index());
            case Op.Result result -> switch (result.op()) {
                case CoreOp.VarOp varOp -> varOp.varName();
                default -> result.op().toText();
            };
        };
    }

    /**
     * Create a new comment op. Can be pruned later with
     * {@link de.jplag.java.babylon.transformer.impl.DeadCodeEliminationTransformer}.
     * @param comment the comment the op should wrap
     * @return the op
     */
    default Op commentOp(String comment) {
        return CoreOp.constant(JavaType.J_L_STRING, comment);
    }

    /**
     * Convert a body to a textual representation.<br>
     * Since the real API for this is not public, this creates a wrapping Op and prints that.
     * @param body the body to convert to text
     * @return the textual representation
     */
    default String toText(Body body) {
        String result = new Op(List.of()) {
            @Override
            public Op transform(CodeContext cc, CodeTransformer ct) {
                throw new UnsupportedOperationException();
            }

            @Override
            public CodeType resultType() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String externalizeOpName() {
                return "";
            }

            @Override
            public List<Body> bodies() {
                return List.of(body);
            }
        }.toText();
        assert result.charAt(0) == ' '; // this space is inserted by OpWriter after the externalizable name and before the actual body
        return result.substring(1); // trim the space, obtaining a string representing exclusively the contained body
    }

    /**
     * Convert a block to a textual representation.
     * @param block the block to convert to text
     * @return the textual representation
     * @throws UncheckedIOException if the internal string writer cannot be closed
     */
    default String toText(Block block) {
        try (StringWriter sw = new StringWriter()) {
            OpWriter opWriter = new OpWriter(sw);
            for (Op op : block.ops()) {
                opWriter.writeOp(op);
            }
            return sw.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Returns true if op1 strictly dominates op2.<br>
     * This is NOT equivalent to {@link Value#isDominatedBy(Value)} in the case where op1 is in an inner block of the parent
     * of op2.
     * @param op1 the first op
     * @param op2 the second op
     * @return if op1 strictly dominates op2
     */
    default boolean dominates(Op op1, Op op2) {
        // Fast path
        if (op1.parent() == op2.parent()) {
            List<Op> blockOps = op1.parent().ops();
            return blockOps.indexOf(op1) < blockOps.indexOf(op2);
        }

        // Find all parents of op1 and save the ops in said parent that are parents of op1
        Map<Block, Op> op1ParentBlocks = new HashMap<>();
        Op currentOp = op1;
        do {
            Block parent = currentOp.parent();
            op1ParentBlocks.put(parent, currentOp);
            currentOp = parent.parent().parent();
        } while (currentOp != null && !(currentOp instanceof CoreOp.FuncOp) && !(currentOp instanceof JavaOp.LambdaOp)); // dominates() is ill-defined
                                                                                                                         // if this is a lambda passed
                                                                                                                         // to somewhere else

        // Find the first parent of op2 that is also a parent of op1, then compare within that parent
        currentOp = op2;
        do {
            Block parent = currentOp.parent();
            Op relevant1Parent = op1ParentBlocks.get(parent);
            if (relevant1Parent != null) {
                List<Op> ops = parent.ops();
                return ops.indexOf(relevant1Parent) < ops.indexOf(currentOp);
            }
            currentOp = parent.parent().parent();
        } while (currentOp != null && !(currentOp instanceof CoreOp.FuncOp) && !(currentOp instanceof JavaOp.LambdaOp)); // dominates() is ill-defined
                                                                                                                         // if this is a lambda passed
                                                                                                                         // to somewhere else
        // No common ancestor block
        return false;
    }

    /**
     * Arguments of the invoke operation. Unlike {@link JavaOp.InvokeOp#argOperands()}, this omits the receiver.
     * @param invokeOp the operation
     * @return the argument list
     */
    default List<Value> argOperands(JavaOp.InvokeOp invokeOp) {
        List<Value> result = invokeOp.argOperands();
        if (invokeOp.hasReceiver()) {
            assert result.getFirst() == invokeOp.receiverOperand();
            return result.subList(1, result.size());
        } else {
            return result;
        }
    }
}
