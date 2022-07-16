package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Metric(@JsonProperty("name") String name, @JsonProperty("threshold") float threshold,
        @JsonProperty("distribution") List<Integer> distribution, @JsonProperty("topComparisons") List<TopComparison> topComparisons) {

    public Metric(String name, float threshold, List<Integer> distribution, List<TopComparison> topComparisons) {
        this.name = name;
        this.threshold = threshold;
        this.distribution = List.copyOf(distribution);
        this.topComparisons = List.copyOf(topComparisons);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public float threshold() {
        return threshold;
    }

    @Override
    public List<Integer> distribution() {
        return distribution;
    }

    @Override
    public List<TopComparison> topComparisons() {
        return topComparisons;
    }
}
