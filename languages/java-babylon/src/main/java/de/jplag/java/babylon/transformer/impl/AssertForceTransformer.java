package de.jplag.java.babylon.transformer.impl;

import java.util.List;

import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.transformer.SimpleTransformation;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.core.CoreType;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.MethodRef;

/**
 * {@link SimpleTransformation} that replaces asserts with conditional throws for environments where asserts are
 * enabled.
 */
@AutoService(SimpleTransformation.class)
public class AssertForceTransformer implements SimpleTransformation, BabylonDSL {
    @Override
    public String getIdentifier() {
        return "assert-force";
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (!(op instanceof JavaOp.AssertOp ao)) {
            builder.add(op);
            return builder;
        }

        Body.Builder passBody = Body.Builder.of(builder.parentBody(), CoreType.FUNCTION_TYPE_VOID);
        place(passBody.entryBlock(), op.location(), CoreOp.core_yield());

        Body.Builder throwBody = Body.Builder.of(builder.parentBody(), CoreType.FUNCTION_TYPE_VOID);
        if (ao.detailsBody() != null) {
            new DetailsTransformer().acceptBody(throwBody.entryBlock(), ao.detailsBody(), List.of());
        } else {
            Op.Result exception = place(throwBody.entryBlock(), ao.location(), JavaOp.new_(MethodRef.constructor(AssertionError.class)));
            place(throwBody.entryBlock(), ao.location(), JavaOp.throw_(exception));
        }
        place(builder, ao.location(),
                JavaOp.if_(List.of(ao.predicateBody().transform(builder.context(), CodeTransformer.COPYING_TRANSFORMER), passBody, throwBody)));
        return builder;
    }

    private static final class DetailsTransformer implements CodeTransformer, BabylonDSL {
        @Override
        public Block.Builder acceptOp(Block.Builder builder, Op op) {
            if (op instanceof CoreOp.YieldOp yield) {
                Op.Result exception = place(builder, yield.location(),
                        JavaOp.new_(MethodRef.constructor(AssertionError.class, Object.class), builder.context().getValue(yield.yieldValue())));
                place(builder, op.location(), JavaOp.throw_(exception));
            } else {
                builder.add(op);
            }
            return builder;
        }
    }
}
