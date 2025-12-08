package de.jplag.highlightextraction.strategy;

/**
 * Enum selector for {@link FrequencyStrategy} implementations for the picocli CLI.
 */
public enum FrequencyStrategySelector {

    COMPLETE {
        @Override
        public FrequencyStrategy create(int unused) {
            return new CompleteMatchesStrategy();
        }
    },

    CONTAINED {
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

    WINDOW {
        @Override
        public FrequencyStrategy create(int windowLength) {
            return new WindowOfMatchesStrategy(windowLength);
        }
    };

    /**
     * Default selector for the {@link FrequencyStrategy}.
     */
    public static final FrequencyStrategySelector DEFAULT_FREQUENCY_STRATEGY_SELECTOR = COMPLETE;

    /**
     * Creates a new {@link FrequencyStrategy} object corresponding to the respective selected option.
     * @param minimumLength the minimum length parameter for the respective strategies.
     * @return the frequency strategy.
     */
    public abstract FrequencyStrategy create(int minimumLength);
}
