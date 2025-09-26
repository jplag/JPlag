package de.jplag;

import static de.jplag.SubmissionState.CANNOT_PARSE;
import static de.jplag.SubmissionState.NOTHING_TO_PARSE;
import static de.jplag.SubmissionState.TOO_SMALL;
import static de.jplag.SubmissionState.UNPARSED;
import static de.jplag.SubmissionState.VALID;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.commentextraction.Comment;
import de.jplag.commentextraction.CommentExtractor;
import de.jplag.commentextraction.CommentExtractorSettings;
import de.jplag.exceptions.LanguageException;
import de.jplag.normalization.TokenSequenceNormalizer;
import de.jplag.options.JPlagOptions;

/**
 * This class represents a single submission, which is either a single file or a directory containing multiple files. It
 * encapsulates the details and processing logic required to handle the submission files, including parsing,
 * tokenization, and normalization.
 */
public class Submission implements Comparable<Submission> {
    private static final Logger logger = LoggerFactory.getLogger(Submission.class);

    private final String name; // identifier for the submission (a directory or file name).
    private final File submissionRootFile; // Root of the submission, a director or file (including the subdir if used).
    private final boolean isNew; // old submissions are only checked against new ones.
    private final Collection<File> files;
    private final Language language;

    private SubmissionState state; // whether an error occurred during parsing or not
    private List<Token> tokenList; // list of tokens from all files, used for comparison
    private JPlagComparison baseCodeComparison; // Comparison of thus submission with the base code
    private Map<File, Integer> fileTokenCount;
    private List<Comment> comments; // list of comments from all files

    /**
     * Creates a submission.
     * @param name is the identifier of the submission (directory or filename). May include parent directory name if JPlag
     * is executed with multiple root directories.
     * @param submissionRootFile is the submission file or the root of the submission itself.
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
        tokenList = Collections.emptyList(); // Placeholder, will be replaced when submission is parsed
        comments = new ArrayList<>();
        state = UNPARSED;
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
     * Provides access to the comparison of this submission to the basecode.
     * @return base code comparison.
     */
    public JPlagComparison getBaseCodeComparison() {
        return baseCodeComparison;
    }

    /**
     * Provided all source code files.
     * @return a collection of files this submission consists of.
     */
    public Collection<File> getFiles() {
        return files;
    }

    /**
     * Provides the submission name. If the submission is a single program file, it is the file name. If the submission
     * contains multiple program files, it is the directory name. If JPlag is executed with multiple root directories, the
     * name starts the root directory identifier, e.g., <code>rootName/submissionName</code>.
     * @return name of the submission (directory or file name).
     */
    public String getName() {
        return name;
    }

    /**
     * @return Number of tokens in the parse result.
     */
    public int getNumberOfTokens() {
        return tokenList.size();
    }

    /**
     * @return the unique root of the submission, which is either in a root folder or a subfolder of root folder when the
     * subdirectory option is used.
     */
    public File getRoot() {
        return submissionRootFile;
    }

    /**
     * The similarity divisor is used for calculating the similarity of two submissions. It is based on the token length of
     * a submission.
     * @return Similarity divisor for the submission.
     */
    public int getSimilarityDivisor() {
        int divisor = getNumberOfTokens() - getFiles().size();
        if (baseCodeComparison != null) {
            divisor -= baseCodeComparison.getNumberOfMatchedTokens();
        }
        return divisor;
    }

    /**
     * @return unmodifiable list of tokens generated by parsing the submission.
     */
    public List<Token> getTokenList() {
        return tokenList;
    }

    /**
     * @return true if a comparison between the submission and the base code is available. Does not imply if there are
     * matches to the base code.
     */
    public boolean hasBaseCodeComparison() {
        return baseCodeComparison != null;
    }

    /**
     * @return true if a comparison between the submission and the base code is available. Does not imply if there are
     * matches to the base code.
     * @deprecated Use {@link #hasBaseCodeComparison()} instead.
     */
    @Deprecated(since = "6.1.0", forRemoval = true)
    public boolean hasBaseCodeMatches() {
        return baseCodeComparison != null;
    }

    /**
     * @return the state of the submissions, indicating whether it is valid or has errors.
     */
    public SubmissionState getState() {
        return state;
    }

    /**
     * @return whether the submission is new, that is, must be checked for plagiarism.
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Sets the base code comparison.
     * @param baseCodeComparison is submissions matches with the base code.
     */
    public void setBaseCodeComparison(JPlagComparison baseCodeComparison) {
        this.baseCodeComparison = baseCodeComparison;
    }

    /**
     * Sets the tokens that have been parsed from the files this submission consists of.
     * @param tokenList is the list of these tokens.
     */
    public void setTokenList(List<Token> tokenList) {
        this.tokenList = Collections.unmodifiableList(new ArrayList<>(tokenList));
    }

