package de.jplag.merging;

/**
 * Collection of options for the subsequence match merging mechanisms, that opposes obfuscation attempts.
 * @param enabled controls whether subsequence match merging is enabled or not.
 * @param minimumNeighborLength controls how short a match can be, to be considered (Defaults to 2).
 * @param maximumGapSize controls how many tokens can be between to neighboring matches (Defaults to 6).
 * @param minimumRequiredMerges controls how many merges per comparison need to be done before the results are
 * considered.
 */
public record MergingOptions(boolean enabled, int minimumNeighborLength, int maximumGapSize, int minimumRequiredMerges) {

    /** default value merging being enabled. **/
    public static final boolean DEFAULT_ENABLED = false;
    /** default value for the minimum neighbor length. **/
    public static final int DEFAULT_NEIGHBOR_LENGTH = 2;
    /** default value for the maximum gap size. **/
    public static final int DEFAULT_GAP_SIZE = 6;
    /** default value for the minimum number of required merges. **/
    public static final int DEFAULT_REQUIRED_MERGES = 6;

    /**
     * Creates merging options with default parameters.
     * @see MergingOptions#DEFAULT_ENABLED
     * @see MergingOptions#DEFAULT_NEIGHBOR_LENGTH
     * @see MergingOptions#DEFAULT_GAP_SIZE
     * @see MergingOptions#DEFAULT_REQUIRED_MERGES
     */
    public MergingOptions() {
        this(DEFAULT_ENABLED, DEFAULT_NEIGHBOR_LENGTH, DEFAULT_GAP_SIZE, DEFAULT_REQUIRED_MERGES);
    }

    /**
     * Builder pattern method for enabling and disabling the subsequence match merging mechanism.
     * @param enabled specifying if merging is enabled or not.
     * @return the options with the specified configuration.
     */
    public MergingOptions withEnabled(boolean enabled) {
        return new MergingOptions(enabled, minimumNeighborLength, maximumGapSize, minimumRequiredMerges);
    }

    /**
     * Builder pattern method for setting minimum length (in tokens) for a pair of neighboring matches to be considered for
     * merging.
     * @param minimumNeighborLength containing the new value.
     * @return the options with the specified configuration.
     */
    public MergingOptions withMinimumNeighborLength(int minimumNeighborLength) {
        return new MergingOptions(enabled, minimumNeighborLength, maximumGapSize, minimumRequiredMerges);
    }

    /**
     * Builder pattern method for setting maximum gap (in tokens) between a pair of matches to be considered for merging.
     * @param maximumGapSize containing the new value.
     * @return the options with the specified configuration.
     */
    public MergingOptions withMaximumGapSize(int maximumGapSize) {
        return new MergingOptions(enabled, minimumNeighborLength, maximumGapSize, minimumRequiredMerges);
    }

    /**
     * Builder pattern method for setting the minimal number of required merges before subsequence match merging has an
     * effect.
     * @param minimumRequiredMerges containing the new value.
     * @return the options with the specified configuration.
     */
    public MergingOptions withMinimumRequiredMerges(int minimumRequiredMerges) {
        return new MergingOptions(enabled, minimumNeighborLength, maximumGapSize, minimumRequiredMerges);
    }
}
