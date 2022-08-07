package de.jplag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a single submission. A submission can contain multiple files.
 */
public class Submission implements Comparable<Submission> {
    private static final Logger logger = LoggerFactory.getLogger(Submission.class);

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
    private final File submissionRootFile;

    /**
     * Whether the submission is new. That is, must be checked for plagiarism.
     */
    private final boolean isNew;

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
     * @param submissionRootFile is the submission file, or the root of the submission itself.
     * @param isNew states whether the submission must be checked for plagiarism.
     * @param files are the files of the submissions, if the root is a single file it should just contain one file.
     * @param language is the language of the submission.
     */
    public Submission(String name, File submissionRootFile, boolean isNew, Collection<File> files, Language language) {
        this.name = name;
        this.submissionRootFile = submissionRootFile;
        this.isNew = isNew;
        this.files = files;
        this.language = language;
    }

    @Override
    public int compareTo(Submission other) {
        return name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Submission otherSubmission)) {
            return false;
        }
        return otherSubmission.getName().equals(name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * @return base code comparison
     */
    public JPlagComparison getBaseCodeComparison() {
        return baseCodeComparison;
    }

    /**
     * @return a list of files this submission consists of.
     */
    public Collection<File> getFiles() {
        return files;
    }

    /**
     * @return name of the submission (directory or file name).
     */
    public String getName() {
        return name;
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
     * @return the unique file of the submission, which is either in a root folder or a subfolder of root folder when the
     * subdirectory option is used.
     */
    public File getRoot() {
        return submissionRootFile;
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
     * @return Whether a comparison between the submission and the base code is available.
     */
    public boolean hasBaseCodeMatches() {
        return baseCodeComparison != null;
    }

    /**
     * @return true if at least one error occurred while parsing this submission; false otherwise.
     */
    public boolean hasErrors() {
        return hasErrors;
    }

    /**
     * @return whether the submission is new, That is, must be checked for plagiarism.
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Resets the base code flag for all tokens of this submission.
     */
    public void resetBaseCode() {
        for (Token token : tokenList.allTokens()) {
            token.setBasecode(false);
        }
    }

    /**
     * Sets the base code comparison
     * @param baseCodeComparison is submissions matches with the base code
     */
    public void setBaseCodeComparison(JPlagComparison baseCodeComparison) {
        this.baseCodeComparison = baseCodeComparison;
    }

    /**
     * Sets the tokens that have been parsed from the files this submission consists of.
     * @param tokenList is the list of these tokens.
     */
    public void setTokenList(TokenList tokenList) {
        this.tokenList = tokenList;
    }

    /**
     * String representation of the code files contained in this submission, annotated with all tokens.
     * @return the annotated code as string.
     */
    public String getTokenAnnotatedSourceCode() {
        return TokenPrinter.printTokens(tokenList, files, submissionRootFile);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * This method is used to copy files that can not be parsed to a special folder.
     */
    private void copySubmission() {
        File rootDirectory = submissionRootFile.getParentFile();
        assert rootDirectory != null;
        File submissionDirectory = createSubdirectory(rootDirectory, ERROR_FOLDER, language.getShortName(), name);
        for (File file : files) {
            try {
                Files.copy(file.toPath(), new File(submissionDirectory, file.getName()).toPath());
            } catch (IOException exception) {
                logger.error("Error copying file: " + exception.getMessage(), exception);
            }
        }
    }

    private static File createSubdirectory(File parent, String... subdirectoryNames) {
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

    /* package-private */ void markAsErroneous() {
        hasErrors = true;
    }

    /**
     * Parse files of the submission.
     * @return Whether parsing was successful.
     */
    /* package-private */ boolean parse(boolean debugParser) {
        if (files == null || files.isEmpty()) {
            logger.error("ERROR: nothing to parse for submission \"{}\"", name);
            tokenList = null;
            hasErrors = true; // invalidate submission
            return false;
        }

        String[] relativeFilePaths = getRelativeFilePaths(submissionRootFile, files);

        tokenList = language.parse(submissionRootFile, relativeFilePaths);
        if (!language.hasErrors()) {
            if (tokenList.size() < 3) {
                logger.error("Submission \"{}\" is too short!", name);
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
}
