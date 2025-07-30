package de.jplag.commenthandling;

import java.io.File;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.commentextraction.Comment;
import de.jplag.text.ParserAdapter;

/**
 * Preprocesses comments into tokens by running them through the JPlag text module.
 */
public class CommentPreprocessor {
    private static final Logger logger = LoggerFactory.getLogger(CommentPreprocessor.class);

    private final List<Comment> comments;
    private final ParserAdapter textParser;

    /**
     * Creates a new preprocessor for the supplied list of comments.
     * @param comments Comments to prcess
     */
    public CommentPreprocessor(List<Comment> comments) {
        this.comments = comments;
        this.comments.sort(Comparator.comparing(Comment::file));
        this.textParser = new ParserAdapter();
    }

    /**
     * Processes all input comments into a list of tokens.
     * @return List of tokens containing all comments
     */
    public List<Token> processToToken() {
        List<Token> result = new ArrayList<>();
        File lastFile = null;
        for (Comment comment : comments) {
            result.addAll(processSingleCommentToToken(comment));
            if (comment.file() != lastFile) {
                if (lastFile != null) {
                    result.add(Token.fileEnd(lastFile));
                }
                lastFile = comment.file();
            }
        }
        result.add(Token.fileEnd(lastFile));
        return result;
    }

    private List<Token> processSingleCommentToToken(Comment comment) {
        try {
            return fixTokenPositions(textParser.parseStrings(Set.of(comment.content())), comment);
        } catch (ParsingException e) {
            logger.error("Could not parse comment: {}", e.getMessage());
            return List.of();
        }
    }

    private List<Token> fixTokenPositions(List<Token> tokens, Comment comment) {
        List<Token> fixedTokens = new ArrayList<>();
        for (Token token : tokens) {
            if (token.getType() == SharedTokenType.FILE_END) {
                continue;
            }

            int line = comment.line() + token.getLine() - 1;
            int column = token.getColumn();
            if (token.getLine() == 1) {
                column += comment.column() - 1;
            }

            fixedTokens.add(new Token(token.getType(), comment.file(), line, column, token.getLength()));
        }
        return fixedTokens;
    }
}
