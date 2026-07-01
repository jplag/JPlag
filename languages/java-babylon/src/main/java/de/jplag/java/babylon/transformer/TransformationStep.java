package de.jplag.java.babylon.transformer;

import java.util.Set;

import javax.annotation.Nullable;
import javax.tools.JavaCompiler;

import de.jplag.java.babylon.extractor.CodeModelExtractor;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * A step in the transformation pipeline.<br>
 * Registered implementations automatically get picked up by {@link TransformationStepLoader} using the
 * {@link java.util.ServiceLoader} mechanism.
 * @param <Context> context obtained by the prepass and provided to the application
 */
public interface TransformationStep<Context> {
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

    /**
     * Context for creating prepasses.<br>
     * Rather than a bunch of method parameters, this encapsulates all relevant objects in a single wrapper.
     * @param extractor the current code model extractor
     * @param task the compilation task in whose context the prepass is being executed
     */
    record PrepassConstructionContext(CodeModelExtractor extractor, JavaCompiler.CompilationTask task) {
    }
}
