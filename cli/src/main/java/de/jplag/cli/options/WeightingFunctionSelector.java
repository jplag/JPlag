package de.jplag.cli.options;

import de.jplag.frequency.WeightingFunction;
import de.jplag.frequency.strategy.FrequencyStrategy;
import de.jplag.frequency.weighting.LinearWeighting;
import de.jplag.frequency.weighting.ProportionalWeighting;
import de.jplag.frequency.weighting.QuadraticWeighting;
import de.jplag.frequency.weighting.SigmoidWeighting;

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
