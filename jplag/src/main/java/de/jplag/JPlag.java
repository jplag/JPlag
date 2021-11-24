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
        coreAlgorithm = new GreedyStringTiling(options);
        initializeLanguage();
        initializeComparisonStrategy();
        checkBaseCodeOption();
        errorCollector = new ErrorCollector(options);
    }

    /**
     * Main procedure, executes the comparison of source code submissions.
     * @return the results of the comparison, specifically the submissions whose similarity exceeds a set threshold.
     * @throws ExitException if the JPlag exits preemptively.
     */
    public JPlagResult run() throws ExitException {
        // 1. Preparation:
        File rootDir = new File(options.getRootDirectoryName());
        if (!rootDir.exists()) {
            throw new ExitException("Root directory " + options.getRootDirectoryName() + " does not exist!");
        }
        if (!rootDir.isDirectory()) {
            throw new ExitException(options.getRootDirectoryName() + " is not a directory!");
        }
        
        // 2. Parse and validate submissions:
        SubmissionSetBuilder builder = new SubmissionSetBuilder(language, options, errorCollector);
        SubmissionSet submissionSet = builder.buildSubmissionSet(rootDir);

        if (submissionSet.hasBaseCode()) {
            coreAlgorithm.createHashes(submissionSet.getBaseCode().getTokenList(), options.getMinimumTokenMatch(), true);
        }

        int submCount = submissionSet.numberOfSubmissions();
        if (submCount < 2) {
            errorCollector.printErrors();
            throw new ExitException("Not enough valid submissions! (found " + submCount + " valid submissions)",
                    ExitException.NOT_ENOUGH_SUBMISSIONS_ERROR);
        }

        // 3. Compare valid submissions:
        JPlagResult result = comparisonStrategy.compareSubmissions(submissionSet);
        System.out.println("Total time for comparing submissions: " + TimeUtil.formatDuration(result.getDuration()));
        return result;
    }

    /**
     * @return the configured language in which the submissions are written.
     */
    public Language getLanguage() {
        return language;
    }


    /**
     * This method checks whether the base code directory value is valid.
     */
    private void checkBaseCodeOption() throws ExitException {
        if (options.hasBaseCode()) {
            if (!new File(options.getRootDirectoryName()).exists()) {
                throw new ExitException("Root directory \"" + options.getRootDirectoryName() + "\" doesn't exist!", ExitException.BAD_PARAMETER);
            }

            String baseCode = options.getBaseCodeSubmissionName().replace(File.separator, ""); // trim problematic file separators
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
            options.setBaseCodeSubmissionName(baseCode);
            System.out.println("Basecode directory \"" + baseCodePath + "\" will be used");
        }
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

        this.options.setLanguageDefaults(this.getLanguage());

        System.out.println("Initialized language " + this.getLanguage().getName());
    }
}
