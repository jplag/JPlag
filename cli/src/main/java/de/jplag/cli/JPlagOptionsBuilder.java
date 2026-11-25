package de.jplag.cli;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.jplag.options.ParsingOptions;
import de.jplag.options.ReportingOptions;
import de.jplag.options.SubmissionFileOptions;
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

    private final CliInputHandler cliInputHandler;
    private final CliOptions cliOptions;

    /**
     * @param cliInputHandler The cli handler containing the parsed cli options.
     */
    public JPlagOptionsBuilder(CliInputHandler cliInputHandler) {
        this.cliInputHandler = cliInputHandler;
        this.cliOptions = this.cliInputHandler.getCliOptions();
    }

    /**
     * Builds the JPlag options.
     * @return The JPlag options.
     * @throws CliException If the input handler could properly parse everything.
     */
    public JPlagOptions buildOptions() throws CliException {
        return new JPlagOptions(
                getSubmissionFileOptions(),
                getParsingOptions(),
                getReportingOptions(),
                getClusteringOptions(),
                getMergingOptions(),
                getFrequencyAnalysisOptions()
        );
    }

    private SubmissionFileOptions getSubmissionFileOptions() {
        Set<File> submissionDirectories = new HashSet<>(List.of(this.cliOptions.rootDirectory));
        Set<File> oldSubmissionDirectories = Set.of(this.cliOptions.oldDirectories);
        submissionDirectories.addAll(List.of(this.cliOptions.newDirectories));
        String baseCodePath = this.cliOptions.baseCode;
        Path baseCodeDirectory = baseCodePath == null ? null : Path.of(baseCodePath);
        return new SubmissionFileOptions(
                mapCliSubmissionDirectoriesToPaths(submissionDirectories),
                mapCliSubmissionDirectoriesToPaths(oldSubmissionDirectories),
                baseCodeDirectory,
                this.cliOptions.advanced.subdirectory,
                this.cliOptions.advanced.exclusionFileName
        );
    }

    private ParsingOptions getParsingOptions() throws CliException {
        List<String> suffixes = List.of(this.cliOptions.advanced.suffixes);
        return new ParsingOptions(
                this.cliInputHandler.getSelectedLanguage(),
                this.cliOptions.minTokenMatch,
                suffixes,
                this.cliOptions.advanced.debug,
                this.cliOptions.normalize,
                this.cliOptions.advanced.analyzeComments,
                ParsingOptions.DEFAULT_SIMILARITY_METRIC,
                this.cliOptions.advanced.similarityThreshold
        );
    }

    private ReportingOptions getReportingOptions() {
        return new ReportingOptions(this.cliOptions.shownComparisons);
    }

    private Set<Path> mapCliSubmissionDirectoriesToPaths(Collection<File> submissionDirectory) {
        return submissionDirectory.stream().map(File::toPath).collect(Collectors.toSet());
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
