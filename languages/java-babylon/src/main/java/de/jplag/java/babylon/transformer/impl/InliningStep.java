package de.jplag.java.babylon.transformer.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.java.babylon.extractor.CodeModelExtractor;
import de.jplag.java.babylon.extractor.ExtractionFailedException;
import de.jplag.java.babylon.transformer.Prepass;
import de.jplag.java.babylon.transformer.TransformationStep;
import de.jplag.java.babylon.transformer.impl.util.JavaMethodId;

import com.google.auto.service.AutoService;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreeScanner;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.core.Inliner;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.PrimitiveType;

/**
 * A {@link TransformationStep} that inlines simple methods.
 */
@AutoService(TransformationStep.class)
public class InliningStep implements TransformationStep<InliningStep.Context> {
    private static final Logger logger = LoggerFactory.getLogger(InliningStep.class);

    /**
     * Identifier of this pipeline step.
     */
    public static final String IDENTIFIER = "inline";

    private final long maxComplexity;
    private final boolean dropLocations;

    /**
     * Create a new instance with config options loaded from system properties.
     */
    public InliningStep() {
        this(Long.parseLong(System.getProperty("jplag.java-babylon.inline.max-complexity", "10")),
                Boolean.parseBoolean(System.getProperty("jplag.java-babylon.inline.drop-locations", "true")));
    }

    /**
     * Create a new instance.
     * @param maxComplexity the maximum complexity before methods are no longer inlined
     * @param dropLocations whether locations should be dropped before inlining
     */
    public InliningStep(long maxComplexity, boolean dropLocations) {
        this.maxComplexity = maxComplexity;
        this.dropLocations = dropLocations;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Nullable
    @Override
    public Prepass<Context> beginPrepass(PrepassConstructionContext context) {
        return new FindCandidates(context.codeModelExtractor(), this::heuristic);
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op, Context context) {
        return op.transform(new Apply(context, dropLocations));
    }

    private static class FindCandidates extends TreeScanner<Void, CompilationUnitTree> implements Prepass<Context> {
        private final Map<String, CoreOp.FuncOp> core = new HashMap<>();
        private final Map<JavaMethodId, CoreOp.FuncOp> java = new HashMap<>();
        private final CodeModelExtractor codeModelExtractor;
        private final BiPredicate<CoreOp.FuncOp, JavaMethodId> heuristic;

        public FindCandidates(CodeModelExtractor codeModelExtractor, BiPredicate<CoreOp.FuncOp, JavaMethodId> heuristic) {
            this.codeModelExtractor = codeModelExtractor;
            this.heuristic = heuristic;
        }

        @Override
        public Context finalizeContext() {
            return new Context(Collections.unmodifiableMap(core), Collections.unmodifiableMap(java));
        }

        @Override
        public Void visitMethod(MethodTree node, CompilationUnitTree ast) {
            JavaMethodId id = JavaMethodId.of(node);
            if (id != null) {
                CoreOp.FuncOp op;
                try {
                    op = codeModelExtractor.toOp(node, ast).orElse(null);
                } catch (ExtractionFailedException e) {
                    logger.debug("Could not extract possible inlining candidate", e);
                    return super.visitMethod(node, ast);
                }
                if (op != null && heuristic.test(op, id)) {
                    java.put(id, op);
                }
            }
            return super.visitMethod(node, ast);
        }
    }

    private static class Apply implements CodeTransformer {
        private final Context context;
        private final boolean dropLocations;

        public Apply(Context context, boolean dropLocations) {
            this.context = context;
            this.dropLocations = dropLocations;
        }

        @Override
        public Block.Builder acceptOp(Block.Builder builder, Op op) {
            CoreOp.FuncOp candidate = switch (op) {
                case JavaOp.InvokeOp invokeOp -> context.javaCandidates().get(JavaMethodId.of(invokeOp.invokeReference()));
                case CoreOp.FuncCallOp funcCallOp -> context.coreCandidates().get(funcCallOp.funcName());
                default -> null;
            };
            if (candidate == null) {
                builder.add(op);
                return builder;
            }
            if (dropLocations) {
                candidate = candidate.transform(CodeTransformer.DROP_LOCATION_TRANSFORMER);
            }
            if (op.resultType() == PrimitiveType.VOID) {
                Inliner.inline(builder, candidate, builder.context().getValues(op.operands()), (_, _) -> {
                });
            } else {
                Op.Result variable = builder.add(CoreOp.var(candidate.resultType()));
                Inliner.inline(builder, candidate, builder.context().getValues(op.operands()), (b, value) -> {
                    b.add(CoreOp.varStore(variable, value));
                });
                Op.Result load = builder.add(CoreOp.varLoad(variable));
                builder.context().mapValue(op.result(), load);
            }
            return builder;
        }
    }

    /**
     * Contains discovered inlining candidates.
     * @param coreCandidates candidates for inlining from {@link CoreOp.FuncCallOp}
     * @param javaCandidates candidates for inlining from {@link JavaOp.InvokeOp}
     */
    public record Context(Map<String, CoreOp.FuncOp> coreCandidates, Map<JavaMethodId, CoreOp.FuncOp> javaCandidates) {
    }

    /**
     * Heuristic to use for identifying methods to inline. Override this in a subclass to experiment with other heuristics.
     * @param func the code model of the candidate
     * @param methodId the identifier of the candidate
     * @return true, if the method should be inlined into call sites
     */
    protected boolean heuristic(CoreOp.FuncOp func, JavaMethodId methodId) {
        return complexity(func) <= maxComplexity;
    }

    /**
     * Corresponds (roughly) to the number of tokens emitted by
     * {@link de.jplag.java.babylon.tokenizer.impl.FullBabylonTokenizer} for this op.
     * @param op the op to analyze
     * @return the complexity of this op
     */
    protected long complexity(Op op) {
        long result = 1;
        for (Body body : op.bodies()) {
            for (Block block : body.blocks()) {
                for (Op op1 : block.ops()) {
                    result += complexity(op1);
                }
            }
        }
        return result;
    }
}
