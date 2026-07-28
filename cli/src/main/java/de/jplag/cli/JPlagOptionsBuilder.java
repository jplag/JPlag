package de.jplag.cli;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import de.jplag.FilePathUtil;
import de.jplag.exceptions.RootDirectoryException;
import de.jplag.inputs.FileSystemSingleSubmissionDirectory;
import de.jplag.inputs.FileSystemSubmissionDirectory;
import de.jplag.inputs.SubmissionDirectory;
import de.jplag.inputs.SubmissionIdentifier;
import de.jplag.inputs.ZipFileSubmissionDirectory;
import de.jplag.util.FileUtils;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.cli.options.CliOptions;
import de.jplag.cli.picocli.CliInputHandler;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.clustering.Preprocessing;
import de.jplag.highlightextraction.FrequencyAnalysisOptions;
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
        Set<SubmissionDirectory> submissionDirectories = buildSubmissionDirectories(cliOptions.rootDirectory);
        Set<SubmissionDirectory> oldSubmissionDirectories = buildSubmissionDirectories(cliOptions.oldDirectories);
        submissionDirectories.addAll(buildSubmissionDirectories(this.cliOptions.newDirectories));
        submissionDirectories.addAll(buildSubmissionDirectories(cliInputHandler.getSubcommandSubmissionDirectories().toArray(new String[0])));
        List<String> suffixes = List.of(this.cliOptions.advanced.suffixes);

        Set<SubmissionDirectory> all = new HashSet<>();
        all.addAll(submissionDirectories);
        all.addAll(oldSubmissionDirectories);
        de.jplag.inputs.SubmissionIdentifier baseCode = findBaseCodeName(all, oldSubmissionDirectories::add);

        JPlagOptions jPlagOptions = initializeJPlagOptions(submissionDirectories, oldSubmissionDirectories, suffixes);
        if(baseCode != null) {
            return jPlagOptions.withBaseCodeSubmission(baseCode);
        } else {
            return jPlagOptions;
        }
    }

    public SubmissionIdentifier findBaseCodeName(Set<SubmissionDirectory> allSubmissionDirectories, Consumer<SubmissionDirectory> addSubmissionDirectory) throws CliException {
        if (cliOptions.baseCodeName != null) {
            return new SubmissionIdentifier(cliOptions.baseCodeName);
        }

        if(cliOptions.baseCode != null) {
            File baseCodeFile = new File(cliOptions.baseCode);
            if(!baseCodeFile.exists()) {
                throw new CliException("Base code directory does not exist");
            }
            Optional<SubmissionDirectory> first = allSubmissionDirectories.stream().filter(submissionDirectory -> submissionDirectory.contains(baseCodeFile)).findFirst();
            if(first.isPresent()) {
                return new SubmissionIdentifier(first.get().name(), baseCodeFile.getName());
            } else {
                FileSystemSingleSubmissionDirectory dir = new FileSystemSingleSubmissionDirectory(baseCodeFile, "baseCode");
                addSubmissionDirectory.accept(dir);
                return dir.getSubmissionIdentifer();
            }
        }

        return null;
    }

    private Set<SubmissionDirectory> buildSubmissionDirectories(String[] identifiers) throws CliException {
        Set<SubmissionDirectory> result = new HashSet<>();
        for (String identifier : identifiers) {
            result.add(buildSubmissionDirectory(identifier));
        }
        return result;
    }

    private SubmissionDirectory buildSubmissionDirectory(String identifier) throws CliException, RootDirectoryException {
        String path = identifier;
        String name = null;

        if(identifier.matches(".*\\[[a-zA-Z0-9]+\\]")) { //has a name component at the end
            int index = identifier.lastIndexOf('[');
            name = identifier.substring(index + 1, identifier.length() - 1);
            path = identifier.substring(0, index);
        }

        File submissionRoot = new File(path);
        if (name == null) {
            name = FilePathUtil.forceRelativePath(submissionRoot.toPath()).toString();
        }

        if(submissionRoot.isDirectory()) {
            return new FileSystemSubmissionDirectory(submissionRoot, name);
        }

        if(submissionRoot.isFile()) {
            try {
                if(FileUtils.isZip(submissionRoot)) {
                    return new ZipFileSubmissionDirectory(submissionRoot, name);
                }
            } catch (IOException e) {
                throw new CliException("Root directory " + identifier + " is not a readable file");
            }
        }

        throw new CliException("Root directory " + identifier + " is neither a zip nor a directory");
    }

    private JPlagOptions initializeJPlagOptions(Set<SubmissionDirectory> submissionDirectories, Set<SubmissionDirectory> oldSubmissionDirectories, List<String> suffixes)
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
                .withAnalysisStrategy(frequencyOptions.frequencyStrategy.create(frequencyOptions.minimumSubsequenceLength))
                .withWeightingFunction(frequencyOptions.weightingFunction.create()).withWeightingFactor(frequencyOptions.weightingFactor);
    }
}
