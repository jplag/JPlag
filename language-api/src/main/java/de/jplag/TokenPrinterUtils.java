package de.jplag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import de.jplag.util.FileUtils;

/**
 * Utility functions for {@link TokenPrinter}.
 */
public class TokenPrinterUtils {
    private TokenPrinterUtils() {
    }

    /**
     * Prints the all tokens sorted by their files.
     * @param tokens The token to print
     * @return The files with token annotations
     */
    public static String printTokensByFile(List<Token> tokens) {
        return printTokensByFile(tokens, UnaryOperator.identity());
    }

    /**
     * Prints the all tokens sorted by their files.
     * @param tokens The token to print
     * @param fileMapper Maps the file of each token to a different one. Can be used if the file of the token doesn't match
     * the source file
     * @return The files with token annotations
     */
    public static String printTokensByFile(List<Token> tokens, UnaryOperator<File> fileMapper) {
        Map<File, List<Token>> groups = groupByFile(tokens);

        StringBuilder outputBuilder = new StringBuilder();

        for (Map.Entry<File, List<Token>> entry : groups.entrySet()) {
            outputBuilder.append(entry.getKey().getAbsolutePath()).append(":").append(System.lineSeparator());
            try {
                outputBuilder.append(printTokensForFile(entry.getValue(), fileMapper.apply(entry.getKey()))).append(System.lineSeparator())
                        .append(System.lineSeparator());
            } catch (IOException e) {
                outputBuilder.append("Could not print tokens: ").append(System.lineSeparator());
                outputBuilder.append(e.getMessage()).append(System.lineSeparator()).append(System.lineSeparator());
            }
        }

        return outputBuilder.toString();
    }

    /**
     * Print the tokens assuming they belong to the given file.
     * @param tokens The tokens to print
     * @param file The file
     * @return The printed tokens
     * @throws IOException If the file cannot be read
     */
    public static String printTokensForFile(List<Token> tokens, File file) throws IOException {
        return new TokenPrinter(List.of(FileUtils.readFileContent(file).split(System.lineSeparator())), tokens).printTokens();
    }

    private static Map<File, List<Token>> groupByFile(List<Token> tokens) {
        Map<File, List<Token>> groups = new HashMap<>();

        for (Token token : tokens) {
            groups.computeIfAbsent(token.getFile(), _ -> new ArrayList<>());
            groups.get(token.getFile()).add(token);
        }

        return groups;
    }
}
