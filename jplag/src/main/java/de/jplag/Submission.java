package de.jplag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.ReportGenerationException;
import de.jplag.exceptions.SubmissionException;
import de.jplag.options.JPlagOptions;

/**
 * Represents a single submission. A submission can contain multiple files.
 */
public class Submission implements Comparable<Submission> {
    private static final Logger logger = LogManager.getLogger(JPlag.class);

    /**
     * Directory name for storing submission files with parse errors if so requested.
     */
    private static final String ERROR_FOLDER = "errors";

    /**
     * Identification of the submission (often a directory or file name).
     */
    private final String name;

    /**
     * Root of the submission files (including the subdir if used).
     */
    private final File submissionRoot;

    /**
     * Files of the submission.
     */
    private final Collection<File> files;

    /**
     * Whether an error occurred during parsing the submission files.
     */
    private boolean hasErrors;

    /**
     * Parse result, tokens from all files.
     */
    private TokenList tokenList;

    /**
     * Base code comparison
     */
    private JPlagComparison baseCodeComparison;

    private final Language language;

    /**
     * Creates a submission.
     * @param name Identification of the submission (directory or filename).
     * @param submissionRoot Root of the submission (either a file or a directory).
     * @param files are the files of the submissions, if the root is a single file it should just contain one file.
     * @param language is the language of the submission.
     */
    public Submission(String name, File submissionRoot, Collection<File> files, Language language) {
        this.name = name;
        this.submissionRoot = submissionRoot;
        this.files = files;
        this.language = language;
    }

    /**
     * @return a list of files this submission consists of.
     */
    public Collection<File> getFiles() {
        return files;
    }

    /**
     * @return Identification of the submission (often a directory or file name).
     */
    public String getName() {
        return name;
    }

    public File getRoot() {
        return submissionRoot;
    }

    public File getCanonicalRoot() throws ExitException {
        try {
            return submissionRoot.getCanonicalFile();
        } catch (IOException exception) {
            throw new SubmissionException(String.format("Cannot compute canonical file path of \"%s\".", submissionRoot.toString()), exception);
        }
    }

    /**
     * @return Number of tokens in the parse result.
     */
    public int getNumberOfTokens() {
        if (tokenList == null) {
            return 0;
        }
        return tokenList.size();
    }

    /**
     * Sets the base code comparison
     * @param baseCodeComparison is submissions matches with the base code
     */
    public void setBaseCodeComparison(JPlagComparison baseCodeComparison) {
        this.baseCodeComparison = baseCodeComparison;
    }

    /**
     * @return base code comparison
     */
    public JPlagComparison getBaseCodeComparison() {
        return baseCodeComparison;
    }

    /**
     * @return Whether a comparison between the submission and the base code is available.
     */
    public boolean hasBaseCodeMatches() {
        return baseCodeComparison != null;
    }

    /**
     * @param subtractBaseCode If true subtract basecode matches if possible.
     * @return Similarity divisor for the submission.
     */
    public int getSimilarityDivisor(boolean subtractBaseCode) {
        int divisor = getNumberOfTokens() - getFiles().size();
        if (subtractBaseCode && baseCodeComparison != null) {
            divisor -= baseCodeComparison.getNumberOfMatchedTokens();
        }
        return divisor;
    }

    /**
     * @return Parse result of the submission.
     */
    public TokenList getTokenList() {
        return tokenList;
    }

    /**
     * @return true if at least one error occurred while parsing this submission; false otherwise.
     */
    public boolean hasErrors() {
        return hasErrors;
    }

    /**
     * Parse files of the submission.
     * @return Whether parsing was successful.
     */
    public boolean parse(boolean debugParser) {
        if (files == null || files.size() == 0) {
            logger.error("nothing to parse for submission \"" + name + "\"");
            tokenList = null;
            hasErrors = true; // invalidate submission
            return false;
        }

        String[] relativeFilePaths = getRelativeFilePaths(submissionRoot, files);

        tokenList = language.parse(submissionRoot, relativeFilePaths);
        if (!language.hasErrors()) {
            if (tokenList.size() < 3) {
                logger.error("Submission \"" + name + "\" is too short!");
                tokenList = null;
                hasErrors = true; // invalidate submission
                return false;
            }
            return true;
        }

        tokenList = null;
        hasErrors = true; // invalidate submission
        if (debugParser) {
            copySubmission();
        }
        return false;
    }

