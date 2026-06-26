package de.jplag.java.babylon.transformer.impl;

import java.util.List;

import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.transformer.SimpleTransformation;
import de.jplag.java.babylon.transformer.impl.util.YieldTransformer;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.Op;
import jdk.incubator.code.Value;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.JavaOp;

/**
 * {@link SimpleTransformation} that desugars {@link JavaOp.ForOp} to a while loop.
 */
@AutoService(SimpleTransformation.class)
public class ForDesugarTransformer implements SimpleTransformation, BabylonDSL {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "for-desugar";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (!(op instanceof JavaOp.ForOp forOp)) {
            builder.add(op);
            return builder;
        }

        List<Block.Parameter> loopVars = forOp.condBody().entryBlock().parameters();

        builder.transformBody(forOp.initBody(), List.of(), builder.context(), (YieldTransformer) (block, yield) -> {
            List<Value> outputs;
            if (loopVars.size() == 1) {
                outputs = List.of(yield.yieldValue());
            } else {
                CoreOp.TupleOp tupleOp = (CoreOp.TupleOp) yield.yieldValue().asResult().op();
                outputs = tupleOp.operands();
            }
            builder.context().mapValues(loopVars, block.context().getValues(outputs));
            return block;
        });

        List<Value> mappedLoopVars = builder.context().getValues(loopVars);

        place(builder, forOp.location(),
                JavaOp.while_(builder.parentBody())
                        .predicate(b -> b.transformBody(forOp.condBody(), mappedLoopVars, builder.context(), CodeTransformer.COPYING_TRANSFORMER))
                        .body(b -> b.transformBody(forOp.loopBody(), mappedLoopVars, builder.context(), (block, innerOp) -> {
                            if (innerOp instanceof JavaOp.ContinueOp continueOp) {
                                block.transformBody(forOp.updateBody(), mappedLoopVars, builder.context(), (YieldTransformer) (block1, _) -> {
                                    block1.add(continueOp);
                                    return block1;
                                });
                            } else {
                                block.add(innerOp);
                            }
                            return block;
                        })));

        return builder;
    }
}
