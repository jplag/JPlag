package de.jplag.java.babylon.transformer.impl;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.transformer.SimpleTransformation;
import de.jplag.java.babylon.transformer.impl.util.YieldAssignTransformer;
import de.jplag.java.babylon.transformer.impl.util.YieldTransformer;

import com.google.auto.service.AutoService;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeContext;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.CodeType;
import jdk.incubator.code.Op;
import jdk.incubator.code.Value;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.core.CoreType;
import jdk.incubator.code.dialect.core.VarType;
import jdk.incubator.code.dialect.java.ArrayType;
import jdk.incubator.code.dialect.java.ClassType;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.JavaType;
import jdk.incubator.code.dialect.java.MethodRef;

/**
 * {@link SimpleTransformation} that desugars enhanced for loops to normal for loops.
 */
@AutoService(SimpleTransformation.class)
public class EnhancedForDesugarTransformer implements SimpleTransformation, BabylonDSL {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "enhanced-for-desugar";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    private sealed interface IterableDescriptor {
        CodeType stateType();

        default CodeType[] spreadStateType() {
            return new CodeType[] {stateType()};
        }

        record Array(CodeType stateType, ArrayType arrayType, Value variable) implements IterableDescriptor {
            public Array(ArrayType arrayType, Value variable) {
                this(CoreType.varType(JavaType.INT), arrayType, variable);
            }
        }

        record Iterable(CodeType stateType, ClassType iteratorType) implements IterableDescriptor {
            public Iterable(JavaType elementType) {
                ClassType iteratorType = JavaType.parameterized(JavaType.type(Iterator.class), elementType);
                VarType stateType = CoreType.varType(iteratorType);
                this(stateType, iteratorType);
            }
        }
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (!(op instanceof JavaOp.EnhancedForOp forOp)) {
            builder.add(op);
            return builder;
        }

        JavaType elementType = (JavaType) forOp.initBody().entryBlock().parameters().getFirst().type();

        IterableDescriptor descriptor = switch (forOp.exprBody().bodySignature().returnType()) {
            case null -> throw new NullPointerException();
            case ArrayType at -> {
                Value variable = sourceVarIfSimple(forOp.exprBody());
                if (variable == null) {
                    variable = place(builder, forOp.location(), CoreOp.var(at));
                    builder.transformBody(forOp.exprBody(), List.of(), new YieldAssignTransformer(variable, false));
                } else {
                    variable = builder.context().getValue(variable);
                }
                yield new IterableDescriptor.Array(at, variable);
            }
            default -> new IterableDescriptor.Iterable(elementType);
        };

        Body.Builder init = buildInit(descriptor, builder.parentBody(), builder.context(), forOp.exprBody());
        Body.Builder cond = buildCond(descriptor, builder.parentBody(), builder.context(), forOp.location());
        Body.Builder update = buildUpdate(descriptor, builder.parentBody(), builder.context(), forOp.location());
        Body.Builder loop = buildLoop(descriptor, builder.parentBody(), builder.context(), forOp.location(), forOp.initBody(), forOp.loopBody());

        place(builder, forOp.location(), JavaOp.for_(init, cond, update, loop));

        return builder;
    }

    private @Nullable Value sourceVarIfSimple(Body exprBody) {
        // Note that this transformation is not correct in the case where the source variable is reassigned while the loop is
        // running.
        // Unfortunately, simply checking whether the variable is reassigned during the body is insufficient,
        // since it could, for example, be packed inside a tuple before the loop, unpacked inside, and only written thereafter.
        // Even worse, simply creating a new variable in every case will almost certainly decrease quality,
        // since that would bloat up the token count for even simple loops.
        // Fortunately, that should not be a problem for our use case.

        if (exprBody.blocks().size() != 1)
            return null;
        Block block = exprBody.entryBlock();
        Iterator<Op> iterator = block.ops().iterator();
        if (!(iterator.next() instanceof CoreOp.VarAccessOp.VarLoadOp varLoadOp))
            return null;
        Value result = varLoadOp.varOperand();
        if (iterator.next() instanceof CoreOp.YieldOp)
            return result;
        else
            return null;
    }

    private static final MethodRef ITERATOR_CREATE = MethodRef.method(Iterable.class, "iterator", Iterator.class);

