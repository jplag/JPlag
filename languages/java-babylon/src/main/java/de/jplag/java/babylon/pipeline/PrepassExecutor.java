package de.jplag.java.babylon.pipeline;

import java.util.List;

import de.jplag.java.babylon.extractor.CodeModelExtractor;
import de.jplag.java.babylon.extractor.TransformingCodeModelExtractor;
import de.jplag.java.babylon.transformer.Prepass;
import de.jplag.java.babylon.transformer.TransformationStep;

import com.sun.source.tree.CompilationUnitTree;

/**
 * Strategy for executing a transformation pipelines prepasses.<br>
 * Pluggable to allow the performance of approaches to be evaluated.
 */
public sealed interface PrepassExecutor permits EagerPrepassExecutor, HybridPrepassExecutor, LazyPrepassExecutor {
    /**
     * Resolves the default pipeline executor of the current process.
     * @return the pipeline executor
     * @throws IllegalArgumentException if the pipeline executor property is not well-formed
     */
    static PrepassExecutor getDefault() {
        return switch (System.getProperty("jplag.java-babylon.pipeline.executor", "eager")) {
            case "eager" -> new EagerPrepassExecutor();
            case "lazy" -> new LazyPrepassExecutor();
            case "hybrid" -> new HybridPrepassExecutor();
            default -> throw new IllegalArgumentException("Selected pipeline executor does not exist. Pick one of [eager, lazy, hybrid]");
        };
    }

    /**
     * Performs a list of prepasses to obtain the context required for tokenization.
     * @param steps the steps comprising the pipeline that is to be executed
     * @param trees the input trees on which the prepass should be run
     * @param context the context to use for constructing prepasses
     * @return the completed CodeModelExtractor containing the prepass context and all transformations
     */
    CodeModelExtractor prepass(List<TransformationStep<?>> steps, Iterable<? extends CompilationUnitTree> trees,
            TransformationStep.PrepassConstructionContext context);

    /**
     * Performs a prepass to obtain the context required for tokenization.
     * @param <T> type of context returned by the prepass
     * @param step the step containing the prepass to be executed
     * @param trees the input trees on which the prepass should be run
     * @param context the context to use for constructing the prepass
     * @return a CodeModelExtractor containing the prepass context and the transformation described by the step
     */
    default <T> CodeModelExtractor prepass(TransformationStep<T> step, Iterable<? extends CompilationUnitTree> trees,
            TransformationStep.PrepassConstructionContext context) {
        Prepass<T> prepass = step.beginPrepass(context);
        T prepassContext;
        if (prepass != null) {
            for (CompilationUnitTree tree : trees) {
                tree.accept(prepass, null);
            }
            prepassContext = prepass.finalizeContext();
        } else {
            prepassContext = null;
        }
        return new TransformingCodeModelExtractor(context.extractor(), op -> step.apply(op, prepassContext));
    }
}
