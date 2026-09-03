package de.jplag.cli.options;

import de.jplag.frequency.strategy.CompleteMatchesStrategy;
import de.jplag.frequency.strategy.ContainedMatchesStrategy;
import de.jplag.frequency.strategy.FrequencyAnalysisStrategy;
import de.jplag.frequency.strategy.SubMatchesStrategy;
import de.jplag.frequency.strategy.WindowOfMatchesStrategy;

/**
 * Enum selector for {@link FrequencyAnalysisStrategy} implementations for the picocli CLI.
 */
public enum FrequencyStrategySelector {

    COMPLETE_MATCHES {
        @Override
        public FrequencyAnalysisStrategy create(int unused) {
            return new CompleteMatchesStrategy();
        }
    },

    CONTAINED_MATCHES {
        @Override
        public FrequencyAnalysisStrategy create(int minimumLength) {
            return new ContainedMatchesStrategy(minimumLength);
        }
    },

    SUBMATCHES {
        @Override
        public FrequencyAnalysisStrategy create(int minimumLength) {
            return new SubMatchesStrategy(minimumLength);
        }
    },

    MATCH_WINDOWS {
        @Override
        public FrequencyAnalysisStrategy create(int windowLength) {
            return new WindowOfMatchesStrategy(windowLength);
        }
    };

    /**
     * Default selector for the {@link FrequencyAnalysisStrategy}.
     */
    public static final FrequencyStrategySelector DEFAULT_FREQUENCY_STRATEGY_SELECTOR = COMPLETE_MATCHES;

    /**
     * Creates a new {@link FrequencyAnalysisStrategy} object corresponding to the respective selected option.
     * @param minimumLength the minimum length parameter for the respective strategies.
     * @return the frequency strategy.
     */
    public abstract FrequencyAnalysisStrategy create(int minimumLength);
}
