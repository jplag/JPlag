package de.jplag.commentextraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.util.FileUtils;

/**
 * Extracts comments from submitted files, by reading and parsing the file content manually.
 */
public class CommentExtractor {
    private static final Logger logger = LoggerFactory.getLogger(CommentExtractor.class);
    private String remainingContent;
    private final List<Comment> comments;
    private final CommentExtractorSettings settings;
    private String lookBehind;
    private int currentCol;
    private int currentLine;
    private final File file;

    /**
     * Creates a new CommentExtractor, reading the contents from the specified file.
     * @param file File to read
     * @param settings Settings for the comment extractor
     */
    public CommentExtractor(File file, CommentExtractorSettings settings) {
        this.settings = settings;
        this.comments = new ArrayList<>();
        this.lookBehind = "";
        this.file = file;
        this.currentCol = 1;
        this.currentLine = 1;
        this.readFile();
    }

    private void readFile() {
        try {
            this.remainingContent = FileUtils.readFileContent(file);
        } catch (IOException e) {
            logger.warn("Could not extract comments from {}: {}", file.getAbsolutePath(), e.getMessage());
            this.remainingContent = "";
        }
    }

    private void match(String expected) throws UnexpectedStringException {
        if (remainingContent.startsWith(expected)) {
            this.advance(expected.length());
        } else {
            throw new UnexpectedStringException(
                    "Matched incorrectly, expected: " + expected + ", received: " + remainingContent.substring(0, expected.length()));
        }
    }

    private String advance(int length) {
        String advancedBy = remainingContent.substring(0, length);
        remainingContent = remainingContent.substring(length);

        if (!this.checkForLineBreaks(advancedBy)) {
            this.currentCol += length;
        }

        return advancedBy;
    }

    private boolean checkForLineBreaks(String advancedBy) {
        String combined = lookBehind + advancedBy;
        if (combined.contains(System.lineSeparator())) {
            String[] lines = combined.split(System.lineSeparator(), -1);

            // Fixing line & column positions
            this.currentLine += lines.length - 1;
            String lastLine = lines[lines.length - 1];
            this.currentCol = lastLine.length() + 1;

            lookBehind = lastLine.substring(Math.max(0, lastLine.length() - System.lineSeparator().length() + 1));
            return true;
        } else {
            lookBehind = advancedBy.substring(Math.max(0, advancedBy.length() - System.lineSeparator().length() + 1));
            return false;
        }
    }

    /**
     * Extracts all comments from the input file and returns them in a list.
     * @return All extracted comments from the input file
     */
    public List<Comment> extract() {
        while (!remainingContent.isEmpty()) {
            try {
                this.parseAny();
            } catch (UnexpectedStringException e) {
                logger.warn("Comment extraction failed, due to unexpected string: {}", e.getMessage());
                this.comments.clear();
                break;
            }
        }
        return comments;
    }

    private void parseAny() throws UnexpectedStringException {
        if (this.parseEscapedCharacter().isPresent()) {
            return;
        }

        for (EnvironmentDelimiter environment : this.settings.noCommentEnvironments()) {
            if (remainingContent.startsWith(environment.begin())) {
                this.parseNoCommentEnvironment(environment);
                return;
            }
        }

        for (String lineComment : this.settings.lineCommentDelimiters()) {
            if (remainingContent.startsWith(lineComment)) {
                this.match(lineComment);
                this.parseLineComment();
                return;
            }
        }

        for (EnvironmentDelimiter blockComment : this.settings.blockCommentDelimiters()) {
            if (remainingContent.startsWith(blockComment.begin())) {
                this.parseBlockComment(blockComment);
                return;
            }
        }

        this.advance(1);
    }

    private Optional<String> parseEscapedCharacter() {
        for (String escapeSequence : this.settings.escapeSequences()) {
            if (remainingContent.startsWith(escapeSequence)) {
                return Optional.of(this.advance(escapeSequence.length() + 1));
            }
        }
        return Optional.empty();
    }

    private void parseNoCommentEnvironment(EnvironmentDelimiter environment) throws UnexpectedStringException {
        this.parseEnvironment(environment);
    }

    private void parseLineComment() {
        StringBuilder comment = new StringBuilder();
        int startLine = this.currentLine;
        int startCol = this.currentCol;
        while (!this.remainingContent.startsWith(System.lineSeparator()) && !this.remainingContent.isEmpty()) {
            comment.append(this.advance(1));
        }
        this.comments.add(new Comment(file, comment.toString(), startLine, startCol, CommentType.LINE));
    }

    private void parseBlockComment(EnvironmentDelimiter blockComment) throws UnexpectedStringException {
        int startLine = this.currentLine;
        int startCol = this.currentCol + blockComment.begin().length();
        String comment = this.parseEnvironment(blockComment);

        this.comments.add(new Comment(file, comment, startLine, startCol, CommentType.BLOCK));
    }

    private String parseEnvironment(EnvironmentDelimiter environment) throws UnexpectedStringException {
        this.match(environment.begin());
        StringBuilder environmentContent = new StringBuilder();

        while (!this.remainingContent.isEmpty()) {
            Optional<String> escaped = parseEscapedCharacter();
            if (escaped.isPresent()) {
                environmentContent.append(escaped.get());
            } else {
                if (this.remainingContent.startsWith(environment.end())) {
                    this.match(environment.end());
                    break;
                }

                environmentContent.append(this.advance(1));
            }
        }

        return environmentContent.toString();
    }
}