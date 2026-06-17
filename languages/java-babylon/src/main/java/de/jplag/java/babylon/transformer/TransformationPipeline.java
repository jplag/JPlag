package de.jplag.java.babylon.transformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Wraps the transformations to perform within the language module in a simplified API.
 */
public final class TransformationPipeline {
    private final List<Step<?>> steps;

    /**
     * Creates a new pipeline wrapping some steps.
     * @param steps the steps to wrap
     */
    public TransformationPipeline(List<? extends Step<?>> steps) {
        this.steps = List.copyOf(steps);
    }

    /**
     * Returns a prepass to perform before beginning the tokenization.
     * @return the prepass visitor
     */
    public Prepass<Prepass.Multicast.Context> prepass() {
        List<Prepass<?>> visitors = new ArrayList<>();
        for (Step<?> step : steps) {
            Prepass<?> visitor = step.beginPrepass();
            Prepass<?> visitorOrDefault = Objects.requireNonNullElse(visitor, Prepass.Unit.INSTANCE);
            visitors.add(visitorOrDefault);
        }
        return new Prepass.Multicast(visitors, steps);
    }

    /**
     * Transform a single {@link CoreOp.FuncOp} according to the transformations represented by this pipeline.
     * @param op the op to transform
     * @param context the context obtained from the prepass
     * @return the transformed op
     * @throws IllegalArgumentException if the context belongs to a different pipeline
     */
    public CoreOp.FuncOp transform(CoreOp.FuncOp op, Prepass.Multicast.Context context) {
        if (steps != context.tag() || steps.size() != context.contexts().size()) {
            throw new IllegalArgumentException("Context does not match this pipeline");
        }
        Iterator<Step<?>> stepIterator = steps.iterator();
        Iterator<?> contextIterator = context.contexts().iterator();
        while (stepIterator.hasNext()) {
            @SuppressWarnings("rawtypes")
            Step step = stepIterator.next();
            Object stepContext = contextIterator.next();

            // noinspection unchecked
            op = step.apply(op, stepContext);
        }
        return op;
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
         * Returns a prepass to perform before beginning the tokenization or null.
         * @return the prepass visitor or null
         */
        default @Nullable Prepass<Context> beginPrepass() {
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
}
