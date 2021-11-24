package de.jplag;

import static de.jplag.options.Verbosity.LONG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
    private HashSet<String> excludedFileNames = null; // Set of file names to be excluded in comparison.
    private Language language;

    // CORE COMPONENTS:
    private ComparisonStrategy comparisonStrategy;
    private GreedyStringTiling gSTiling = new GreedyStringTiling(this); // Contains the comparison logic.
    private final JPlagOptions options;
    private final ErrorCollector errorCollector;

    /**
     * Creates and initializes a JPlag instance, parameterized by a set of options.
     * @param options determines the parameterization.
     * @throws ExitException if the initialization fails.
     */
    public JPlag(JPlagOptions options) throws ExitException {
        this.options = options;
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
        readExclusionFile(); // This file contains all files names which are excluded

        // 2. Parse and validate submissions:
        SubmissionSet submissionSet = findSubmissions(rootDir);

        if (submissionSet.hasBaseCode()) {
            gSTiling.createHashes(submissionSet.getBaseCode().getTokenList(), options.getMinimumTokenMatch(), true);
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
     * @return the program options which allow to configure JPlag.
     */
    protected JPlagOptions getOptions() {
        return this.options; // TODO TS: Should not be accessible, as options should be set before passing them to this class.
    }

    /**
     * Checks if a file has a valid suffix for the current language.
     * @param file is the file to check.
     * @return true if the file suffix matches the language.
     */
    public boolean hasValidSuffix(File file) {
        String[] validSuffixes = options.getFileSuffixes();

        // This is the case if either the language frontends or the CLI did not set the valid suffixes array in options
        if (validSuffixes == null || validSuffixes.length == 0) {
            return true;
        }
        return Arrays.stream(validSuffixes).anyMatch(suffix -> file.getName().endsWith(suffix));
    }

    /**
     * Checks if a file is excluded or not.
     */
    public boolean isFileExcluded(File file) {
        if (excludedFileNames == null) {
            return false;
        }
        return excludedFileNames.stream().anyMatch(excludedName -> file.getName().endsWith(excludedName));
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

    /**
     * Find all submissions in the given root directory.
     */
    private SubmissionSet findSubmissions(File rootDir) throws ExitException {
        String[] fileNamesInRootDir;

        try {
            fileNamesInRootDir = rootDir.list();
        } catch (SecurityException e) {
            throw new ExitException("Cannot list files of the root directory! " + e.getMessage());
        }

        if (fileNamesInRootDir == null) {
            throw new ExitException("Cannot list files of the root directory! " + "Make sure the specified root directory is in fact a directory.");
        }

        Arrays.sort(fileNamesInRootDir);

        return mapFileNamesInRootDirToSubmissions(fileNamesInRootDir, rootDir);
    }

    private void initializeComparisonStrategy() throws ExitException {
        switch (options.getComparisonMode()) {
        case NORMAL:
            comparisonStrategy = new NormalComparisonStrategy(options, gSTiling);
            break;
        case PARALLEL:
            comparisonStrategy = new ParallelComparisonStrategy(options, gSTiling);
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

    private SubmissionSet mapFileNamesInRootDirToSubmissions(String[] fileNames, File rootDir) throws ExitException {
        List<Submission> submissions = new ArrayList<>();
        Optional<Submission> baseCodeSubmission = Optional.empty();

        for (String fileName : fileNames) {
            File submissionFile = new File(rootDir, fileName);

            if (isFileExcluded(submissionFile)) {
                System.out.println("Exclude submission: " + submissionFile.getName());
                continue;
            }

            if (submissionFile.isFile() && !hasValidSuffix(submissionFile)) {
                System.out.println("Ignore submission with invalid suffix: " + submissionFile.getName());
                continue;
            }

            if (submissionFile.isDirectory() && options.getSubdirectoryName() != null) {
                // Use subdirectory instead
                submissionFile = new File(submissionFile, options.getSubdirectoryName());

                if (!submissionFile.exists()) {
                    throw new ExitException(
                            String.format("Submission %s does not contain the given subdirectory '%s'", fileName, options.getSubdirectoryName()));
                }

                if (!submissionFile.isDirectory()) {
                    throw new ExitException(String.format("The given subdirectory '%s' is not a directory!", options.getSubdirectoryName()));
                }
            }

            Submission submission = new Submission(fileName, submissionFile, this);

            if (options.hasBaseCode() && options.getBaseCodeSubmissionName().equals(fileName)) {
                baseCodeSubmission = Optional.of(submission);
            } else {
                submissions.add(submission);
            }
        }

        return new SubmissionSet(submissions, baseCodeSubmission, errorCollector, options);
    }

    public void print(String message, String longMessage) {
        errorCollector.print(message, longMessage);
    }

    /*
     * If an exclusion file is given, it is read in and all stings are saved in the set "excluded".
     */
    private void readExclusionFile() {
        if (options.getExclusionFileName() == null) {
            return;
        }

        excludedFileNames = new HashSet<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(options.getExclusionFileName(), JPlagOptions.CHARSET));
            String line;

            while ((line = reader.readLine()) != null) {
                excludedFileNames.add(line.trim());
            }

            reader.close();
        } catch (IOException exception) {
            System.out.println("Could not read exclusion file: " + exception.getMessage());
        }

        if (options.getVerbosity() == LONG) {
            errorCollector.print(null, "Excluded files:\n");

            for (String excludedFileName : excludedFileNames) {
                errorCollector.print(null, "  " + excludedFileName + "\n");
            }
        }
    }
}
