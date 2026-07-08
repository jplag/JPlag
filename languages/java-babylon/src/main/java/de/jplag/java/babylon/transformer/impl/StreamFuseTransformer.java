package de.jplag.java.babylon.transformer.impl;

import static de.jplag.java.babylon.BabylonUtils.argOperands;
import static de.jplag.java.babylon.BabylonUtils.inline;
import static de.jplag.java.babylon.BabylonUtils.place;
import static de.jplag.java.babylon.BabylonUtils.requireSingle;
import static jdk.incubator.code.dialect.core.CoreOp.constant;
import static jdk.incubator.code.dialect.core.CoreOp.core_yield;
import static jdk.incubator.code.dialect.core.CoreOp.var;
import static jdk.incubator.code.dialect.core.CoreOp.varLoad;
import static jdk.incubator.code.dialect.core.CoreOp.varStore;
import static jdk.incubator.code.dialect.core.CoreType.functionType;
import static jdk.incubator.code.dialect.java.JavaOp.add;
import static jdk.incubator.code.dialect.java.JavaOp.arrayLength;
import static jdk.incubator.code.dialect.java.JavaOp.arrayStoreOp;
import static jdk.incubator.code.dialect.java.JavaOp.if_;
import static jdk.incubator.code.dialect.java.JavaOp.invoke;
import static jdk.incubator.code.dialect.java.JavaOp.newArray;
import static jdk.incubator.code.dialect.java.JavaOp.new_;
import static jdk.incubator.code.dialect.java.JavaOp.sub;
import static jdk.incubator.code.dialect.java.JavaType.BOOLEAN;
import static jdk.incubator.code.dialect.java.JavaType.INT;
import static jdk.incubator.code.dialect.java.JavaType.LONG;
import static jdk.incubator.code.dialect.java.JavaType.array;
import static jdk.incubator.code.dialect.java.JavaType.parameterized;
import static jdk.incubator.code.dialect.java.JavaType.type;
import static jdk.incubator.code.dialect.java.MethodRef.method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

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
import jdk.incubator.code.dialect.java.ArrayType;
import jdk.incubator.code.dialect.java.ClassType;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.JavaType;
import jdk.incubator.code.dialect.java.MethodRef;

/**
 * {@link SimpleTransformation} that fuses simple stream operations into loops.
 */
@AutoService(SimpleTransformation.class)
public class StreamFuseTransformer implements SimpleTransformation {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "stream-fuse";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    private static final Set<MethodRef> COLLECTION_STREAM = Stream.of(Iterable.class, Collection.class, List.class, Set.class)
            .map(receiver -> method(receiver, "stream", Stream.class)).collect(Collectors.toSet());
    private static final MethodRef ARRAY_STREAM = method(Arrays.class, "stream", Stream.class, Object[].class);
    private static final MethodRef ARRAY_STREAM_I = method(Arrays.class, "stream", IntStream.class, int[].class);
    private static final MethodRef ARRAY_STREAM_L = method(Arrays.class, "stream", LongStream.class, long[].class);
    private static final MethodRef ARRAY_STREAM_D = method(Arrays.class, "stream", DoubleStream.class, double[].class);
    private static final MethodRef STRING_STREAM = method(String.class, "chars", IntStream.class);

    @Override
    public Block.Builder acceptOp(Block.Builder builder, Op op) {
        if (!(op instanceof JavaOp.InvokeOp invokeOp)) {
            builder.add(op);
            return builder;
        } else if (builder.context().getProperty(op) == IDENTIFIER) {
            return builder;
        } else if (builder.context().getProperty(op) instanceof Collect collect) {
            return addAll(builder, collect);
        } else if (COLLECTION_STREAM.contains(invokeOp.invokeReference()) && invokeOp.resultType() instanceof ClassType ct
                && ct.typeArguments().size() == 1) {
            return markBeginningIfPipeline(builder, new Step.Begin.Collection(invokeOp, ct.typeArguments().getFirst()));
        } else if (invokeOp.invokeReference().equals(ARRAY_STREAM) && invokeOp.resultType() instanceof ClassType ct
                && ct.typeArguments().size() == 1) {
            return markBeginningIfPipeline(builder, new Step.Begin.Array(invokeOp, ct.typeArguments().getFirst()));
        } else if (invokeOp.invokeReference().equals(ARRAY_STREAM_I)) {
            return markBeginningIfPipeline(builder, new Step.Begin.Array(invokeOp, INT));
        } else if (invokeOp.invokeReference().equals(ARRAY_STREAM_L)) {
            return markBeginningIfPipeline(builder, new Step.Begin.Array(invokeOp, LONG));
        } else if (invokeOp.invokeReference().equals(ARRAY_STREAM_D)) {
            return markBeginningIfPipeline(builder, new Step.Begin.Array(invokeOp, JavaType.DOUBLE));
        } else if (invokeOp.invokeReference().equals(STRING_STREAM)) {
            return markBeginningIfPipeline(builder, new Step.Begin.String(invokeOp));
        } else {
            builder.add(op);
            return builder;
        }
    }

