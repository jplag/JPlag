package de.jplag.reporting.reportobject.model;

import java.util.List;

public record Cluster(double averageSimilarity, double strength, List<String> members) {
}
