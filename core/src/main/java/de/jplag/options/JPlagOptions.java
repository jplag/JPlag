package de.jplag.options;

import static de.jplag.options.Verbosity.LONG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

/**
 * This record defines the options to configure {@link JPlag}.
 * @param language Language to use when parsing the submissions.
 * @param minimumTokenMatch Tunes the comparison sensitivity by adjusting the minimum token required to be counted as
 * matching section. A smaller {@code <n>} increases the sensitivity but might lead to more false-positives.
 * @param submissionDirectories Directories with new submissions. These must be checked for plagiarism.
 * @param oldSubmissionDirectories Directories with old submissions to check against.
 * @param baseCodeSubmissionName Path name of the directory containing the base code.
 * @param subdirectoryName Example: If the subdirectoryName is 'src', only the code inside submissionDir/src of each
 * submission will be used for comparison.
 * @param fileSuffixes List of file suffixes that should be included.
 * @param exclusionFileName Name of the file that contains the names of files to exclude from comparison.
 * @param similarityMetric The similarity metric determines how the minimum similarity threshold required for a
 * comparison (of two submissions) is calculated. This affects which comparisons are stored and thus make it into the
 * result object.
 * @param similarityThreshold Similarity value (must be between 0 and 1). Comparisons (of submissions pairs) with a
 * similarity below this threshold will be ignored. The default value of 0 allows all matches to be stored. This affects
 * which comparisons are stored and thus make it into the result object. See also {@link #similarityMetric()}.
 * @param maximumNumberOfComparisons The maximum number of comparisons that will be shown in the generated report. If
 * set to {@link #SHOW_ALL_COMPARISONS} all comparisons will be shown.
 * @param clusteringOptions Clustering options
 * @param verbosity Level of output verbosity.
 * @param debugParser If true, submissions that cannot be parsed will be stored in a separate directory.
 */
