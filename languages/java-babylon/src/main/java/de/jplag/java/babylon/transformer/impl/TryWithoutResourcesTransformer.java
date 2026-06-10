package de.jplag.java.babylon.transformer.impl;

import com.google.auto.service.AutoService;
import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.transformer.SimpleTransformation;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.Op;
import jdk.incubator.code.Value;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.core.CoreType;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.MethodRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jdk.incubator.code.dialect.java.JavaType.J_L_OBJECT;
import static jdk.incubator.code.dialect.java.JavaType.VOID;

/**
 * {@link SimpleTransformation} that converts try-with-resources into regular try-finally statements.
 */
@AutoService(SimpleTransformation.class)
public class TryWithoutResourcesTransformer implements SimpleTransformation, BabylonDSL {
    @Override
    public String getIdentifier() {
        return "tryWithoutResources";
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (Objects.requireNonNull(op) instanceof JavaOp.TryOp tryOp) {
            var resources = tryOp.resourcesBody();
            if (resources == null) {
                builder.op(op);
                return builder;
            }
            var resourceBlock = requireSingle(resources.blocks());

            var it = resourceBlock.children().reversed().iterator();
            CoreOp.YieldOp resourceYield = (CoreOp.YieldOp) it.next();
            CoreOp.TupleOp tupleOp = (CoreOp.TupleOp) it.next();
            var initializeResources = resourceBlock.children().subList(0, resourceBlock.children().size() - 2);

            List<Value> liftedParameters = new ArrayList<>();
            for (Value operand : tupleOp.operands()) {
                liftedParameters.add(place(builder, location(operand), CoreOp.var(operand.type())));
            }

            var ancestorBody = builder.parentBody();

            Body.Builder tryBody = Body.Builder.of(ancestorBody, CoreType.FUNCTION_TYPE_VOID, builder.context());
            {
                var bd = tryBody.entryBlock();
                bd.context().mapBlock(resourceBlock, bd);
                bd.context().mapBlock(tryOp.body().entryBlock(), bd);
                bd.context().mapValues(tryOp.body().entryBlock().parameters(), liftedParameters);
                bd.context().mapValues(tupleOp.operands(), liftedParameters);
                for (var initOp : initializeResources) {
                    bd.op(initOp);
                }
                copy(tryOp.body(), bd);
            }

            List<Body.Builder> catchBodies = new ArrayList<>();
            for (Body catchBody : tryOp.catchBodies()) {
                var t = catchBody.bodySignature().parameterTypes().getFirst();
                var body = Body.Builder.of(ancestorBody, CoreType.functionType(VOID, t), builder.context());
                var bd = body.entryBlock();

                // the first entry loads the exception which is located at the try
                // this corrects that
                var cbit = catchBody.entryBlock().ops().iterator();
                if (cbit.hasNext()) {
                    if (!(cbit.next() instanceof CoreOp.VarOp)) throw new IllegalStateException();
                    if (cbit.hasNext()) bd.op(locationMarker(location(cbit.next())));
                }

                bd.context().mapValues(catchBody.entryBlock().parameters(), bd.parameters());
                copy(catchBody, bd);
                catchBodies.add(body);
            }

            Body.Builder finallyBody = Body.Builder.of(ancestorBody, CoreType.FUNCTION_TYPE_VOID, builder.context());
            {
                var bd = finallyBody.entryBlock();

                var finloc = location(tryOp.finallyBody());
                bd.op(locationMarker(finloc));

                for (var var : liftedParameters) {
                    var loc = location(var);
                    // technically not correct since this skips the extra exception handling
                    bd.op(JavaOp.if_(bd.parentBody()).if_(bd1 -> {
                        var nil = place(bd1, loc, CoreOp.constant(J_L_OBJECT, null));
                        place(bd1, loc, CoreOp.core_yield(place(bd1, loc, JavaOp.neq(var, nil))));
                    }).then(bd1 -> {
                        place(bd1, loc, JavaOp.invoke(MethodRef.method(AutoCloseable.class, "close", void.class), var));
                        place(bd1, loc, CoreOp.core_yield());
                    }).else_());
                }

                bd.context().mapValues(finallyBody.entryBlock().parameters(), liftedParameters);
                var fb = tryOp.finallyBody();
                if (fb != null) {
                    bd.context().mapBlock(fb.entryBlock(), bd);
                    copy(fb, bd);
                }
            }

            var newTry = JavaOp.try_(null, tryBody, catchBodies, finallyBody);
            newTry.setLocation(tryOp.location());
            builder.op(newTry);
        } else {
            builder.op(op);
        }
        return builder;
    }
}