    /**
     * Marks {@code begin} as the beginning of a stream pipeline if the result is used in a complete, supported, stream
     * pipeline definition.
     * @param builder the parent block builder
     * @param begin the potential pipeline beginning
     * @return the block builder to continue with
     */
    private Block.Builder markBeginningIfPipeline(Block.Builder builder, Step.Begin begin) {
        if (!markStepIfPipeline(builder, begin))
            builder.add(begin.source());
        return builder;
    }

    private static final Set<MethodRef> STREAM_MAP = Set.of(method(Stream.class, "map", Stream.class, Function.class),
            method(Stream.class, "mapToInt", IntStream.class, ToIntFunction.class),
            method(Stream.class, "mapToLong", LongStream.class, ToLongFunction.class),
            method(Stream.class, "mapToDouble", DoubleStream.class, ToDoubleFunction.class),
            method(IntStream.class, "map", IntStream.class, IntUnaryOperator.class),
            method(IntStream.class, "mapToObj", Stream.class, IntFunction.class),
            method(IntStream.class, "mapToLong", LongStream.class, IntToLongFunction.class),
            method(IntStream.class, "mapToDouble", DoubleStream.class, IntToDoubleFunction.class),
            method(LongStream.class, "map", LongStream.class, LongUnaryOperator.class),
            method(LongStream.class, "mapToObj", Stream.class, LongFunction.class),
            method(LongStream.class, "mapToInt", IntStream.class, LongToIntFunction.class),
            method(LongStream.class, "mapToDouble", DoubleStream.class, LongToDoubleFunction.class),
            method(DoubleStream.class, "map", DoubleStream.class, DoubleUnaryOperator.class),
            method(DoubleStream.class, "mapToObj", Stream.class, DoubleFunction.class),
            method(DoubleStream.class, "mapToInt", IntStream.class, DoubleToIntFunction.class),
            method(DoubleStream.class, "mapToLong", LongStream.class, DoubleToLongFunction.class));
    private static final Set<MethodRef> STREAM_FILTER = Set.of(method(Stream.class, "filter", Stream.class, Predicate.class),
            method(IntStream.class, "filter", IntStream.class, IntPredicate.class),
            method(LongStream.class, "filter", LongStream.class, LongPredicate.class),
            method(DoubleStream.class, "filter", DoubleStream.class, DoublePredicate.class));
    private static final MethodRef STREAM_TO_LIST = method(Stream.class, "toList", List.class);
    private static final Set<MethodRef> STREAM_TO_ARRAY = Set.of(method(Stream.class, "toArray", Object[].class),
            method(IntStream.class, "toArray", int[].class), method(LongStream.class, "toArray", long[].class),
            method(DoubleStream.class, "toArray", double[].class));
    private static final Set<MethodRef> STREAM_COUNT = Set.of(method(Stream.class, "count", long.class), method(IntStream.class, "count", long.class),
            method(LongStream.class, "count", long.class), method(DoubleStream.class, "count", long.class));
    private static final Set<MethodRef> STREAM_SUM = Set.of(method(IntStream.class, "sum", int.class), method(LongStream.class, "sum", long.class),
            method(DoubleStream.class, "sum", double.class));

