package de.jplag.testutils.datacollector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.testutils.TemporaryFileHolder;
import de.jplag.util.FileUtils;

/**
 * Test sources with token information Reads token position test specifications form a file and provides the token
 * information for tests. The sources can be used as regular test sources.
 */
public class TokenPositionTestData implements TestData {
    private static final String INVALID_LINE_ERROR_MESSAGE = "Invalid line for token position test %s at line: %s";
    private static final String TOKEN_DEFINITION_LINE_REGEX = "\\$[\\s]*\\| [a-zA-Z0-9_]+ [0-9]+[\\s]*";
    private static final char SOURCE_FILE_LINE_PREFIX = '>';
    private static final char TOKEN_LINE_PREFIX = '$';
    private static final char COMMENT_LINE_PREFIX = '#';
    private static final char TOKEN_COLUMN_MARKER = '|';

    private final List<String> sourceLines;
    private final List<TokenData> expectedTokens;

    private final String descriptor;
    private final String fileName;

    /**
     * @param testFile The file containing the test specifications
     * @throws IOException If the file cannot be read
     */
    public TokenPositionTestData(File testFile) throws IOException {
        this.sourceLines = new ArrayList<>();
        this.expectedTokens = new ArrayList<>();
        this.descriptor = "(Token position file: " + testFile.getName() + ")";
        this.fileName = testFile.getName();
        this.readFile(testFile);
    }

    private void readFile(File testFile) throws IOException {
        List<String> testFileLines = FileUtils.readFileContent(testFile).lines().toList();
        int currentLine = 0;

        for (String sourceLine : testFileLines) {
            if (!sourceLine.isBlank()) {
                switch (sourceLine.charAt(0)) {
                    case SOURCE_FILE_LINE_PREFIX -> {
                        this.sourceLines.add(sourceLine.substring(1));
                        currentLine++;
                    }

                    case TOKEN_LINE_PREFIX -> {
                        this.extractTokenData(sourceLine, currentLine);
                        int column = sourceLine.indexOf(TOKEN_COLUMN_MARKER);
                        String[] tokenDescriptionParts = sourceLine.split(" ", 0);

                        String typeName = tokenDescriptionParts[tokenDescriptionParts.length - 2];
                        int length = Integer.parseInt(tokenDescriptionParts[tokenDescriptionParts.length - 1]);
                        this.expectedTokens.add(new TokenData(typeName, currentLine, column, currentLine, column + length));
                    }

                    case COMMENT_LINE_PREFIX -> {
                        // Line is considered a comment
                    }

                    default -> Assertions.fail(String.format(INVALID_LINE_ERROR_MESSAGE, this.descriptor, sourceLine));
                }
            }
        }
    }

    private void extractTokenData(String line, int currentSourceLine) {
        if (!line.matches(TOKEN_DEFINITION_LINE_REGEX)) {
            Assertions.fail("Invalid line for token position test " + this.descriptor + " at line: " + line);
        }

        int column = line.indexOf('|');
        String[] tokenDescriptionParts = line.split(" ", 0);

        String typeName = tokenDescriptionParts[tokenDescriptionParts.length - 2];
        int length = Integer.parseInt(tokenDescriptionParts[tokenDescriptionParts.length - 1]);
        this.expectedTokens.add(new TokenData(typeName, currentSourceLine, column, currentSourceLine, column + length));
    }

    @Override
    public List<Token> parseTokens(Language language) throws ParsingException, IOException {
        File file = File.createTempFile("testSource", language.fileExtensions().getFirst());
        FileUtils.write(file, String.join(System.lineSeparator(), sourceLines));
        List<Token> tokens = language.parse(Collections.singleton(file), false);
        TemporaryFileHolder.addTemporaryFile(file);
        return tokens;
    }

    @Override
    public String[] getSourceLines() {
        return this.sourceLines.toArray(new String[0]);
    }

    @Override
    public String describeTestSource() {
        return this.descriptor;
    }

    /**
     * @return A list of the expected tokens for this test source
     */
    public List<TokenData> getExpectedTokens() {
        return expectedTokens;
    }

    /**
     * Information about a single token.
     * @param typeName The name of the token type
     * @param startLine The line the token starts in (1 based)
     * @param startColumn The column the token starts (1 based)
     * @param endLine The line the token ends in (1 based)
     * @param endColumn The column the token end (1 based)
     */
    public record TokenData(String typeName, int startLine, int startColumn, int endLine, int endColumn) {
    }

    @Override
    public String toString() {
        return this.fileName;
    }
}
