package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MergingOptionsReport(@JsonProperty("enabled") boolean enabled, @JsonProperty("min_neighbour_length") int minimumNeighborLength,
        @JsonProperty("max_gap_size") int maximumGapSize) {
}
