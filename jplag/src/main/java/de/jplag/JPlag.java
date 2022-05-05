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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.clustering.ClusteringFactory;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.SubmissionException;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;
import de.jplag.strategy.ComparisonMode;
import de.jplag.strategy.ComparisonStrategy;
import de.jplag.strategy.NormalComparisonStrategy;
import de.jplag.strategy.ParallelComparisonStrategy;

/**
 * This class coordinates the whole errorConsumer flow.
 */
public class JPlag {
    private static final Logger logger = LoggerFactory.getLogger("JPlag");

    private final JPlagOptions options;

    private final Language language;
    private final ComparisonStrategy comparisonStrategy;
    private final GreedyStringTiling coreAlgorithm; // Contains the comparison logic.
    private final ErrorCollector errorCollector;
    private final Set<String> excludedFileNames;

    /**
     * Creates and initializes a JPlag instance, parameterized by a set of options.
     * @param options determines the parameterization.
     */
    public JPlag(JPlagOptions options) {
        this.options = options;
        errorCollector = new ErrorCollector(options);
        coreAlgorithm = new GreedyStringTiling(options);
        language = initializeLanguage();
        comparisonStrategy = initializeComparisonStrategy(options.getComparisonMode());
        excludedFileNames = Optional.ofNullable(this.options.getExclusionFileName()).map(this::readExclusionFile).orElse(Collections.emptySet());
        options.setExcludedFiles(excludedFileNames); // store for report
    }

    /**
     * If an exclusion file is given, it is read in and all strings are saved in the set "excluded".
     * @param exclusionFileName the file name or path
     */
    private Set<String> readExclusionFile(final String exclusionFileName) {
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
            logger.error("Could not read exclusion file: " + e.getMessage(), e);
            return Collections.emptySet();
        }
    }

    /**
     * Main procedure, executes the comparison of source code submissions.
     * @return the results of the comparison, specifically the submissions whose similarity exceeds a set threshold.
     * @throws ExitException if the JPlag exits preemptively.
     */
    public JPlagResult run() throws ExitException {
        // Parse and validate submissions.
        SubmissionSetBuilder builder = new SubmissionSetBuilder(language, options, errorCollector, excludedFileNames);
        SubmissionSet submissionSet = builder.buildSubmissionSet();

        if (submissionSet.hasBaseCode()) {
            coreAlgorithm.createHashes(submissionSet.getBaseCode().getTokenList(), options.getMinimumTokenMatch(), true);
        }

        int submissionCount = submissionSet.numberOfSubmissions();
        if (submissionCount < 2) {
            throw new SubmissionException("Not enough valid submissions! (found " + submissionCount + " valid submissions)");
        }

        // Compare valid submissions.
        JPlagResult result = comparisonStrategy.compareSubmissions(submissionSet);
        errorCollector.print("\nTotal time for comparing submissions: " + TimeUtil.formatDuration(result.getDuration()), null);

        result.setClusteringResult(ClusteringFactory.getClusterings(result.getComparisons(), options.getClusteringOptions()));

        return result;
    }

    private ComparisonStrategy initializeComparisonStrategy(final ComparisonMode comparisonMode) {
        return switch (comparisonMode) {
            case NORMAL -> new NormalComparisonStrategy(options, coreAlgorithm);
            case PARALLEL -> new ParallelComparisonStrategy(options, coreAlgorithm);
        };
    }

    private Language initializeLanguage() {
        LanguageOption languageOption = this.options.getLanguageOption();

        try {
            Constructor<?> constructor = Class.forName(languageOption.getClassPath()).getConstructor(ErrorConsumer.class);
            Object[] constructorParams = {errorCollector};

            Language language = (Language) constructor.newInstance(constructorParams);

            this.options.setLanguage(language);
            this.options.setLanguageDefaults(language);
            logger.info("Initialized language " + language.getName());
            return language;
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalStateException("Language instantiation failed:" + e.getMessage());
        }

    }
}
