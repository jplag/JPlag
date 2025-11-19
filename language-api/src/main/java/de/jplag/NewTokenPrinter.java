package de.jplag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Visualizes tokens by printing their positions along with the source code to help with debugging. Tokens are
 * visualized using Unicode box drawing characters to mark the ranges
 */
public class NewTokenPrinter {
    private static final String ZERO_WIDTH_TOKEN_MARKER = "┴";
    private static final String TOKEN_START = "└";
    private static final String TOKEN_END = "┘";
    private static final String TOKEN_CONT = "─";

    private final List<String> fileLines;
    private final int maxLineLength;
    private final String lineNumberIndent;
    private final int lineNumberLength;
    private final StringBuilder outputBuilder;

    private final List<List<Token>> tokensByLine;

    /**
     * Creates a new token printer
     * @param fileLines The lines of the file
     * @param allTokens The list of all tokens
     */
    public NewTokenPrinter(List<String> fileLines, List<Token> allTokens) {
        this.fileLines = fileLines;
        this.maxLineLength = fileLines.stream().mapToInt(String::length).max().orElse(0);
        this.lineNumberLength = String.valueOf(fileLines.size() + 1).length();
        this.lineNumberIndent = " ".repeat(this.lineNumberLength + 1);
        this.outputBuilder = new StringBuilder();

        this.tokensByLine = new ArrayList<>();
        for (int i = 0; i < fileLines.size(); i++) {
            this.tokensByLine.add(new ArrayList<>());
        }
        for (Token token : allTokens) {
            if (token.getStartLine() > 0) {
                tokensByLine.get(token.getStartLine() - 1).add(token);
            }
        }
        for (List<Token> lineTokens : this.tokensByLine) {
            lineTokens.sort(Comparator.comparingInt(Token::getStartColumn));
        }
    }

    /**
     * Prints the tokens and the source code
     * @return The visualization as a single string
     */
    public String printTokens() {
        this.outputBuilder.setLength(0);
        printTokensToOutputBuilder();
        return this.outputBuilder.toString();
    }

    /**
     * Prints the source code and the tokens to the output builder
     */
    private void printTokensToOutputBuilder() {
        List<Token> continuations = Collections.emptyList();

        for (int i = 0; i < fileLines.size(); i++) {
            String lineNumber = String.valueOf(i + 1);
            outputBuilder.append("0".repeat(lineNumberLength - lineNumber.length()));
            outputBuilder.append(lineNumber);
            outputBuilder.append(" ");
            outputBuilder.append(fileLines.get(i));
            outputBuilder.append(System.lineSeparator());

            continuations = printSourceLineTokens(continuations, i + 1);
        }
    }

    /**
     * Prints the tokens for the current source line
     * @param continuations The tokens that have to be continued from the previous line
     * @param currentLine The index of the current line (1-based)
     * @return The list of tokens that have to be continued
     */
    private List<Token> printSourceLineTokens(List<Token> continuations, int currentLine) {
        List<TokenLineBuilder> oldLines = new ArrayList<>();
        List<TokenLineBuilder> newLines = new ArrayList<>();
        List<Token> oldContinuedTokens = new ArrayList<>();
        List<Token> newContinuedTokens = new ArrayList<>();

        for (Token token : continuations) {
            TokenLineBuilder continuationLine = new TokenLineBuilder();
            if (token.getEndLine() == currentLine) {
                continuationLine.addContinuedTokenEnd(token);
            } else {
                continuationLine.addContinuedThroughToken(token);
                oldContinuedTokens.add(token);
            }
            oldLines.add(continuationLine);
        }

        for (Token lineToken : this.tokensByLine.get(currentLine - 1)) {
            TokenLineBuilder line = findMatchingLine(lineToken.getStartColumn(), newLines, oldLines).orElseGet(() -> {
                TokenLineBuilder builder = new TokenLineBuilder();
                newLines.add(builder);
                return (builder);
            });

            if (lineToken.getStartLine() == lineToken.getEndLine()) {
                line.addInLineToken(lineToken);
            } else {
                line.addStartingToken(lineToken);
                newContinuedTokens.add(lineToken);
            }
        }

        printTokenLines(newLines);
        printTokenLines(oldLines);

        List<Token> newContinuations = new ArrayList<>();
        newContinuations.addAll(newContinuedTokens);
        newContinuations.addAll(oldContinuedTokens);
        return newContinuations;
    }

