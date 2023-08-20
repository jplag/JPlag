package de.jplag.merging;

/**
 * Collection of parameters that describe how a match merging should be performed.
 * @param mergeBuffer describes how short a match can be, to be considered (Defaults to 2).
 * @param gapSize describes how many tokens can be between to neighboring matches (Defaults to 6).
 */
public record MergingParameters(boolean enabled, int mergeBuffer, int gapSize) {

    /**
     * The default values of MergingParameters are false for the enable-switch, which deactivate MatchMerging, while
     * mergeBuffer and gapSize default to (2,6), which in testing yielded the best results.
     */
    public MergingParameters() {
        this(false, 2, 6);
    }

    /**
     * Builder pattern method for setting enabled
     * @param enabled containing the new value
     * @return MergingParameters with specified enabled
     */
    public MergingParameters withEnabled(boolean enabled) {
        return new MergingParameters(enabled, mergeBuffer, gapSize);
    }

    /**
     * Builder pattern method for setting mergeBuffer
     * @param mergeBuffer containing the new value
     * @return MergingParameters with specified mergeBuffer
     */
    public MergingParameters withMergeBuffer(int mergeBuffer) {
        return new MergingParameters(enabled, mergeBuffer, gapSize);
    }

    /**
     * Builder pattern method for setting gapSize
     * @param gapSize containing the new value
     * @return MergingParameters with specified gapSize
     */
    public MergingParameters withGapSize(int gapSize) {
        return new MergingParameters(enabled, mergeBuffer, gapSize);
    }
}
