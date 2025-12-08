package de.jplag.cli.options;

import java.io.File;
import java.nio.charset.Charset;

import org.slf4j.event.Level;

import de.jplag.Language;
import de.jplag.clustering.ClusteringAlgorithm;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.clustering.algorithm.InterClusterSimilarity;
import de.jplag.highlightextraction.FrequencyAnalysisOptions;
import de.jplag.highlightextraction.strategy.FrequencyStrategySelector;
import de.jplag.java.JavaLanguage;
import de.jplag.merging.MergingOptions;
import de.jplag.options.JPlagOptions;
import de.jplag.options.SimilarityMetric;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * CLI options for the JPlag command-line interface. Uses picocli annotations for arguments and options.
 */
@CommandLine.Command(name = "jplag", description = "", usageHelpAutoWidth = true, abbreviateSynopsis = true)
public class CliOptions implements Runnable {

    /** Default language (Java). */
    public static final Language defaultLanguage = new JavaLanguage();

    /** Root directories with submissions to check or a report file if in VIEW mode. */
    @Parameters(paramLabel = "root-dirs", description = "Root-directory with submissions to check for plagiarism. If mode is set to VIEW, this parameter can be used to specify a report file to open. In that case only a single file may be specified.", split = ",")
    public File[] rootDirectory = new File[0];

    /** New submission directories to check for plagiarism. */
    @Option(names = {"--new", "-new"}, split = ",", description = "Root-directories with submissions to check for plagiarism (same as root).")
    public File[] newDirectories = new File[0];

    /** Old submission directories to compare against. */
    @Option(names = {"--old", "-old"}, split = ",", description = "Root-directories with prior submissions to compare against.")
    public File[] oldDirectories = new File[0];

    /** Language of submissions (default: Java). */
    @Option(names = {"--language",
            "-l"}, arity = "1", converter = LanguageConverter.class, completionCandidates = LanguageCandidates.class, description = "Select the language of the submissions (default: ${DEFAULT-VALUE}). See subcommands below.")
    public Language language = defaultLanguage;

    /** Path to base code directory (common framework for all submissions). */
    @Option(names = {"-bc", "--bc", "--base-code"}, description = "Path to the base code directory (common framework used in all submissions).")
    public String baseCode;

    /** Minimum tokens to count as matching (affects sensitivity). */
    @Option(names = {"-t",
            "--min-tokens"}, description = "Tunes the comparison sensitivity by adjusting the minimum token required to be counted as a matching section. A smaller value increases the sensitivity but might lead to more false-positives.")
    public Integer minTokenMatch = null;

    /** Show help and exit. */
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help text", hidden = true)
    public boolean help;

    /** Maximum comparisons shown in the report (-1 means all). */
    @Option(names = {"-n",
            "--shown-comparisons"}, description = "The maximum number of comparisons that will be shown in the generated report, if set to -1 all comparisons will be shown (default: ${DEFAULT-VALUE})")
    public int shownComparisons = JPlagOptions.DEFAULT_SHOWN_COMPARISONS;

    /** File to store comparison results (adds .jplag if missing). */
    @Option(names = {"-r",
            "--result-file"}, description = "Name of the file in which the comparison results will be stored (default: ${DEFAULT-VALUE}). Missing .jplag extension will be automatically added.")
    public String resultFile = "results";

    /** JPlag mode: AUTO, VIEW, etc. */
    @Option(names = {"-M",
            "--mode"}, description = "The mode of JPlag. One of: ${COMPLETION-CANDIDATES} (default: ${DEFAULT_VALUE}). If VIEW is chosen, you can optionally specify a path to an existing report.")
    public JPlagMode mode = JPlagMode.AUTO;

    /** Enable token normalization (Java, C++). */
    @Option(names = {"--normalize"}, description = "Activate the normalization of tokens. Supported for languages: Java, C++.")
    public boolean normalize = false;

    /** Advanced options group. */
    @ArgGroup(heading = "%nAdvanced%n", exclusive = false)
    public Advanced advanced = new Advanced();

    /** Clustering options group. */
    @ArgGroup(validate = false, heading = "%nClustering%n")
    public Clustering clustering = new Clustering();

    /** Subsequence merging options group. */
    @ArgGroup(validate = false, heading = "%nSubsequence Match Merging%n")
    public Merging merging = new Merging();

    /** Frequency Analysis options group. */
    @ArgGroup(validate = false, heading = "%nFrequency Analysis%n")
    public FrequencyAnalysis highlightExtraction = new FrequencyAnalysis();

    /**
     * Empty run method to enable automatic help printing by picocli.
     */
    @Override
    public void run() {
        // empty: picocli handles help output
    }

