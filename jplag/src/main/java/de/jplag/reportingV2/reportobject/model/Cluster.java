package de.jplag.reportingV2.reportobject.model;

import java.util.List;

public class Cluster {
    private final float average_similarity;
    private final float strength;
    private final List<String> members;

    public Cluster(float average_similarity, float strength, List<String> members) {
        this.average_similarity = average_similarity;
        this.strength = strength;
        this.members = List.copyOf(members);
    }

    public float getAverage_similarity() {
        return average_similarity;
    }

    public float getStrength() {
        return strength;
    }

    public List<String> getMembers() {
        return members;
    }
}
