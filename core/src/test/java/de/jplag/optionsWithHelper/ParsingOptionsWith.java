package de.jplag.optionsWithHelper;

import java.util.List;

import de.jplag.Language;
import de.jplag.options.JPlagOptions;
import de.jplag.options.ParsingOptions;
import de.jplag.options.SimilarityMetric;

/**
 * Utility class providing "with" helpers that return a copy of {@link JPlagOptions} with a single field in
 * {@link ParsingOptions} replaced.
 */
public final class ParsingOptionsWith {

    private ParsingOptionsWith() {
        // utility class
    }

    /**
     * Returns a copy of the options with the given language.
     * @param options the original options
     * @param language the new language
     * @return the updated options
     */
    public static JPlagOptions language(JPlagOptions options, Language language) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(language, p.minimumTokenMatch(), p.fileSuffixes(), p.debugParser(), p.normalize(),
                p.analyzeComments(), p.similarityMetric(), p.similarityThreshold()));
    }

    /**
     * Returns a copy of the options with the given minimum token match.
     * @param options the original options
     * @param minimumTokenMatch the new minimum token match
     * @return the updated options
     */
    public static JPlagOptions minimumTokenMatch(JPlagOptions options, Integer minimumTokenMatch) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(p.language(), minimumTokenMatch, p.fileSuffixes(), p.debugParser(), p.normalize(),
                p.analyzeComments(), p.similarityMetric(), p.similarityThreshold()));
    }

    /**
     * Returns a copy of the options with the given file suffixes.
     * @param options the original options
     * @param fileSuffixes the new file suffixes
     * @return the updated options
     */
    public static JPlagOptions fileSuffixes(JPlagOptions options, List<String> fileSuffixes) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(p.language(), p.minimumTokenMatch(), fileSuffixes, p.debugParser(), p.normalize(),
                p.analyzeComments(), p.similarityMetric(), p.similarityThreshold()));
    }

    /**
     * Returns a copy of the options with the debug parser flag set as given.
     * @param options the original options
     * @param debugParser whether the parser should run in debug mode
     * @return the updated options
     */
    public static JPlagOptions debugParser(JPlagOptions options, boolean debugParser) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(p.language(), p.minimumTokenMatch(), p.fileSuffixes(), debugParser, p.normalize(),
                p.analyzeComments(), p.similarityMetric(), p.similarityThreshold()));
    }

    /**
     * Returns a copy of the options with the normalization flag set as given.
     * @param options the original options
     * @param normalize whether token normalization is enabled
     * @return the updated options
     */
    public static JPlagOptions normalize(JPlagOptions options, boolean normalize) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(p.language(), p.minimumTokenMatch(), p.fileSuffixes(), p.debugParser(), normalize,
                p.analyzeComments(), p.similarityMetric(), p.similarityThreshold()));
    }

    /**
     * Returns a copy of the options with the comment analysis flag set as given.
     * @param options the original options
     * @param analyzeComments whether comments should be analyzed
     * @return the updated options
     */
    public static JPlagOptions analyzeComments(JPlagOptions options, boolean analyzeComments) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(p.language(), p.minimumTokenMatch(), p.fileSuffixes(), p.debugParser(), p.normalize(),
                analyzeComments, p.similarityMetric(), p.similarityThreshold()));
    }

    /**
     * Returns a copy of the options with the given similarity metric.
     * @param options the original options
     * @param similarityMetric the new similarity metric
     * @return the updated options
     */
    public static JPlagOptions similarityMetric(JPlagOptions options, SimilarityMetric similarityMetric) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(p.language(), p.minimumTokenMatch(), p.fileSuffixes(), p.debugParser(), p.normalize(),
                p.analyzeComments(), similarityMetric, p.similarityThreshold()));
    }

    /**
     * Returns a copy of the options with the given similarity threshold.
     * @param options the original options
     * @param similarityThreshold the new similarity threshold
     * @return the updated options
     */
    public static JPlagOptions similarityThreshold(JPlagOptions options, double similarityThreshold) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(p.language(), p.minimumTokenMatch(), p.fileSuffixes(), p.debugParser(), p.normalize(),
                p.analyzeComments(), p.similarityMetric(), similarityThreshold));
    }
}