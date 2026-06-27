package de.jplag.java.babylon.pipeline;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.jplag.java.babylon.extractor.CodeModelExtractor;
import de.jplag.java.babylon.transformer.TransformationStep;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Wraps the transformations to perform within the language module in a simplified API.
 */
public final class TransformationPipeline {
    private final List<TransformationStep<?>> steps;
    private final PrepassExecutor prepassExecutor;

    /**
     * Creates a new pipeline wrapping some steps.
     * @param steps the steps to wrap
     * @throws IllegalArgumentException if the sequence of steps is not well-formed
     */
    public TransformationPipeline(List<? extends TransformationStep<?>> steps) {
        this(steps, PrepassExecutor.getDefault());
    }

    /**
     * Creates a new pipeline wrapping some steps.
     * @param steps the steps to wrap
     * @param prepassExecutor the prepass executor for this pipeline
     * @throws IllegalArgumentException if the sequence of steps is not well-formed
     */
    public TransformationPipeline(List<? extends TransformationStep<?>> steps, PrepassExecutor prepassExecutor) {
        this.steps = List.copyOf(steps);
        this.prepassExecutor = prepassExecutor;

        // ensure all dependencies are fulfilled
        Set<String> applied = new HashSet<>();
        for (TransformationStep<?> step : steps) {
            Set<String> difference = new HashSet<>(step.getDependencies());
            difference.removeAll(applied);
            if (!difference.isEmpty()) {
                throw new IllegalArgumentException(String.format("The following steps were not applied: %s", difference));
            }

            applied.add(step.getIdentifier());
        }
    }

    /**
     * Performs a prepass to obtain the context required for tokenization.
     * @param trees the input trees on which the prepass should be run
     * @param extractor the extractor to use for obtaining code models
     * @return the prepass visitor
     */
    public Context prepass(Iterable<? extends CompilationUnitTree> trees, CodeModelExtractor extractor) {
        return new Context(prepassExecutor.prepass(steps, trees, extractor));
    }

    /**
     * Transform a single method according to the transformations represented by this pipeline.
     * @param methodTree the method tree to transform
     * @param ast the compilation unit in which the method is contained
     * @param context the context obtained from the prepass
     * @return the transformed op
     * @throws IllegalArgumentException if the context belongs to a different pipeline
     */
    public Optional<CoreOp.FuncOp> transform(MethodTree methodTree, CompilationUnitTree ast, Context context) {
        return context.finalExtractor.toOp(methodTree, ast);
    }

    /**
     * Context about the input code obtained from {@link #prepass}.<br>
     * Only intended as the object to be passed to {@link #transform}, do not use this elsewhere.
     */
    public static class Context {
        private final CodeModelExtractor finalExtractor;

        private Context(CodeModelExtractor finalExtractor) {
            this.finalExtractor = finalExtractor;
        }
    }
}