    /**
     * String representation of the code files contained in this submission, annotated with all tokens.
     * @return the annotated code as string.
     */
    public String getTokenAnnotatedSourceCode() {
        return TokenPrinter.printTokens(tokenList, submissionRootFile);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * This method is used to copy files that can not be parsed to a special folder.
     */
    private void copySubmission() {
        File errorDirectory = createErrorDirectory(language.getIdentifier(), name);
        logger.info("Copying erroneous submission to {}", errorDirectory.getAbsolutePath());
        for (File file : files) {
            try {
                Files.copy(file.toPath(), new File(errorDirectory, file.getName()).toPath());
            } catch (IOException exception) {
                logger.error("Error copying file: " + exception.getMessage(), exception);
            }
        }
    }

    private static File createErrorDirectory(String... subdirectoryNames) {
        File subdirectory = Path.of(JPlagOptions.ERROR_FOLDER, subdirectoryNames).toFile();
        if (!subdirectory.exists()) {
            subdirectory.mkdirs();
        }
        return subdirectory;
    }

    /**
     * Parse files of the submission.
     * @param debugParser specifies if the submission should be copied upon parsing errors.
     * @param normalize specifies if the token sequences should be normalized.
     * @param minimalTokens specifies the minimum number of tokens required of a valid submission.
     * @param analyzeComments specifies if comments should be extracted and analyzed.
     * @return Whether parsing was successful.
     * @throws LanguageException if the language parser is not able to parse at all.
     */
    /* package-private */ boolean parse(boolean debugParser, boolean normalize, int minimalTokens, boolean analyzeComments) throws LanguageException {
        if (files == null || files.isEmpty()) {
            logger.error("Nothing to parse for submission \"{}\"", name);
            state = NOTHING_TO_PARSE;
            return false;
        }

        try {
            tokenList = language.parse(new HashSet<>(files), normalize);
        } catch (CriticalParsingException e) {
            throw new LanguageException(e.getMessage(), e.getCause());
        } catch (ParsingException e) {
            String shortenedMessage = e.getMessage().replace(submissionRootFile.toString(), name);
            logger.warn("Failed to parse submission {}:{}{}", name, System.lineSeparator(), shortenedMessage);
            state = CANNOT_PARSE;
            if (debugParser) {
                copySubmission();
            }
            return false;
        }

        if (tokenList.size() < minimalTokens) {
            // print the number of tokens without the file-end token to help users choose the right parameters:
            logger.error("Submission {} contains {} tokens, which is below the minimum match length {}!", name, tokenList.size() - 1, minimalTokens);
            state = TOO_SMALL;
            return false;
        }

        if (analyzeComments) {
            this.extractAndParseComments();
        }

        tokenList = Collections.unmodifiableList(tokenList);
        state = VALID;
        return true;
    }

    private void extractAndParseComments() {
        Optional<CommentExtractorSettings> commentExtractorSettings = language.getCommentExtractorSettings();
        if (commentExtractorSettings.isPresent()) {
            for (File file : files) {
                CommentExtractor extractor = new CommentExtractor(file, commentExtractorSettings.get());
                comments.addAll(extractor.extract());
            }
            logger.debug("Found {} comments", comments.size());
        }
    }

    /**
     * Perform token sequence normalization, which makes the token sequence invariant to dead code insertion and independent
     * statement reordering.
     */
    void normalize() {
        List<Integer> originalOrder = getOrder(tokenList);
        tokenList = TokenSequenceNormalizer.normalize(tokenList);
        List<Integer> normalizedOrder = getOrder(tokenList);

        logger.debug("original line order: {}", originalOrder);
        logger.debug("line order after normalization: {}", normalizedOrder);
        Set<Integer> normalizedSet = new HashSet<>(normalizedOrder);
        List<Integer> removed = originalOrder.stream().filter(l -> !normalizedSet.contains(l)).toList();
        logger.debug("removed {} line(s): {}", removed.size(), removed);
    }

    private List<Integer> getOrder(List<Token> tokenList) {
        List<Integer> order = new ArrayList<>(tokenList.size());  // a little too big
        int currentLineNumber = tokenList.get(0).getStartLine();
        order.add(currentLineNumber);
        for (Token token : tokenList) {
            if (token.getStartLine() != currentLineNumber) {
                currentLineNumber = token.getStartLine();
                order.add(currentLineNumber);
            }
        }
        return order;
    }

    /**
     * @return A shallow copy of this submission with the same name, root, files, etc.
     */
    public Submission copy() {
        Submission copy = new Submission(name, submissionRootFile, isNew, files, language);
        copy.setTokenList(tokenList);
        copy.setBaseCodeComparison(baseCodeComparison);
        copy.state = state;
        return copy;
    }

    /**
     * @return A mapping of each file in the submission to the number of tokens in the file
     */
    public Map<File, Integer> getTokenCountPerFile() {
        if (this.tokenList == null) {
            return Collections.emptyMap();
        }

        if (fileTokenCount == null) {
            fileTokenCount = new HashMap<>();
            for (File file : this.files) {
                fileTokenCount.put(file, 0);
            }
            for (Token token : this.tokenList) {
                fileTokenCount.put(token.getFile(), fileTokenCount.get(token.getFile()) + 1);
            }
        }
        return fileTokenCount;
    }
}
