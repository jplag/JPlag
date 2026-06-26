package de.jplag.java.babylon.transformer.impl.util;

import java.util.List;
import java.util.Objects;

import de.jplag.java.babylon.BabylonDSL;

import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link CodeTransformer} that replaces the final {@link CoreOp.YieldOp} in a body.<br>
 * Use {@link Block.Builder#transformBody(Body, List, CodeTransformer)} to use this, see
 * {@link de.jplag.java.babylon.transformer.impl.AssertForceTransformer} for an example.
 */
public interface YieldTransformer extends CodeTransformer, BabylonDSL {
    @Override
    default Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (op instanceof CoreOp.YieldOp yield) {
            return acceptYield(Objects.requireNonNull(builder), yield);
        } else {
            placeExact(builder, op.location(), op);
            return builder;
        }
    }

    /**
     * Transforms the yield operation into the output model.<br>
     * See {@link #acceptOp} for a detailed description on how to use this.
     * @param builder the current output block builder
     * @param yield the input operation to transform
     * @return the continuation builder to use to transform subsequent input operations from the same input block
     */
    Block.Builder acceptYield(Block.Builder builder, CoreOp.YieldOp yield);
}
