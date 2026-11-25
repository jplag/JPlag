package de.jplag.options;

import de.jplag.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Options used to parse and compare submissions
 *
 * @param language Language to use when parsing the submissions.
 * @param minimumTokenMatch Tunes the comparison sensitivity by adjusting the minimum token required to be counted as
 * matching section. A smaller {@code <n>} increases the sensitivity but might lead to more false-positives.
 * @param debugParser If true, submissions that cannot be parsed will be stored in a separate directory.
 * @param normalize enables additional normalization mechanisms. Only supported by some language modules.
 * @param analyzeComments If true, comments will be extracted from the submissions.
 * @param similarityMetric The similarity metric determines how the minimum similarity threshold required for a
 * comparison (of two submissions) is calculated. This affects which comparisons are stored and thus make it into the
 * result object.
 * @param similarityThreshold Similarity value (must be between 0 and 1). Comparisons (of submissions pairs) with a
 * similarity below this threshold will be ignored. The default value of 0 allows all matches to be stored. This affects
 * which comparisons are stored and thus make it into the result object. See also {@link #similarityMetric()}.
 */
public record ParsingOptions(Language language, Integer minimumTokenMatch, boolean debugParser, boolean normalize, boolean analyzeComments, SimilarityMetric similarityMetric, double similarityThreshold) {


    /** Default value for the similarity metric. **/
    public static final SimilarityMetric DEFAULT_SIMILARITY_METRIC = SimilarityMetric.AVG;
    /** Default value for the similarity threshold. **/
    public static final double DEFAULT_SIMILARITY_THRESHOLD = 0;

    private static final Logger logger = LoggerFactory.getLogger(ParsingOptions.class);

    /**
     * Constructs a fully specified {@link ParsingOptions} instance.
     * @param language Language used for parsing submissions
     * @param minimumTokenMatch Minimum number of matching tokens to consider a match
     * @param similarityMetric Metric to compute similarity threshold for comparisons
     * @param similarityThreshold Threshold below which comparisons are ignored
     * @param debugParser If true, stores unparsed submissions separately
     * @param normalize Enables additional normalization mechanisms (language-dependent)
     * @param analyzeComments Whether to extract comments from submissions
     */
    public ParsingOptions(Language language, Integer minimumTokenMatch, boolean debugParser, boolean normalize, boolean analyzeComments,SimilarityMetric similarityMetric, double similarityThreshold) {
        this.language = language;
        this.minimumTokenMatch = normalizeMinimumTokenMatch(minimumTokenMatch);
        this.debugParser = debugParser;
        this.normalize = normalize;
        this.analyzeComments = analyzeComments;
        this.similarityMetric = similarityMetric;
        this.similarityThreshold = normalizeSimilarityThreshold(similarityThreshold);
    }

    /**
     * Constructs a {@link ParsingOptions} instance with minimal required parameters.
     * @param language Language used for parsing submissions
     */
    public ParsingOptions(Language language) {
        this(language, null, false, false, false, DEFAULT_SIMILARITY_METRIC, DEFAULT_SIMILARITY_THRESHOLD);
    }

    @Override
    public Integer minimumTokenMatch() {
        var language = language();
        if (minimumTokenMatch == null && language != null) {
            return language.minimumTokenMatch();
        }
        return minimumTokenMatch;
    }


    private static Integer normalizeMinimumTokenMatch(Integer minimumTokenMatch) {
        return minimumTokenMatch != null && minimumTokenMatch < 1 ? Integer.valueOf(1) : minimumTokenMatch;
    }

    private static double normalizeSimilarityThreshold(double similarityThreshold) {
        if (similarityThreshold > 1) {
            logger.warn("Maximum threshold of 1 used instead of {}", similarityThreshold);
            return 1;
        }
        if (similarityThreshold < 0) {
            logger.warn("Minimum threshold of 0 used instead of {}", similarityThreshold);
            return 0;
        }
        return similarityThreshold;
    }
}
