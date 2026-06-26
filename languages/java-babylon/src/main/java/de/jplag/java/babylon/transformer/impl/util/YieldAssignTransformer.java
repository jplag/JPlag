package de.jplag.java.babylon.transformer.impl.util;

import jdk.incubator.code.Block;
import jdk.incubator.code.Value;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * {@link YieldTransformer} that replaces value yields with void yields, storing the result in a predefined variable.
 * @param variable the variable to store the result to
 * @param emitYield whether a new yield with no yield value should be emitted
 */
public record YieldAssignTransformer(Value variable, boolean emitYield) implements YieldTransformer {
    /**
     * Create a new instance that emits yields.
     * @param variable the variable to store the result to
     */
    public YieldAssignTransformer(Value variable) {
        this(variable, true);
    }

    @Override
    public Block.Builder acceptYield(Block.Builder builder, CoreOp.YieldOp yield) {
        Value result = builder.context().getValue(yield.yieldValue());
        place(builder, yield.location(), CoreOp.varStore(variable, result));
        if (emitYield)
            place(builder, yield.location(), CoreOp.core_yield());
        return builder;
    }
}
