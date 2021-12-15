package de.jplag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.SubmissionException;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;
import de.jplag.strategy.ComparisonStrategy;
import de.jplag.strategy.NormalComparisonStrategy;
import de.jplag.strategy.ParallelComparisonStrategy;

/**
 * This class coordinates the whole errorConsumer flow.
 */
public class JPlag {
    // INPUT:
    private Language language;

    // CORE COMPONENTS:
    private ComparisonStrategy comparisonStrategy;
    private GreedyStringTiling coreAlgorithm; // Contains the comparison logic.
    private final JPlagOptions options;
    private final ErrorCollector errorCollector;

    /**
     * Creates and initializes a JPlag instance, parameterized by a set of options.
     * @param options determines the parameterization.
     * @throws ExitException if the initialization fails.
     */
    public JPlag(JPlagOptions options) throws ExitException {
        this.options = options;
        errorCollector = new ErrorCollector(options);
        coreAlgorithm = new GreedyStringTiling(options);
        initializeLanguage();
        initializeComparisonStrategy();
    }

    /**
     * Main procedure, executes the comparison of source code submissions.
     * @return the results of the comparison, specifically the submissions whose similarity exceeds a set threshold.
     * @throws ExitException if the JPlag exits preemptively.
     */
    public JPlagResult run() throws ExitException {
        // Parse and validate submissions.
        SubmissionSetBuilder builder = new SubmissionSetBuilder(language, options, errorCollector);
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
        return result;
    }

    private void initializeComparisonStrategy() {
        switch (options.getComparisonMode()) {
        case NORMAL:
            comparisonStrategy = new NormalComparisonStrategy(options, coreAlgorithm);
            break;
        case PARALLEL:
            comparisonStrategy = new ParallelComparisonStrategy(options, coreAlgorithm);
            break;
        default:
            throw new UnsupportedOperationException("Comparison mode not properly supported: " + options.getComparisonMode());
        }
    }

    private void initializeLanguage() {
        LanguageOption languageOption = this.options.getLanguageOption();

        try {
            Constructor<?> constructor = Class.forName(languageOption.getClassPath()).getConstructor(ErrorConsumer.class);
            Object[] constructorParams = {errorCollector};

            Language language = (Language) constructor.newInstance(constructorParams);

            this.language = language;
            this.options.setLanguage(language);
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalStateException("Language instantiation failed:" + e.getMessage());
        }

        this.options.setLanguageDefaults(this.language);

        System.out.println("Initialized language " + this.language.getName());
    }
}
