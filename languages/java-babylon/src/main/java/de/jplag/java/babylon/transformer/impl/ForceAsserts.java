package de.jplag.java.babylon.transformer.impl;

import java.util.List;

import de.jplag.java.babylon.transformer.SimpleTransformation;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.MethodRef;

/**
 * {@link SimpleTransformation} that replaces asserts with conditional throws for environments where asserts are enabled.
 */
@AutoService(SimpleTransformation.class)
public class ForceAsserts implements SimpleTransformation {
    @Override
    public String getIdentifier() {
        return "asserts-force";
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (op instanceof JavaOp.AssertOp ao) {
            builder.add(JavaOp.if_(List.of(ao.predicateBody().transform(builder.context(), CodeTransformer.COPYING_TRANSFORMER),
                    ao.detailsBody().transform(builder.context(), new DetailsTransformer()))));
        } else {
            builder.add(op);
        }
        return builder;
    }

    private static final class DetailsTransformer implements CodeTransformer {
        @Override
        public Block.Builder acceptOp(Block.Builder builder, Op op) {
            if (op instanceof CoreOp.YieldOp yield) {
                var exception = builder.add(JavaOp.new_(MethodRef.constructor(AssertionError.class, Object.class), yield.yieldValue()));
                builder.add(JavaOp.throw_(exception));
            } else {
                builder.add(op);
            }
            return builder;
        }
    }
}
