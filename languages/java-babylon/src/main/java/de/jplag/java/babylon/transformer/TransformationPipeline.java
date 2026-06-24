package de.jplag.java.babylon.transformer;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import de.jplag.java.babylon.extractor.CodeModelExtractor;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Wraps the transformations to perform within the language module in a simplified API.
 */
public final class TransformationPipeline {
    private final List<Step<?>> steps;
    private final PrepassExecutor prepassExecutor;

    /**
     * Creates a new pipeline wrapping some steps.
     * @param steps the steps to wrap
     * @throws IllegalArgumentException if the sequence of steps is not well-formed
     */
    public TransformationPipeline(List<? extends Step<?>> steps) {
        this(steps, PrepassExecutor.getDefault());
    }

    /**
     * Creates a new pipeline wrapping some steps.
     * @param steps the steps to wrap
     * @param prepassExecutor the prepass executor for this pipeline
     * @throws IllegalArgumentException if the sequence of steps is not well-formed
     */
    public TransformationPipeline(List<? extends Step<?>> steps, PrepassExecutor prepassExecutor) {
        this.steps = List.copyOf(steps);
        this.prepassExecutor = prepassExecutor;

        // ensure all dependencies are fulfilled
        Set<String> applied = new HashSet<>();
        for (Step<?> step : steps) {
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

    /**
     * A step in the transformation pipeline.<br>
     * Registered implementations automatically get picked up by {@link TransformationStepLoader} using the
     * {@link java.util.ServiceLoader} mechanism.
     * @param <Context> context obtained by the prepass and provided to the application
     */
    public interface Step<Context> {
        /**
         * @return Identifier of the transformation used for CLI options and dynamic loading. You should use some name within
         * {@code [a-z_-]+}
         */
        String getIdentifier();

        /**
         * Set of identifiers for steps that must be applied before this one.
         * @return the identifiers
         */
        default Set<String> getDependencies() {
            return Set.of();
        }

        /**
         * Returns a prepass to perform before beginning the tokenization or null.
         * @param context the context of the prepass
         * @return the prepass visitor or null
         */
        default @Nullable Prepass<Context> beginPrepass(PrepassConstructionContext context) {
            return null;
        }

        /**
         * Apply the transformation represented by this step to a single {@link CoreOp.FuncOp}.
         * @param op the op to apply the transformation to
         * @param context the context obtained from the prepass, if any
         * @return the transformed op
         */
        CoreOp.FuncOp apply(CoreOp.FuncOp op, Context context);
    }

    /**
     * Context for creating prepasses.<br>
     * Rather than a bunch of method parameters, this encapsulates all relevant objects in a single wrapper.
     * @param codeModelExtractor the current code model extractor
     */
    public record PrepassConstructionContext(CodeModelExtractor codeModelExtractor) {
    }
}
