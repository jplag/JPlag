package de.jplag.logging;

/**
 * The available processes. Used as a hint for the ui, which step JPlag is currently performing.
 */
public enum ProgressBarType {
    LOADING("Loading Submissions  ", false),
    PARSING("Parsing Submissions  ", false),
    COMPARING("Comparing Submissions", false),
    TOKEN_STRING_NORMALIZER("Normalizing token Sequence ", true);

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
