package de.jplag.java.babylon.transformer.impl;

import static jdk.incubator.code.dialect.java.JavaType.J_L_OBJECT;
import static jdk.incubator.code.dialect.java.JavaType.VOID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.transformer.SimpleTransformation;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
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
public class TryWithResourcesDesugarTransformer implements SimpleTransformation, BabylonDSL {
    @Override
    public String getIdentifier() {
        return "try-with-resources-desugar";
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

        Body.Builder ancestorBody = builder.parentBody();

        Body.Builder tryBody = Body.Builder.of(ancestorBody, CoreType.FUNCTION_TYPE_VOID, builder.context());
        {
            Block.Builder bd = tryBody.entryBlock();
            bd.context().mapBlock(tryOp.body().entryBlock(), bd);
            bd.context().mapValues(tryOp.body().entryBlock().parameters(), liftedParameters);
            for (int i = 0, resourcesSize = resources.size(); i < resourcesSize; i++) {
                Body resource = resources.get(i);
                Block resourceBlock = requireSingle(resource.blocks());
                Iterator<Op> it = resourceBlock.children().reversed().iterator();
                CoreOp.YieldOp resourceYield = (CoreOp.YieldOp) it.next();
                CoreOp.VarOp varOp = (CoreOp.VarOp) it.next();

                bd.context().mapBlock(resourceBlock, bd);
                bd.context().mapValue(varOp.result(), liftedParameters.get(i));

                List<Op> initializeResources = resourceBlock.children().subList(0, resourceBlock.children().size() - 2);
                for (Op initOp : initializeResources) {
                    bd.add(initOp);
                }

                Value initOperand = requireSingle(varOp.operands());
                place(bd, varOp.location(), CoreOp.varStore(liftedParameters.get(i), bd.context().getValue(initOperand)));
            }
            copy(tryOp.body(), bd);
        }

        List<Body.Builder> catchBodies = new ArrayList<>();
        for (Body catchBody : tryOp.catchBodies()) {
            CodeType t = catchBody.bodySignature().parameterTypes().getFirst();
            Body.Builder body = Body.Builder.of(ancestorBody, CoreType.functionType(VOID, t), builder.context());
            Block.Builder bd = body.entryBlock();

            // the first entry loads the exception which is located at the try
            // this corrects that
            Iterator<Op> cbit = catchBody.entryBlock().ops().iterator();
            if (cbit.hasNext()) {
                if (!(cbit.next() instanceof CoreOp.VarOp))
                    throw new IllegalStateException();
                if (cbit.hasNext())
                    bd.add(locationMarker(location(cbit.next())));
            }

            bd.context().mapValues(catchBody.entryBlock().parameters(), bd.parameters());
            copy(catchBody, bd);
            catchBodies.add(body);
        }

        Body.Builder finallyBody = Body.Builder.of(ancestorBody, CoreType.FUNCTION_TYPE_VOID, builder.context());
        {
            Block.Builder bd = finallyBody.entryBlock();

            Op.Location finloc = location(tryOp.finallyBody());
            bd.add(locationMarker(finloc));

            for (Value var : liftedParameters) {
                Value loadedVar = bd.add(CoreOp.varLoad(var));
                Op.Location loc = location(var);
                // technically not correct since this skips the extra exception handling
                bd.add(JavaOp.if_(bd.parentBody()).if_(bd1 -> {
                    Op.Result nil = place(bd1, loc, CoreOp.constant(J_L_OBJECT, null));
                    place(bd1, loc, CoreOp.core_yield(place(bd1, loc, JavaOp.neq(loadedVar, nil))));
                }).then(bd1 -> {
                    place(bd1, loc, JavaOp.invoke(MethodRef.method(AutoCloseable.class, "close", void.class), loadedVar));
                    place(bd1, loc, CoreOp.core_yield());
                }).else_());
            }

            Body fb = tryOp.finallyBody();
            if (fb != null) {
                bd.context().mapBlock(fb.entryBlock(), bd);
                copy(fb, bd);
            }
        }

        JavaOp.TryOp newTry = JavaOp.try_(List.of(), tryBody, catchBodies, finallyBody);
        newTry.setLocation(tryOp.location());
        builder.add(newTry);
        return builder;
    }
}
