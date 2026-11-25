package de.jplag.optionsWithHelper;

import de.jplag.Language;
import de.jplag.options.JPlagOptions;
import de.jplag.options.ParsingOptions;
import de.jplag.options.SimilarityMetric;

import java.util.List;

public final class ParsingOptionsWith {

    private ParsingOptionsWith() {
        // utility class
    }

    public static JPlagOptions language(JPlagOptions options, Language language) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(
                language,
                p.minimumTokenMatch(),
                p.fileSuffixes(),
                p.debugParser(),
                p.normalize(),
                p.analyzeComments(),
                p.similarityMetric(),
                p.similarityThreshold()));
    }

    public static JPlagOptions minimumTokenMatch(JPlagOptions options, Integer minimumTokenMatch) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(
                p.language(),
                minimumTokenMatch,
                p.fileSuffixes(),
                p.debugParser(),
                p.normalize(),
                p.analyzeComments(),
                p.similarityMetric(),
                p.similarityThreshold()));
    }

    public static JPlagOptions fileSuffixes(JPlagOptions options, List<String> fileSuffixes) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(
                p.language(),
                p.minimumTokenMatch(),
                fileSuffixes,
                p.debugParser(),
                p.normalize(),
                p.analyzeComments(),
                p.similarityMetric(),
                p.similarityThreshold()));
    }

    public static JPlagOptions debugParser(JPlagOptions options, boolean debugParser) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(
                p.language(),
                p.minimumTokenMatch(),
                p.fileSuffixes(),
                debugParser,
                p.normalize(),
                p.analyzeComments(),
                p.similarityMetric(),
                p.similarityThreshold()));
    }

    public static JPlagOptions normalize(JPlagOptions options, boolean normalize) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(
                p.language(),
                p.minimumTokenMatch(),
                p.fileSuffixes(),
                p.debugParser(),
                normalize,
                p.analyzeComments(),
                p.similarityMetric(),
                p.similarityThreshold()));
    }

    public static JPlagOptions analyzeComments(JPlagOptions options, boolean analyzeComments) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(
                p.language(),
                p.minimumTokenMatch(),
                p.fileSuffixes(),
                p.debugParser(),
                p.normalize(),
                analyzeComments,
                p.similarityMetric(),
                p.similarityThreshold()));
    }

    public static JPlagOptions similarityMetric(JPlagOptions options, SimilarityMetric similarityMetric) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(
                p.language(),
                p.minimumTokenMatch(),
                p.fileSuffixes(),
                p.debugParser(),
                p.normalize(),
                p.analyzeComments(),
                similarityMetric,
                p.similarityThreshold()));
    }

    public static JPlagOptions similarityThreshold(JPlagOptions options, double similarityThreshold) {
        ParsingOptions p = options.parsingOptions();
        return options.withParsingOptions(new ParsingOptions(
                p.language(),
                p.minimumTokenMatch(),
                p.fileSuffixes(),
                p.debugParser(),
                p.normalize(),
                p.analyzeComments(),
                p.similarityMetric(),
                similarityThreshold));
    }
}