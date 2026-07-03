package de.jplag.java.babylon.transformer.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import de.jplag.java.babylon.BabylonDSL;
import de.jplag.java.babylon.transformer.SimpleTransformation;

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
import jdk.incubator.code.dialect.java.ClassType;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.JavaType;
import jdk.incubator.code.dialect.java.MethodRef;

/**
 * {@link SimpleTransformation} that fuses simple stream operations into loops.
 */
@AutoService(SimpleTransformation.class)
public class StreamFuseTransformer implements SimpleTransformation, BabylonDSL {
    /**
     * Identifier of this transformer.
     */
    public static final String IDENTIFIER = "stream-fuse";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    private static final Set<MethodRef> COLLECTION_STREAM = List.of(Iterable.class, Collection.class, List.class, Set.class).stream()
            .map(receiver -> MethodRef.method(receiver, "stream", Stream.class)).collect(Collectors.toSet());
    private static final MethodRef ARRAY_STREAM = MethodRef.method(Arrays.class, "stream", Stream.class);
    private static final MethodRef ARRAY_STREAM_I = MethodRef.method(Arrays.class, "stream", IntStream.class);
    private static final MethodRef ARRAY_STREAM_L = MethodRef.method(Arrays.class, "stream", LongStream.class);
    private static final MethodRef ARRAY_STREAM_D = MethodRef.method(Arrays.class, "stream", DoubleStream.class);
    private static final MethodRef STRING_STREAM = MethodRef.method(String.class, "chars", IntStream.class);

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
            return handle(builder, new Step.Begin.Collection(invokeOp, ct.typeArguments().getFirst()));
        } else if (invokeOp.invokeReference().equals(ARRAY_STREAM) && invokeOp.resultType() instanceof ClassType ct
                && ct.typeArguments().size() == 1) {
            return handle(builder, new Step.Begin.Array(invokeOp, ct.typeArguments().getFirst()));
        } else if (invokeOp.invokeReference().equals(ARRAY_STREAM_I)) {
            return handle(builder, new Step.Begin.Array(invokeOp, JavaType.INT));
        } else if (invokeOp.invokeReference().equals(ARRAY_STREAM_L)) {
            return handle(builder, new Step.Begin.Array(invokeOp, JavaType.LONG));
        } else if (invokeOp.invokeReference().equals(ARRAY_STREAM_D)) {
            return handle(builder, new Step.Begin.Array(invokeOp, JavaType.DOUBLE));
        } else if (invokeOp.invokeReference().equals(STRING_STREAM)) {
            return handle(builder, new Step.Begin.Array(invokeOp, JavaType.INT));
        } else {
            builder.add(op);
            return builder;
        }
    }

    private Block.Builder handle(Block.Builder builder, Step.Begin begin) {
        if (!handle(builder, (Step) begin))
            builder.add(begin.source());
        return builder;
    }

    private static final MethodRef STREAM_MAP = MethodRef.method(Stream.class, "map", Stream.class, Function.class);
    private static final MethodRef STREAM_FILTER = MethodRef.method(Stream.class, "filter", Stream.class, Predicate.class);
    private static final MethodRef STREAM_TO_LIST = MethodRef.method(Stream.class, "toList", List.class);

    private boolean handle(Block.Builder builder, Step pipeline) {
        Op.Result beginResult = pipeline.source().result();
        if (beginResult.uses().size() != 1 || !(requireSingle(beginResult.uses()).op() instanceof JavaOp.InvokeOp invokeOp)) {
            return false;
        }
        if (invokeOp.invokeReference().equals(STREAM_FILTER) && argOperands(invokeOp).size() == 1
                && requireSingle(argOperands(invokeOp)) instanceof Op.Result predicate && predicate.op() instanceof JavaOp.LambdaOp lambda) {
            if (handle(builder, new Step.Intermediate.Filter(invokeOp, pipeline, lambda))) {
                builder.context().putProperty(invokeOp, IDENTIFIER);
                return true;
            } else {
                return false;
            }
        } else if (invokeOp.invokeReference().equals(STREAM_MAP) && argOperands(invokeOp).size() == 1
                && requireSingle(argOperands(invokeOp)) instanceof Op.Result predicate && predicate.op() instanceof JavaOp.LambdaOp lambda) {
            if (handle(builder, new Step.Intermediate.Map(invokeOp, pipeline, lambda, lambda.body().yieldType()))) {
                builder.context().putProperty(invokeOp, IDENTIFIER);
                return true;
            } else {
                return false;
            }
        } else if (invokeOp.invokeReference().equals(STREAM_TO_LIST) && invokeOp.resultType() instanceof ClassType ct
                && ct.typeArguments().size() == 1) {
            builder.context().putProperty(invokeOp, new Collect.ToList(invokeOp, pipeline, requireSingle(ct.typeArguments())));
            return true;
        } else {
            return false;
        }
    }

    private static final ClassType LIST = ClassType.J_U_LIST;
    private static final MethodRef LIST_NEW = MethodRef.constructor(ArrayList.class);
    private static final MethodRef LIST_ADD = MethodRef.method(List.class, "add", boolean.class, Object.class);

    private Block.Builder addAll(Block.Builder builder, Collect pipeline) {
        switch (pipeline) {
            case Collect.ToList toList -> {
                Value newHolder = place(builder, pipeline.source().location(), JavaOp.new_(LIST_NEW));
                Value holderVariable = place(builder, pipeline.source().location(),
                        CoreOp.var("streamResult", JavaType.parameterized(LIST, toList.elementType()), newHolder));
                builder = addAll(builder, builder.context(), pipeline.from(), (value, inner) -> {
                    place(inner, pipeline.source().location(), JavaOp.invoke(LIST_ADD, holderVariable, value.apply(inner)));
                });
                Value holder = place(builder, pipeline.source().location(), CoreOp.varLoad(holderVariable));
                builder.context().mapValue(pipeline.source().result(), holder);
            }
        }
        return builder;
    }

    private static final MethodRef STRING_TO_CHAR_ARRAY = MethodRef.method(String.class, "toCharArray", char[].class);

    private Block.Builder addAll(Block.Builder builder, CodeContext context, Step pipeline,
            BiConsumer<Function<Block.Builder, Value>, Block.Builder> inner) {
        Op.Location location = pipeline.source().location();
        return switch (pipeline) {
            case Step.Begin begin -> {
                Body.Builder exprBody = Body.Builder.of(builder.parentBody(), CoreType.functionType(begin.collectionType()), context);
                switch (begin) {
                    case Step.Begin.Array _,Step.Begin.Collection _ -> exprBody.entryBlock().add(CoreOp.core_yield(context.getValue(begin.from())));
                    case Step.Begin.String string -> {
                        Op.Result charArray = exprBody.entryBlock().add(JavaOp.invoke(STRING_TO_CHAR_ARRAY, context.getValue(string.from())));
                        exprBody.entryBlock().add(CoreOp.core_yield(charArray));
                    }
                }
                Body.Builder initBody = Body.Builder.of(builder.parentBody(),
                        CoreType.functionType(CoreType.varType(begin.elementType()), begin.elementType()), context);
                Op.Result initVariable = initBody.entryBlock()
                        .add(CoreOp.var("s", begin.elementType(), initBody.entryBlock().parameters().getFirst()));
                initBody.entryBlock().add(CoreOp.core_yield(initVariable));
                Body.Builder loopBody = Body.Builder.of(builder.parentBody(),
                        CoreType.functionType(JavaType.VOID, CoreType.varType(begin.elementType())), context);
                inner.accept(b -> place(b, location, CoreOp.varLoad(loopBody.entryBlock().parameters().getFirst())), loopBody.entryBlock());
                place(loopBody.entryBlock(), location, CoreOp.core_yield());
                place(builder, location, JavaOp.enhancedFor(exprBody, initBody, loopBody));
                yield builder;
            }
            case Step.Intermediate intermediate -> addAll(builder, context, intermediate.from(), (value, b) -> {
                switch (intermediate) {
                    case Step.Intermediate.Filter filter -> {
                        Value predicateVariable = place(b, location, CoreOp.var(JavaType.BOOLEAN));
                        b.transformBody(filter.predicate().body(), List.of(value.apply(b)), context, new ReturnAssignTransformer(predicateVariable));
                        b.add(JavaOp.if_(b.parentBody()).if_(b2 -> {
                            Value predicateValue = place(b2, location, CoreOp.varLoad(predicateVariable));
                            place(b2, location, CoreOp.core_yield(predicateValue));
                        }).then(b2 -> {
                            inner.accept(value, b2);
                            place(b2, location, CoreOp.core_yield());
                        }).else_());
                    }
                    case Step.Intermediate.Map map -> {
                        Value mappedVariable = place(b, location, CoreOp.var(map.elementType()));
                        b.transformBody(map.mapping().body(), List.of(value.apply(b)), context, new ReturnAssignTransformer(mappedVariable));
                        inner.accept(b2 -> place(b2, location, CoreOp.varLoad(mappedVariable)), b);
                    }
                }
            });
        };
    }

    private record ReturnAssignTransformer(Value variable) implements CodeTransformer, BabylonDSL {
        @Override
        public Block.Builder acceptOp(Block.Builder builder, Op op) {
            switch (op) {
                case CoreOp.ReturnOp returnOp -> {
                    Value result = builder.context().getValue(returnOp.returnValue());
                    place(builder, returnOp.location(), CoreOp.varStore(variable, result));
                }
                case JavaOp.LambdaOp _ -> placeExact(builder, null, op);
                default -> builder.add(op);
            }
            return builder;
        }
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
                    return JavaType.parameterized(JavaType.type(java.util.Collection.class), elementType);
                }
            }

            record Array(JavaOp.InvokeOp source, JavaType elementType) implements Begin {
                @Override
                public Value from() {
                    return source.argOperands().getFirst();
                }

                @Override
                public JavaType collectionType() {
                    return JavaType.array(elementType);
                }
            }

            record String(JavaOp.InvokeOp source) implements Begin {
                @Override
                public Value from() {
                    return source.argOperands().getFirst();
                }

                @Override
                public JavaType elementType() {
                    return JavaType.INT;
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
    }
}
