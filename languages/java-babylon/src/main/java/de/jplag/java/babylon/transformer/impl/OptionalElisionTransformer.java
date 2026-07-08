package de.jplag.java.babylon.transformer.impl;

import static de.jplag.java.babylon.BabylonUtils.argOperands;
import static de.jplag.java.babylon.BabylonUtils.place;
import static de.jplag.java.babylon.BabylonUtils.requireSingle;
import static jdk.incubator.code.dialect.core.CoreOp.Result;
import static jdk.incubator.code.dialect.core.CoreOp.constant;
import static jdk.incubator.code.dialect.core.CoreOp.core_yield;
import static jdk.incubator.code.dialect.core.CoreOp.var;
import static jdk.incubator.code.dialect.core.CoreOp.varLoad;
import static jdk.incubator.code.dialect.core.CoreOp.varStore;
import static jdk.incubator.code.dialect.core.CoreType.functionType;
import static jdk.incubator.code.dialect.java.JavaOp.conditionalExpression;
import static jdk.incubator.code.dialect.java.JavaOp.invoke;
import static jdk.incubator.code.dialect.java.JavaOp.neq;
import static jdk.incubator.code.dialect.java.JavaType.BOOLEAN;
import static jdk.incubator.code.dialect.java.MethodRef.method;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
import jdk.incubator.code.dialect.core.Inliner;
import jdk.incubator.code.dialect.java.ClassType;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.JavaType;
import jdk.incubator.code.dialect.java.MethodRef;
import jdk.incubator.code.dialect.java.PrimitiveType;

/**
 * {@link SimpleTransformation} that elides usages of {@link Optional}.<br>
 * (Useful in the context of inlining)<br>
 * Deliberately does not handle primitive variants of {@link Optional} as those lack {@link Optional#ofNullable}.
 */
