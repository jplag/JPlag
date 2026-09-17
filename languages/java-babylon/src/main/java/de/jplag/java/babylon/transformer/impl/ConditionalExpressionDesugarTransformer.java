package de.jplag.java.babylon.transformer.impl;

import static de.jplag.java.babylon.BabylonUtils.place;

import java.util.List;

import de.jplag.java.babylon.transformer.SimpleTransformation;
import de.jplag.java.babylon.transformer.impl.util.YieldAssignTransformer;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.JavaOp;

/**
 * {@link SimpleTransformation} that desugars {@link JavaOp.ConditionalExpressionOp} into an if statement.
 */
@AutoService(SimpleTransformation.class)
public class ConditionalExpressionDesugarTransformer implements SimpleTransformation {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "conditional-expression-desugar";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (!(op instanceof JavaOp.ConditionalExpressionOp cExprOp)) {
            builder.add(op);
            return builder;
        }

        Op.Result variable = place(builder, cExprOp.location(), CoreOp.var(cExprOp.resultType()));
        place(builder, cExprOp.location(),
                JavaOp.if_(builder.parentBody())
                        .if_(b -> b.transformBody(cExprOp.predicateBody(), List.of(), builder.context(), CodeTransformer.COPYING_TRANSFORMER))
                        .then(b -> b.transformBody(cExprOp.trueBody(), List.of(), builder.context(), new YieldAssignTransformer(variable)))
                        .else_(b -> b.transformBody(cExprOp.falseBody(), List.of(), builder.context(), new YieldAssignTransformer(variable))));
        Op.Result result = place(builder, cExprOp.location(), CoreOp.varLoad(variable));
        builder.context().mapValue(cExprOp.result(), result);

        return builder;
    }
}
