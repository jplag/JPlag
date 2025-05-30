package de.jplag.commenthandling;

import de.jplag.ParsingException;
import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.commentextraction.Comment;
import de.jplag.text.NaturalLanguage;
import de.jplag.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CommentPreprocessor {
    private static final Logger logger = LoggerFactory.getLogger(CommentPreprocessor.class);

    private final List<Comment> comments;
    private final String submissionName;

    public CommentPreprocessor(List<Comment> comments, String submissionName) {
        this.comments = comments;
        this.submissionName = submissionName;
    }

    public List<Token> processToToken() {
        File tempFile;
        try {
            tempFile = this.createTempFile(this.buildCommentString());
        } catch (IOException e) {
            logger.error("Could not create temp file for comments: {}", e.getMessage());
            return List.of();
        }

        NaturalLanguage languageProcessor = new NaturalLanguage();
        try {
            return fixTokenPositions(languageProcessor.parse(Set.of(tempFile), false));
        } catch (ParsingException e) {
            logger.error("Could not parse comments: {}", e.getMessage());
            return List.of();
        }
    }

    public List<String> processToStrings() {
        List<Token> tokens = this.processToToken();
        HashMap<Integer, String> commentsPerLine = new HashMap<>();

        for (Token token : tokens) {
            if (token.getType() == SharedTokenType.FILE_END) {
                continue;
            }
            int line = token.getLine();
            String tokenContent = token.getType().getDescription();
            if (commentsPerLine.containsKey(line)) {
                commentsPerLine.put(line, commentsPerLine.get(line) + " " + tokenContent);
            } else {
                commentsPerLine.put(line, tokenContent);
            }
        }

        return new ArrayList<>(commentsPerLine.values());
    }

    private List<Token> fixTokenPositions(List<Token> tokens) {
        List<Token> fixedTokens = new ArrayList<>();
        for (Token token : tokens) {
            if (token.getType() == SharedTokenType.FILE_END) {
                continue;
            }
            Comment originalComment = this.comments.get(token.getLine() - 1);
            if (originalComment == null) {
                logger.warn("Original comment not found for token {}!", token);
                fixedTokens.add(token);
                continue;
            }

            int line = originalComment.line();
            // Incrementing line for line breaks within the comment (e.g. multiline comments)
            line += originalComment.content().substring(0, token.getColumn() - 1).split(System.lineSeparator(), -1).length - 1;

            Token newToken = new Token(
                token.getType(),
                originalComment.file(),
                line,
                token.getColumn() + originalComment.column() - 1,
                token.getLength(),
                token.getSemantics()
            );
            fixedTokens.add(newToken);
        }
        return fixedTokens;
    }

    private String buildCommentString() {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (Comment comment : comments) {
            String singleLineComment = comment.content().replaceAll("\\R", " ");
            joiner.add(singleLineComment);
        }
        return joiner.toString();
    }

    private File createTempFile(String contents) throws IOException {
        File tempFile = File.createTempFile("comments-" + this.submissionName, ".tmp");
        tempFile.deleteOnExit();
        FileUtils.write(tempFile, contents);
        return tempFile;
    }

}
