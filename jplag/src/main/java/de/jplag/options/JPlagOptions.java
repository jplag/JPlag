package de.jplag.options;

import static de.jplag.strategy.ComparisonMode.NORMAL;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import de.jplag.Language;
import de.jplag.clustering.Algorithms;
import de.jplag.clustering.Preprocessors;
import de.jplag.clustering.algorithm.TopDownHierarchicalClustering;
import de.jplag.strategy.ComparisonMode;

public class JPlagOptions {

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
     * @see JPlagOptions.similarityMetric
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
     * @see JPlagOptions.similarityThreshold
     */
    private SimilarityMetric similarityMetric = SimilarityMetric.AVG;

    /**
     * Tunes the comparison sensitivity by adjusting the minimum token required to be counted as matching section. A smaller
     * <n> increases the sensitivity but might lead to more false-positves.
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
     * Directory that contains all submissions.
     */
    private String rootDirectoryName;

    /**
     * Path name of the directory containing the base code.
     * <p>
     * For backwards compatibility it may also be a directory name inside the root directory.
     * Condition for the latter is
     * <ul><li>Specified path does not exist.</li>
     *     <li>Name has not have a separator character after trimming them from both ends (leaving at least a one-character name).</li>
     *     <li>A submission with the specified name exists in the root directory.</li>
     * </ul>
     * It's an error if a string has been provided but it is neither an existing path nor does it fulfill all the
     * conditions of the compatibility fallback listed above.
     * </p>
     */
    private Optional<String> baseCodeSubmissionName = Optional.empty();

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
     * This similarity metric is used for clustering.
     */
    private SimilarityMetric clusteringSimilarityMetric = SimilarityMetric.MAX;

    /**
     * The kernel bandwidth for the matern kernel used in the gaussian process for the automatic search for the number of clusters in spectral clustering.
     * Affects the runtime and results of the spectral clustering.
     */
    private float clusteringSpectralKernelBandwidth = 20.f;

    /**
     * This is the assumed level of noise in the evaluation results of a spectral clustering.
     * Acts as normalization parameter for the gaussian process.
     * The default setting works well with similarity scores in the range between zero and one.
     * Affects the runtime and results of the spectral clustering.
     */
    private float clusteringSpectralGPVariance = 0.05f * 0.05f;

    /**
     * The minimal number of runs of the spectral clustering algorithm.
     * These runs will use predefined numbers of clusters and will not use the bayesian optimization to determine the number of clusters. 
     */
    private int clusteringSpectralMinRuns = 5;

    /**
     * Maximal number of runs of the spectral clustering algorithm.
     * The bayesian optimization may be stopped before, when no more maxima of the acquisition-function are found.
     */
    private int clusteringSpectralMaxRuns = 50;

    /**
     * Maximum number of iterations of the kMeans clustering per run of the spectral clustering algorithm.
     */
    private int clusteringSpectralMaxKMeansIterationPerRun = 200;

    /**
     * Remove clusters that are evaluated as especially bad from the final set of clusters.
     */
    private boolean clusteringPruneBadClusters = true;

    /**
     * Preprocessing for the similarity values before clustering.
     * This is mandatory for spectral clustering and optional for agglomerative clustering.
     */
    private Preprocessors clusteringPreprocessor = Preprocessors.CDF;

    /**
     * Sets up to which similarity the threshold preprocessor zeroes out the similarities.
     */
    private float clusteringPreprocessorThreshold = 0.2f;

    /**
     * Sets up to which percentile of the percentile preprocessor zeroes out the similarities.
     */
    private float clusteringPreprocessorPercentile = 0.5f;

    /**
     * Agglomerative clustering will merge clusters that have a similarity higher than this threshold.
     */
    private float clusteringAgglomerativeThreshold = 0.2f;

    /**
     * Similarity measure between clusters in agglomerative clustering. 
     */
    private TopDownHierarchicalClustering.InterClusterSimilarity clusteringAgglomerativeInterClusterSimilarity = TopDownHierarchicalClustering.InterClusterSimilarity.AVERAGE;

    /**
     * The clustering algorithm to use.
     */
    private Algorithms clusteringAlgorithm = Algorithms.SPECTRAL;

    /**
     * If clustering should be performed.
     */
    private boolean clusteringDoClustering = true;


    /**
     * Constructor with required attributes.
     */
    public JPlagOptions(String rootDirectoryName, LanguageOption languageOption) {
        this.rootDirectoryName = rootDirectoryName;
        this.languageOption = languageOption;
    }

