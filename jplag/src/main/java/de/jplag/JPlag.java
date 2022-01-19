package de.jplag;

import static de.jplag.options.Verbosity.LONG;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.SubmissionException;
import de.jplag.options.JPlagOptions;
import de.jplag.options.SimilarityMetric;
import de.jplag.strategy.ComparisonMode;
import de.jplag.strategy.ComparisonStrategy;
import de.jplag.strategy.NormalComparisonStrategy;
import de.jplag.strategy.ParallelComparisonStrategy;

/**
 * This class coordinates the whole errorConsumer flow.
 */
public class JPlag {
    private final JPlagOptions options;

    private final Language language;
    private final ComparisonStrategy comparisonStrategy;
    private final GreedyStringTiling coreAlgorithm; // Contains the comparison logic.
    private final ErrorCollector errorCollector;
    private final Set<String> excludedFileNames;
    private final int minimumTokenMatch;
    private final String[] fileSuffixes;

    /**
     * Creates and initializes a JPlag instance, parameterized by a set of options.
     * @param options determines the parameterization.
     * @throws ExitException if the initialization fails.
     */
    public JPlag(JPlagOptions options) throws ExitException {
        this.options = options;
        errorCollector = new ErrorCollector(options.getVerbosity());

        language = loadLanguage(errorCollector, options.getLanguageOption().getClassPath());

        minimumTokenMatch = Optional.ofNullable(options.getMinimumTokenMatch())
                .map(v -> Math.max(1, v))
                .orElse(language.minimumTokenMatch());

        fileSuffixes = Optional.ofNullable(options.getFileSuffixes())
                .orElse(language.suffixes());

        System.out.println("Initialized language " + language.getName());

        coreAlgorithm = new GreedyStringTiling(options, minimumTokenMatch);

        comparisonStrategy = initializeComparisonStrategy(options.getComparisonMode(), options.getSimilarityMetric(), options.getSimilarityThreshold());
        excludedFileNames = Optional.ofNullable(options.getExclusionFileName()).map(this::readExclusionFile).orElse(Collections.emptySet());

        options.setLanguageDefaults(language);
        options.setExcludedFiles(excludedFileNames); // store for report
    }

    /**
     * If an exclusion file is given, it is read in and all strings are saved in the set "excluded".
     * @param exclusionFileName
     */
    Set<String> readExclusionFile(final String exclusionFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(exclusionFileName, JPlagOptions.CHARSET))) {
            final var excludedFileNames = reader.lines().collect(Collectors.toSet());
            if (options.getVerbosity() == LONG) {
                errorCollector.print(null, "Excluded files:");
                for (var excludedFilename : excludedFileNames) {
                    errorCollector.print(null, " " + excludedFilename);
                }
            }
            return excludedFileNames;
        } catch (IOException e) {
            System.out.println("Could not read exclusion file: " + e.getMessage());
            return Collections.emptySet();
        }
    }

    public Language getLanguage() {
        return language;
    }

    /**
     * Main procedure, executes the comparison of source code submissions.
     * @return the results of the comparison, specifically the submissions whose similarity exceeds a set threshold.
     * @throws ExitException if the JPlag exits preemptively.
     */
    public JPlagResult run() throws ExitException {
        // Parse and validate submissions.
        SubmissionSetBuilder builder = new SubmissionSetBuilder(language, options, errorCollector, excludedFileNames, minimumTokenMatch,
                fileSuffixes);
        SubmissionSet submissionSet = builder.buildSubmissionSet();

        if (submissionSet.hasBaseCode()) {
            coreAlgorithm.createHashes(submissionSet.getBaseCode().getTokenList(), minimumTokenMatch, true);
        }

        int submissionCount = submissionSet.numberOfSubmissions();
        if (submissionCount < 2) {
            throw new SubmissionException("Not enough valid submissions! (found " + submissionCount + " valid submissions)");
        }

        // Compare valid submissions.
        JPlagResult result = comparisonStrategy.compareSubmissions(submissionSet);
        errorCollector.print("\nTotal time for comparing submissions: " + TimeUtil.formatDuration(result.getDuration()), null);
        return result;
    }

    private ComparisonStrategy initializeComparisonStrategy(final ComparisonMode comparisonMode, SimilarityMetric similarityMetric,
            float similarityThreshold) {
        return switch (comparisonMode) {
            case NORMAL -> new NormalComparisonStrategy(coreAlgorithm, similarityMetric, similarityThreshold);
            case PARALLEL -> new ParallelComparisonStrategy(coreAlgorithm, similarityMetric, similarityThreshold);
        };
    }

    private Language loadLanguage(final ErrorCollector errorCollector, final String classPath) {
        try {
            Constructor<?> constructor = Class.forName(classPath).getConstructor(ErrorConsumer.class);
            Object[] constructorParams = {errorCollector};

            return (Language) constructor.newInstance(constructorParams);
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalStateException("Language instantiation failed:" + e.getMessage(), e);
        }
    }
}