@AutoService(SimpleTransformation.class)
public class OptionalElisionTransformer implements SimpleTransformation {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "optional-elision";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    private static final MethodRef OPTIONAL_OF = method(Optional.class, "of", Optional.class, Object.class);
    private static final MethodRef OPTIONAL_EMPTY = method(Optional.class, "empty", Optional.class);
    private static final MethodRef OPTIONAL_OF_NULLABLE = method(Optional.class, "ofNullable", Optional.class, Object.class);

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (builder.context().getProperty(op) instanceof Replacement replacement) {
            return transformUse(builder, op, (Replacement.NeedsModification) replacement);
        } else if (op instanceof CoreOp.VarOp varOp && elementType(varOp.varValueType()) instanceof JavaType elementType
                && shouldTransform(builder.context(), varOp)) {
            Value init = null;
            if (!varOp.isUninitialized()) {
                init = unwrap(builder, op.location(), builder.context().getValue(varOp.initOperand()), elementType);
            }
            transformVariable(builder, varOp, elementType, init);
            return builder;
        } else if (!(op instanceof JavaOp.InvokeOp invokeOp)) {
            builder.add(op);
            return builder;
        } else if (OPTIONAL_OF.equals(invokeOp.invokeReference()) && invokeOp.resultType() instanceof ClassType ct
                && elementType(ct) instanceof JavaType elementType && shouldTransform(builder.context(), invokeOp)) {
            Value constructed = builder.context().getValue(requireSingle(argOperands(invokeOp)));
            if (constructed.type() instanceof PrimitiveType) {
                constructed = place(builder, op.location(), invoke(method(elementType, "valueOf", elementType, constructed.type()), constructed));
            }
            return markSourceReplaced(builder, invokeOp.result(), new Replacement.Modified(constructed, elementType));
        } else if (OPTIONAL_EMPTY.equals(invokeOp.invokeReference()) && invokeOp.resultType() instanceof ClassType ct
                && elementType(ct) instanceof JavaType elementType && shouldTransform(builder.context(), invokeOp)) {
            Op.Result constructed = place(builder, op.location(), constant(elementType, null));
            return markSourceReplaced(builder, invokeOp.result(), new Replacement.Modified(constructed, elementType));
        } else if (invokeOp.invokeReference().equals(OPTIONAL_OF_NULLABLE) && invokeOp.resultType() instanceof ClassType ct
                && elementType(ct) instanceof JavaType elementType && shouldTransform(builder.context(), invokeOp)) {
            Value constructed = builder.context().getValue(requireSingle(argOperands(invokeOp)));
            return markSourceReplaced(builder, invokeOp.result(), new Replacement.Modified(constructed, elementType));
        } else {
            builder.add(op);
            return builder;
        }
    }

    sealed interface Replacement {
        record NeedsModification() implements Replacement {
        }

        record Modified(Value constructed, JavaType elementType) implements Replacement {
        }

        record ModifiedVar(Value variable, JavaType elementType) implements Replacement {
        }
    }

    private static final JavaType OPTIONAL = JavaType.type(Optional.class);

    private JavaType elementType(CodeType t) {
        if (t instanceof ClassType ct && OPTIONAL.equals(ct.erasure())) {
            if (ct.typeArguments().size() == 1) {
                return requireSingle(ct.typeArguments());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static final Set<MethodRef> OPTIONAL_OR_ELSE_THROW = Set.of(method(Optional.class, "get", Object.class),
            method(Optional.class, "orElseThrow", Object.class));
    private static final MethodRef OPTIONAL_OR_ELSE = method(Optional.class, "orElse", Object.class, Object.class);
    private static final MethodRef OPTIONAL_OR_ELSE_GET = method(Optional.class, "orElseGet", Object.class, Supplier.class);

    private static final Set<MethodRef> OPTIONAL_IS_EMPTY = Set.of(method(Optional.class, "isEmpty", boolean.class),
            method(OptionalInt.class, "isEmpty", boolean.class), method(OptionalLong.class, "isEmpty", boolean.class),
            method(OptionalDouble.class, "isEmpty", boolean.class));
    private static final Set<MethodRef> OPTIONAL_IS_PRESENT = Set.of(method(Optional.class, "isPresent", boolean.class),
            method(OptionalInt.class, "isPresent", boolean.class), method(OptionalLong.class, "isPresent", boolean.class),
            method(OptionalDouble.class, "isPresent", boolean.class));
    private static final MethodRef OPTIONAL_MAP = method(Optional.class, "map", Optional.class, Function.class);
    private static final MethodRef OPTIONAL_FILTER = method(Optional.class, "filter", Optional.class, Predicate.class);

    private boolean shouldTransform(CodeContext context, Op op) {
        if (op instanceof CoreOp.VarOp) {
            for (Op.Result use : op.result().uses()) {
                if (!(use.op() instanceof CoreOp.VarAccessOp)) {
                    return false;
                }
            }
            return true;
        } else {
            for (Op.Result use : op.result().uses()) {
                if (isApplicable(context, use)) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean isApplicable(CodeContext context, Op.Result use) {
        return switch (use.op()) {
            case JavaOp.InvokeOp invokeOp -> OPTIONAL_OR_ELSE_THROW.contains(invokeOp.invokeReference())
                    || OPTIONAL_OR_ELSE.equals(invokeOp.invokeReference()) || OPTIONAL_OR_ELSE_GET.equals(invokeOp.invokeReference())
                    || OPTIONAL_IS_EMPTY.contains(invokeOp.invokeReference()) || OPTIONAL_IS_PRESENT.contains(invokeOp.invokeReference())
                    || OPTIONAL_MAP.equals(invokeOp.invokeReference()) || OPTIONAL_FILTER.equals(invokeOp.invokeReference());
            case CoreOp.VarOp varOp -> shouldTransform(context, varOp);
            case CoreOp.VarAccessOp.VarStoreOp varStoreOp when varStoreOp.varOperand() instanceof Op.Result var
                    && context.getProperty(var.op()) instanceof Replacement -> true;
            case CoreOp.VarAccessOp.VarLoadOp _ -> true;
            default -> false;
        };
    }

    private Block.Builder markSourceReplaced(Block.Builder builder, Value source, Replacement.Modified modified) {
        builder.context().putProperty(source, modified);
        for (Op.Result use : source.uses()) {
            builder.context().putProperty(use.op(), new Replacement.NeedsModification());
        }
        return builder;
    }

    private static final MethodRef OBJECTS_REQUIRE_NON_NULL = method(Objects.class, "requireNonNull", Object.class, Object.class);
    private static final MethodRef OBJECTS_REQUIRE_NON_NULL_ELSE = method(Objects.class, "requireNonNullElse", Object.class, Object.class,
            Object.class);
    private static final MethodRef OBJECTS_REQUIRE_NON_NULL_ELSE_GET = method(Objects.class, "requireNonNullElseGet", Object.class, Object.class,
            Supplier.class);

    private Block.Builder transformUse(Block.Builder builder, Op op, Replacement.NeedsModification replacement) {
        assert replacement != null;
        switch (op) {
            case JavaOp.InvokeOp invokeOp when OPTIONAL_OR_ELSE_THROW.contains(invokeOp.invokeReference())
                    && builder.context().getProperty(invokeOp.receiverOperand()) instanceof Replacement.Modified replacedReceiver -> {
                Op.Result result = place(builder, op.location(),
                        invoke(replacedReceiver.elementType(), OBJECTS_REQUIRE_NON_NULL, replacedReceiver.constructed()));
                builder.context().mapValue(op.result(), result);
            }
            case JavaOp.InvokeOp invokeOp when OPTIONAL_OR_ELSE.equals(invokeOp.invokeReference())
                    && builder.context().getProperty(invokeOp.receiverOperand()) instanceof Replacement.Modified replacedReceiver -> {
                List<Value> operands = boxReplacedOperands(builder, op.location(), argOperands(invokeOp));
                Value result;
                if (operands.getLast() instanceof Op.Result result1 && result1.op() instanceof CoreOp.ConstantOp constantOp
                        && constantOp.value() == null) {
                    result = replacedReceiver.constructed();
                } else {
                    result = place(builder, op.location(),
                            invoke(replacedReceiver.elementType(), OBJECTS_REQUIRE_NON_NULL_ELSE, prepend(replacedReceiver.constructed(), operands)));
                }
                builder.context().mapValue(op.result(), result);
            }
            case JavaOp.InvokeOp invokeOp when OPTIONAL_OR_ELSE_GET.equals(invokeOp.invokeReference())
                    && builder.context().getProperty(invokeOp.receiverOperand()) instanceof Replacement.Modified replacedReceiver -> {
                List<Value> operands = boxReplacedOperands(builder, op.location(), argOperands(invokeOp));
                Op.Result result = place(builder, op.location(),
                        invoke(replacedReceiver.elementType(), OBJECTS_REQUIRE_NON_NULL_ELSE_GET, prepend(replacedReceiver.constructed(), operands)));
                builder.context().mapValue(op.result(), result);
            }
            case JavaOp.InvokeOp invokeOp when OPTIONAL_IS_EMPTY.contains(invokeOp.invokeReference())
                    && builder.context().getProperty(invokeOp.receiverOperand()) instanceof Replacement.Modified replacedReceiver -> {
                Op.Result nil = place(builder, op.location(), constant(replacedReceiver.elementType(), null));
                builder.context().mapValue(op.result(), place(builder, op.location(), JavaOp.eq(replacedReceiver.constructed(), nil)));
            }
            case JavaOp.InvokeOp invokeOp when OPTIONAL_IS_PRESENT.contains(invokeOp.invokeReference())
                    && builder.context().getProperty(invokeOp.receiverOperand()) instanceof Replacement.Modified replacedReceiver -> {
                Op.Result nil = place(builder, op.location(), constant(replacedReceiver.elementType(), null));
                builder.context().mapValue(op.result(), place(builder, op.location(), JavaOp.neq(replacedReceiver.constructed(), nil)));
            }
            case JavaOp.InvokeOp invokeOp when OPTIONAL_MAP.equals(invokeOp.invokeReference())
                    && builder.context().getProperty(invokeOp.receiverOperand()) instanceof Replacement.Modified replacedReceiver
                    && builder.context().getValue(invokeOp.operands().getLast()) instanceof Op.Result mapperArg
                    && mapperArg.op() instanceof JavaOp.LambdaOp lambdaOp && invokeOp.resultType() instanceof ClassType ct
                    && ct.typeArguments().size() == 1 -> {
                Op.Result var = place(builder, op.location(), var(null, replacedReceiver.elementType(), replacedReceiver.constructed()));
                JavaType resultType = requireSingle(ct.typeArguments());

                Body.Builder predicateBody = Body.Builder.of(builder.parentBody(), functionType(BOOLEAN), builder.context());
                Result predicateLoaded = place(predicateBody.entryBlock(), op.location(), varLoad(var));
                Result predicateNull = place(predicateBody.entryBlock(), op.location(), constant(resultType, null));
                Result predicateCompare = place(predicateBody.entryBlock(), op.location(), neq(predicateLoaded, predicateNull));
                place(predicateBody.entryBlock(), op.location(), core_yield(predicateCompare));

                Body.Builder trueBody = Body.Builder.of(builder.parentBody(), functionType(resultType), builder.context());
                Op.Result trueLoaded = place(trueBody.entryBlock(), op.location(), varLoad(var));
                Inliner.inline(trueBody.entryBlock(), lambdaOp, List.of(trueLoaded), (b, value) -> place(b, op.location(), CoreOp.core_yield(value)));

                Body.Builder falseBody = Body.Builder.of(builder.parentBody(), functionType(resultType), builder.context());
                place(falseBody.entryBlock(), op.location(), core_yield(place(falseBody.entryBlock(), op.location(), constant(resultType, null))));

                Op.Result result = place(builder, op.location(), conditionalExpression(resultType, List.of(predicateBody, trueBody, falseBody)));
                markSourceReplaced(builder, invokeOp.result(), new Replacement.Modified(result, resultType));
            }
            case JavaOp.InvokeOp invokeOp when OPTIONAL_FILTER.equals(invokeOp.invokeReference())
                    && builder.context().getProperty(invokeOp.receiverOperand()) instanceof Replacement.Modified replacedReceiver
                    && builder.context().getValue(invokeOp.operands().getLast()) instanceof Op.Result predicateArg
                    && predicateArg.op() instanceof JavaOp.LambdaOp lambdaOp -> {
                Op.Result var = place(builder, op.location(), var(null, replacedReceiver.elementType(), replacedReceiver.constructed()));

                Body.Builder predicateBody = Body.Builder.of(builder.parentBody(), functionType(BOOLEAN), builder.context());
                Result predicateResult = place(predicateBody.entryBlock(), op.location(), JavaOp.conditionalAnd(predicateBody, b -> {
                    Result predicateLoaded = place(b, op.location(), varLoad(var));
                    Result predicateNull = place(b, op.location(), constant(replacedReceiver.elementType(), null));
                    Result predicateCompare = place(b, op.location(), neq(predicateLoaded, predicateNull));
                    place(b, op.location(), core_yield(predicateCompare));
                }, b -> {
                    Result operand = place(predicateBody.entryBlock(), op.location(), varLoad(var));
                    Inliner.inline(b, lambdaOp, List.of(operand), (rb, value) -> place(rb, op.location(), CoreOp.core_yield(value)));
                }).build());
                place(predicateBody.entryBlock(), op.location(), core_yield(predicateResult));

                Body.Builder trueBody = Body.Builder.of(builder.parentBody(), functionType(replacedReceiver.elementType()), builder.context());
                place(trueBody.entryBlock(), op.location(), core_yield(place(trueBody.entryBlock(), op.location(), varLoad(var))));

                Body.Builder falseBody = Body.Builder.of(builder.parentBody(), functionType(replacedReceiver.elementType()), builder.context());
                place(falseBody.entryBlock(), op.location(),
                        core_yield(place(falseBody.entryBlock(), op.location(), constant(replacedReceiver.elementType(), null))));

                Op.Result filterResult = place(builder, op.location(),
                        conditionalExpression(replacedReceiver.elementType(), List.of(predicateBody, trueBody, falseBody)));
                markSourceReplaced(builder, invokeOp.result(), new Replacement.Modified(filterResult, replacedReceiver.elementType()));
            }
            case CoreOp.VarOp varOp when elementType(varOp.varValueType()) instanceof JavaType elementType
                    && shouldTransform(builder.context(), varOp) -> {
                transformVariable(builder, varOp, elementType,
                        Objects.requireNonNull((Replacement.Modified) builder.context().getProperty(varOp.initOperand())).constructed());
            }
            case CoreOp.VarAccessOp.VarStoreOp varStoreOp when varStoreOp.varOperand() instanceof Op.Result variableOperand
                    && builder.context().getProperty(variableOperand) instanceof Replacement.ModifiedVar(var variable, var elementType) -> {
                if (builder.context().getProperty(varStoreOp.storeOperand()) instanceof Replacement.Modified modified) {
                    assert elementType.equals(elementType(varStoreOp.storeOperand().type()));
                    place(builder, op.location(), varStore(variable, modified.constructed()));
                } else {
                    Value unwrapped = unwrap(builder, op.location(), builder.context().getValue(varStoreOp.storeOperand()), elementType);
                    place(builder, op.location(), varStore(variable, unwrapped));
                }
            }
            case CoreOp.VarAccessOp.VarLoadOp varLoadOp when varLoadOp.varOperand() instanceof Op.Result variableOperand
                    && builder.context().getProperty(variableOperand) instanceof Replacement.ModifiedVar(var variable, var elementType) -> {
                Value loaded = place(builder, op.location(), varLoad(variable));
                if (shouldTransform(builder.context(), varLoadOp)) {
                    markSourceReplaced(builder, varLoadOp.result(), new Replacement.Modified(loaded, elementType));
                } else {
                    loaded = wrap(builder, op.location(), new Replacement.Modified(loaded, elementType));
                    builder.context().mapValue(varLoadOp.result(), loaded);
                }
            }
            default -> {
                boxReplacedOperands(builder, op.location(), op.operands());
                builder.add(op);
            }
        }
        return builder;
    }

    private void transformVariable(Block.Builder builder, CoreOp.VarOp varOp, JavaType elementType, @Nullable Value transformedInit) {
        Op.Result variable = place(builder, varOp.location(), var(varOp.varName(), elementType));
        builder.context().putProperty(varOp.result(), new Replacement.ModifiedVar(variable, elementType));
        for (Op.Result use : varOp.result().uses()) {
            builder.context().putProperty(use.op(), new Replacement.NeedsModification());
        }
    }

    private List<Value> boxReplacedOperands(Block.Builder builder, Op.Location location, Iterable<? extends Value> operands) {
        List<Value> results = new ArrayList<>();
        for (Value operand : operands) {
            if (operand instanceof Op.Result result && builder.context().getProperty(result) instanceof Replacement.Modified modified) {
                Value wrapped = wrap(builder, location, modified);
                builder.context().mapValue(operand, wrapped);
                results.add(wrapped);
            } else {
                results.add(builder.context().getValue(operand));
            }
        }
        return results;
    }

    private Value wrap(Block.Builder builder, Op.Location location, Replacement.Modified value) {
        return place(builder, location, invoke(OPTIONAL_OF_NULLABLE, value.constructed()));
    }

    private Value unwrap(Block.Builder builder, Op.Location location, Value wrapped, CodeType elementType) {
        Op.Result initNull = place(builder, location, constant(elementType, null));
        return place(builder, location, invoke(elementType, OPTIONAL_OR_ELSE, builder.context().getValue(wrapped), initNull));
    }

    private <T> List<T> prepend(T previous, List<T> after) {
        return Stream.concat(Stream.of(previous), after.stream()).toList();
    }
}