public record JPlagOptions(Language language, Integer minimumTokenMatch, Set<File> submissionDirectories, Set<File> oldSubmissionDirectories,
        String baseCodeSubmissionName, String subdirectoryName, List<String> fileSuffixes, String exclusionFileName,
        SimilarityMetric similarityMetric, double similarityThreshold, int maximumNumberOfComparisons, ClusteringOptions clusteringOptions,
        Verbosity verbosity, boolean debugParser) {

    public static final double DEFAULT_SIMILARITY_THRESHOLD = 0;
    public static final int DEFAULT_SHOWN_COMPARISONS = 30;
    public static final int SHOW_ALL_COMPARISONS = 0;
    public static final SimilarityMetric DEFAULT_SIMILARITY_METRIC = SimilarityMetric.AVG;
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final Logger logger = LoggerFactory.getLogger(JPlag.class);

    public JPlagOptions(Language language, Set<File> submissionDirectories, Set<File> oldSubmissionDirectories) {
        this(language, null, submissionDirectories, oldSubmissionDirectories, null, null, null, null, DEFAULT_SIMILARITY_METRIC,
                DEFAULT_SIMILARITY_THRESHOLD, DEFAULT_SHOWN_COMPARISONS, new ClusteringOptions(), null, false);
    }

    public JPlagOptions(Language language, Integer minimumTokenMatch, Set<File> submissionDirectories, Set<File> oldSubmissionDirectories,
            String baseCodeSubmissionName, String subdirectoryName, List<String> fileSuffixes, String exclusionFileName,
            SimilarityMetric similarityMetric, double similarityThreshold, int maximumNumberOfComparisons, ClusteringOptions clusteringOptions,
            Verbosity verbosity, boolean debugParser) {
        this.language = language;
        this.debugParser = debugParser;
        this.fileSuffixes = fileSuffixes == null || fileSuffixes.isEmpty() ? null : Collections.unmodifiableList(fileSuffixes);
        this.similarityThreshold = normalizeSimilarityThreshold(similarityThreshold);
        this.maximumNumberOfComparisons = normalizeMaximumNumberOfComparisons(maximumNumberOfComparisons);
        this.similarityMetric = similarityMetric;
        this.minimumTokenMatch = normalizeMinimumTokenMatch(minimumTokenMatch);
        this.exclusionFileName = exclusionFileName;
        this.submissionDirectories = submissionDirectories == null ? null : Collections.unmodifiableSet(submissionDirectories);
        this.oldSubmissionDirectories = oldSubmissionDirectories == null ? null : Collections.unmodifiableSet(oldSubmissionDirectories);
        this.baseCodeSubmissionName = (baseCodeSubmissionName == null || baseCodeSubmissionName.isBlank()) ? null : baseCodeSubmissionName;
        this.subdirectoryName = subdirectoryName;
        this.verbosity = verbosity;
        this.clusteringOptions = clusteringOptions;
    }

    public JPlagOptions withLanguageOption(Language language) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withDebugParser(boolean debugParser) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withFileSuffixes(List<String> fileSuffixes) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withSimilarityThreshold(double similarityThreshold) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withMaximumNumberOfComparisons(int maximumNumberOfComparisons) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withSimilarityMetric(SimilarityMetric similarityMetric) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withMinimumTokenMatch(Integer minimumTokenMatch) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withExclusionFileName(String exclusionFileName) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withSubmissionDirectories(Set<File> submissionDirectories) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withOldSubmissionDirectories(Set<File> oldSubmissionDirectories) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withBaseCodeSubmissionName(String baseCodeSubmissionName) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withSubdirectoryName(String subdirectoryName) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withVerbosity(Verbosity verbosity) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public JPlagOptions withClusteringOptions(ClusteringOptions clusteringOptions) {
        return new JPlagOptions(language, minimumTokenMatch, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, fileSuffixes, exclusionFileName, similarityMetric, similarityThreshold, maximumNumberOfComparisons,
                clusteringOptions, verbosity, debugParser);
    }

    public boolean hasBaseCode() {
        return baseCodeSubmissionName != null;
    }

    public Set<String> excludedFiles() {
        return Optional.ofNullable(exclusionFileName()).map(this::readExclusionFile).orElse(Collections.emptySet());
    }

    @Override
    public List<String> fileSuffixes() {
        var language = language();
        if ((fileSuffixes == null || fileSuffixes.isEmpty()) && language != null)
            return Arrays.stream(language.suffixes()).toList();
        return fileSuffixes == null ? null : Collections.unmodifiableList(fileSuffixes);
    }

    /**
     * Path name of the directory containing the base code.<br>
     * For backwards compatibility it may also be a directory name inside the root directory. Condition for the latter is
     * <ul>
     * <li>Specified path does not exist.</li>
     * <li>Name has not have a separator character after trimming them from both ends (leaving at least a one-character
     * name).</li>
     * <li>A submission with the specified name exists in the root directory.</li>
     * </ul>
     * It's an error if a string has been provided, but it is neither an existing path nor does it fulfill all the
     * conditions of the compatibility fallback listed above.
     */
    @Override
    public String baseCodeSubmissionName() {
        return baseCodeSubmissionName;
    }

    @Override
    public Integer minimumTokenMatch() {
        var language = language();
        if (minimumTokenMatch == null && language != null)
            return language.minimumTokenMatch();
        return minimumTokenMatch;
    }

    private Set<String> readExclusionFile(final String exclusionFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(exclusionFileName, JPlagOptions.CHARSET))) {
            final var excludedFileNames = reader.lines().collect(Collectors.toSet());
            if (verbosity() == LONG && logger.isInfoEnabled()) {
                logger.info("Excluded files:\n{}", String.join("\n", excludedFileNames));
            }
            return excludedFileNames;
        } catch (IOException e) {
            logger.error("Could not read exclusion file: " + e.getMessage(), e);
            return Collections.emptySet();
        }
    }

    private static double normalizeSimilarityThreshold(double similarityThreshold) {
        if (similarityThreshold > 1) {
            logger.warn("Maximum threshold of 1 used instead of {}", similarityThreshold);
            return 1;
        } else if (similarityThreshold < 0) {
            logger.warn("Minimum threshold of 0 used instead of {}", similarityThreshold);
            return 0;
        } else {
            return similarityThreshold;
        }
    }

    private Integer normalizeMaximumNumberOfComparisons(Integer maximumNumberOfComparisons) {
        return Math.max(maximumNumberOfComparisons, SHOW_ALL_COMPARISONS);
    }

    private Integer normalizeMinimumTokenMatch(Integer minimumTokenMatch) {
        return (minimumTokenMatch != null && minimumTokenMatch < 1) ? Integer.valueOf(1) : minimumTokenMatch;
    }
}