    /**
     * Prints the token lines to the output builder
     * @param lines The list of lines to print
     */
    private void printTokenLines(List<TokenLineBuilder> lines) {
        for (TokenLineBuilder line : lines) {
            outputBuilder.append(lineNumberIndent);
            outputBuilder.append(line.toString());
            outputBuilder.append(System.lineSeparator());
        }
    }

    /**
     * Finds a line that still has space at the given position by examining first set1 and then set2
     * @param startPosition The position to look for
     * @param set1 The first set
     * @param set2 The second set
     * @return The first line with space or null
     */
    private Optional<TokenLineBuilder> findMatchingLine(int startPosition, List<TokenLineBuilder> set1, List<TokenLineBuilder> set2) {
        Optional<TokenLineBuilder> resultFromFirst = set1.stream().filter(builder -> builder.canPrintForIndex(startPosition)).findFirst();
        if (resultFromFirst.isPresent()) {
            return resultFromFirst;
        }

        return set2.stream().filter(builder -> builder.canPrintForIndex(startPosition)).findFirst();
    }

    /**
     * Helper for creating the token annotation lines
     */
    private class TokenLineBuilder {
        private final StringBuilder lineBuilder;

        private TokenLineBuilder() {
            this.lineBuilder = new StringBuilder();
        }

        boolean canPrintForIndex(int index) {
            return index >= this.lineBuilder.length() + 1;
        }

        /**
         * Adds a token that starts and ends in the current line
         * @param token The token to add
         */
        void addInLineToken(Token token) {
            lineBuilder.append(" ".repeat(token.getStartColumn() - this.lineBuilder.length() - 1));
            int tokenLength = token.getEndColumn() - token.getStartColumn();
            String description = token.getType().getDescription();

            if (tokenLength == 0) {
                lineBuilder.append(ZERO_WIDTH_TOKEN_MARKER);
                lineBuilder.append(" ");
                lineBuilder.append(description);
            } else if (tokenLength - 1 < description.length()) {
                lineBuilder.append(TOKEN_START);
                lineBuilder.append(TOKEN_CONT.repeat(tokenLength - 1));
                lineBuilder.append(TOKEN_END);
                lineBuilder.append(" ");
                lineBuilder.append(description);
            } else {
                int numberOfFilledSpaces = (tokenLength - 1) - description.length();
                int firstHalf = numberOfFilledSpaces / 2;
                int secondHalf = numberOfFilledSpaces - firstHalf;

                lineBuilder.append(TOKEN_START);
                lineBuilder.append(TOKEN_CONT.repeat(firstHalf));
                lineBuilder.append(description);
                lineBuilder.append(TOKEN_CONT.repeat(secondHalf));
                lineBuilder.append(TOKEN_END);
            }
        }

        /**
         * Adds a token that starts in the current line and ends later
         * @param token The token to add
         */
        void addStartingToken(Token token) {
            lineBuilder.append(" ".repeat(token.getStartColumn() - this.lineBuilder.length() + 1));
            lineBuilder.append(TOKEN_START);
            lineBuilder.append(token.getType().getDescription());
            int remainingSpaces = maxLineLength - lineBuilder.length();
            lineBuilder.append(TOKEN_CONT.repeat(remainingSpaces));
        }

        /**
         * Adds information for a token that starts before the current line and ends later
         * @param token The token to add information for
         */
        void addContinuedThroughToken(Token token) {
            lineBuilder.append(TOKEN_CONT);
            lineBuilder.append(token.getType().getDescription());
            int remainingSpaces = maxLineLength - lineBuilder.length();
            lineBuilder.append(TOKEN_CONT.repeat(remainingSpaces));
        }

        /**
         * Adds a token that started in a previous line and ends in the current line
         * @param token The token to add
         */
        void addContinuedTokenEnd(Token token) {
            int numberOfContinues = token.getEndColumn() - 1;

            if (numberOfContinues - 1 < token.getType().getDescription().length()) {
                lineBuilder.append(TOKEN_CONT.repeat(numberOfContinues));
                lineBuilder.append(TOKEN_END);
                lineBuilder.append(" ");
                lineBuilder.append(token.getType().getDescription());
            } else {
                lineBuilder.append(numberOfContinues - token.getType().getDescription().length());
                lineBuilder.append(token.getType().getDescription());
                lineBuilder.append(TOKEN_END);
            }
        }

        @Override
        public String toString() {
            return this.lineBuilder.toString();
        }
    }
}
