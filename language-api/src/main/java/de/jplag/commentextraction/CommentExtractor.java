package de.jplag.commentextraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.jplag.util.FileUtils;

public class CommentExtractor {

    private String remainingContent;
    private final List<String> comments;
    private final CommentExtractorSettings settings;

    public CommentExtractor(File file, CommentExtractorSettings settings) throws IOException {
        this(FileUtils.readFileContent(file), settings);
    }

    public CommentExtractor(String fileContent, CommentExtractorSettings settings) {
        this.remainingContent = fileContent;
        this.settings = settings;
        this.comments = new ArrayList<>();
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
        return advancedBy;
    }

    public List<String> extract() {
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
        while (!this.remainingContent.startsWith("\n") && !this.remainingContent.isEmpty()) {
            comment.append(this.remainingContent.charAt(0));
            this.advance(1);
        }
        this.comments.add("LINE COMMENT: " + comment.toString());
    }

    private void parseBlockComment(EnvironmentDelimiter blockComment) {
        String comment = this.parseEnvironment(blockComment);

        this.comments.add("BLOCK COMMENT: " + comment);
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