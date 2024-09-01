package de.jplag.testutils.datacollector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private final List<String> sourceLines;
    private final List<TokenData> expectedTokens;

    private final String descriptor;

    /**
     * @param testFile The file containing the test specifications
     * @throws IOException If the file cannot be read
     */
    public TokenPositionTestData(File testFile) throws IOException {
        this.sourceLines = new ArrayList<>();
        this.expectedTokens = new ArrayList<>();
        this.descriptor = "(Token position file: " + testFile.getName() + ")";
        this.readFile(testFile);
    }

    private void readFile(File testFile) throws IOException {
        List<String> testFileLines = FileUtils.readFileContent(testFile).lines().toList();
        int currentLine = 0;

        for (String sourceLine : testFileLines) {
            if (sourceLine.charAt(0) == '>') {
                this.sourceLines.add(sourceLine.substring(1));
                currentLine++;
            }

            if (sourceLine.charAt(0) == '$') {
                int column = sourceLine.indexOf('|');
                String[] tokenDescriptionParts = sourceLine.split(" ", 0);

                String typeName = tokenDescriptionParts[tokenDescriptionParts.length - 2];
                int length = Integer.parseInt(tokenDescriptionParts[tokenDescriptionParts.length - 1]);
                this.expectedTokens.add(new TokenData(typeName, currentLine, column, length));
            }
        }
    }

    @Override
    public List<Token> parseTokens(Language language) throws ParsingException, IOException {
        File file = File.createTempFile("testSource", language.suffixes()[0]);
        FileUtils.write(file, String.join(System.lineSeparator(), sourceLines));
        List<Token> tokens = language.parse(Collections.singleton(file));
        TemporaryFileHolder.temporaryFiles.add(file);
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
     * Information about a single token
     * @param typeName The name of the token type
     * @param lineNumber The line the token is in (1 based)
     * @param columnNumber The column the token is in (1 based)
     * @param length The length of the token
     */
    public record TokenData(String typeName, int lineNumber, int columnNumber, int length) {
    }
}