    /**
     * Marks {@code pipeline} as a step of a stream pipeline if the result is used in a complete, supported, stream pipeline
     * definition.<br>
     * Recursively calls this method to validate the next pipeline step.
     * @param builder the parent block builder
     * @param pipeline the potential pipeline step
     * @return true if the pipeline step was marked, false otherwise
     */
    private boolean markStepIfPipeline(Block.Builder builder, Step pipeline) {
        Op.Result beginResult = pipeline.source().result();
        if (beginResult.uses().size() != 1 || !(requireSingle(beginResult.uses()).op() instanceof JavaOp.InvokeOp invokeOp)) {
            return false;
        }
        if (STREAM_FILTER.contains(invokeOp.invokeReference()) && argOperands(invokeOp).size() == 1
                && requireSingle(argOperands(invokeOp)) instanceof Op.Result predicate && predicate.op() instanceof JavaOp.LambdaOp lambda) {
            if (markStepIfPipeline(builder, new Step.Intermediate.Filter(invokeOp, pipeline, lambda))) {
                builder.context().putProperty(invokeOp, IDENTIFIER);
                return true;
            } else {
                return false;
            }
        } else if (STREAM_MAP.contains(invokeOp.invokeReference()) && argOperands(invokeOp).size() == 1
                && requireSingle(argOperands(invokeOp)) instanceof Op.Result predicate && predicate.op() instanceof JavaOp.LambdaOp lambda) {
            if (markStepIfPipeline(builder, new Step.Intermediate.Map(invokeOp, pipeline, lambda, lambda.body().yieldType()))) {
                builder.context().putProperty(invokeOp, IDENTIFIER);
                return true;
            } else {
                return false;
            }
        } else if (invokeOp.invokeReference().equals(STREAM_TO_LIST) && invokeOp.resultType() instanceof ClassType ct
                && ct.typeArguments().size() == 1) {
            builder.context().putProperty(invokeOp, new Collect.ToList(invokeOp, pipeline, requireSingle(ct.typeArguments())));
            return true;
        } else if (STREAM_TO_ARRAY.contains(invokeOp.invokeReference()) && invokeOp.resultType() instanceof ArrayType at) {
            builder.context().putProperty(invokeOp, new Collect.ToArray(invokeOp, pipeline, at.componentType()));
            return true;
        } else if (STREAM_COUNT.contains(invokeOp.invokeReference())) {
            builder.context().putProperty(invokeOp, new Collect.Count(invokeOp, pipeline));
            return true;
        } else if (STREAM_SUM.contains(invokeOp.invokeReference())) {
            builder.context().putProperty(invokeOp, new Collect.Sum(invokeOp, pipeline, invokeOp.resultType()));
            return true;
        } else {
            return false;
        }
    }

    private static final ClassType LIST = ClassType.J_U_LIST;
    private static final MethodRef LIST_NEW = MethodRef.constructor(ArrayList.class);
    private static final MethodRef LIST_ADD = method(List.class, "add", boolean.class, Object.class);

