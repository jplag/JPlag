package de.jplag.reportingV2.reportobject.model;

import java.util.List;

public class Metric {
    private final String name;
    private final float threshold;
    private final List<Integer> distribution;
    private final List<TopComparison> topComparisons;

    public Metric(String name, float threshold, List<Integer> distribution, List<TopComparison> topComparisons) {
        this.name = name;
        this.threshold = threshold;
        this.distribution = List.copyOf(distribution);
        this.topComparisons = List.copyOf(topComparisons);
    }

    public String getName() {
        return name;
    }

    public float getThreshold() {
        return threshold;
    }

    public List<Integer> getDistribution() {
        return distribution;
    }

    public List<TopComparison> getTopComparisons() {
        return topComparisons;
    }
}
