package de.jplag.logging;

/**
 * The available processes. Used as a hint for the ui, which step JPlag is currently performing.
 */
public enum ProgressBarType {
    LOADING("Loading Submissions", false),
    PARSING("Parsing Submissions", false),
    TOKEN_SEQUENCE_CREATION("Preparing Submissions", false),
    COMPARING("Comparing Submission Pairs", false),
    MATCH_MERGING("Merging Matched Subsequences ", false),
    TOKEN_SEQUENCE_NORMALIZATION("Normalizing Token Sequences", false),
    CLUSTERING("Finding Clusters ", true);

    private final String defaultText;
    private final boolean isIdleBar;

    ProgressBarType(String defaultText, boolean isIdleBar) {
        this.defaultText = defaultText;
        this.isIdleBar = isIdleBar;
    }

    /**
     * @return The default display text for the type
     */
    public String getDefaultText() {
        return defaultText;
    }

    /**
     * @return True, if this bar should be rendered as an idle bar instead.
     */
    public boolean isIdleBar() {
        return isIdleBar;
    }
}
