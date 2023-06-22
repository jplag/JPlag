package de.jplag.merging;

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
