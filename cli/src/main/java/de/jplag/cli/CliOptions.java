package de.jplag.cli;

import java.io.File;

import de.jplag.Language;
import de.jplag.clustering.ClusteringAlgorithm;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.clustering.algorithm.InterClusterSimilarity;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;
import de.jplag.options.SimilarityMetric;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "jplag", description = "", usageHelpAutoWidth = true, abbreviateSynopsis = true)
public class CliOptions implements Runnable {
    public static final Language defaultLanguage = new JavaLanguage();

    @Parameters(paramLabel = "root-dirs", description = "Root-directory with submissions to check for plagiarism%n", split = ",")
    public File[] rootDirectory = new File[0];

    @Option(names = {"--new",
            "-new"}, split = ",", description = "Root-directory with submissions to check for plagiarism (same as the root directory)%n")
    public File[] newDirectories = new File[0];

    @Option(names = {"--old", "-old"}, split = ",", description = "Root-directory with prior submissions to compare against%n")
    public File[] oldDirectories = new File[0];

    @Option(names = {"--language",
            "-l"}, arity = "1", converter = LanguageConverter.class, completionCandidates = LanguageCandidates.class, description = "Select the language to parse the submissions (default: java). The language names are the same as the subcommands.%n")
    public Language language = defaultLanguage;

    @Option(names = {"-bc", "--bc",
            "--base-code"}, description = "Path of  the  directory  containing  the  base  code  (common  framework  used  in  all submissions)%n")
    public String baseCode;

    @Option(names = {"-t", "--min-tokens"}, description = "Tunes the comparison sensitivity by adjusting the  minimum token required to be counted "
            + "as a matching section. A smaller <n>  increases  the sensitivity but might lead to more " + "false-positives%n")
    public Integer minTokenMatch = null;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help and exit")
    public boolean help;

    @Option(names = {"-n",
            "--shown-comparisons"}, description = "The maximum number of comparisons that will  be  shown  in the generated report, if set "
                    + "to -1 all comparisons will be shown (default: 100)%n")
    public int shownComparisons = JPlagOptions.DEFAULT_SHOWN_COMPARISONS;

    @Option(names = {"-r",
            "--result-directory"}, description = "Name of the directory in which the comparison results will be stored (default: result)%n")
    public String resultFolder = "results";

    @ArgGroup(heading = "Advanced%n", exclusive = false)
    public Advanced advanced = new Advanced();

    @ArgGroup(validate = false, heading = "Clustering%n")
    public Clustering clustering = new Clustering();

    @ArgGroup(validate = false, heading = "Merging of neighboring matches to increase the similarity of concealed plagiarism:%n")
    public Merging merging = new Merging();

    /**
     * Empty run method, so picocli prints help automatically
     */
    @Override
    public void run() {
        // Empty run method, so picocli prints help automatically
    }

    public static class Advanced {
        @Option(names = {"-d", "--debug"}, description = "Debug parser. Non-parsable files will be stored (default: false)%n")
        public boolean debug;

        @Option(names = {"-s", "--subdirectory"}, description = "Look in directories <root-dir>/*/<dir> for programs%n")
        public String subdirectory;

        @Option(names = {"-p", "--suffixes"}, split = ",", description = "comma-separated list of all filename suffixes that are included%n")
        public String[] suffixes = new String[0];

        @Option(names = {"-x",
                "--exclusion-file"}, description = "All files named in this file will be ignored in the comparison (line-separated list)%n")
        public String exclusionFileName;

        @Option(names = {"-m",
                "--similarity-threshold"}, description = "Comparison similarity threshold [0.0-1.0]:  All  comparisons  above this threshold will "
                        + "be saved (default: 0.0)%n")
        public double similarityThreshold = JPlagOptions.DEFAULT_SIMILARITY_THRESHOLD;

        @Option(names = {"--normalize"}, description = "Activate the normalization of tokens. Only allowed if the language supports it.")
        public boolean normalize = false;
    }

    public static class Clustering {
        @Option(names = {"--cluster-skip"}, description = "Skips the clustering (default: false)%n")
        public boolean disable;

        @ArgGroup
        public ClusteringEnabled enabled = new ClusteringEnabled();

        public static class ClusteringEnabled {
            @Option(names = {"--cluster-alg",
                    "--cluster-algorithm"}, description = "Which clustering algorithm to use. Agglomerative  merges similar submissions bottom up. "
                            + "Spectral clustering is  combined  with  Bayesian  Optimization  to  execute the k-Means "
                            + "clustering  algorithm  multiple   times,   hopefully   finding   a   \"good\"  clustering "
                            + "automatically. (default: spectral)%n")
            public ClusteringAlgorithm algorithm = new ClusteringOptions().algorithm();

            @Option(names = {
                    "--cluster-metric"}, description = "The metric used for clustering. AVG  is  intersection  over  union, MAX can expose some "
                            + "attempts of obfuscation. (default: MAX)%n")
            public SimilarityMetric metric = new ClusteringOptions().similarityMetric();
        }
    }

    public static class Merging {
        @Option(names = {"--match-merging"}, description = "Enables match merging (default: false)%n")
        public boolean enabled;

        @Option(names = {"--neighbor-length"}, description = "Defines how short a match can be, to be considered (default: 2)%n")
        public int minimumNeighborLength;

        @Option(names = {"--gap-size"}, description = "Defines how many token there can be between two neighboring matches (default: 6)%n")
        public int maximumGapSize;

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
}
