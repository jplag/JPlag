package de.jplag.cli.options;

import java.io.File;

import org.slf4j.event.Level;

import de.jplag.Language;
import de.jplag.clustering.ClusteringAlgorithm;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.clustering.algorithm.InterClusterSimilarity;
import de.jplag.highlightextraction.*;
import de.jplag.java.JavaLanguage;
import de.jplag.merging.MergingOptions;
import de.jplag.options.JPlagOptions;
import de.jplag.options.SimilarityMetric;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "jplag", description = "", usageHelpAutoWidth = true, abbreviateSynopsis = true)
public class CliOptions implements Runnable {
    public static final Language defaultLanguage = new JavaLanguage();

    @Parameters(paramLabel = "root-dirs", description = "Root-directory with submissions to check for plagiarism. If mode is set to VIEW, this parameter can be used to specify a report file to open. In that case only a single file may be specified.", split = ",")
    public File[] rootDirectory = new File[0];

    @Option(names = {"--new", "-new"}, split = ",", description = "Root-directories with submissions to check for plagiarism (same as root).")
    public File[] newDirectories = new File[0];

    @Option(names = {"--old", "-old"}, split = ",", description = "Root-directories with prior submissions to compare against.")
    public File[] oldDirectories = new File[0];

    @Option(names = {"--language",
            "-l"}, arity = "1", converter = LanguageConverter.class, completionCandidates = LanguageCandidates.class, description = "Select the language of the submissions (default: ${DEFAULT-VALUE}). See subcommands below.")
    public Language language = defaultLanguage;

    @Option(names = {"-bc", "--bc", "--base-code"}, description = "Path to the base code directory (common framework used in all submissions).")
    public String baseCode;

    @Option(names = {"-t",
            "--min-tokens"}, description = "Tunes the comparison sensitivity by adjusting the minimum token required to be counted as a matching section. A smaller value increases the sensitivity but might lead to more false-positives.")
    public Integer minTokenMatch = null;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help text", hidden = true)
    public boolean help;

    @Option(names = {"-n",
            "--shown-comparisons"}, description = "The maximum number of comparisons that will be shown in the generated report, if set to -1 all comparisons will be shown (default: ${DEFAULT-VALUE})")
    public int shownComparisons = JPlagOptions.DEFAULT_SHOWN_COMPARISONS;

    @Option(names = {"-r",
            "--result-file"}, description = "Name of the file in which the comparison results will be stored (default: ${DEFAULT-VALUE}). Missing .jplag endings will be automatically added.")
    public String resultFile = "results";

    @Option(names = {"-M",
            "--mode"}, description = "The mode of JPlag. One of: ${COMPLETION-CANDIDATES} (default: ${DEFAULT_VALUE}). If VIEW is chosen, you can optionally specify a path to an existing report.")
    public JPlagMode mode = JPlagMode.AUTO;

    @Option(names = {"--normalize"}, description = "Activate the normalization of tokens. Supported for languages: Java, C++.")
    public boolean normalize = false;

    @ArgGroup(heading = "%nAdvanced%n", exclusive = false)
    public Advanced advanced = new Advanced();

    @ArgGroup(validate = false, heading = "%nClustering%n")
    public Clustering clustering = new Clustering();

    @ArgGroup(validate = false, heading = "%nSubsequence Match Merging%n")
    public Merging merging = new Merging();

    @ArgGroup(validate = false, heading = "%nFrequency Analysis%n")
    public FrequencyAnalysis frequencyAnalysis = new FrequencyAnalysis();

    /**
     * Empty run method, so picocli prints help automatically
     */
    @Override
    public void run() {
        // Empty run method, so picocli prints help automatically
    }

    public static class Advanced {
        @Option(names = {"-d", "--debug"}, description = "Store on-parsable files in error folder.")
        public boolean debug;

        @Option(names = {"-s", "--subdirectory"}, description = "Look in directories <root-dir>/*/<dir> for programs.")
        public String subdirectory;

        @Option(names = {"-p", "--suffixes"}, split = ",", description = "comma-separated list of all filename suffixes that are included.")
        public String[] suffixes = new String[0];

        @Option(names = {"-x",
                "--exclusion-file"}, description = "All files named in this file will be ignored in the comparison (line-separated list).")
        public String exclusionFileName;

        @Option(names = {"-m",
                "--similarity-threshold"}, description = "Comparison similarity threshold [0.0-1.0]: All comparisons above this threshold will "
                        + "be saved (default: ${DEFAULT-VALUE}).")
        public double similarityThreshold = JPlagOptions.DEFAULT_SIMILARITY_THRESHOLD;

        @Option(names = {"-P", "--port"}, description = "The port used for the internal report viewer (default: ${DEFAULT-VALUE}).")
        public int port = 1996;

        @Option(names = "--csv-export", description = "Export pairwise similarity values as a CSV file.")
        public boolean csvExport = false;