    /**
     * Writes the loop-based reimplementation of the {@code pipeline} to the {@code builder}.<br>
     * This method handles the final collect step and delegates previous steps to
     * {@link #addAll(Block.Builder, CodeContext, Step, BiConsumer)}.
     * @param builder the builder to write the pipeline to
     * @param pipeline the pipeline to write
     * @return the builder to continue with
     */
    private Block.Builder addAll(Block.Builder builder, Collect pipeline) {
        Op.Location location = pipeline.source().location();
        Value resultVariable = switch (pipeline) {
            case Collect.ToList toList -> {
                Value newHolder = place(builder, location, new_(LIST_NEW));
                Value holderVariable = place(builder, location, var(null, parameterized(LIST, toList.elementType()), newHolder));
                builder = addAll(builder, builder.context(), pipeline.from(), (value, inner) -> {
                    place(inner, location, invoke(LIST_ADD, holderVariable, value.apply(inner)));
                });
                yield holderVariable;
            }
            case Collect.ToArray toArray -> {
                // TODO there is almost certainly a more common way to do this (note that it needs to support primitive arrays!)
                JavaType arrayType = array(toArray.elementType());
                Value initialArraySize = place(builder, location, constant(INT, 0));
                Value newHolder = place(builder, location, newArray(arrayType, initialArraySize));
                Value holderVariable = place(builder, location, var(null, arrayType, newHolder));
                builder = addAll(builder, builder.context(), pipeline.from(), (value, inner) -> {
                    Value oldSize = place(inner, location, arrayLength(place(inner, location, varLoad(holderVariable))));
                    Value newSize = place(inner, location, add(oldSize, place(inner, location, constant(INT, 1))));
                    Op.Result modifiedHolder = place(inner, location,
                            invoke(method(type(Arrays.class), "copyOf", arrayType, List.of(arrayType, INT)), holderVariable, newSize));
                    place(inner, location, varStore(holderVariable, modifiedHolder));
                    newSize = place(inner, location, arrayLength(place(inner, location, varLoad(holderVariable))));
                    Value index = place(inner, location, sub(newSize, place(inner, location, constant(INT, 1))));
                    place(inner, location, arrayStoreOp(place(inner, location, varLoad(holderVariable)), index, value.apply(inner)));
                });
                yield holderVariable;
            }
            case Collect.Count _ -> {
                Value countVariable = place(builder, location, var(null, LONG, place(builder, location, constant(LONG, 0))));
                builder = addAll(builder, builder.context(), pipeline.from(), (_, inner) -> {
                    Value newCount = place(inner, location,
                            add(place(inner, location, varLoad(countVariable)), place(inner, location, constant(INT, 1))));
                    place(inner, location, varStore(countVariable, newCount));
                });
                yield countVariable;
            }
            case Collect.Sum sum -> {
                Value sumVariable = place(builder, location, var(null, sum.elementType(), place(builder, location, constant(sum.elementType(), 0))));
                builder = addAll(builder, builder.context(), pipeline.from(), (value, inner) -> {
                    Value newSum = place(inner, location, add(place(inner, location, varLoad(sumVariable)), value.apply(inner)));
                    place(inner, location, varStore(sumVariable, newSum));
                });
                yield sumVariable;
            }
        };
        Value holder = place(builder, location, varLoad(resultVariable));
        builder.context().mapValue(pipeline.source().result(), holder);
        return builder;
    }

    private static final MethodRef STRING_TO_CHAR_ARRAY = method(String.class, "toCharArray", char[].class);

    /**
     * Writes the loop-based reimplementation of the {@code pipeline} to the {@code builder}.<br>
     * Assumes that the collection (and, potentially, other downstream steps) are already handled in {@code inner}.
     * @param builder the builder to write the pipeline to
     * @param context the current context for resolving values
     * @param pipeline the remaining pipeline steps
     * @param inner the downstream of the current pipeline steps, to be inserted into the handling of this step
     * @return the builder to continue with
     */
    private Block.Builder addAll(Block.Builder builder, CodeContext context, Step pipeline,
            BiConsumer<Function<Block.Builder, Value>, Block.Builder> inner) {
        Op.Location location = pipeline.source().location();
        return switch (pipeline) {
            case Step.Begin begin -> {
                Body.Builder exprBody = Body.Builder.of(builder.parentBody(), functionType(begin.collectionType()), context);
                switch (begin) {
                    case Step.Begin.Array _,Step.Begin.Collection _ -> exprBody.entryBlock().add(core_yield(context.getValue(begin.from())));
                    case Step.Begin.String string -> {
                        Op.Result charArray = exprBody.entryBlock().add(invoke(STRING_TO_CHAR_ARRAY, context.getValue(string.from())));
                        exprBody.entryBlock().add(core_yield(charArray));
                    }
                }
                Body.Builder initBody = Body.Builder.of(builder.parentBody(),
                        functionType(CoreType.varType(begin.elementType()), begin.elementType()), context);
                Op.Result initVariable = initBody.entryBlock().add(var(null, begin.elementType(), initBody.entryBlock().parameters().getFirst()));
                initBody.entryBlock().add(core_yield(initVariable));
                Body.Builder loopBody = Body.Builder.of(builder.parentBody(), functionType(JavaType.VOID, CoreType.varType(begin.elementType())),
                        context);
                inner.accept(b -> place(b, location, varLoad(loopBody.entryBlock().parameters().getFirst())), loopBody.entryBlock());
                place(loopBody.entryBlock(), location, core_yield());
                place(builder, location, JavaOp.enhancedFor(exprBody, initBody, loopBody));
                yield builder;
            }
            case Step.Intermediate intermediate -> addAll(builder, context, intermediate.from(), (value, b) -> {
                switch (intermediate) {
                    case Step.Intermediate.Filter filter when !containsStatement(filter.predicate.body()) -> b.add(if_(b.parentBody()).if_(b2 -> {
                        Value predicateVariable = place(b2, location, var(BOOLEAN));
                        inline(b2, location, filter.predicate(), List.of(value.apply(b2)), predicateVariable);
                        Value predicateValue = place(b2, location, varLoad(predicateVariable));
                        place(b2, location, core_yield(predicateValue));
                    }).then(b2 -> {
                        inner.accept(value, b2);
                        place(b2, location, core_yield());
                    }).else_());
                    case Step.Intermediate.Filter filter -> {
                        Value predicateVariable = place(b, location, var(BOOLEAN));
                        inline(b, location, filter.predicate(), List.of(value.apply(b)), predicateVariable);
                        b.add(if_(b.parentBody()).if_(b2 -> {
                            Value predicateValue = place(b2, location, varLoad(predicateVariable));
                            place(b2, location, core_yield(predicateValue));
                        }).then(b2 -> {
                            inner.accept(value, b2);
                            place(b2, location, core_yield());
                        }).else_());
                    }
                    case Step.Intermediate.Map map -> {
                        Value mappedVariable = place(b, location, var(map.elementType()));
                        inline(b, location, map.mapping(), List.of(value.apply(b)), mappedVariable);
                        inner.accept(b2 -> place(b2, location, varLoad(mappedVariable)), b);
                    }
                }
            });
        };
    }

