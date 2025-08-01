package de.jplag.reporting.reportobject.model;

import java.util.List;

/**
 * Represents a cluster with its average similarity, strength, and member identifiers.
 * @param averageSimilarity the average similarity score within the cluster
 * @param strength the strength metric of the cluster
 * @param members the list of members (submission IDs) in the cluster
 */
public record Cluster(double averageSimilarity, double strength, List<String> members) {
}
