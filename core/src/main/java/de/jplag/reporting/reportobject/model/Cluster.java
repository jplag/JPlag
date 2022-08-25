package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Cluster(@JsonProperty("average_similarity") float averageSimilarity, @JsonProperty("strength") float strength,
        @JsonProperty("members") List<String> members) {
}
