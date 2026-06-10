package de.jplag.java.babylon.transformer;

import com.sun.source.tree.TreeVisitor;
import de.jplag.java.babylon.MulticastTreeVisitor;
import jdk.incubator.code.dialect.core.CoreOp;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Wraps the transformations to perform within the language module in a simplified API.
 */
public final class TransformationPipeline {
    private final List<Step> steps;

    /**
     * Creates a new pipeline wrapping some steps.
     *
     * @param steps the steps to wrap
     */
    public TransformationPipeline(List<Step> steps) {
        this.steps = List.copyOf(steps);
    }

    /**
     * Returns a prepass to perform before beginning the tokenization.
     *
     * @return the prepass visitor
     */
    public TreeVisitor<?, ?> prepass() {
        List<TreeVisitor<?, ?>> visitors = new ArrayList<>();
        for (Step step : steps) {
            TreeVisitor<?, ?> visitor = step.beginPrepass();
            if (visitor != null) {
                visitors.add(visitor);
            }
        }
        return MulticastTreeVisitor.create(visitors);
    }

    /**
     * Transform a single {@link CoreOp.FuncOp} according to the transformations represented by this pipeline.
     *
     * @param op the op to transform
     * @return the transformed op
     */
    public CoreOp.FuncOp transform(CoreOp.FuncOp op) {
        for (Step step : steps) {
            op = step.apply(op);
        }
        return op;
    }

    /**
     * A step in the transformation pipeline. Registered implementations automatically get picked up by {@link TransformationStepLoader} using the {@link java.util.ServiceLoader} mechanism.
     */
    public interface Step {
        /**
         * @return Identifier of the transformation used for CLI options and dynamic loading. You should use some name within
         * {@code [a-z_-]+}
         */
        String getIdentifier();

        /**
         * Returns a prepass to perform before beginning the tokenization or null.
         *
         * @return the prepass visitor or null
         */
        default @Nullable TreeVisitor<?, ?> beginPrepass() {
            return null;
        }

        /**
         * Apply the transformation represented by this step to a single {@link CoreOp.FuncOp}.
         *
         * @param op the op to apply the transformation to
         * @return the transformed op
         */
        CoreOp.FuncOp apply(CoreOp.FuncOp op);
    }
}
