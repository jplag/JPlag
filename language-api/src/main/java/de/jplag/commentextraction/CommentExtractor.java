package de.jplag.commentextraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.jplag.util.FileUtils;

/**
 * Extracts comments from submitted files, by reading and parsing the file content manually.
 * @author Moritz Rimpf
 */
public class CommentExtractor {

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
     * @throws IOException If an IO error during the file read occurred
     */
    public CommentExtractor(File file, CommentExtractorSettings settings) throws IOException {
        this(file, FileUtils.readFileContent(file), settings);
    }

    /**
     * Creates a new CommentExtractor, using the supplied file and content.
     * @param file File to associate comments with
     * @param fileContent Textual content of the file
     * @param settings Settings for the comment extractor
     */
    public CommentExtractor(File file, String fileContent, CommentExtractorSettings settings) {
        this.remainingContent = fileContent;
        this.settings = settings;
        this.comments = new ArrayList<>();
        this.lookBehind = "";
        this.file = file;
        this.currentCol = 1;
        this.currentLine = 1;
    }

    private void match(String expected) {
        if (remainingContent.startsWith(expected)) {
            this.advance(expected.length());
        } else {
            throw new RuntimeException("Matched incorrectly");
        }
    }

    private String advance(int length) {
        String advancedBy = remainingContent.substring(0, length);
        remainingContent = remainingContent.substring(length);

        // Checking for line breaks
        String combined = lookBehind + advancedBy;
        if (combined.contains(System.lineSeparator())) {
            String[] lines = combined.split(System.lineSeparator(), -1);
            this.currentLine += lines.length - 1;
            String lastLine = lines[lines.length - 1];
            this.currentCol = lastLine.length() + 1;
            lookBehind = lastLine.substring(Math.max(0, lastLine.length() - System.lineSeparator().length() + 1));
        } else {
            this.currentCol += length;
            lookBehind = advancedBy.substring(Math.max(0, advancedBy.length() - System.lineSeparator().length() + 1));
        }

        return advancedBy;
    }

    /**
     * Extracts all comments from the input file and returns them in a list.
     * @return All extracted comments from the input file
     */
    public List<Comment> extract() {
        while (!remainingContent.isEmpty()) {
            this.parseAny();
        }
        return comments;
    }

    private void parseAny() {
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

    private void parseNoCommentEnvironment(EnvironmentDelimiter environment) {
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

    private void parseBlockComment(EnvironmentDelimiter blockComment) {
        int startLine = this.currentLine;
        int startCol = this.currentCol + blockComment.begin().length();
        String comment = this.parseEnvironment(blockComment);

        this.comments.add(new Comment(file, comment, startLine, startCol, CommentType.BLOCK));
    }

    private String parseEnvironment(EnvironmentDelimiter environment) {
        this.match(environment.begin());
        StringBuilder environmentContent = new StringBuilder();

        while (!this.remainingContent.isEmpty()) {
            Optional<String> escaped = parseEscapedCharacter();
            if (escaped.isPresent()) {
                environmentContent.append(escaped.get());
                continue;
            }

            if (this.remainingContent.startsWith(environment.end())) {
                this.match(environment.end());
                break;
            }

            environmentContent.append(this.advance(1));
        }

        return environmentContent.toString();
    }
}