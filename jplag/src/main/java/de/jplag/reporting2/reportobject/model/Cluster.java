package de.jplag.reporting2.reportobject.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cluster {

    @JsonProperty("average_similarity")
    private final float averageSimilarity;

    @JsonProperty("strength")
    private final float strength;

    @JsonProperty("members")
    private final List<String> members;

    public Cluster(float averageSimilarity, float strength, List<String> members) {
        this.averageSimilarity = averageSimilarity;
        this.strength = strength;
        this.members = List.copyOf(members);
    }

    public float getAverageSimilarity() {
        return averageSimilarity;
    }

    public float getStrength() {
        return strength;
    }

    public List<String> getMembers() {
        return members;
    }
}
