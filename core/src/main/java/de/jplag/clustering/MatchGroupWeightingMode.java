package de.jplag.clustering;

import java.util.function.IntToDoubleFunction;

/**
 * This enum represents different strategies, for how the {@link ClusterFocusedSimilarityMatrixCreator} should weight
 * different appearances of some code, depending on how many submissions contain that code.
 */
public enum MatchGroupWeightingMode {
    /**
     * In this mode, no special weighting is applied and all appearances get weighted equally.
     */
    NO_WEIGHTING(_ -> 1, false),
    /**
     * In this mode, we ignore code (so give it a weight of zero) that is shared between only two submissions, and weight
     * all other appearances equally. The idea behind this mode is that code that is only shared between two students is
     * irrelevant for finding clusters of similar submissions (containing at least three different submissions).
     */
    NO_PAIRS(n -> n == 2 ? 0 : 1, false),
    /**
     * In this mode, we weight appearances of code anti-proportionally to how many submissions contain that code. The idea
     * behind this mode is that the more common some code is the less useful it is for identifying clusters of students that
     * might have plagiarized, as it is likely just some generic code.
     */
    ANTI_PROPORTIONAL(n -> (double) 1 / n, true),
    /**
     * This mode combines the {@link #NO_PAIRS} mode with the {@link #ANTI_PROPORTIONAL} mode, meaning code that is shared
     * by only two submissions gets a weight of zero, all other matches get a weight anti-proportionally to the number of
     * appearances of that code.
     */
    NO_PAIRS_ANTI_PROP(n -> n == 2 ? 0 : (double) 1 / n, true);

    private static final int NUMBER_OF_SUBMISSIONS_IN_GROUP_TO_START_SKIPPING = 100;

    private final IntToDoubleFunction weightingRule;
    private final boolean skipTooLargeGroups;

    MatchGroupWeightingMode(IntToDoubleFunction weightingRule, boolean skipTooLargeGroups) {
        this.weightingRule = weightingRule;
        this.skipTooLargeGroups = skipTooLargeGroups;
    }

    /**
     * Gets the weight that should be used for code that appears in the specified number of submissions according to this
     * weighting strategy.
     * @param groupSize in how many different submissions this code appears
     * @return the weight according to this mode
     */
    public double getWeight(int groupSize) {
        return weightingRule.applyAsDouble(groupSize);
    }

    /**
     * Due to performance reasons, some modes that already weight code less that appears many times, might want to skip the
     * assignment step completely for large groups. If this is the case, this method will return true, otherwise false.
     * @param size in how many different submissions this code appears
     * @return if this group should just be skipped
     */
    public boolean skipGroupOfSize(int size) {
        return skipTooLargeGroups && size >= NUMBER_OF_SUBMISSIONS_IN_GROUP_TO_START_SKIPPING;
    }
}
