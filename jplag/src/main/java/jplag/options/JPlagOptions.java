package jplag.options;

import static jplag.strategy.ComparisonMode.NORMAL;

import jplag.Language;
import jplag.strategy.ComparisonMode;

public class JPlagOptions {

    public static final ComparisonMode DEFAULT_COMPARISON_MODE = NORMAL;
    public static final int DEFAULT_SIMILARITY_THRESHOLD = 0;
    public static final int DEFAULT_STORED_MATCHES = 30;

    /**
     * Language used to parse the submissions.
     */
    private Language language;

    /**
     * Determines which strategy to use for the comparison of submissions.
     */
    private ComparisonMode comparisonMode = DEFAULT_COMPARISON_MODE;

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
    private float similarityThreshold = DEFAULT_SIMILARITY_THRESHOLD;

    /**
     * The maximum number of matches that will be saved. This does affect the generated report as well as the internally
     * saved comparisons. If set to -1 all matches will be saved.
     */
    private int maxNumberOfMatches = DEFAULT_STORED_MATCHES;

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
    public void setLanguageDefaults(Language language) {
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

    private boolean hasFileSuffixes() {
        return this.fileSuffixes != null && this.fileSuffixes.length > 0;
    }

    private boolean hasMinTokenMatch() {
        return this.minTokenMatch != null;
    }
    
    public ComparisonMode getComparisonMode() {
        return comparisonMode;
    }

    public String[] getFileSuffixes() {
        return fileSuffixes;
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

    public float getSimilarityThreshold() {
        return similarityThreshold;
    }

    public int getMaxNumberOfMatches() {
        return this.maxNumberOfMatches;
    }

    public SimilarityMetric getSimilarityMetric() {
        return similarityMetric;
    }

    public void setLanguage(Language language) {
        this.language = language;
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
            System.out.println("Maximum threshold of 100 used instead of " + similarityThreshold);
            this.similarityThreshold = 100;
        } else if (similarityThreshold < 0) {
            System.out.println("Minimum threshold of 0 used instead of " + similarityThreshold);
            this.similarityThreshold = 0;
        } else {
            this.similarityThreshold = similarityThreshold;
        }
    }

    public void setMaxNumberOfMatches(int maxNumberOfMatches) {
        if (maxNumberOfMatches < -1) {
            this.maxNumberOfMatches = -1;
        } else {
            this.maxNumberOfMatches = maxNumberOfMatches;
        }
    }

    public void setSimilarityMetric(SimilarityMetric similarityMetric) {
        this.similarityMetric = similarityMetric;
    }
}
