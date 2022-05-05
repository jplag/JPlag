package de.jplag.options;

import static de.jplag.strategy.ComparisonMode.NORMAL;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Language;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.strategy.ComparisonMode;

public class JPlagOptions {

    private static final Logger logger = LoggerFactory.getLogger("JPlag");

    public static final ComparisonMode DEFAULT_COMPARISON_MODE = NORMAL;
    public static final float DEFAULT_SIMILARITY_THRESHOLD = 0;
    public static final int DEFAULT_SHOWN_COMPARISONS = 30;

    public static final Charset CHARSET = StandardCharsets.UTF_8;

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
     * Percentage value (must be between 0 and 100). Comparisons (of submissions pairs) with a similarity below this
     * threshold will be ignored. The default value of 0 allows all matches to be stored. This affects which comparisons are
     * stored and thus make it into the result object.
     * @see JPlagOptions#similarityMetric
     */
    private float similarityThreshold = DEFAULT_SIMILARITY_THRESHOLD;

    /**
     * The maximum number of comparisons that will be shown in the generated report. If set to -1 all comparisons will be
     * shown.
     */
    private int maximumNumberOfComparisons = DEFAULT_SHOWN_COMPARISONS;

    /**
     * The similarity metric determines how the minimum similarity threshold required for a comparison (of two submissions)
     * is calculated. This affects which comparisons are stored and thus make it into the result object.
     * @see JPlagOptions#similarityThreshold
     */
    private SimilarityMetric similarityMetric = SimilarityMetric.AVG;

    /**
     * Tunes the comparison sensitivity by adjusting the minimum token required to be counted as matching section. A smaller
     * <n> increases the sensitivity but might lead to more false-positives.
     */
    private Integer minimumTokenMatch;

    /**
     * Name of the file that contains the names of files to exclude from comparison.
     */
    private String exclusionFileName;

    /**
     * Names of the excluded files.
     */
    private Set<String> excludedFiles = Collections.emptySet();

    /**
     * Directories with new submissions. These must be checked for plagiarism.
     */
    private List<String> submissionDirectories;

    /**
     * Directories with old submissions to check against.
     */
    private List<String> oldSubmissionDirectories;

    /**
     * Path name of the directory containing the base code.
     * <p>
     * For backwards compatibility it may also be a directory name inside the root directory. Condition for the latter is
     * <ul>
     * <li>Specified path does not exist.</li>
     * <li>Name has not have a separator character after trimming them from both ends (leaving at least a one-character
     * name).</li>
     * <li>A submission with the specified name exists in the root directory.</li>
     * </ul>
     * It's an error if a string has been provided but it is neither an existing path nor does it fulfill all the conditions
     * of the compatibility fallback listed above.
     * </p>
     */
    private String baseCodeSubmissionName = null;

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
     * Clustering options
     */
    private ClusteringOptions clusteringOptions = new ClusteringOptions.Builder().build();

    /**
     * Constructor with required attributes.
     */
    public JPlagOptions(List<String> submissionDirectories, List<String> oldSubmissionDirectories, LanguageOption languageOption) {
        this.submissionDirectories = submissionDirectories;
        this.oldSubmissionDirectories = oldSubmissionDirectories;
        this.languageOption = languageOption;
    }

    public Optional<String> getBaseCodeSubmissionName() {
        return Optional.ofNullable(baseCodeSubmissionName);
    }

    public ComparisonMode getComparisonMode() {
        return comparisonMode;
    }

    public Set<String> getExcludedFiles() {
        return excludedFiles;
    }

    public String getExclusionFileName() {
        return exclusionFileName;
    }

    public String[] getFileSuffixes() {
        return fileSuffixes;
    }

    public Language getLanguage() {
        return language;
    }

    public LanguageOption getLanguageOption() {
        return languageOption;
    }

    public int getMaximumNumberOfComparisons() {
        return this.maximumNumberOfComparisons;
    }

    public Integer getMinimumTokenMatch() {
        return minimumTokenMatch;
    }

    public List<String> getSubmissionDirectories() {
        return submissionDirectories;
    }

