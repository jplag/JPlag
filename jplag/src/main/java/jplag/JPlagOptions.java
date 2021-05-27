package jplag;

import static jplag.strategy.ComparisonMode.NORMAL;

import jplag.options.ClusterType;
import jplag.options.LanguageOption;
import jplag.options.SimilarityMetric;
import jplag.options.Verbosity;
import jplag.strategy.ComparisonMode;

public class JPlagOptions {

    /**
     * This is related to `storeMatches`.
     */
    public static final int MAX_RESULT_PAIRS = 1000;

    /**
     * Language used to parse the submissions.
     */
    private Language language;

    /**
     * Deprecated - use similarityThreshold instead! Maximum number of comparisons to store per run.
     */
    @Deprecated
    private int storeMatches = 30;

    /**
     * Deprecated - use similarityThreshold instead!
     * <p>
     * True, if `storeMatches` should be interpreted as a percentage threshold for the similarity of a comparison; false
     * otherwise;
     */
    @Deprecated
    private boolean storePercent = false;

    /**
     * TODO PB: Decide what to do with this.
     * <p>
     * Note: Previously, this option had two effects:
     * <ol>
     * <li>If this option was > 0, it told JPlag to use the 'special' comparison strategy</li>
     * <li>It specifies the number of submissions to compare each submission to during the 'special' comparison</li>
     * </ol>
     */
    private int numberOfSubmissionsToCompareTo = 0; // 0 = deactivated

    /**
     * Clustering option.
     */
    private ClusterType clusterType;

    /**
     * Determines which strategy to use for the comparison of submissions.
     */
    private ComparisonMode comparisonMode = NORMAL;

    /**
     * If true, submissions that cannot be parsed will be stored in a separate directory.
     */
    private boolean debugParser = false;

    /**
     * Array of file suffixes that should be included.
     */
    private String[] fileSuffixes;

    /**
     * Percentage value (must be between 0 and 100). Matches with a similarity below this threshold will be ignored. The
     * default value of 0 allows all matches to be stored.
     */
    private float similarityThreshold = 0;

    /**
     * TODO PB: Not happy with the name yet.
     */
    private SimilarityMetric similarityMetric = SimilarityMetric.AVG;

    /**
     * Tune the sensitivity of the comparison. A smaller <n> increases the sensitivity
     */
    private Integer minTokenMatch;

    /**
     * Name of the file that contains the names of files to exclude from comparison.
     */
    private String exclusionFileName;

    /**
     * Directory that contains all submissions.
     */
    private String rootDirName;

    /**
     * Name of the directory which contains the base code.
     */
    private String baseCodeSubmissionName;

    /**
     * Example: If the subdirectoryName is 'src', only the code inside submissionDir/src of each submission will be used for
     * comparison.
     */
    private String subdirectoryName;

    /**
     * Language to use when parsing the submissions.
     */
    private LanguageOption languageOption;

    /**
     * Level of output verbosity.
     */
    private Verbosity verbosity;

    /**
     * Constructor with required attributes.
     */
    public JPlagOptions(String rootDirName, LanguageOption languageOption) {
        this.rootDirName = rootDirName;
        this.languageOption = languageOption;
    }

    /**
     * After the selected language has been initialized, this method is called by JPlag to set default values for options
     * not set by the user.
     * @param language - initialized language instance
     */
    void setLanguageDefaults(Language language) {
        if (!this.hasMinTokenMatch()) {
            this.minTokenMatch = language.min_token_match();
        }

        if (!this.hasFileSuffixes()) {
            this.fileSuffixes = language.suffixes();
        }
    }

    public LanguageOption getLanguageOption() {
        return languageOption;
    }

    public Verbosity getVerbosity() {
        return verbosity;
    }

    public boolean hasBaseCode() {
        return this.baseCodeSubmissionName != null;
    }

    public boolean hasFileSuffixes() {
        return this.fileSuffixes != null && this.fileSuffixes.length > 0;
    }

    public boolean hasMinTokenMatch() {
        return this.minTokenMatch != null;
    }

    public ClusterType getClusterType() {
        return clusterType;
    }

    public ComparisonMode getComparisonMode() {
        return comparisonMode;
    }

    public String[] getFileSuffixes() {
        return fileSuffixes;
    }

    @Deprecated
    public int getStoreMatches() {
        return storeMatches;
    }

    @Deprecated
    public boolean isStorePercent() {
        return storePercent;
    }

    public Language getLanguage() {
        return language;
    }

    public Integer getMinTokenMatch() {
        return minTokenMatch;
    }

    public String getExclusionFileName() {
        return exclusionFileName;
    }

    public String getRootDirName() {
        return rootDirName;
    }

    public String getBaseCodeSubmissionName() {
        return baseCodeSubmissionName;
    }

    public String getSubdirectoryName() {
        return subdirectoryName;
    }

    public boolean isDebugParser() {
        return debugParser;
    }

    public int getNumberOfSubmissionsToCompareTo() {
        return numberOfSubmissionsToCompareTo;
    }

    public float getSimilarityThreshold() {
        return similarityThreshold;
    }

    public SimilarityMetric getSimilarityMetric() {
        return similarityMetric;
    }

    void setLanguage(Language language) {
        this.language = language;
    }

    public void setNumberOfSubmissionsToCompareTo(int numberOfSubmissionsToCompareTo) {
        this.numberOfSubmissionsToCompareTo = numberOfSubmissionsToCompareTo;
    }

    public void setClusterType(ClusterType clusterType) {
        this.clusterType = clusterType;
    }

    public void setComparisonMode(ComparisonMode comparisonMode) {
        this.comparisonMode = comparisonMode;
    }

    public void setDebugParser(boolean debugParser) {
        this.debugParser = debugParser;
    }

    public void setFileSuffixes(String[] fileSuffixes) {
        this.fileSuffixes = fileSuffixes;
    }

    @Deprecated
    public void setStoreMatches(int storeMatches) {
        this.storeMatches = storeMatches;
    }

    @Deprecated
    public void setStorePercent(boolean storePercent) {
        this.storePercent = storePercent;
    }

    public void setMinTokenMatch(Integer minTokenMatch) {
        this.minTokenMatch = minTokenMatch;
    }

    public void setExclusionFileName(String exclusionFileName) {
        this.exclusionFileName = exclusionFileName;
    }

    public void setRootDirName(String rootDirName) {
        this.rootDirName = rootDirName;
    }

    public void setBaseCodeSubmissionName(String baseCodeSubmissionName) {
        this.baseCodeSubmissionName = baseCodeSubmissionName;
    }

    public void setSubdirectoryName(String subdirectoryName) {
        this.subdirectoryName = subdirectoryName;
    }

    public void setLanguageOption(LanguageOption languageOption) {
        this.languageOption = languageOption;
    }

    public void setVerbosity(Verbosity verbosity) {
        this.verbosity = verbosity;
    }

    public void setSimilarityThreshold(float similarityThreshold) {
        if (similarityThreshold > 100) {
            this.similarityThreshold = 100;
        } else if (similarityThreshold < 0) {
            this.similarityThreshold = 0;
        } else {
            this.similarityThreshold = similarityThreshold;
        }
    }

    public void setSimilarityMetric(SimilarityMetric similarityMetric) {
        this.similarityMetric = similarityMetric;
    }
}
