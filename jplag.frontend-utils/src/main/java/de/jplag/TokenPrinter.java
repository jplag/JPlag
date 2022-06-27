package de.jplag;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for printing JPlag tokens from a submission. Each line of code is printed starting with the line
 * number. Under these lines the tokens are annotated in the format <code>|TOKEN|</code>. The first vertical line marks
 * the token start, while the last vertical line marks the token end. Tokens that are shorter than the name do not end
 * with a vertical line, e.g. <code>|TOKEN</code>. Tokens with length 1 or 0 are printed in lower case, e.g.
 * <code>|token</code>.
 * @author Timur Saglam
 */
public final class TokenPrinter {
    private static final Logger logger = LoggerFactory.getLogger(TokenPrinter.class);
    // Representation:
    private static final String BAR = "|";
    private static final String TAB = "\t";
    private static final String SPACE = " ";
    public static final String NON_WHITESPACE = "\\S";
    public static final int MIN_PADDING = 1;
    private static final int TAB_LENGTH = 8;
    private static final String TAB_REPLACEMENT = SPACE.repeat(TAB_LENGTH); // might depend on files

    // Configuration:
    private static final boolean INDICATE_TINY_TOKEN = true; // print token with length <= 1 in lowercase
    private static final boolean REPLACE_TABS = false;

    private TokenPrinter() {
        // Utility class, no public constructor.
    }

    /**
     * Creates a string representation of a set of files line by line and adds the tokens under the lines.
     * @param tokens is the set of tokens parsed from the files.
     * @param directory is the common directory of the files.
     * @param fileNames is a collection of the file names.
     * @return the string representation.
     */
    public static String printTokens(TokenList tokens, File directory, Collection<String> fileNames) {
        Collection<File> files = fileNames.stream().map(name -> new File(directory, name)).collect(toList());
        return printTokens(tokens, files, directory);
    }

    /**
     * Creates a string representation of a collection of files line by line and adds the tokens under the lines.
     * @param tokens is the set of tokens parsed from the files.
     * @param files are the parsed files.
     * @return the string representation.
     */
    public static String printTokens(TokenList tokens, Collection<File> files, File root) {
        Map<String, List<String>> filesToLines = readFiles(files);
        StringBuilder builder = new StringBuilder();
        int lineIndex = 0;
        int columnIndex = 1;
        String file = null; // no file yet
        String currentLine = null;

        for (Token token : tokens.allTokens()) {
            // New code file:
            if (!token.getFile().equals(file)) {
                if (file != null) {
                    builder.append(System.lineSeparator());
                }
                file = token.getFile();
                builder.append(file);
                lineIndex = 0;
            }

            // New code line:
            if (token.getLine() > lineIndex) {
                lineIndex = token.getLine();
                columnIndex = 1;

                String fileName = token.getFile().isEmpty() ? root.getName() : token.getFile();
                currentLine = filesToLines.get(fileName).get(lineIndex - 1);

                appendCodeLinePrefix(builder, lineIndex);
                appendCodeLine(builder, currentLine);
                appendTokenLinePrefix(builder, lineIndex);
            }

            assert currentLine != null;

            // New line if already past token index:
            if (columnIndex > token.getColumn()) {
                appendTokenLinePrefix(builder, lineIndex);
                columnIndex = 1;
            }
            // Move to token index:
            columnIndex = appendPadding(builder, currentLine, columnIndex, token.getColumn());

            // Print the actual token:
            String stringRepresentation = getStringRepresentation(token);
            builder.append(BAR).append(stringRepresentation);
            columnIndex += stringRepresentation.length() + 1;

            // Move up to token end:
            int tokenEndIndex = token.getColumn() + token.getLength() - 1;
            columnIndex = appendPadding(builder, currentLine, columnIndex, tokenEndIndex);

            // Print token end if not already past it:
            if (columnIndex == tokenEndIndex) {
                builder.append(BAR);
                columnIndex++;
            }
        }
        return builder.toString();
    }

    /**
     * Appends the code line to the StringBuilder, applying the configuration of this TokenPrinter.
     * @param builder The StringBuilder to add code to
     * @param currentLine The line of code to append
     * @return a reference to that same StringBuilder
     */
    private static StringBuilder appendCodeLine(StringBuilder builder, String currentLine) {
        if (REPLACE_TABS) {
            currentLine = currentLine.replaceAll(TAB, TAB_REPLACEMENT);
        }
        return builder.append(currentLine);
    }

    /**
     * Appends whitespace padding to the given StringBuilder in order to reach the targetPosition. Note that <b>the indices
     * are 1-based</b>, whereas the positions in currentLine are 0-based. The convention that lines start at position 1
     * comes from the frontends, specifically the Java frontend.
     * @param builder The StringBuilder to add padding to
     * @param currentLine The current line in the code file, indicating where it containes tab characters
     * @param currentPosition The (1-based) index of the last character within the currentLine that is labeled, or 1.
     * @param targetPosition The (1-based) index of the next character within the currentLine that should be labeled
     * @return the new position, which is the targetPosition or the end of the line.
     */
    private static int appendPadding(StringBuilder builder, String currentLine, int currentPosition, int targetPosition) {
        targetPosition = Math.min(targetPosition, currentLine.length());
        if (currentPosition >= targetPosition) {
            return currentPosition;
        }

        // The replacement operation preserves TAB characters, which is essential for correct alignment
        String padding = currentLine.substring(currentPosition - 1, targetPosition - 1).replaceAll(TAB, REPLACE_TABS ? TAB_REPLACEMENT : TAB)
                .replaceAll(NON_WHITESPACE, SPACE);
        builder.append(padding);

        return targetPosition;
    }

    private static StringBuilder appendCodeLinePrefix(StringBuilder builder, int index) {
        builder.append(System.lineSeparator()).append(index);

        // Code line should be padded up to at least one tab length, and no less than MIN_PADDING
        int paddingLength = Math.max(TAB_LENGTH - digitCount(index), MIN_PADDING);
        return builder.append(SPACE.repeat(paddingLength));
    }

    private static StringBuilder appendTokenLinePrefix(StringBuilder builder, int index) {
        int paddingLength = Math.max(digitCount(index) + MIN_PADDING, TAB_LENGTH);
        String padding = SPACE.repeat(paddingLength);
        return builder.append(System.lineSeparator()).append(padding);
    }

    /**
     * Determines the string representation of the token.
     */
    private static String getStringRepresentation(Token token) {
        return token.getLength() <= 1 && INDICATE_TINY_TOKEN ? token.toString().toLowerCase() : token.toString();
    }

    /**
     * Turns a list of files into a map which maps the file names to a list of the contained lines of that file.
     */
    private static Map<String, List<String>> readFiles(Collection<File> allFiles) {
        return allFiles.stream().collect(toMap(File::getName, TokenPrinter::linesFromFile));
    }

    /**
     * Parses a file and returns a list of the contained lines.
     */
    private static List<String> linesFromFile(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException exception) {
            logger.error("Cannot read " + file.getAbsolutePath() + ":", exception);
        }
        return Collections.emptyList();
    }

    /**
     * Returns the number of digits (including a minus) of the given number.
     */
    private static int digitCount(int number) {
        if (number == 0) {
            return 1;
        }
        int minusLength = number < 0 ? 1 : 0;
        // The 'log10' variant is supposedly faster than the 'toString' variant.
        return (int) Math.log10(Math.abs(number)) + minusLength + 1;
    }
}