    /**
     * Checks whether a {@link Body} contains any {@link JavaOp.JavaStatement}.<br>
     * Does NOT search bodies of operations.
     * @param body the body to search
     * @return true if a statement was found
     */
    private boolean containsStatement(Body body) {
        for (Block block : body.blocks()) {
            for (Op op : block.ops()) {
                if (op instanceof JavaOp.JavaStatement && !(op instanceof CoreOp.ReturnOp)) {
                    return true;
                }
            }
        }
        return false;
    }

    private sealed interface Step {
        JavaOp.InvokeOp source();

        CodeType elementType();

        default String toText() {
            return getClass().getSimpleName() + "(" + source().toText() + ")";
        }

        sealed interface Begin extends Step {
            Value from();

            JavaType collectionType();

            record Collection(JavaOp.InvokeOp source, JavaType elementType) implements Begin {
                @Override
                public Value from() {
                    return source.receiverOperand();
                }

                @Override
                public JavaType collectionType() {
                    return parameterized(type(java.util.Collection.class), elementType);
                }
            }

            record Array(JavaOp.InvokeOp source, JavaType elementType) implements Begin {
                @Override
                public Value from() {
                    return source.argOperands().getFirst();
                }

                @Override
                public JavaType collectionType() {
                    return array(elementType);
                }
            }

            record String(JavaOp.InvokeOp source) implements Begin {
                @Override
                public Value from() {
                    return source.argOperands().getFirst();
                }

                @Override
                public JavaType elementType() {
                    return INT;
                }

                @Override
                public JavaType collectionType() {
                    return JavaType.CHAR_ARRAY;
                }
            }
        }

        sealed interface Intermediate extends Step {
            Step from();

            @Override
            default String toText() {
                return from().toText() + " -> " + Step.super.toText();
            }

            record Filter(JavaOp.InvokeOp source, Step from, JavaOp.LambdaOp predicate) implements Intermediate {
                @Override
                public CodeType elementType() {
                    return from.elementType();
                }
            }

            record Map(JavaOp.InvokeOp source, Step from, JavaOp.LambdaOp mapping, CodeType elementType) implements Intermediate {
            }
        }
    }

    private sealed interface Collect {
        JavaOp.InvokeOp source();

        Step from();

        default String toText() {
            return from().toText() + " -> " + getClass().getSimpleName() + "(" + source().toText() + ")";
        }

        record ToList(JavaOp.InvokeOp source, Step from, JavaType elementType) implements Collect {
        }

        record ToArray(JavaOp.InvokeOp source, Step from, JavaType elementType) implements Collect {
        }

        record Count(JavaOp.InvokeOp source, Step from) implements Collect {
        }

        record Sum(JavaOp.InvokeOp source, Step from, CodeType elementType) implements Collect {
        }
    }
}