    public Optional<String> getBaseCodeSubmissionName() {
        return baseCodeSubmissionName;
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

    public String getRootDirectoryName() {
        return rootDirectoryName;
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
        return this.baseCodeSubmissionName.isPresent();
    }

    public boolean isDebugParser() {
        return debugParser;
    }

    public void setBaseCodeSubmissionName(String baseCodeSubmissionName) {
        if (baseCodeSubmissionName == null || baseCodeSubmissionName.isEmpty()) {
            this.baseCodeSubmissionName = Optional.empty();
        } else {
            this.baseCodeSubmissionName = Optional.of(baseCodeSubmissionName);
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


    public SimilarityMetric getClusteringSimilarityMetric() {
        return clusteringSimilarityMetric;
    }

    public float getClusteringSpectralKernelBandwidth() {
        return clusteringSpectralKernelBandwidth;
    }

    public float getClusteringSpectralGPVariance() {
        return clusteringSpectralGPVariance;
    }

    public int getClusteringSpectralMinRuns() {
        return clusteringSpectralMinRuns;
    }

    public int getClusteringSpectralMaxRuns() {
        return clusteringSpectralMaxRuns;
    }

    public int getClusteringSpectralMaxKMeansIterationPerRun() {
        return clusteringSpectralMaxKMeansIterationPerRun;
    }

    public boolean isClusteringPruneBadClusters() {
        return clusteringPruneBadClusters;
    }

    public float getClusteringAgglomerativeThreshold() {
        return clusteringAgglomerativeThreshold;
    }

    public Preprocessors getClusteringPreprocessor() {
        return clusteringPreprocessor;
    }

    public boolean isClustering() {
        return clusteringDoClustering;
    }

    public Algorithms getClusteringAlgorithm() {
        return clusteringAlgorithm;
    }

    public TopDownHierarchicalClustering.InterClusterSimilarity getClusteringAgglomerativeInterClusterSimilarity() {
        return clusteringAgglomerativeInterClusterSimilarity;
    }

    public float getClusteringPreprocessorThreshold() {
        return clusteringPreprocessorThreshold;
    }

    public float getClusteringPreprocessorPercentile() {
        return clusteringPreprocessorPercentile;
    }
    
    public void setMinimumTokenMatch(Integer minimumTokenMatch) {
        if (minimumTokenMatch != null && minimumTokenMatch < 1) {
            this.minimumTokenMatch = 1;
        } else {
            this.minimumTokenMatch = minimumTokenMatch;
        }
    }

    public void setRootDirectoryName(String rootDirectoryName) {
        this.rootDirectoryName = rootDirectoryName;
    }

    public void setSimilarityMetric(SimilarityMetric similarityMetric) {
        this.similarityMetric = similarityMetric;
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

    public void setSubdirectoryName(String subdirectoryName) {
        // Trim problematic file separators.
        this.subdirectoryName = (subdirectoryName == null) ? null : subdirectoryName.replace(File.separator, "");
    }

    public void setVerbosity(Verbosity verbosity) {
        this.verbosity = verbosity;
    }

    private boolean hasFileSuffixes() {
        return fileSuffixes != null && fileSuffixes.length > 0;
    }

    private boolean hasMinimumTokenMatch() {
        return minimumTokenMatch != null;
    }

    public void setClusteringSimilarityMetric(SimilarityMetric clusteringSimilarityMetric) {
        this.clusteringSimilarityMetric = clusteringSimilarityMetric;
    }

    public void setClusteringSpectralKernelBandwidth(float clusteringSpectralKernelBandwidth) {
        this.clusteringSpectralKernelBandwidth = clusteringSpectralKernelBandwidth;
    }

    public void setClusteringSpectralGPVariance(float clusteringSpectralGPVariance) {
        this.clusteringSpectralGPVariance = clusteringSpectralGPVariance;
    }

    public void setClusteringSpectralMinRuns(int clusteringSpectralMinRuns) {
        this.clusteringSpectralMinRuns = clusteringSpectralMinRuns;
    }

    public void setClusteringSpectralMaxRuns(int clusteringSpectralMaxRuns) {
        this.clusteringSpectralMaxRuns = clusteringSpectralMaxRuns;
    }

    public void setClusteringSpectralMaxKMeansIterationPerRun(int clusteringSpectralMaxKMeansIterationPerRun) {
        this.clusteringSpectralMaxKMeansIterationPerRun = clusteringSpectralMaxKMeansIterationPerRun;
    }

    public void setClusteringPruneBadClusters(boolean clusteringPruneBadClusters) {
        this.clusteringPruneBadClusters = clusteringPruneBadClusters;
    }

    public void setClusteringPreprocessor(Preprocessors clusteringPreprocessor) {
        this.clusteringPreprocessor = clusteringPreprocessor;
    }

    public void setClusteringAgglomerativeThreshold(float clusteringAgglomerativeThreshold) {
        this.clusteringAgglomerativeThreshold = clusteringAgglomerativeThreshold;
    }

    public void setClusteringAgglomerativeInterClusterSimilarity(
            TopDownHierarchicalClustering.InterClusterSimilarity clusteringAgglomerativeInterClusterSimilarity) {
        this.clusteringAgglomerativeInterClusterSimilarity = clusteringAgglomerativeInterClusterSimilarity;
    }

    public void setClusteringAlgorithm(Algorithms clusteringAlgorithm) {
        this.clusteringAlgorithm = clusteringAlgorithm;
    }

    public void setClustering(boolean clustering) {
        this.clusteringDoClustering = clustering;
    }

    public void setClusteringPreprocessorThreshold(float clusteringPreprocessorThreshold) {
        this.clusteringPreprocessorThreshold = clusteringPreprocessorThreshold;
    }

    public void setClusteringPreprocessorPercentile(float clusteringPreprocessorPercentile) {
        this.clusteringPreprocessorPercentile = clusteringPreprocessorPercentile;
    }
}