    /** Advanced CLI options. */
    public static class Advanced {
        /** Store unparsable files in error folder. */
        @Option(names = {"-d", "--debug"}, description = "Store on-parsable files in error folder.")
        public boolean debug;

        /** Look in root-dir/subdirectory for programs. */
        @Option(names = {"-s", "--subdirectory"}, description = "Look in directories <root-dir>/*/<dir> for programs.")
        public String subdirectory;

        /** Comma-separated filename suffixes to include. */
        @Option(names = {"-p", "--suffixes"}, split = ",", description = "comma-separated list of all filename suffixes that are included.")
        public String[] suffixes = new String[0];

        /** File listing files to exclude (line-separated). */
        @Option(names = {"-x",
                "--exclusion-file"}, description = "All files named in this file will be ignored in the comparison (line-separated list).")
        public String exclusionFileName;

        /** Similarity threshold to save comparisons [0.0-1.0]. */
        @Option(names = {"-m",
                "--similarity-threshold"}, description = "Comparison similarity threshold [0.0-1.0]: All comparisons above this threshold will be saved (default: ${DEFAULT-VALUE}).")
        public double similarityThreshold = JPlagOptions.DEFAULT_SIMILARITY_THRESHOLD;

        /** Port for internal report viewer. */
        @Option(names = {"-P", "--port"}, description = "The port used for the internal report viewer (default: ${DEFAULT-VALUE}).")
        public int port = 1996;

        /** Export similarity as CSV. */
        @Option(names = "--csv-export", description = "Export pairwise similarity values as a CSV file.")
        public boolean csvExport = false;

        /** Overwrite existing result files. */
        @Option(names = "--overwrite", description = "Existing result files will be overwritten.")
        public boolean overwrite = false;

        /** CLI log level. */
        @Option(names = "--log-level", description = "Set the log level for the cli.")
        public Level logLevel = Level.INFO;

        /** Analyze similarity of comments (hidden). */
        @Option(names = "--comments", description = "Analyze similarity of comments. Increases the similarity of submissions if similar comments are found, but never decreases it.", hidden = true)
        public boolean analyzeComments = false;

        /** Override charset for submissions. */
        @Option(names = "--encoding", description = "Specifies the charset of the submissions. This disables the automatic charset detection", completionCandidates = CharsetCandidates.class, converter = CharsetConverter.class)
        public Charset submissionCharsetOverride;

        /** Skip check for new version (hidden). */
        @Option(names = "--skip-version-check", description = "Skip fetching latest version information from the API.", hidden = true)
        public boolean skipVersionCheck = false;
    }

    /** Clustering options. */
    public static class Clustering {
        /** Skip cluster calculation. */
        @Option(names = {"--cluster-skip"}, description = "Skips the cluster calculation.")
        public boolean disable;

        /** Clustering enabled options. */
        @ArgGroup
        public ClusteringEnabled enabled = new ClusteringEnabled();

        /** Enabled clustering settings. */
        public static class ClusteringEnabled {
            /** Clustering algorithm to use. */
            @Option(names = {"--cluster-alg",
                    "--cluster-algorithm"}, description = "Specifies the clustering algorithm. Available algorithms: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
            public ClusteringAlgorithm algorithm = new ClusteringOptions().algorithm();

            /** Similarity metric used for clustering. */
            @Option(names = {
                    "--cluster-metric"}, description = "The similarity metric used for clustering. Available metrics: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
            public SimilarityMetric metric = new ClusteringOptions().similarityMetric();
        }
    }

    /** Highlight extraction options. */
    public static class FrequencyAnalysis {

        /**
         * Enables frequency-based highlighting of matches. Supports the detection of rare and unique matches in contrast to
         * common code, where matches have weak relevance.
         */
        @Option(names = {"--frequency"}, description = "Enables analysis and highlighting of rare matches.")
        public boolean enabled;

