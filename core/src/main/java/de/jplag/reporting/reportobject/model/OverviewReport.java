package de.jplag.reporting.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OverviewReport(

        @JsonProperty("top_comparisons") List<TopComparison> topComparisons,

        @JsonProperty("clusters") List<Cluster> clusters

) {
}
