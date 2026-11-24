package de.jplag.options;

/**
 * Options used to configure what JPlag exports
 * @param maximumNumberOfComparisons The maximum number of comparisons that will be shown in the generated report. If
 * set to {@link #SHOW_ALL_COMPARISONS} all comparisons will be shown.
 */
public record ReportingOptions(int maximumNumberOfComparisons) {

    /** Default value for the shown comparisons in the report. **/
    public static final int DEFAULT_SHOWN_COMPARISONS = 2500;
    /** Constant that denotes that all comparisons are shown in the report viewer. **/
    public static final int SHOW_ALL_COMPARISONS = 0;

    public ReportingOptions(int maximumNumberOfComparisons) {
        this.maximumNumberOfComparisons = normalizeMaximumNumberOfComparisons(maximumNumberOfComparisons);
    }

    public ReportingOptions() {
        this(DEFAULT_SHOWN_COMPARISONS);
    }

    private static Integer normalizeMaximumNumberOfComparisons(Integer maximumNumberOfComparisons) {
        return Math.max(maximumNumberOfComparisons, SHOW_ALL_COMPARISONS);
    }
}
