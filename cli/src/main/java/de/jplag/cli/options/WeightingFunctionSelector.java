package de.jplag.cli.options;

import de.jplag.highlightextraction.WeightingFunction;
import de.jplag.highlightextraction.strategy.FrequencyStrategy;
import de.jplag.highlightextraction.weighting.LinearWeighting;
import de.jplag.highlightextraction.weighting.ProportionalWeighting;
import de.jplag.highlightextraction.weighting.QuadraticWeighting;
import de.jplag.highlightextraction.weighting.SigmoidWeighting;

/**
 * Enum selector for {@link FrequencyStrategy} implementations for the picocli CLI.
 */
public enum WeightingFunctionSelector {

    PROPORTIONAL {
        @Override
        public WeightingFunction create() {
            return new ProportionalWeighting();
        }
    },
    LINEAR {
        @Override
        public WeightingFunction create() {
            return new LinearWeighting();
        }
    },
    QUADRATIC {
        @Override
        public WeightingFunction create() {
            return new QuadraticWeighting();
        }
    },
    SIGMOID {
        @Override
        public WeightingFunction create() {
            return new SigmoidWeighting();
        }
    };

    /**
     * Default selector for the {@link WeightingFunction}.
     */
    public static final WeightingFunctionSelector DEFAULT_WEIGHTING_FUNCTION = SIGMOID;

    /**
     * Creates a new {@link WeightingFunction} object corresponding to the respective selected option.
     * @return the weighting function.
     */
    public abstract WeightingFunction create();
}
