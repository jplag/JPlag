package de.jplag;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;
import de.jplag.strategy.ComparisonStrategy;
import de.jplag.strategy.NormalComparisonStrategy;
import de.jplag.strategy.ParallelComparisonStrategy;

/**
 * This class coordinates the whole program flow.
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
            throw new ExitException("Not enough valid submissions! (found " + submissionCount + " valid submissions)",
                    ExitException.NOT_ENOUGH_SUBMISSIONS_ERROR);
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
                throw new ExitException("The basecode directory name \"" + baseCode + "\" cannot contain dots!", ExitException.BAD_PARAMETER);
            }
            String baseCodePath = options.getRootDirectoryName() + File.separator + baseCode;
            if (!new File(baseCodePath).exists()) {
                throw new ExitException("Basecode directory \"" + baseCodePath + "\" doesn't exist!", ExitException.BAD_PARAMETER);
            }

            String subdirectory = options.getSubdirectoryName();
            if (subdirectory != null && subdirectory.length() != 0) {
                if (!new File(baseCodePath, subdirectory).exists()) {
                    throw new ExitException("Basecode directory doesn't contain" + " the subdirectory \"" + subdirectory + "\"!",
                            ExitException.BAD_PARAMETER);
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
            String msg = String.format("Root directory \"%s\" does not exist!", rootDirectoryName);
            throw new ExitException(msg, ExitException.BAD_PARAMETER);
        }
        if (!rootDir.isDirectory()) {
            String msg = String.format("Root directory \"%s\" is not a directory!", rootDirectoryName);
            throw new ExitException(msg, ExitException.BAD_PARAMETER);
        }
        return rootDir;
    }

    private void initializeComparisonStrategy() throws ExitException {
        switch (options.getComparisonMode()) {
        case NORMAL:
            comparisonStrategy = new NormalComparisonStrategy(options, coreAlgorithm);
            break;
        case PARALLEL:
            comparisonStrategy = new ParallelComparisonStrategy(options, coreAlgorithm);
            break;
        default:
            throw new ExitException("Illegal comparison mode: " + options.getComparisonMode());
        }
    }

    private void initializeLanguage() throws ExitException {
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

            throw new ExitException("Language instantiation failed", ExitException.BAD_LANGUAGE_ERROR);
        }

        this.options.setLanguageDefaults(this.language);

        System.out.println("Initialized language " + this.language.getName());
    }
}
