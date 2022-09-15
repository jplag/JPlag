package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Cluster(@JsonProperty("average_similarity") double averageSimilarity, @JsonProperty("strength") double strength,
        @JsonProperty("members") List<String> members) {
}
