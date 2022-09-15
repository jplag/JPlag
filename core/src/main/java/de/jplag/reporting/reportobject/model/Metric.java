package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Metric(@JsonProperty("name") String name, @JsonProperty("distribution") List<Integer> distribution,
        @JsonProperty("topComparisons") List<TopComparison> topComparisons, @JsonProperty String description) {
}
