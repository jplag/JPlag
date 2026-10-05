package de.jplag.java.babylon.transformer.impl;

import static de.jplag.java.babylon.BabylonUtils.place;
import static de.jplag.java.babylon.BabylonUtils.requireSingle;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import de.jplag.java.babylon.transformer.SimpleTransformation;
import de.jplag.java.babylon.transformer.impl.util.YieldAssignTransformer;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.Op;
import jdk.incubator.code.Value;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.JavaType;

/**
 * {@link SimpleTransformation} that desugars {@link JavaOp.JavaSwitchOp}.
 */
@AutoService(SimpleTransformation.class)
public class SwitchExpressionDesugarTransformer implements SimpleTransformation {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "switch-expression-desugar";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        // JavaSwitchOp is the superclass of SwitchStatementOp and SwitchExpressionOp, this allows us to handle both.
        if (!(op instanceof JavaOp.JavaSwitchOp switchOp) || containsFallthrough(switchOp)) {
            builder.add(op);
            return builder;
        }

        @Nullable
        Op.Result resultVar;
        if (switchOp.resultType() != JavaType.VOID) {
            resultVar = place(builder, switchOp.location(), CoreOp.var(switchOp.resultType()));
        } else {
            resultVar = null;
        }

        Value parameter = builder.context().getValue(switchOp.operands().getFirst());

        JavaOp.IfOp.ElseIfBuilder elseIfBuilder = null;
        for (Iterator<Body> iterator = switchOp.bodies().iterator(); iterator.hasNext();) {
            Body predicate = iterator.next();
            Body action = iterator.next();

            boolean hasParams = !predicate.entryBlock().parameters().isEmpty();
            Consumer<Block.Builder> predicateBuilder = b -> b.transformBody(predicate, hasParams ? List.of(parameter) : List.of(), builder.context(),
                    CodeTransformer.COPYING_TRANSFORMER);

            JavaOp.IfOp.ThenBuilder thenBuilder;
            if (elseIfBuilder == null) {
                thenBuilder = JavaOp.if_(builder.parentBody()).if_(predicateBuilder);
            } else {
                thenBuilder = elseIfBuilder.elseif(predicateBuilder);
            }

            elseIfBuilder = thenBuilder.then(b -> b.transformBody(action, List.of(), builder.context(),
                    resultVar == null ? CodeTransformer.COPYING_TRANSFORMER : new YieldAssignTransformer(resultVar)));
        }
        if (elseIfBuilder != null) {
            place(builder, switchOp.location(), elseIfBuilder.else_());
        }

        if (resultVar != null) {
            Op.Result result = place(builder, switchOp.location(), CoreOp.varLoad(resultVar));
            builder.context().mapValue(switchOp.result(), result);
        }

        return builder;
    }

    private boolean containsFallthrough(JavaOp.JavaSwitchOp switchOp) {
        for (Iterator<Body> iterator = switchOp.bodies().iterator(); iterator.hasNext();) {
            Body predicate = iterator.next();
            Body action = iterator.next();

            Op finalOp = requireSingle(action.blocks()).ops().getLast();
            if (finalOp instanceof JavaOp.SwitchFallthroughOp)
                return true;
        }

        return false;
    }
}
