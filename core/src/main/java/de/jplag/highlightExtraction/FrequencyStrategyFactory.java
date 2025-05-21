package de.jplag.highlightExtraction;

public class FrequencyStrategyFactory {
    public static FrequencyStrategy create(FrequencyStrategies strategy) {
        return switch (strategy) {
            case COMPLETE_MATCHES -> new CompleteMatchesStrategy();
            case SUB_MATCHES -> new SubMatchesStrategy();
            case WINDOW_OF_MATCHES -> new WindowOfMatchesStrategy();
            case CONTAINED_MATCHES -> new ContainedStrategy();
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategy);
        };
    }
}
