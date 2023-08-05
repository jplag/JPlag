package de.jplag.merging;

/**
 * Collection of parameters that describe how a match merging should be performed.
 * @param mergeBuffer describes how shorter a match can be than the Minimum Token Match (Defaults to 0).
 * @param seperatingThreshold describes how many tokens can be between to neighboring matches (Defaults to 0).
 */
public record MergingParameters(boolean enable, int mergeBuffer, int seperatingThreshold) {

    public MergingParameters() {
        this(false, 0, 0);
    }

    public MergingParameters withEnable(boolean enable) {
        return new MergingParameters(enable, mergeBuffer, seperatingThreshold);
    }

    public MergingParameters withMergeBuffer(int mergeBuffer) {
        return new MergingParameters(enable, mergeBuffer, seperatingThreshold);
    }

    public MergingParameters withSeperatingThreshold(int seperatingThreshold) {
        return new MergingParameters(enable, mergeBuffer, seperatingThreshold);
    }
}
