package de.jplag;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public static final String NON_WHITESPACE = "\\S";
    public static final int MIN_PADDING = 1;
    private static final Logger logger = LoggerFactory.getLogger(TokenPrinter.class);
    // Representation:
    private static final String BAR = "|";
    private static final String TAB = "\t";
    private static final String SPACE = " ";
    private static final int TAB_LENGTH = 8;
    private static final String TAB_REPLACEMENT = SPACE.repeat(TAB_LENGTH); // might depend on files

    // Configuration:
    private static final boolean INDICATE_TINY_TOKEN = true;    // print token with length <= 1 in lowercase
    private static final boolean REPLACE_TABS = false;
    private static final boolean PRINT_EMPTY_LINES = true;      // print code lines with no tokens
    private static final boolean SPACIOUS = true;               // print empty line after last token of a line

    private TokenPrinter() {
        // Utility class, no public constructor.
    }

    /**
     * Creates a string representation of a set of files line by line and adds the tokens under the lines.
     * @param tokens is the set of tokens parsed from the files.
     * @param directory is the common directory of the files.
     * @param fileNames is a collection of the file names.
     * @param suffix is the optional view file suffix.
     * @return the string representation.
     */
    public static String printTokens(TokenList tokens, File directory, Collection<String> fileNames, Optional<String> suffix) {
        Collection<File> files = fileNames.stream().map(name -> new File(directory, name)).toList();
        return printTokens(tokens, files, directory, suffix);
    }

    /**
     * Creates a string representation of a set of files line by line and adds the tokens under the lines.
     * @param tokens is the set of tokens parsed from the files.
     * @param directory is the common directory of the files.
     * @param fileNames is a collection of the file names.
     * @return the string representation.
     */
    public static String printTokens(TokenList tokens, File directory, Collection<String> fileNames) {
        Collection<File> files = fileNames.stream().map(name -> new File(directory, name)).toList();
        return printTokens(tokens, files, directory, Optional.empty());
    }

    /**
     * Creates a string representation of a collection of files line by line and adds the tokens under the lines.
     * @param tokens is the set of tokens parsed from the files.
     * @param files are the parsed files.
     * @return the string representation.
     */
    public static String printTokens(TokenList tokens, Collection<File> files, File root) {
        return printTokens(tokens, files, root, Optional.empty());
    }

    /**
     * Creates a string representation of a collection of files line by line and adds the tokens under the lines.
     * @param tokenList is the set of tokens parsed from the files.
     * @param files are the parsed files.
     * @param root is the common directory of the files.
     * @param suffix is the optional view file suffix.
     * @return the string representation.
     */
    public static String printTokens(TokenList tokenList, Collection<File> files, File root, Optional<String> suffix) {
        PrinterOutputBuilder builder = new PrinterOutputBuilder();
        Map<String, List<LineData>> fileToLineData = getLineData(files, tokenList, suffix, root);

        fileToLineData.forEach((String fileName, List<LineData> lineDatas) -> {
            builder.append(fileName);

            LineData.forEachApply(lineDatas, (String file, Integer lineNumber, String currentLine, List<Token> tokens) -> {
                builder.setLine(lineNumber);

                // Print (prefix and) code line
                builder.appendCodeLine(currentLine);

                if (tokens.isEmpty()) {
                    return;
                }

                builder.appendTokenLinePrefix();

                // Print tokens
                for (Token token : tokens) {
                    // Move to token index, possibly adding a new line:
                    builder.advanceToTokenPosition(currentLine, token.getColumn(), true);

                    // Print the actual token:
                    String stringRepresentation = getStringRepresentation(token);
                    builder.append(BAR).append(stringRepresentation);

                    // Move up to token end:
                    int tokenEndIndex = token.getColumn() + token.getLength() - 1;
                    builder.advanceToTokenPosition(currentLine, tokenEndIndex, false);

                    // Print token end if not already past it:
                    if (builder.positionBeforeOrEqualTo(tokenEndIndex)) {
                        builder.append(BAR);
                    }
                }

                builder.appendTokenLineSuffix();
            });
            builder.advanceToNextLine();
        });

        return builder.toString();
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
     * Turns a list of files and a list of tokens into a map of the file name to [a list of LineData objects for the lines
     * of the file].
     * @param allFiles a collection of files
     * @param allTokens a TokenList containing all tokens of the files
     * @param suffix the optional view file suffix
     * @param root the root element of the submission
     * @return map of file -> [LineData]
     */
    private static Map<String, List<LineData>> getLineData(Collection<File> allFiles, TokenList allTokens, Optional<String> suffix, File root) {
        Map<String, List<Token>> filesToTokens = allTokens.stream().collect(Collectors.groupingBy(Token::getFile));
        Map<String, List<String>> filesToLines = readFiles(allFiles);

        // handle 'files as submissions' mode
        final String EMPTY_STRING = "";
        if (filesToTokens.containsKey(EMPTY_STRING)) {
            filesToTokens.put(root.getName(), filesToTokens.get(EMPTY_STRING));
            filesToTokens.remove(EMPTY_STRING);
        }

        // Sort tokens by file and line -> tokens can be processed without any further checks
        Function<File, List<LineData>> fileToLineDataList = file -> {
            String fileName = file.getName();
            String tokenFileName = restoreTokenFileName(file.getName(), suffix);

            List<String> lines = filesToLines.get(fileName);
            Map<Integer, List<Token>> lineNumbersToTokens = filesToTokens.get(tokenFileName).stream().collect(Collectors.groupingBy(Token::getLine));

            // create LineData for each line -- 1-based line index
            return IntStream.range(1, lines.size() + 1).mapToObj(
                    lineIndex -> new LineData(fileName, lineIndex, lines.get(lineIndex - 1), lineNumbersToTokens.getOrDefault(lineIndex, List.of())))
                    .filter(lineData -> PRINT_EMPTY_LINES || !lineData.tokens.isEmpty()).toList();
        };

        return allFiles.stream().collect(toMap(File::getName, fileToLineDataList));
    }

    /**
     * Returns the fileName with the given suffix cut off at the end, if any.
     * @param fileName the file name with a suffix
     * @param suffix the suffix to cut off
     * @return the shortened file name
     */
    private static String restoreTokenFileName(String fileName, Optional<String> suffix) {
        if (suffix.isEmpty()) {
            return fileName;
        } else if (!fileName.endsWith(suffix.get())) {
            return fileName;
        }

        int newLength = fileName.length() - suffix.get().length();
        return fileName.substring(0, newLength);
    }

    /**
     * Parses a file and returns a list of the contained lines.
     */
    private static List<String> linesFromFile(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (NoSuchFileException exception) {
            logger.error("File does not exist, thus no tokens are printed: " + file.getAbsolutePath());
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

    /**
     * This contains all data concerning a line of code in a file and the tokens found in that line.
     * @param file the file
     * @param lineNumber the line number inside the file
     * @param text the code line
     * @param tokens the tokens found in the code line
     */
    private record LineData(String file, Integer lineNumber, String text, List<Token> tokens) {

        /**
         * Applies the given LineDataConsumer to this LineData.
         * @param consumer the LineDataConsumer
         */
        public void apply(LineDataConsumer consumer) {
            consumer.apply(file, lineNumber, text, tokens);
        }

        /**
         * Convenience method to apply a LineDataConsumer to all lineDatas in the given collection.
         * @param lineDatas the LineData objects to apply the consumer to
         * @param consumer the LineDataConsumer to apply
         */
        public static void forEachApply(Collection<LineData> lineDatas, LineDataConsumer consumer) {
            lineDatas.forEach(lineData -> lineData.apply(consumer));
        }

        /**
         * The LineDataConsumer interface allows to declare variables for all fields of a LineData object in a functional
         * interface, thus saving manually declaring variables for each one.
         */
        @FunctionalInterface
        interface LineDataConsumer {
            void apply(String file, Integer lineNumber, String text, List<Token> tokens);
            default void apply(LineData lineData) {
                apply(lineData.file, lineData.lineNumber, lineData.text, lineData.tokens);
            }
        }
    }

    /**
     * A proxy for the StringBuilder that keeps track of the position inside the output.
     */
    private static class PrinterOutputBuilder {
        public static final String LINE_SEPARATOR = System.lineSeparator();
        private final StringBuilder builder = new StringBuilder();
        private int columnIndex = 1;
        private int lineNumber;
        private int trailingLineSeparators = 0;

        private void resetLinePosition() {
            columnIndex = 1;
        }

        /**
         * Appends the given string to the output
         * @param str the string to append
         * @return this
         */
        public PrinterOutputBuilder append(String str) {
            //Avoid too many blank lines
            trailingLineSeparators = str.equals(LINE_SEPARATOR) ? trailingLineSeparators + 1 : 0;
            if (trailingLineSeparators >= 2)
                return this;

            builder.append(str);
            columnIndex += str.length();
            return this;
        }

        /**
         * Appends the given integer to the output.
         * @param i the integer to append
         * @return this
         */
        public PrinterOutputBuilder append(int i) {
            return append(Integer.toString(i));
        }

        /**
         * In SPACIOUS mode, appends an empty line before the next code line.
         * @return a reference to this
         */
        private PrinterOutputBuilder appendTokenLineSuffix() {
            return SPACIOUS ? advanceToNextLine() : this;
        }

        /**
         * Appends the code line to the StringBuilder, applying the configuration of this TokenPrinter.
         * @param currentLine The line of code to append
         * @return a reference to this
         */
        private PrinterOutputBuilder appendCodeLine(String currentLine) {
            // Prefix
            advanceToNextLine().append(lineNumber);

            // Code line should be padded up to at least one tab length, and no less than MIN_PADDING
            int paddingLength = Math.max(TAB_LENGTH - digitCount(lineNumber), MIN_PADDING);
            appendPadding(paddingLength);

            // Code line
            if (REPLACE_TABS) {
                currentLine = currentLine.replaceAll(TAB, TAB_REPLACEMENT);
            }

            return append(currentLine);
        }

        /**
         * Appends whitespace padding to the given StringBuilder in order to reach the targetPosition. Note that <b>the indices
         * are 1-based</b>, whereas the positions in currentLine are 0-based. The convention that lines start at position 1
         * comes from the frontends, specifically the Java frontend.
         * @param currentLine The current line in the code file, indicating where it containes tab characters
         * @param targetPosition The (1-based) index of the next character within the currentLine that should be labeled
         * @param breakLine If true, a lineSeparator will be added if the currentPosition is past targetPosition.
         * @return the new position, which is the targetPosition or the end of the line.
         */
        PrinterOutputBuilder advanceToTokenPosition(String currentLine, int targetPosition, boolean breakLine) {
            targetPosition = Math.min(targetPosition, currentLine.length());
            if (positionBeforeOrEqualTo(targetPosition)) {
                if (!breakLine) {
                    // we are past targetPosition, do nothing
                    return this;
                }

                // new line
                appendTokenLinePrefix();
                if (positionBeforeOrEqualTo(targetPosition)) {
                    // still past targetPosition -> negative targetPosition
                    return this;
                }
            }

            // The replacement operation preserves TAB characters, which is essential for correct alignment
            String padding = currentLine.substring(columnIndex - 1, targetPosition - 1).replaceAll(TAB, REPLACE_TABS ? TAB_REPLACEMENT : TAB)
                    .replaceAll(NON_WHITESPACE, SPACE);
            append(padding);

            return this;
        }

        /**
         * Returns true if the current position is before or at the target position.
         * @param targetPosition the target position
         * @return true if before or at target position
         */
        private boolean positionBeforeOrEqualTo(int targetPosition) {
            return columnIndex > targetPosition;
        }

        /**
         * Appends a newline, plus the amount of padding necessary to align tokens correctly.
         * @return this
         */
        PrinterOutputBuilder appendTokenLinePrefix() {
            int paddingLength = Math.max(digitCount(lineNumber) + MIN_PADDING, TAB_LENGTH);
            return advanceToNextLine().appendPadding(paddingLength);
        }

        /**
         * Appends the amount of padding given.
         * @param paddingLength Length of padding to add
         * @return this
         */
        private PrinterOutputBuilder appendPadding(int paddingLength) {
            String padding = SPACE.repeat(paddingLength);
            append(padding);
            return this;
        }

        /**
         * Appends a newline.
         * @return this
         */
        private PrinterOutputBuilder advanceToNextLine() {
            append(LINE_SEPARATOR);
            resetLinePosition();
            return this;
        }

        public void setLine(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }
}
