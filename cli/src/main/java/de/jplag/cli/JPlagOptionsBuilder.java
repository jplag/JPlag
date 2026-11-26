package de.jplag.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.cli.options.CliOptions;
import de.jplag.cli.picocli.CliInputHandler;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.clustering.Preprocessing;
import de.jplag.frequency.FrequencyAnalysisOptions;
import de.jplag.merging.MergingOptions;
import de.jplag.options.JPlagOptions;

/**
 * Handles the building of JPlag options from the cli options.
 */
public class JPlagOptionsBuilder {
    private static final Logger logger = LoggerFactory.getLogger(JPlagOptionsBuilder.class);
    private static final String ERROR_OPEN_ZIP = "Cannot open zip file as submission directory: %s";

    private final CliInputHandler cliInputHandler;
    private final CliOptions cliOptions;

    private final List<AutoCloseable> closeableCollector;

    /**
     * @param cliInputHandler The cli handler containing the parsed cli options.
     * @param closeableCollector A collector for closeables that have to be handled after the cli run
     */
    public JPlagOptionsBuilder(CliInputHandler cliInputHandler, List<AutoCloseable> closeableCollector) {
        this.cliInputHandler = cliInputHandler;
        this.cliOptions = this.cliInputHandler.getCliOptions();
        this.closeableCollector = closeableCollector;
    }

    /**
     * Builds the JPlag options.
     * @return The JPlag options.
     * @throws CliException If the input handler could properly parse everything.
     */
    public JPlagOptions buildOptions() throws CliException {
        Set<File> submissionDirectories = new HashSet<>(List.of(this.cliOptions.rootDirectory));
        Set<File> oldSubmissionDirectories = Set.of(this.cliOptions.oldDirectories);
        List<String> suffixes = List.of(this.cliOptions.advanced.suffixes);
        submissionDirectories.addAll(List.of(this.cliOptions.newDirectories));

        JPlagOptions jPlagOptions = initializeJPlagOptions(mapCliSubmissionDirectoriesToPaths(submissionDirectories),
                mapCliSubmissionDirectoriesToPaths(oldSubmissionDirectories), suffixes);

        String baseCodePath = this.cliOptions.baseCode;
        Path baseCodeDirectory = baseCodePath == null ? null : Path.of(baseCodePath);
        if (baseCodeDirectory == null || Files.exists(baseCodeDirectory)) {
            return jPlagOptions.withBaseCodeSubmissionDirectory(baseCodeDirectory);
        }
        logger.error("Using legacy partial base code API. Please migrate to new full path base code API.");
        return jPlagOptions.withBaseCodeSubmissionDirectory(baseCodeDirectory);
    }

    private Set<Path> mapCliSubmissionDirectoriesToPaths(Collection<File> submissionDirectory) throws CliException {
        Set<Path> result = new HashSet<>();
        for (File dir : submissionDirectory) {
            if (dir.isFile() && dir.getName().endsWith(".zip")) {
                try {
                    FileSystem fileSystem = FileSystems.newFileSystem(dir.toPath());
                    closeableCollector.add(fileSystem);
                    Path root = fileSystem.getRootDirectories().iterator().next();
                    try (Stream<Path> childStream = Files.list(root)) {
                        List<Path> children = childStream.toList();
                        if (children.size() == 1 && Files.isDirectory(children.getFirst())) {
                            root = children.getFirst();
                        }
                        result.add(root);
                    }
                } catch (IOException _) {
                    throw new CliException(String.format(ERROR_OPEN_ZIP, dir.getAbsolutePath()));
                }
            } else {
                result.add(dir.toPath());
            }
        }
        return result;
    }

    private JPlagOptions initializeJPlagOptions(Set<Path> submissionDirectories, Set<Path> oldSubmissionDirectories, List<String> suffixes)
            throws CliException {
        ClusteringOptions clusteringOptions = getClusteringOptions();
        MergingOptions mergingOptions = getMergingOptions();
        FrequencyAnalysisOptions frequencyAnalysisOptions = getFrequencyAnalysisOptions();

        return new JPlagOptions(this.cliInputHandler.getSelectedLanguage(), this.cliOptions.minTokenMatch, submissionDirectories,
                oldSubmissionDirectories, null, this.cliOptions.advanced.subdirectory, suffixes, this.cliOptions.advanced.exclusionFileName,
                JPlagOptions.DEFAULT_SIMILARITY_METRIC, this.cliOptions.advanced.similarityThreshold, this.cliOptions.shownComparisons,
                clusteringOptions, this.cliOptions.advanced.debug, mergingOptions, this.cliOptions.normalize,
                this.cliOptions.advanced.analyzeComments, frequencyAnalysisOptions);
    }

    private ClusteringOptions getClusteringOptions() {
        ClusteringOptions clusteringOptions = new ClusteringOptions().withEnabled(!this.cliOptions.clustering.disable)
                .withAlgorithm(this.cliOptions.clustering.enabled.algorithm).withSimilarityMetric(this.cliOptions.clustering.enabled.metric)
                .withSpectralKernelBandwidth(this.cliOptions.clusterSpectralBandwidth)
                .withSpectralGaussianProcessVariance(this.cliOptions.clusterSpectralNoise).withSpectralMinRuns(this.cliOptions.clusterSpectralMinRuns)
                .withSpectralMaxRuns(this.cliOptions.clusterSpectralMaxRuns)
                .withSpectralMaxKMeansIterationPerRun(this.cliOptions.clusterSpectralKMeansIterations)
                .withAgglomerativeThreshold(this.cliOptions.clusterAgglomerativeThreshold)
                .withAgglomerativeInterClusterSimilarity(this.cliOptions.clusterAgglomerativeInterClusterSimilarity);

        if (this.cliOptions.clusterPreprocessingNone) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.NONE);
        }

        if (this.cliOptions.clusterPreprocessingCdf) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION);
        }

        if (this.cliOptions.clusterPreprocessingPercentile != 0) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.PERCENTILE)
                    .withPreprocessorPercentile(this.cliOptions.clusterPreprocessingPercentile);
        }

        if (this.cliOptions.clusterPreprocessingThreshold != 0) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.THRESHOLD)
                    .withPreprocessorThreshold(this.cliOptions.clusterPreprocessingThreshold);
        }

        return clusteringOptions;
    }

    private MergingOptions getMergingOptions() {
        return new MergingOptions(this.cliOptions.merging.enabled, this.cliOptions.merging.minimumNeighborLength,
                this.cliOptions.merging.maximumGapSize, this.cliOptions.merging.minimumRequiredMerges);
    }

    private FrequencyAnalysisOptions getFrequencyAnalysisOptions() {
        CliOptions.FrequencyAnalysis frequencyOptions = this.cliOptions.highlightExtraction;
        return new FrequencyAnalysisOptions().withEnabled(frequencyOptions.enabled)
                .withFrequencyStrategy(frequencyOptions.frequencyStrategy.create(frequencyOptions.minimumSubsequenceLength))
                .withFrequencyStrategyMinValue(frequencyOptions.minimumSubsequenceLength)
                .withWeightingFunction(frequencyOptions.weightingFunction.create()).withWeightingFactor(frequencyOptions.weightingFactor);
    }
}
