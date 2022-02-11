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

import de.jplag.exceptions.ReportGenerationException;
import de.jplag.options.JPlagOptions;

/**
 * Represents a single submission. A submission can contain multiple files.
 */
public class Submission implements Comparable<Submission> {
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
    private final ErrorCollector errorCollector;

    /**
     * Creates a submission.
     * @param name Identification of the submission (directory or filename).
     * @param submissionRootFile is the submission file, or the root of the submission itself.
     * @param files are the files of the submissions, if the root is a single file it should just contain one file.
     * @param language is the language of the submission.
     * @param errorCollector is the interface for error reporting.
     */
    public Submission(String name, File submissionRootFile, Collection<File> files, Language language, ErrorCollector errorCollector) {
        this.name = name;
        this.submissionRootFile = submissionRootFile;
        this.files = files;
        this.language = language;
        this.errorCollector = errorCollector;
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
     * @return the unique file of the submission, which is either in a root folder or a subfolder of root folder when the
     * subdirectory option is used.
     */
    public File getRoot() {
        return submissionRootFile;
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
            errorCollector.print("ERROR: nothing to parse for submission \"" + name + "\"", null);
            tokenList = null;
            hasErrors = true; // invalidate submission
            return false;
        }

        String[] relativeFilePaths = getRelativeFilePaths(submissionRootFile, files);

        tokenList = language.parse(submissionRootFile, relativeFilePaths);
        if (!language.hasErrors()) {
            if (tokenList.size() < 3) {
                errorCollector.print("Submission \"" + name + "\" is too short!", null);
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
                FileInputStream fileInputStream = new FileInputStream(new File(submissionRootFile, files[i]));
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
                System.out.println("File not found: " + ((new File(submissionRootFile, files[i])).toString()));
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
                file = new File(submissionRootFile, files[i]);
            }

            try {
                int size = (int) file.length();
                char[] buffer = new char[size];

                FileReader reader = new FileReader(file, JPlagOptions.CHARSET);

                if (size != reader.read(buffer)) {
                    System.out.println("Not right size read from the file, but I will still continue...");
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
            token.setBasecode(false);
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
        File rootDirectory = submissionRootFile.getParentFile();
        assert rootDirectory != null;
        File submissionDirectory = createSubdirectory(rootDirectory, ERROR_FOLDER, language.getShortName(), name);
        for (File file : files) {
            try {
                Files.copy(file.toPath(), new File(submissionDirectory, file.getName()).toPath());
            } catch (IOException exception) {
                errorCollector.print("Error copying file: " + exception.toString() + "\n", null);
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
