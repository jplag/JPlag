package de.jplag.java.babylon.transformer.impl;

import java.util.List;

import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.transformer.SimpleTransformation;
import de.jplag.java.babylon.transformer.impl.util.YieldTransformer;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.Op;
import jdk.incubator.code.Value;
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
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "assert-force";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    private static final MethodRef ERROR_CREATE = MethodRef.constructor(AssertionError.class);
    private static final MethodRef ERROR_CREATE_WITH_PARAM = MethodRef.constructor(AssertionError.class, Object.class);

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (!(op instanceof JavaOp.AssertOp ao)) {
            builder.add(op);
            return builder;
        }

        Body.Builder passBody = Body.Builder.of(builder.parentBody(), CoreType.FUNCTION_TYPE_VOID, builder.context());
        place(passBody.entryBlock(), op.location(), CoreOp.core_yield());

        Body.Builder throwBody = Body.Builder.of(builder.parentBody(), CoreType.FUNCTION_TYPE_VOID, builder.context());
        if (ao.detailsBody() != null) {
            throwBody.entryBlock().transformBody(ao.detailsBody(), List.of(), (YieldTransformer) (block, yield) -> {
                Value result = block.context().getValue(yield.yieldValue());
                Op.Result exception = place(block, yield.location(), JavaOp.new_(ERROR_CREATE_WITH_PARAM, result));
                place(block, yield.location(), JavaOp.throw_(exception));
                return block;
            });
        } else {
            Op.Result exception = place(throwBody.entryBlock(), ao.location(), JavaOp.new_(ERROR_CREATE));
            place(throwBody.entryBlock(), ao.location(), JavaOp.throw_(exception));
        }
        place(builder, ao.location(),
                JavaOp.if_(List.of(ao.predicateBody().transform(builder.context(), CodeTransformer.COPYING_TRANSFORMER), passBody, throwBody)));
        return builder;
    }
}
