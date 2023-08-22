package de.jplag.merging;

/**
 * Collection of parameters that describe how a match merging should be performed.
 * @param neighborLength describes how short a match can be, to be considered (Defaults to 2).
 * @param gapSize describes how many tokens can be between to neighboring matches (Defaults to 6).
 */
public record MergingOptions(boolean enabled, int neighborLength, int gapSize) {

    /**
     * The default values of MergingOptions are false for the enable-switch, which deactivate MatchMerging, while
     * neighborLength and gapSize default to (2,6), which in testing yielded the best results.
     */
    public MergingOptions() {
        this(false, 2, 6);
    }

    /**
     * Builder pattern method for setting enabled
     * @param enabled containing the new value
     * @return MergingOptions with specified enabled
     */
    public MergingOptions withEnabled(boolean enabled) {
        return new MergingOptions(enabled, neighborLength, gapSize);
    }

    /**
     * Builder pattern method for setting neighborLength
     * @param neighborLength containing the new value
     * @return MergingOptions with specified neighborLength
     */
    public MergingOptions withNeighborLength(int neighborLength) {
        return new MergingOptions(enabled, neighborLength, gapSize);
    }

    /**
     * Builder pattern method for setting gapSize
     * @param gapSize containing the new value
     * @return MergingOptions with specified gapSize
     */
    public MergingOptions withGapSize(int gapSize) {
        return new MergingOptions(enabled, neighborLength, gapSize);
    }
}
