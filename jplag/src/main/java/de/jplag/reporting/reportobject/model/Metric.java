package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Metric {

    @JsonProperty("name")
    private final String name;

    @JsonProperty("threshold")
    private final float threshold;

    @JsonProperty("distribution")
    private final List<Integer> distribution;

    @JsonProperty("topComparisons")
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
