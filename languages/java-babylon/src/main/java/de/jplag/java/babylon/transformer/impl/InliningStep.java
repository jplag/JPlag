package de.jplag.java.babylon.transformer.impl;

import static de.jplag.java.babylon.BabylonUtils.inline;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;
import javax.tools.JavaCompiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.java.babylon.extractor.CodeModelExtractor;
import de.jplag.java.babylon.extractor.ExtractionFailedException;
import de.jplag.java.babylon.transformer.Prepass;
import de.jplag.java.babylon.transformer.TransformationStep;

import com.google.auto.service.AutoService;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreeScanner;
import jdk.incubator.code.Block;
import jdk.incubator.code.Body;
import jdk.incubator.code.CodeTransformer;
import jdk.incubator.code.Op;
import jdk.incubator.code.dialect.core.CoreOp;
import jdk.incubator.code.dialect.java.JavaOp;
import jdk.incubator.code.dialect.java.MethodRef;
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
        return new FindCandidates(context.extractor(), context.task(), this::heuristic);
    }

    @Override
    public CoreOp.FuncOp apply(CoreOp.FuncOp op, Context context) {
        return op.transform(new Apply(context, dropLocations));
    }

    private static class FindCandidates extends TreeScanner<Void, Void> implements Prepass<Context> {
        private final Map<String, CoreOp.FuncOp> core = new HashMap<>();
        private final Map<MethodRef, CoreOp.FuncOp> java = new HashMap<>();
        private final CodeModelExtractor codeModelExtractor;
        private final BiPredicate<CoreOp.FuncOp, MethodRef> heuristic;
        private final JavaCompiler.CompilationTask task;

        public FindCandidates(CodeModelExtractor codeModelExtractor, JavaCompiler.CompilationTask task,
                BiPredicate<CoreOp.FuncOp, MethodRef> heuristic) {
            this.codeModelExtractor = codeModelExtractor;
            this.task = task;
            this.heuristic = heuristic;
        }

        @Override
        public Context finalizeContext() {
            return new Context(Collections.unmodifiableMap(core), Collections.unmodifiableMap(java));
        }

        @Override
        public Void visitMethod(MethodTree node, Void unused) {
            MethodRef id = MethodRef.method(task, node);
            CoreOp.FuncOp op;
            try {
                op = codeModelExtractor.toOp(node).orElse(null);
            } catch (ExtractionFailedException e) {
                logger.debug("Could not extract possible inlining candidate", e);
                return super.visitMethod(node, unused);
            }
            if (op != null && heuristic.test(op, id)) {
                java.put(id, op);
            }
            return super.visitMethod(node, unused);
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
                case JavaOp.InvokeOp invokeOp -> context.javaCandidates().get(invokeOp.invokeReference());
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
            Op.Result variable = op.resultType() == PrimitiveType.VOID ? null : builder.add(CoreOp.var(op.resultType()));
            inline(builder, op.location(), candidate, builder.context().getValues(op.operands()), variable);
            if (variable != null) {
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
    public record Context(Map<String, CoreOp.FuncOp> coreCandidates, Map<MethodRef, CoreOp.FuncOp> javaCandidates) {
    }

    /**
     * Heuristic to use for identifying methods to inline. Override this in a subclass to experiment with other heuristics.
     * @param func the code model of the candidate
     * @param methodId the identifier of the candidate
     * @return true, if the method should be inlined into call sites
     */
    protected boolean heuristic(CoreOp.FuncOp func, MethodRef methodId) {
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
