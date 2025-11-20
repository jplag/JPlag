package de.jplag.commenthandling;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.commentextraction.Comment;
import de.jplag.text.ParserAdapter;

/**
 * Preprocesses comments into tokens by running them through the JPlag text module.
 */
public class CommentPreprocessor {
    private CommentPreprocessor() {
    }

    /**
     * Processes all input comments into a list of tokens.
     * @param comments List of comments to process
     * @return List of tokens containing all comments
     */
    public static List<Token> processToToken(List<Comment> comments) {
        ParserAdapter textParser = new ParserAdapter();
        comments.sort(Comparator.comparing(Comment::file));
        List<Token> result = new ArrayList<>();
        File lastFile = null;
        for (Comment comment : comments) {
            if (comment.file() != lastFile) {
                if (lastFile != null) {
                    result.add(Token.fileEnd(lastFile));
                }
                lastFile = comment.file();
            }
            result.addAll(processSingleCommentToToken(comment, textParser));
        }
        result.add(Token.fileEnd(lastFile));
        return result;
    }

    private static List<Token> processSingleCommentToToken(Comment comment, ParserAdapter textParser) {
        return fixTokenPositions(textParser.parseStrings(Set.of(comment.content())), comment);
    }

    private static List<Token> fixTokenPositions(List<Token> tokens, Comment comment) {
        List<Token> fixedTokens = new ArrayList<>();
        for (Token token : tokens) {
            if (token.getType() == SharedTokenType.FILE_END) {
                continue;
            }

            int startLine = comment.line() + token.getStartLine() - 1;
            int endLine = comment.line() + token.getEndLine() - 1;
            int startColumn = token.getStartColumn();
            int endColumn = token.getEndColumn();
            if (token.getStartLine() == 1) {
                startColumn += comment.column() - 1;
            }
            if (token.getEndLine() == 1) {
                endColumn += comment.column() - 1;
            }
            int length = token.getType().getDescription().length();

            fixedTokens.add(new Token(token.getType(), comment.file(), startLine, startColumn, endLine, endColumn, length));
        }
        return fixedTokens;
    }
}
