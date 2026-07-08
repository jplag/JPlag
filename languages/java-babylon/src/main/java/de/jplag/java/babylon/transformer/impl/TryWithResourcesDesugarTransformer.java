package de.jplag.java.babylon.transformer.impl;

import static de.jplag.java.babylon.BabylonUtils.copy;
import static de.jplag.java.babylon.BabylonUtils.location;
import static de.jplag.java.babylon.BabylonUtils.locationMarker;
import static de.jplag.java.babylon.BabylonUtils.place;
import static de.jplag.java.babylon.BabylonUtils.requireSingle;
import static jdk.incubator.code.dialect.java.JavaType.J_L_OBJECT;
import static jdk.incubator.code.dialect.java.JavaType.VOID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import de.jplag.java.babylon.transformer.SimpleTransformation;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeContext;
import jdk.incubator.code.CodeType;
import jdk.incubator.code.Op;
import jdk.incubator.code.Value;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.core.CoreType;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.MethodRef;

/**
 * {@link SimpleTransformation} that converts try-with-resources into regular try-finally statements.
 */
@AutoService(SimpleTransformation.class)
public class TryWithResourcesDesugarTransformer implements SimpleTransformation {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "try-with-resources-desugar";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (!(op instanceof JavaOp.TryOp tryOp)) {
            builder.add(op);
            return builder;
        }

        List<Body> resources = tryOp.resourceBodies();
        if (resources.isEmpty()) {
            builder.add(op);
            return builder;
        }

        List<Value> liftedParameters = new ArrayList<>();
        for (Body resource : resources) {
            Block resourceBlock = requireSingle(resource.blocks());
            Iterator<Op> it = resourceBlock.children().reversed().iterator();
            CoreOp.YieldOp resourceYield = (CoreOp.YieldOp) it.next();
            CoreOp.VarOp varOp = (CoreOp.VarOp) it.next();
            for (Value operand : varOp.operands()) {
                liftedParameters.add(place(builder, location(operand), CoreOp.var(varOp.varName(), operand.type())));
            }
        }

        Body.Builder tryBody = buildTry(builder.parentBody(), builder.context(), liftedParameters, resources, tryOp.body());

        List<Body.Builder> catchBodies = new ArrayList<>();
        for (Body catchBody : tryOp.catchBodies()) {
            catchBodies.add(buildCatch(builder.parentBody(), builder.context(), catchBody));
        }

        Body.Builder finallyBody = buildFinally(builder.parentBody(), builder.context(), liftedParameters, tryOp.finallyBody());

        place(builder, tryOp.location(), JavaOp.try_(List.of(), tryBody, catchBodies, finallyBody));
        return builder;
    }

    private Body.Builder buildTry(Body.Builder connectedAncestorBody, CodeContext cc, List<Value> liftedParameters, List<Body> resources,
            Body tryBody) {
        Body.Builder bodyBuilder = Body.Builder.of(connectedAncestorBody, CoreType.FUNCTION_TYPE_VOID, cc);
        bodyBuilder.entryBlock().context().mapBlock(tryBody.entryBlock(), bodyBuilder.entryBlock());
        bodyBuilder.entryBlock().context().mapValues(tryBody.entryBlock().parameters(), liftedParameters);
        for (int i = 0, resourcesSize = resources.size(); i < resourcesSize; i++) {
            Body resource = resources.get(i);
            Block resourceBlock = requireSingle(resource.blocks());
            Iterator<Op> it = resourceBlock.children().reversed().iterator();
            CoreOp.YieldOp resourceYield = (CoreOp.YieldOp) it.next();
            CoreOp.VarOp varOp = (CoreOp.VarOp) it.next();

            bodyBuilder.entryBlock().context().mapBlock(resourceBlock, bodyBuilder.entryBlock());
            bodyBuilder.entryBlock().context().mapValue(varOp.result(), liftedParameters.get(i));

            List<Op> initializeResources = resourceBlock.children().subList(0, resourceBlock.children().size() - 2);
            for (Op initOp : initializeResources) {
                bodyBuilder.entryBlock().add(initOp);
            }

            Value initOperand = requireSingle(varOp.operands());
            place(bodyBuilder.entryBlock(), varOp.location(),
                    CoreOp.varStore(liftedParameters.get(i), bodyBuilder.entryBlock().context().getValue(initOperand)));
        }
        copy(tryBody, bodyBuilder.entryBlock());
        return bodyBuilder;
    }

    private Body.Builder buildCatch(Body.Builder connectedAncestorBody, CodeContext cc, Body catchBody) {
        CodeType exceptionType = catchBody.bodySignature().parameterTypes().getFirst();
        Body.Builder bodyBuilder = Body.Builder.of(connectedAncestorBody, CoreType.functionType(VOID, exceptionType), cc);

        // the first entry loads the exception which is located at the try
        // this corrects that
        Iterator<Op> it = catchBody.entryBlock().ops().iterator();
        if (it.hasNext()) {
            if (!(it.next() instanceof CoreOp.VarOp))
                throw new IllegalStateException();
            if (it.hasNext())
                bodyBuilder.entryBlock().add(locationMarker(location(it.next())));
        }

        bodyBuilder.entryBlock().context().mapValues(catchBody.entryBlock().parameters(), bodyBuilder.entryBlock().parameters());
        copy(catchBody, bodyBuilder.entryBlock());
        return bodyBuilder;
    }

    private Body.Builder buildFinally(Body.Builder connectedAncestorBody, CodeContext cc, List<Value> liftedParameters, @Nullable Body finallyBody) {
        Body.Builder bodyBuilder = Body.Builder.of(connectedAncestorBody, CoreType.FUNCTION_TYPE_VOID, cc);

        if (finallyBody != null) {
            bodyBuilder.entryBlock().add(locationMarker(location(finallyBody)));
        }

        for (Value var : liftedParameters) {
            Value loadedVar = bodyBuilder.entryBlock().add(CoreOp.varLoad(var));
            Op.Location loc = location(var);
            // technically not correct since this skips the extra exception handling
            bodyBuilder.entryBlock().add(JavaOp.if_(bodyBuilder.entryBlock().parentBody()).if_(bd1 -> {
                Op.Result nil = place(bd1, loc, CoreOp.constant(J_L_OBJECT, null));
                place(bd1, loc, CoreOp.core_yield(place(bd1, loc, JavaOp.neq(loadedVar, nil))));
            }).then(bd1 -> {
                place(bd1, loc, JavaOp.invoke(MethodRef.method(AutoCloseable.class, "close", void.class), loadedVar));
                place(bd1, loc, CoreOp.core_yield());
            }).else_());
        }

        if (finallyBody != null) {
            bodyBuilder.entryBlock().context().mapBlock(finallyBody.entryBlock(), bodyBuilder.entryBlock());
            copy(finallyBody, bodyBuilder.entryBlock());
        }
        return bodyBuilder;
    }
}