        /** Frequency Determination strategy. */
        @Option(names = {
                "--analysis-strategy"}, description = "Specifies the strategy for frequency analysis, one of: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
        public FrequencyStrategySelector frequencyStrategy = FrequencyStrategySelector.DEFAULT_FREQUENCY_STRATEGY_SELECTOR;

        /** Minimum subsequence length in frequency analysis. */
        @Option(names = {
                "--min-subsequence-length"}, description = "Minimum length of submatches to consider for the strategies CONTAINED_MATCHES, SUBMATCHES/length of windows to consider for the MATCH_WINDOWS strategy (default: ${DEFAULT-VALUE})", hidden = true)
        public int minimumSubsequenceLength = FrequencyAnalysisOptions.DEFAULT_MINIMUM_SUBSEQUENCE_LENGTH;

        /** Weighting function to combine with frequency Determination strategy. */
        @Option(names = {
                "--weighting"}, description = "The function for frequency-based match weighting, one of: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
        public WeightingFunctionSelector weightingFunction = WeightingFunctionSelector.DEFAULT_WEIGHTING_FUNCTION;

        /** How strong the weighting maximal influences a match length with up to double the length. */
        @Option(names = {
                "--weighting-factor"}, description = "Controls the influence of the frequency-based match weighting (between 0 and 1, default: ${DEFAULT-VALUE}).", hidden = true)
        public double weightingFactor = FrequencyAnalysisOptions.DEFAULT_WEIGHTING_FACTOR;

    }

    /**
     * Options for merging neighboring matches in the token sequence. Useful for reducing false negatives in cases of mild
     * obfuscation.
     */
    public static class Merging {

        /**
         * Enables merging of neighboring token matches. Helps detect similarity in submissions where code has been slightly
         * shuffled or obfuscated.
         */
        @Option(names = {"--match-merging"}, description = "Enables merging of neighboring matches to counteract obfuscation attempts.")
        public boolean enabled = MergingOptions.DEFAULT_ENABLED;

        /**
         * Minimum token length of neighboring matches that can be considered for merging. Should be between 1 and
         * minTokenMatch.
         */
        @Option(names = {
                "--neighbor-length"}, description = "Minimal length of neighboring matches to be merged (between 1 and minTokenMatch, default: ${DEFAULT-VALUE}).")
        public int minimumNeighborLength = MergingOptions.DEFAULT_NEIGHBOR_LENGTH;

        /**
         * Maximum allowed gap size (in tokens) between neighboring matches to be eligible for merging.
         */
        @Option(names = {
                "--gap-size"}, description = "Maximal gap between neighboring matches to be merged (between 1 and minTokenMatch, default: ${DEFAULT-VALUE}).")
        public int maximumGapSize = MergingOptions.DEFAULT_GAP_SIZE;

        /**
         * Minimum number of merges required before merging logic is applied. Helps avoid merging that is not statistically
         * meaningful.
         */
        @Option(names = {
                "--required-merges"}, description = "Minimal required merges for the merging to be applied (between 1 and 50, default: ${DEFAULT-VALUE}).")
        public int minimumRequiredMerges = MergingOptions.DEFAULT_REQUIRED_MERGES;
    }

    /** Bandwidth for spectral clustering kernel (hidden). */
    @Option(names = {"--cluster-spectral-bandwidth"}, hidden = true)
    public double clusterSpectralBandwidth = new ClusteringOptions().spectralKernelBandwidth();

    /** Gaussian process noise for spectral clustering (hidden). */
    @Option(names = {"--cluster-spectral-noise"}, hidden = true)
    public double clusterSpectralNoise = new ClusteringOptions().spectralGaussianProcessVariance();

    /** Minimum number of spectral clustering runs to keep (hidden). */
    @Option(names = {"--cluster-spectral-min-runs"}, hidden = true)
    public int clusterSpectralMinRuns = new ClusteringOptions().spectralMinRuns();

    /** Maximum number of spectral clustering runs (hidden). */
    @Option(names = {"--cluster-spectral-max-runs"}, hidden = true)
    public int clusterSpectralMaxRuns = new ClusteringOptions().spectralMaxRuns();

    /** Maximum KMeans iterations per spectral clustering run (hidden). */
    @Option(names = {"--cluster-spectral-kmeans-iterations", "--cluster-spectral-kmeans-interations"}, hidden = true)
    public int clusterSpectralKMeansIterations = new ClusteringOptions().spectralMaxKMeansIterationPerRun();

    /** Similarity threshold for agglomerative clustering (hidden). */
    @Option(names = {"--cluster-agglomerative-threshold"}, hidden = true)
    public double clusterAgglomerativeThreshold = new ClusteringOptions().agglomerativeThreshold();

    /** Similarity function for agglomerative clustering (hidden). */
    @Option(names = {"--cluster-agglomerative-inter-cluster-similarity"}, hidden = true)
    public InterClusterSimilarity clusterAgglomerativeInterClusterSimilarity = new ClusteringOptions().agglomerativeInterClusterSimilarity();

    /** Disable preprocessing before clustering (hidden). */
    @Option(names = {"--cluster-pp-none"}, hidden = true)
    public boolean clusterPreprocessingNone;

    /** Enable CDF preprocessing before clustering (hidden). */
    @Option(names = {"--cluster-pp-cdf"}, hidden = true)
    public boolean clusterPreprocessingCdf;

    /** Percentile used in preprocessing to cut off low-similarity pairs (hidden). */
    @Option(names = {"--cluster-pp-percentile"}, hidden = true)
    public double clusterPreprocessingPercentile;

    /** Absolute threshold used in preprocessing to cut off low-similarity pairs (hidden). */
    @Option(names = {"--cluster-pp-threshold"}, hidden = true)
    public double clusterPreprocessingThreshold;

}
