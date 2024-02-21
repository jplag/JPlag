package de.jplag.merging;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Collection of parameters that describe how a match merging should be performed.
 * @param minimumNeighborLength describes how short a match can be, to be considered (Defaults to 2).
 * @param maximumGapSize describes how many tokens can be between to neighboring matches (Defaults to 6).
 */
public record MergingOptions(@JsonProperty("enabled") boolean enabled, @JsonProperty("min_neighbour_length") int minimumNeighborLength,
        @JsonProperty("max_gap_size") int maximumGapSize) {

    public static final boolean DEFAULT_ENABLED = false;
    public static final int DEFAULT_NEIGHBOR_LENGTH = 2;
    public static final int DEFAULT_GAP_SIZE = 6;

    /**
     * The default values of MergingOptions are false for the enable-switch, which deactivate MatchMerging, while
     * minimumNeighborLength and maximumGapSize default to (2,6), which in testing yielded the best results.
     */
    public MergingOptions() {
        this(DEFAULT_ENABLED, DEFAULT_NEIGHBOR_LENGTH, DEFAULT_GAP_SIZE);
    }

    /**
     * Builder pattern method for setting enabled
     * @param enabled containing the new value
     * @return MergingOptions with specified enabled
     */
    public MergingOptions withEnabled(boolean enabled) {
        return new MergingOptions(enabled, minimumNeighborLength, maximumGapSize);
    }

    /**
     * Builder pattern method for setting minimumNeighborLength
     * @param minimumNeighborLength containing the new value
     * @return MergingOptions with specified minimumNeighborLength
     */
    public MergingOptions withMinimumNeighborLength(int minimumNeighborLength) {
        return new MergingOptions(enabled, minimumNeighborLength, maximumGapSize);
    }

    /**
     * Builder pattern method for setting maximumGapSize
     * @param maximumGapSize containing the new value
     * @return MergingOptions with specified maximumGapSize
     */
    public MergingOptions withMaximumGapSize(int maximumGapSize) {
        return new MergingOptions(enabled, minimumNeighborLength, maximumGapSize);
    }
}