        @Option(names = "--overwrite", description = "Existing result files will be overwritten.")
        public boolean overwrite = false;

        @Option(names = "--log-level", description = "Set the log level for the cli.")
        public Level logLevel = Level.INFO;
    }

    public static class Clustering {
        @Option(names = {"--cluster-skip"}, description = "Skips the cluster calculation.")
        public boolean disable;

        @ArgGroup
        public ClusteringEnabled enabled = new ClusteringEnabled();

        public static class ClusteringEnabled {
            @Option(names = {"--cluster-alg",
                    "--cluster-algorithm"}, description = "Specifies the clustering algorithm. Available algorithms: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
            public ClusteringAlgorithm algorithm = new ClusteringOptions().algorithm();

            @Option(names = {
                    "--cluster-metric"}, description = "The similarity metric used for clustering. Available metrics: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).")
            public SimilarityMetric metric = new ClusteringOptions().similarityMetric();
        }
    }

    public static class Merging {
        @Option(names = {"--match-merging"}, description = "Enables merging of neighboring matches to counteract obfuscation attempts.")
        public boolean enabled = MergingOptions.DEFAULT_ENABLED;

        @Option(names = {
                "--neighbor-length"}, description = "Minimal length of neighboring matches to be merged (between 1 and minTokenMatch, default: ${DEFAULT-VALUE}).")
        public int minimumNeighborLength = MergingOptions.DEFAULT_NEIGHBOR_LENGTH;

        @Option(names = {
                "--gap-size"}, description = "Maximal gap between neighboring matches to be merged (between 1 and minTokenMatch, default: ${DEFAULT-VALUE}).")
        public int maximumGapSize = MergingOptions.DEFAULT_GAP_SIZE;

        @Option(names = {
                "--required-merges"}, description = "Minimal required merges for the merging to be applied (between 1 and 50, default: ${DEFAULT-VALUE}).")
        public int minimumRequiredMerges = MergingOptions.DEFAULT_REQUIRED_MERGES;

    }

    @Option(names = {"--cluster-spectral-bandwidth"}, hidden = true)
    public double clusterSpectralBandwidth = new ClusteringOptions().spectralKernelBandwidth();

    @Option(names = {"--cluster-spectral-noise"}, hidden = true)
    public double clusterSpectralNoise = new ClusteringOptions().spectralGaussianProcessVariance();

    @Option(names = {"--cluster-spectral-min-runs"}, hidden = true)
    public int clusterSpectralMinRuns = new ClusteringOptions().spectralMinRuns();

    @Option(names = {"--cluster-spectral-max-runs"}, hidden = true)
    public int clusterSpectralMaxRuns = new ClusteringOptions().spectralMaxRuns();

    @Option(names = {"--cluster-spectral-kmeans-iterations", "--cluster-spectral-kmeans-interations"}, hidden = true)
    public int clusterSpectralKMeansIterations = new ClusteringOptions().spectralMaxKMeansIterationPerRun();

    @Option(names = {"--cluster-agglomerative-threshold"}, hidden = true)
    public double clusterAgglomerativeThreshold = new ClusteringOptions().agglomerativeThreshold();

    @Option(names = {"--cluster-agglomerative-inter-cluster-similarity"}, hidden = true)
    public InterClusterSimilarity clusterAgglomerativeInterClusterSimilarity = new ClusteringOptions().agglomerativeInterClusterSimilarity();

    @Option(names = {"--cluster-pp-none"}, hidden = true)
    public boolean clusterPreprocessingNone;

    @Option(names = {"--cluster-pp-cdf"}, hidden = true)
    public boolean clusterPreprocessingCdf;

    @Option(names = {"--cluster-pp-percentile"}, hidden = true)
    public double clusterPreprocessingPercentile;

    @Option(names = {"--cluster-pp-threshold"}, hidden = true)
    public double clusterPreprocessingThreshold;

    public static class FrequencyAnalysis {
        @Option(names = {
                "--frequency-strategy"}, description = "strategy for frequency Analysis, Options: completeMatches, containedMatches, subMatches, windowOfMatches")
        public FrequencyStrategies frequencyStrategy = FrequencyStrategies.COMPLETE_MATCHES;

        @Option(names = {
                "--frequency-min-value"}, description = "max of min match length that will be compared and this value, is min size of considered submatches")
        public int frequencyStrategyMinValue = 1;

        @Option(names = {"--weighting-strategy"}, description = "strategy for frequency Weighting, Options: PROPORTIONAL, LINEAR, QUADRATIC, SIGMOID")
        public WeightingStrategies weightingStrategy = WeightingStrategies.SIGMOID;

        @Option(names = {
                "--weighting-factor"}, description = "factor on how strong the weighting will be considered, scale factor for max stretch of a token sequence")
        public double weightingStrategyWeightingFactor = 0.25;

    }
}