    /**
     * Used by the "Report" class. All source files are returned as an array of an array of strings.
     */
    public String[][] readFiles(String[] files) throws ReportGenerationException {
        String[][] result = new String[files.length][];
        String help;
        List<String> text = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            text.clear();

            try {
                FileInputStream fileInputStream = new FileInputStream(new File(submissionRoot, files[i]));
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, JPlagOptions.CHARSET);
                BufferedReader in = new BufferedReader(inputStreamReader);

                while ((help = in.readLine()) != null) {
                    help = help.replaceAll("&", "&amp;");
                    help = help.replaceAll("<", "&lt;");
                    help = help.replaceAll(">", "&gt;");
                    help = help.replaceAll("\"", "&quot;");
                    text.add(help);
                }

                in.close();
                inputStreamReader.close();
                fileInputStream.close();
            } catch (FileNotFoundException e) {
                logger.error("File not found: " + ((new File(submissionRoot, files[i])).toString()), e);
            } catch (IOException e) {
                throw new ReportGenerationException("I/O exception!", e);
            }

            result[i] = new String[text.size()];
            text.toArray(result[i]);
        }

        return result;
    }

    /**
     * Used by the "Report" class. All source files are returned as an array of an array of chars.
     */
    public char[][] readFilesChar(String[] files) throws ReportGenerationException {
        char[][] result = new char[files.length][];

        for (int i = 0; i < files.length; i++) {
            // If the token path is absolute, ignore the provided directory
            File file = new File(files[i]);
            if (!file.isAbsolute()) {
                file = new File(submissionRoot, files[i]);
            }

            try {
                int size = (int) file.length();
                char[] buffer = new char[size];

                FileReader reader = new FileReader(file, JPlagOptions.CHARSET);

                if (size != reader.read(buffer)) {
                    logger.warn("Not right size read from the file, but I will still continue...");
                }

                result[i] = buffer;
                reader.close();
            } catch (FileNotFoundException e) {
                throw new ReportGenerationException("File not found: " + file.getPath(), e);
            } catch (IOException e) {
                throw new ReportGenerationException("I/O exception reading file \"" + file.getPath() + "\"!", e);
            }
        }

        return result;
    }

    /**
     * Resets the base code flag for all tokens of this submission.
     */
    public void resetBaseCode() {
        for (Token token : tokenList.allTokens()) {
            token.basecode = false;
        }
    }

    /**
     * Sets the tokens that have been parsed from the files this submission consists of.
     * @param tokenList is the list of these tokens.
     */
    public void setTokenList(TokenList tokenList) {
        this.tokenList = tokenList;
    }

    public void markAsErroneous() {
        hasErrors = true;
    }

    @Override
    public int compareTo(Submission other) {
        return name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * This method is used to copy files that can not be parsed to a special folder.
     */
    private void copySubmission() {
        File rootDirectory = submissionRoot.getParentFile();
        assert rootDirectory != null;
        File submissionDirectory = createSubdirectory(rootDirectory, ERROR_FOLDER, language.getShortName(), name);
        for (File file : files) {
            try {
                Files.copy(file.toPath(), new File(submissionDirectory, file.getName()).toPath());
            } catch (IOException exception) {
                logger.error("Error copying file: " + exception.toString() + "\n", exception);
            }
        }
    }

    private File createSubdirectory(File parent, String... subdirectoryNames) {
        File subdirectory = parent;
        for (String name : subdirectoryNames) {
            subdirectory = new File(subdirectory, name);
        }
        if (!subdirectory.exists()) {
            subdirectory.mkdirs();
        }
        return subdirectory;
    }

    /**
     * Map all files of this submission to their path relative to the submission directory.
     * <p>
     * This method is required to stay compatible with `language.parse(...)` as it requires the given file paths to be
     * relative to the submission directory.
     * <p>
     * In a future update, `language.parse(...)` should probably just take a list of files.
     * @param baseFile - File to base all relative file paths on.
     * @param files - List of files to map.
     * @return an array of file paths relative to the submission directory.
     */
    private String[] getRelativeFilePaths(File baseFile, Collection<File> files) {
        Path baseFilePath = baseFile.toPath();

        return files.stream().map(File::toPath).map(baseFilePath::relativize).map(Path::toString).toArray(String[]::new);
    }
}
