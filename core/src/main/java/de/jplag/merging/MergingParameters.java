package de.jplag.merging;

/**
 * Collection of parameters that describe how a match merging should be performed.
 * @param mergeBuffer describes how shorter a match can be than the Minimum Token Match (Defaults to 0).
 * @param seperatingThreshold describes how many tokens can be between to neighboring matches (Defaults to 0).
 */
public record MergingParameters(int mergeBuffer, int seperatingThreshold) {

    public MergingParameters(int mergeBuffer, int seperatingThreshold) {
        this.mergeBuffer = mergeBuffer;
        this.seperatingThreshold = seperatingThreshold;
    }

    public MergingParameters() {
        this(0, 0);
    }

    public MergingParameters withMergeBuffer(int mergeBuffer) {
        return new MergingParameters(mergeBuffer, seperatingThreshold);
    }

    public MergingParameters withSeperatingThreshold(int seperatingThreshold) {
        return new MergingParameters(mergeBuffer, seperatingThreshold);
    }
}
