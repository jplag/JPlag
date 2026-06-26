package de.jplag.java.babylon.transformer.impl.util;

import jdk.incubator.code.Block;
import jdk.incubator.code.Op;
import jdk.incubator.code.Value;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link YieldTransformer} that replaces value yields with void yields, storing the result in a predefined variable.
 * @param variable the variable to store the result to
 */
public record YieldAssignTransformer(Op.Result variable) implements YieldTransformer {
    @Override
    public Block.Builder acceptYield(Block.Builder builder, CoreOp.YieldOp yield) {
        Value result = builder.context().getValue(yield.yieldValue());
        place(builder, yield.location(), CoreOp.varStore(variable, result));
        place(builder, yield.location(), CoreOp.core_yield());
        return builder;
    }
}
