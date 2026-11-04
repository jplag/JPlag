package de.jplag.options;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlag;
import de.jplag.Language;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.exceptions.BasecodeException;
import de.jplag.frequency.FrequencyAnalysisOptions;
import de.jplag.merging.MergingOptions;
import de.jplag.util.FileUtils;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * This record defines the options to configure {@link JPlag}.
 * @param language Language to use when parsing the submissions.
 * @param minimumTokenMatch Tunes the comparison sensitivity by adjusting the minimum token required to be counted as
 * matching section. A smaller {@code <n>} increases the sensitivity but might lead to more false-positives.
 * @param similarityMetric The similarity metric determines how the minimum similarity threshold required for a
 * comparison (of two submissions) is calculated. This affects which comparisons are stored and thus make it into the
 * result object.
 * @param similarityThreshold Similarity value (must be between 0 and 1). Comparisons (of submissions pairs) with a
 * similarity below this threshold will be ignored. The default value of 0 allows all matches to be stored. This affects
 * which comparisons are stored and thus make it into the result object. See also {@link #similarityMetric()}.
 * @param maximumNumberOfComparisons The maximum number of comparisons that will be shown in the generated report. If
 * set to {@link #SHOW_ALL_COMPARISONS} all comparisons will be shown.
 * @param clusteringOptions Clustering options
 * @param debugParser If true, submissions that cannot be parsed will be stored in a separate directory.
 * @param mergingOptions are the options related to the subsequence match merging mechanism that opposed obfuscation.
 * @param normalize enables additional normalization mechanisms. Only supported by some language modules.
 * @param analyzeComments If true, comments will be extracted from the submissions.
 * @param frequencyAnalysisOptions are the options related to highlight extraction.
 */
@RecordBuilder()
public record JPlagOptions(SubmissionFileOptions submissionFileOptions, ParsingOptions parsingOptions, ReportingOptions reportingOptions,
        ClusteringOptions clusteringOptions,
        MergingOptions mergingOptions,
        FrequencyAnalysisOptions frequencyAnalysisOptions) implements JPlagOptionsBuilder.With {

    /** Default value for the error directory name. **/
    public static final String ERROR_FOLDER = "errors";

    /**
     * Constructs a {@link JPlagOptions} instance with minimal required parameters.
     * @param language The programming language for parsing submissions
     * @param submissionDirectories Directories containing new submissions to check
     * @param oldSubmissionDirectories Directories containing old submissions to check against
     */
    public JPlagOptions(Language language, Set<Path> submissionDirectories, Set<Path> oldSubmissionDirectories) {
        this(new SubmissionFileOptions(submissionDirectories, oldSubmissionDirectories, null),
                new ParsingOptions(language), new ReportingOptions(),  new ClusteringOptions(), new MergingOptions(), new FrequencyAnalysisOptions());
    }

    /**
     * @deprecated
     * @since 7.0.0
     *
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
     */
    public JPlagOptions(Language language, Integer minimumTokenMatch, Set<Path> submissionDirectories, Set<Path> oldSubmissionDirectories,
            Path baseCodeSubmissionDirectory, String subdirectoryName, List<String> fileSuffixes, String exclusionFileName,
            SimilarityMetric similarityMetric, double similarityThreshold, int maximumNumberOfComparisons, ClusteringOptions clusteringOptions,
            boolean debugParser, MergingOptions mergingOptions, boolean normalize, boolean analyzeComments,
            FrequencyAnalysisOptions frequencyAnalysisOptions) {
        this(new SubmissionFileOptions(submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionDirectory, subdirectoryName, fileSuffixes, exclusionFileName), new ParsingOptions(language, minimumTokenMatch, debugParser, normalize, analyzeComments, similarityMetric, similarityThreshold), new ReportingOptions(maximumNumberOfComparisons), clusteringOptions, mergingOptions, frequencyAnalysisOptions);
    }

    public List<String> fileSuffixes() {
        var language = parsingOptions().language();
        if ((submissionFileOptions.fileSuffixes() == null || submissionFileOptions.fileSuffixes().isEmpty()) && language != null) {
            return language.fileExtensions();
        }
        return submissionFileOptions.fileSuffixes() == null ? null : Collections.unmodifiableList(submissionFileOptions.fileSuffixes());
    }

}