    public List<String> getOldSubmissionDirectories() {
        return oldSubmissionDirectories;
    }

    public SimilarityMetric getSimilarityMetric() {
        return similarityMetric;
    }

    public float getSimilarityThreshold() {
        return similarityThreshold;
    }

    public String getSubdirectoryName() {
        return subdirectoryName;
    }

    public Verbosity getVerbosity() {
        return verbosity;
    }

    public boolean hasBaseCode() {
        return this.baseCodeSubmissionName != null;
    }

    public boolean isDebugParser() {
        return debugParser;
    }

    public ClusteringOptions getClusteringOptions() {
        return this.clusteringOptions;
    }

    public void setBaseCodeSubmissionName(String baseCodeSubmissionName) {
        if (baseCodeSubmissionName == null || baseCodeSubmissionName.isEmpty()) {
            this.baseCodeSubmissionName = null;
        } else {
            this.baseCodeSubmissionName = baseCodeSubmissionName;
        }
    }

    public void setComparisonMode(ComparisonMode comparisonMode) {
        this.comparisonMode = comparisonMode;
    }

    public void setDebugParser(boolean debugParser) {
        this.debugParser = debugParser;
    }

    public void setExcludedFiles(Set<String> excludedFiles) {
        this.excludedFiles = excludedFiles;
    }

    public void setExclusionFileName(String exclusionFileName) {
        this.exclusionFileName = exclusionFileName;
    }

    public void setFileSuffixes(String[] fileSuffixes) {
        this.fileSuffixes = fileSuffixes;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * After the selected language has been initialized, this method is called by JPlag to set default values for options
     * not set by the user.
     * @param language - initialized language instance
     */
    public void setLanguageDefaults(Language language) {
        if (!hasMinimumTokenMatch()) {
            setMinimumTokenMatch(language.minimumTokenMatch());
        }

        if (!hasFileSuffixes()) {
            fileSuffixes = language.suffixes();
        }
    }

    public void setLanguageOption(LanguageOption languageOption) {
        this.languageOption = languageOption;
    }

    public void setMaximumNumberOfComparisons(int maximumNumberOfComparisons) {
        this.maximumNumberOfComparisons = Math.max(maximumNumberOfComparisons, -1);
    }

    public void setMinimumTokenMatch(Integer minimumTokenMatch) {
        if (minimumTokenMatch != null && minimumTokenMatch < 1) {
            this.minimumTokenMatch = 1;
        } else {
            this.minimumTokenMatch = minimumTokenMatch;
        }
    }

    public void setSubmissionDirectories(List<String> submissionDirectories) {
        this.submissionDirectories = submissionDirectories;
    }

    public void setOldSubmissionDirectories(List<String> oldSubmissionDirectories) {
        this.oldSubmissionDirectories = oldSubmissionDirectories;
    }

    public void setSimilarityMetric(SimilarityMetric similarityMetric) {
        this.similarityMetric = similarityMetric;
    }

    public void setSimilarityThreshold(float similarityThreshold) {
        if (similarityThreshold > 100) {
            logger.warn("Maximum threshold of 100 used instead of " + similarityThreshold);
            this.similarityThreshold = 100;
        } else if (similarityThreshold < 0) {
            logger.warn("Minimum threshold of 0 used instead of " + similarityThreshold);
            this.similarityThreshold = 0;
        } else {
            this.similarityThreshold = similarityThreshold;
        }
    }

    public void setSubdirectoryName(String subdirectoryName) {
        // Trim problematic file separators.
        this.subdirectoryName = (subdirectoryName == null) ? null : subdirectoryName.replace(File.separator, "");
    }

    public void setVerbosity(Verbosity verbosity) {
        this.verbosity = verbosity;
    }

    public void setClusteringOptions(ClusteringOptions clusteringOptions) {
        this.clusteringOptions = clusteringOptions;
    }

    private boolean hasFileSuffixes() {
        return fileSuffixes != null && fileSuffixes.length > 0;
    }

    private boolean hasMinimumTokenMatch() {
        return minimumTokenMatch != null;
    }

}
