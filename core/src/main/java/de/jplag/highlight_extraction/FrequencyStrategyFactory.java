package de.jplag.highlight_extraction;

/**
 * Factory class to create instances of FrequencyStrategy based on the specified FrequencyStrategies enum.
 */

public class FrequencyStrategyFactory {

    private FrequencyStrategyFactory() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Creates an instance of a FrequencyStrategy based on the chosen FrequencyStrategies enum.
     * @param strategy the applied frequency-strategy from the input
     * @return An instance of the corresponding FrequencyStrategy implementation.
     */
    public static FrequencyStrategy create(FrequencyStrategies strategy) {
        return switch (strategy) {
            case COMPLETE_MATCHES -> new CompleteMatchesStrategy();
            case SUB_MATCHES -> new SubMatchesStrategy();
            case WINDOW_OF_MATCHES -> new WindowOfMatchesStrategy();
            case CONTAINED_MATCHES -> new ContainedMatchesStrategy();
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategy);
        };
    }
}
