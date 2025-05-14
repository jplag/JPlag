package de.jplag.highlightExtraction;

public class FrequencyStrategyFactory {
    public static FrequencyStrategy create(FrequencyStrategies strategy) {
        return switch (strategy) {
            case COMPLETEMATCHES -> new CompleteMatchesStrategy();
            case SUBMATCHES -> new SubMatchesStrategy();
            case WINDOWOFMATCHES -> new WindowOfMatchesStrategy();
            case CONTAINEDMATCHES -> new ContainedStrategy();
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategy);
        };
    }
}
