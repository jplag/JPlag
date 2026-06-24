package de.jplag.java.babylon;

import java.util.List;

import de.jplag.java.babylon.transformer.TransformationPipeline;

/**
 * Separate class to match the non-public API of {@link BabylonOptions}.
 */
public class DefaultTransformations {
    /**
     * Obtain the default list of transformation steps.
     * @return the list
     */
    public static List<? extends TransformationPipeline.Step<?>> getDefaultSteps() {
        return new BabylonOptions().getPipelineSteps();
    }
}
