package de.jplag.options;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import de.jplag.JPlag;
import de.jplag.Language;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.frequency.FrequencyAnalysisOptions;
import de.jplag.merging.MergingOptions;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * This record is a collection of the options used to configure {@link JPlag}.
 * @param submissionFileOptions are the options used to define which files JPlag reads as submissions.
 * @param parsingOptions are the options used to configure parsing and comparing the submissions.
 * @param reportingOptions are the options used to configure how results are exported.
 * @param clusteringOptions are the options related to the grouping of similar comparisons.
 * @param mergingOptions are the options related to the subsequence match merging mechanism that opposed obfuscation.
 * @param frequencyAnalysisOptions are the options related to highlight extraction.
 */
@RecordBuilder()
public record JPlagOptions(SubmissionFileOptions submissionFileOptions, ParsingOptions parsingOptions, ReportingOptions reportingOptions,
        ClusteringOptions clusteringOptions, MergingOptions mergingOptions, FrequencyAnalysisOptions frequencyAnalysisOptions)
        implements JPlagOptionsBuilder.With {

    /** Default value for the error directory name. **/
    public static final String ERROR_FOLDER = "errors";

    /**
     * Constructs a {@link JPlagOptions} instance with minimal required parameters.
     * @param submissionFileOptions The options used to define which files JPlag reads as submissions
     * @param parsingOptions The options used to configure parsing and comparing the submissions
     */
    public JPlagOptions(SubmissionFileOptions submissionFileOptions, ParsingOptions parsingOptions) {
        this(submissionFileOptions, parsingOptions, new ReportingOptions(), new ClusteringOptions(), new MergingOptions(),
                new FrequencyAnalysisOptions());
    }

    /**
     * Constructs a {@link JPlagOptions} instance with minimal required parameters.
     * @param language The programming language for parsing submissions
     * @param submissionDirectories Directories containing new submissions to check
     * @param oldSubmissionDirectories Directories containing old submissions to check against
     * @deprecated non split up constructor
     */
    @Deprecated(since = "7.0.0", forRemoval = true)
    public JPlagOptions(Language language, Set<Path> submissionDirectories, Set<Path> oldSubmissionDirectories) {
        this(new SubmissionFileOptions(submissionDirectories, oldSubmissionDirectories, null), new ParsingOptions(language), new ReportingOptions(),
                new ClusteringOptions(), new MergingOptions(), new FrequencyAnalysisOptions());
    }

    /**
     * Constructs a fully specified {@link JPlagOptions} instance.
     * @param language Language used for parsing submissions
     * @param minimumTokenMatch Minimum number of matching tokens to consider a match
     * @param submissionDirectories Directories of new submissions to check
     * @param oldSubmissionDirectories Directories of old submissions to compare against
     * @param baseCodeSubmissionDirectory Directory containing the base code submissions
     * @param subdirectoryName Subdirectory within submissions to restrict scanning
     * @param fileSuffixes List of file suffixes to include in the comparison
     * @param exclusionFileName File name containing list of files to exclude
     * @param similarityMetric Metric to compute similarity threshold for comparisons
     * @param similarityThreshold Threshold below which comparisons are ignored
     * @param maximumNumberOfComparisons Maximum number of comparisons to show in the report
     * @param clusteringOptions Options for clustering algorithm
     * @param debugParser If true, stores unparsed submissions separately
     * @param mergingOptions Options related to subsequence merging to oppose obfuscation
     * @param normalize Enables additional normalization mechanisms (language-dependent)
     * @param analyzeComments Whether to extract comments from submissions
     * @param frequencyAnalysisOptions Options related to highlight extraction
     * @deprecated non split up constructor
     */
    @Deprecated(since = "7.0.0", forRemoval = true)
    public JPlagOptions(Language language, Integer minimumTokenMatch, Set<Path> submissionDirectories, Set<Path> oldSubmissionDirectories,
            Path baseCodeSubmissionDirectory, String subdirectoryName, List<String> fileSuffixes, String exclusionFileName,
            SimilarityMetric similarityMetric, double similarityThreshold, int maximumNumberOfComparisons, ClusteringOptions clusteringOptions,
            boolean debugParser, MergingOptions mergingOptions, boolean normalize, boolean analyzeComments,
            FrequencyAnalysisOptions frequencyAnalysisOptions) {
        this(new SubmissionFileOptions(submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionDirectory, subdirectoryName,
                exclusionFileName),
                new ParsingOptions(language, minimumTokenMatch, fileSuffixes, debugParser, normalize, analyzeComments, similarityMetric,
                        similarityThreshold),
                new ReportingOptions(maximumNumberOfComparisons), clusteringOptions, mergingOptions, frequencyAnalysisOptions);
    }

}