    private Body.Builder buildInit(IterableDescriptor descriptor, Body.Builder connectedAncestorBody, CodeContext cc, Body exprBody) {
        Body.Builder init = Body.Builder.of(connectedAncestorBody, CoreType.functionType(descriptor.stateType()), cc);
        switch (descriptor) {
            case IterableDescriptor.Array array -> place(init.entryBlock(), location(exprBody), CoreOp.core_yield(array.variable()));
            case IterableDescriptor.Iterable iterable -> init.entryBlock().transformBody(exprBody, List.of(), (YieldTransformer) (block, yield) -> {
                Value result = block.context().getValue(yield.yieldValue());
                Value iterator = place(block, yield.location(), JavaOp.invoke(iterable.iteratorType(), ITERATOR_CREATE, result));
                Value state = place(block, yield.location(), CoreOp.var(iterator));
                place(block, yield.location(), CoreOp.core_yield(state));
                return block;
            });
        }
        return init;
    }

    private static final MethodRef ITERATOR_HAS_NEXT = MethodRef.method(Iterator.class, "hasNext", boolean.class);

    private Body.Builder buildCond(IterableDescriptor descriptor, Body.Builder connectedAncestorBody, CodeContext cc, Op.Location location) {
        Body.Builder cond = Body.Builder.of(connectedAncestorBody, CoreType.functionType(JavaType.BOOLEAN, descriptor.spreadStateType()), cc);
        Value state = requireSingle(cond.entryBlock().parameters());
        Op.Result result = switch (descriptor) {
            case IterableDescriptor.Array arrayDesc -> {
                Value array = place(cond.entryBlock(), location, CoreOp.varLoad(arrayDesc.variable()));
                Value index = place(cond.entryBlock(), location, CoreOp.varLoad(state));
                Value arrayLength = place(cond.entryBlock(), location, JavaOp.arrayLength(array));
                yield place(cond.entryBlock(), location, JavaOp.lt(index, arrayLength));
            }
            case IterableDescriptor.Iterable _ -> {
                Value iterator = place(cond.entryBlock(), location, CoreOp.varLoad(state));
                yield place(cond.entryBlock(), location, JavaOp.invoke(ITERATOR_HAS_NEXT, iterator));
            }
        };
        place(cond.entryBlock(), location, CoreOp.core_yield(result));
        return cond;
    }

    private Body.Builder buildUpdate(IterableDescriptor descriptor, Body.Builder connectedAncestorBody, CodeContext cc, Op.Location location) {
        Body.Builder update = Body.Builder.of(connectedAncestorBody, CoreType.functionType(JavaType.VOID, descriptor.spreadStateType()), cc);
        Value state = requireSingle(update.entryBlock().parameters());
        switch (descriptor) {
            case IterableDescriptor.Array _ -> {
                Value index = place(update.entryBlock(), location, CoreOp.varLoad(state));
                Value one = place(update.entryBlock(), location, CoreOp.constant(JavaType.INT, 1));
                Value incremented = place(update.entryBlock(), location, JavaOp.add(index, one));
                place(update.entryBlock(), location, CoreOp.varStore(state, incremented));
            }
            case IterableDescriptor.Iterable _ -> {
            }
        }
        place(update.entryBlock(), location, CoreOp.core_yield());
        return update;
    }

    private static final MethodRef ITERATOR_NEXT = MethodRef.method(Iterator.class, "next", Object.class);

    private Body.Builder buildLoop(IterableDescriptor descriptor, Body.Builder connectedAncestorBody, CodeContext cc, Op.Location location,
            Body initBody, Body loopBody) {
        Body.Builder loop = Body.Builder.of(connectedAncestorBody, CoreType.functionType(JavaType.VOID, descriptor.spreadStateType()), cc);
        Value state = requireSingle(loop.entryBlock().parameters());
        Value loopValue;
        switch (descriptor) {
            case IterableDescriptor.Array arrayDesc -> {
                Value array = place(loop.entryBlock(), location, CoreOp.varLoad(arrayDesc.variable()));
                Value index = place(loop.entryBlock(), location, CoreOp.varLoad(state));
                loopValue = place(loop.entryBlock(), location, JavaOp.arrayLoadOp(array, index));
            }
            case IterableDescriptor.Iterable _ -> {
                Value iterator = place(loop.entryBlock(), location, CoreOp.varLoad(state));
                loopValue = place(loop.entryBlock(), location, JavaOp.invoke(ITERATOR_NEXT, iterator));
            }
        }
        Value loopVar = loopBody.entryBlock().parameters().getFirst();
        loop.entryBlock().transformBody(initBody, List.of(loopValue), (YieldTransformer) (block, yield) -> {
            loop.entryBlock().context().mapValue(loopVar, block.context().getValue(yield.yieldValue()));
            return block;
        });
        loop.entryBlock().transformBody(loopBody, List.of(loop.entryBlock().context().getValue(loopVar)), CodeTransformer.COPYING_TRANSFORMER);
        return loop;
    }
}
