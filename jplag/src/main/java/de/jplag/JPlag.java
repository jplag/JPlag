package de.jplag;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.RootDirectoryException;
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
        checkBaseCodeOption();
    }

    /**
     * Main procedure, executes the comparison of source code submissions.
     * @return the results of the comparison, specifically the submissions whose similarity exceeds a set threshold.
     * @throws ExitException if the JPlag exits preemptively.
     */
    public JPlagResult run() throws ExitException {
        // Parse and validate submissions.
        SubmissionSetBuilder builder = new SubmissionSetBuilder(language, options, errorCollector);
        SubmissionSet submissionSet = builder.buildSubmissionSet(getRootDirectory());

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

    /**
     * This method checks whether the base code directory value is valid.
     */
    private void checkBaseCodeOption() throws ExitException {
        getRootDirectory(); // Performs checks on the root directory.

        if (options.hasBaseCode()) {
            String baseCode = options.getBaseCodeSubmissionName();
            if (baseCode.contains(".")) {
                throw new BasecodeException("The basecode directory name \"" + baseCode + "\" cannot contain dots!");
            }
            String baseCodePath = options.getRootDirectoryName() + File.separator + baseCode;
            if (!new File(baseCodePath).exists()) {
                throw new BasecodeException("Basecode directory \"" + baseCodePath + "\" doesn't exist!");
            }

            String subdirectory = options.getSubdirectoryName();
            if (subdirectory != null && subdirectory.length() != 0) {
                if (!new File(baseCodePath, subdirectory).exists()) {
                    throw new BasecodeException("Basecode directory doesn't contain" + " the subdirectory \"" + subdirectory + "\"!");
                }
            }
            System.out.println("Basecode directory \"" + baseCodePath + "\" will be used");
        }
    }

    /**
     * Check sanity of the root directory name in the options, and construct file system access to it.
     */
    private File getRootDirectory() throws ExitException {
        String rootDirectoryName = options.getRootDirectoryName();
        File rootDir = new File(rootDirectoryName);
        if (!rootDir.exists()) {
            throw new RootDirectoryException(String.format("Root directory \"%s\" does not exist!", rootDirectoryName));
        }
        if (!rootDir.isDirectory()) {
            throw new RootDirectoryException(String.format("Root directory \"%s\" is not a directory!", rootDirectoryName));
        }
        return rootDir;
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
