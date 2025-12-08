package de.jplag.highlightextraction.strategy;

/**
 * Enum selector for {@link FrequencyStrategy} implementations for the picocli CLI.
 */
public enum FrequencyStrategySelector {

    COMPLETE_MATCHES {
        @Override
        public FrequencyStrategy create(int unused) {
            return new CompleteMatchesStrategy();
        }
    },

    CONTAINED_MATCHES {
        @Override
        public FrequencyStrategy create(int minimumLength) {
            return new ContainedMatchesStrategy(minimumLength);
        }
    },

    SUBMATCHES {
        @Override
        public FrequencyStrategy create(int minimumLength) {
            return new SubmatchesStrategy(minimumLength);
        }
    },

    MATCH_WINDOWS {
        @Override
        public FrequencyStrategy create(int windowLength) {
            return new WindowOfMatchesStrategy(windowLength);
        }
    };

    /**
     * Default selector for the {@link FrequencyStrategy}.
     */
    public static final FrequencyStrategySelector DEFAULT_FREQUENCY_STRATEGY_SELECTOR = COMPLETE_MATCHES;

    /**
     * Creates a new {@link FrequencyStrategy} object corresponding to the respective selected option.
     * @param minimumLength the minimum length parameter for the respective strategies.
     * @return the frequency strategy.
     */
    public abstract FrequencyStrategy create(int minimumLength);
}
