package de.jplag.java.babylon.transformer.impl;

import java.util.Iterator;
import java.util.List;

import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.transformer.SimpleTransformation;
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
import jdk.incubator.code.dialect.core.TupleType;
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
    @Override
    public String getIdentifier() {
        return "enhanced-for-desugar";
    }

    private sealed interface IterableDescriptor {
        CodeType stateType();

        CodeType[] spreadStateType();

        record Array(CodeType stateType, CodeType[] spreadStateType) implements IterableDescriptor {
        }

        record Iterable(CodeType stateType, CodeType[] spreadStateType, ClassType iteratorType) implements IterableDescriptor {
        }
    }

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (!(op instanceof JavaOp.EnhancedForOp forOp)) {
            builder.add(op);
            return builder;
        }

        JavaType elementType = (JavaType) forOp.initBody().entryBlock().parameters().getFirst().type();

        IterableDescriptor descriptor;
        if (forOp.exprBody().bodySignature().returnType() instanceof ArrayType at) {
            CodeType[] spreadStateType = new CodeType[] {CoreType.varType(at), CoreType.varType(JavaType.INT)};
            TupleType stateType = CoreType.tupleType(spreadStateType);
            descriptor = new IterableDescriptor.Array(stateType, spreadStateType);
        } else {
            ClassType iteratorType = JavaType.parameterized(JavaType.type(Iterator.class), elementType);
            VarType stateType = CoreType.varType(iteratorType);
            CodeType[] spreadStateType = new CodeType[] {stateType};
            descriptor = new IterableDescriptor.Iterable(stateType, spreadStateType, iteratorType);
        }

        Body.Builder init = buildInit(descriptor, builder.parentBody(), builder.context(), forOp.exprBody());
        Body.Builder cond = buildCond(descriptor, builder.parentBody(), builder.context(), forOp.location());
        Body.Builder update = buildUpdate(descriptor, builder.parentBody(), builder.context(), forOp.location());
        Body.Builder loop = buildLoop(descriptor, builder.parentBody(), builder.context(), forOp.location(), forOp.initBody(), forOp.loopBody());

        place(builder, forOp.location(), JavaOp.for_(init, cond, update, loop));

        return builder;
    }

    private static final MethodRef ITERATOR_CREATE = MethodRef.method(Iterable.class, "iterator", Iterator.class);

    private Body.Builder buildInit(IterableDescriptor descriptor, Body.Builder connectedAncestorBody, CodeContext cc, Body exprBody) {
        Body.Builder init = Body.Builder.of(connectedAncestorBody, CoreType.functionType(descriptor.stateType()), cc);
        init.entryBlock().transformBody(exprBody, List.of(), (YieldTransformer) (block, yield) -> {
            Value result = block.context().getValue(yield.yieldValue());
            Value state = switch (descriptor) {
                case IterableDescriptor.Array _ -> {
                    Value arrayVar = place(block, yield.location(), CoreOp.var(result));
                    Value index = place(block, yield.location(), CoreOp.constant(JavaType.INT, 0));
                    Value indexVar = place(block, yield.location(), CoreOp.var(index));
                    yield place(block, yield.location(), CoreOp.tuple(arrayVar, indexVar));
                }
                case IterableDescriptor.Iterable iterable -> {
                    Value iterator = place(block, yield.location(), JavaOp.invoke(iterable.iteratorType(), ITERATOR_CREATE, result));
                    yield place(block, yield.location(), CoreOp.var(iterator));
                }
            };
            place(block, yield.location(), CoreOp.core_yield(state));
            return block;
        });
        return init;
    }

    private static final MethodRef ITERATOR_HAS_NEXT = MethodRef.method(Iterator.class, "hasNext", boolean.class);

    private Body.Builder buildCond(IterableDescriptor descriptor, Body.Builder connectedAncestorBody, CodeContext cc, Op.Location location) {
        Body.Builder cond = Body.Builder.of(connectedAncestorBody, CoreType.functionType(JavaType.BOOLEAN, descriptor.spreadStateType()), cc);
        List<Block.Parameter> state = cond.entryBlock().parameters();
        Op.Result result = switch (descriptor) {
            case IterableDescriptor.Array _ -> {
                Value arrayVar = state.get(0);
                Value indexVar = state.get(1);
                Value array = place(cond.entryBlock(), location, CoreOp.varLoad(arrayVar));
                Value index = place(cond.entryBlock(), location, CoreOp.varLoad(indexVar));
                Value arrayLength = place(cond.entryBlock(), location, JavaOp.arrayLength(array));
                yield place(cond.entryBlock(), location, JavaOp.lt(index, arrayLength));
            }
            case IterableDescriptor.Iterable _ -> {
                Value iteratorVar = state.getFirst();
                Value iterator = place(cond.entryBlock(), location, CoreOp.varLoad(iteratorVar));
                yield place(cond.entryBlock(), location, JavaOp.invoke(ITERATOR_HAS_NEXT, iterator));
            }
        };
        place(cond.entryBlock(), location, CoreOp.core_yield(result));
        return cond;
    }

    private Body.Builder buildUpdate(IterableDescriptor descriptor, Body.Builder connectedAncestorBody, CodeContext cc, Op.Location location) {
        Body.Builder update = Body.Builder.of(connectedAncestorBody, CoreType.functionType(JavaType.VOID, descriptor.spreadStateType()), cc);
        List<Block.Parameter> state = update.entryBlock().parameters();
        switch (descriptor) {
            case IterableDescriptor.Array _ -> {
                Value arrayVar = state.get(0);
                Value indexVar = state.get(1);
                Value index = place(update.entryBlock(), location, CoreOp.varLoad(indexVar));
                Value one = place(update.entryBlock(), location, CoreOp.constant(JavaType.INT, 1));
                Value incremented = place(update.entryBlock(), location, JavaOp.add(index, one));
                place(update.entryBlock(), location, CoreOp.varStore(indexVar, incremented));
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
        List<Block.Parameter> state = loop.entryBlock().parameters();
        Value loopValue;
        switch (descriptor) {
            case IterableDescriptor.Array _ -> {
                Value arrayVar = state.get(0);
                Value indexVar = state.get(1);
                Value array = place(loop.entryBlock(), location, CoreOp.varLoad(arrayVar));
                Value index = place(loop.entryBlock(), location, CoreOp.varLoad(indexVar));
                loopValue = place(loop.entryBlock(), location, JavaOp.arrayLoadOp(array, index));
            }
            case IterableDescriptor.Iterable _ -> {
                Value iteratorVar = state.getFirst();
                Value iterator = place(loop.entryBlock(), location, CoreOp.varLoad(iteratorVar));
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
