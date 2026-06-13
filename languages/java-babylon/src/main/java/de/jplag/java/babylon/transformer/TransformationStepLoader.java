package de.jplag.java.babylon.transformer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Utility class for loading installed {@link TransformationPipeline.Step} implementations.<br>
 * Based on {@link de.jplag.LanguageLoader}.
 */
public class TransformationStepLoader {
    private static final Logger logger = LoggerFactory.getLogger(TransformationStepLoader.class);

    private static volatile Map<String, TransformationPipeline.Step> cachedStepInstances = null;
    private static volatile ServiceLoader<TransformationPipeline.Step> stepLoader = null;
    private static volatile ServiceLoader<SimpleTransformation> simpleTransformationLoader = null;

    private TransformationStepLoader() {
        throw new IllegalAccessError();
    }

    /**
     * Get all transformation steps that are currently in the classpath. The result will be cached.<br>
     * Use {@link #clearCache()} to obtain new instances.
     * @return the transformation steps as an unmodifiable map from identifiers to {@link TransformationPipeline.Step}
     * instances
     */
    public static Map<String, TransformationPipeline.Step> getAllAvailableTransformationSteps() {
        if (cachedStepInstances == null) {
            synchronized (TransformationStepLoader.class) {
                if (cachedStepInstances == null) {
                    Map<String, TransformationPipeline.Step> steps = new TreeMap<>();
                    Set<String> skipped = new HashSet<>();

                    if (stepLoader == null)
                        stepLoader = ServiceLoader.load(TransformationPipeline.Step.class);
                    if (simpleTransformationLoader == null)
                        simpleTransformationLoader = ServiceLoader.load(SimpleTransformation.class);

                    List<TransformationPipeline.Step> stepsList = Stream.concat(stepLoader.stream().map(ServiceLoader.Provider::get),
                            simpleTransformationLoader.stream().map(ServiceLoader.Provider::get).map(SimpleTransformationStep::new)).toList();

                    for (TransformationPipeline.Step step : stepsList) {
                        String identifier = step.getIdentifier();
                        if (steps.remove(identifier) != null && skipped.add(identifier)) {
                            logger.error("Multiple implementations for a transformation step '{}' are present in the classpath! Skipping ..",
                                    identifier);
                            continue;
                        }
                        logger.trace("Loading transformation step '{}'", identifier);
                        steps.put(identifier, step);
                    }
                    logger.debug("Available transformation steps: '{}'", steps.keySet().stream().toList());

                    cachedStepInstances = Collections.unmodifiableMap(steps);
                }
            }
        }

        return cachedStepInstances;
    }

    private record SimpleTransformationStep(SimpleTransformation transformation) implements TransformationPipeline.Step {
        @Override
        public String getIdentifier() {
            return transformation.getIdentifier();
        }

        @Override
        public CoreOp.FuncOp apply(CoreOp.FuncOp op) {
            return op.transform(transformation);
        }
    }

    /**
     * Load a transformation step that is currently in the classpath by its identifier.
     * @param identifier the identifier of the transformation step
     * @return the transformation step or an empty optional if no corresponding step was found
     * @see TransformationPipeline.Step#getIdentifier()
     */
    public static Optional<TransformationPipeline.Step> getTransformationStep(String identifier) {
        var step = getAllAvailableTransformationSteps().get(identifier);
        if (step == null) {
            logger.warn("Attempt to load transformation step {} was not successful", identifier);
        }
        return Optional.ofNullable(step);
    }

    /**
     * Get an unmodifiable set of all available transformation step identifiers.
     * @return identifiers of all available transformation steps
     * @see TransformationPipeline.Step#getIdentifier()
     */
    public static Set<String> getAllAvailableTransformationStepIdentifiers() {
        return new TreeSet<>(getAllAvailableTransformationSteps().keySet());
    }

    /**
     * Clears the internal cache of {@link TransformationPipeline.Step} instances, allowing new steps to be found.
     */
    public static void clearCache() {
        synchronized (TransformationStepLoader.class) {
            cachedStepInstances = null;
            if (stepLoader != null)
                stepLoader.reload();
            if (simpleTransformationLoader != null)
                simpleTransformationLoader.reload();
        }
    }
}
