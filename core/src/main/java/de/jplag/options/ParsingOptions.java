package de.jplag.options;

import de.jplag.Language;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RecordBuilder()
public record ParsingOptions(Language language, Integer minimumTokenMatch, boolean debugParser, boolean normalize, boolean analyzeComments, SimilarityMetric similarityMetric, double similarityThreshold) implements ParsingOptionsBuilder.With {


    /** Default value for the similarity metric. **/
    public static final SimilarityMetric DEFAULT_SIMILARITY_METRIC = SimilarityMetric.AVG;
    /** Default value for the similarity threshold. **/
    public static final double DEFAULT_SIMILARITY_THRESHOLD = 0;

    private static final Logger logger = LoggerFactory.getLogger(ParsingOptions.class);

    public ParsingOptions(Language language, Integer minimumTokenMatch, boolean debugParser, boolean normalize, boolean analyzeComments,SimilarityMetric similarityMetric, double similarityThreshold) {
        this.language = language;
        this.minimumTokenMatch = normalizeMinimumTokenMatch(minimumTokenMatch);
        this.debugParser = debugParser;
        this.normalize = normalize;
        this.analyzeComments = analyzeComments;
        this.similarityMetric = similarityMetric;
        this.similarityThreshold = normalizeSimilarityThreshold(similarityThreshold);;
    }

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
