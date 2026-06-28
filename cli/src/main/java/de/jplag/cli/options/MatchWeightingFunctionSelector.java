package de.jplag.cli.options;

import de.jplag.frequency.MatchWeightingFunction;
import de.jplag.frequency.strategy.FrequencyStrategy;
import de.jplag.frequency.weighting.LinearWeighting;
import de.jplag.frequency.weighting.ProportionalWeighting;
import de.jplag.frequency.weighting.QuadraticWeighting;
import de.jplag.frequency.weighting.SigmoidWeighting;

/**
 * Enum selector for {@link FrequencyStrategy} implementations for the picocli CLI.
 */
public enum MatchWeightingFunctionSelector {

    PROPORTIONAL {
        @Override
        public MatchWeightingFunction create() {
            return new ProportionalWeighting();
        }
    },
    LINEAR {
        @Override
        public MatchWeightingFunction create() {
            return new LinearWeighting();
        }
    },
    QUADRATIC {
        @Override
        public MatchWeightingFunction create() {
            return new QuadraticWeighting();
        }
    },
    SIGMOID {
        @Override
        public MatchWeightingFunction create() {
            return new SigmoidWeighting();
        }
    };

    /**
     * Default selector for the {@link MatchWeightingFunction}.
     */
    public static final MatchWeightingFunctionSelector DEFAULT_WEIGHTING_FUNCTION = SIGMOID;

    /**
     * Creates a new {@link MatchWeightingFunction} object corresponding to the respective selected option.
     * @return the weighting function.
     */
    public abstract MatchWeightingFunction create();
}
