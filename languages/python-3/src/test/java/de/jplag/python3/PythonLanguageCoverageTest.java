package de.jplag.python3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.util.FileUtils;

public class PythonLanguageCoverageTest {
    private static final File testFileLocation = Path.of("src", "test", "resources", "de", "jplag", "python3").toFile();
    private static final String[] tokenCoverageFileNames = {"test_utils.py"};

    private static final String[] lineIgnorePrefixes = {"else:", "elif", "#", "pass"};
    private static final String multilineStringMarker = "\"\"\"";

    private final PythonLanguage language = new PythonLanguage();

    @ParameterizedTest
    @MethodSource("collectSourceCoverageTestFiles")
    void testSourceCoverage(File testFile) throws ParsingException, IOException {
        List<Token> tokens = language.parse(Set.of(testFile));
        List<Integer> lines = new ArrayList<>(getRelevantSourceFiles(testFile));

        tokens.forEach(token -> lines.remove((Object) token.getLine()));

        if (!lines.isEmpty()) {
            Assertions.fail("There were uncovered lines in: " + testFile.getPath() + "\n" + lines);
        }
    }

    @ParameterizedTest
    @MethodSource("collectTokenCoverageTestFiles")
    void testTokenCoverage(File testFile) throws ParsingException {
        Set<TokenType> foundTokens = language.parse(Set.of(testFile)).stream().map(Token::getType).collect(Collectors.toSet());
        List<TokenType> allTokens = new ArrayList<>(List.of(Python3TokenType.values()));

        allTokens.removeAll(foundTokens);

        if (!allTokens.isEmpty()) {
            Assertions.fail("There are token, that were not found in: " + testFile.getPath() + "\n" + allTokens);
        }
    }

    @ParameterizedTest
    @MethodSource("collectSourceCoverageTestFiles")
    void testMonotoneTokenOrder(File testFile) throws ParsingException {
        List<Token> tokens = language.parse(Set.of(testFile));

        for (int i = 0; i < tokens.size() - 2; i++) {
            if (tokens.get(i).getLine() > tokens.get(i + 1).getLine()) {
                Assertions.fail("Token " + tokens.get(i).getType() + " reported in line " + tokens.get(i).getLine()
                        + " has a bigger line index, than token " + tokens.get(i + 1).getType() + " in line " + tokens.get(i + 1).getLine());
            }
        }
    }

    public static List<File> collectSourceCoverageTestFiles() {
        return Arrays.asList(Objects.requireNonNull(testFileLocation.listFiles()));
    }

    public static List<File> collectTokenCoverageTestFiles() {
        return Arrays.stream(tokenCoverageFileNames).map(it -> new File(testFileLocation, it)).toList();
    }

    private List<Integer> getRelevantSourceFiles(File file) throws IOException {
        List<String> lines = FileUtils.readFileContent(file).lines().toList();
        List<Integer> relevantLineIndices = new ArrayList<>();

        boolean inMultilineString = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!inMultilineString) {
                if (isLineInlineIgnore(line)) {
                    continue;
                }

                if (isMultilineStringStart(line)) {
                    inMultilineString = true;
                    continue;
                }
            } else {
                if (isMultilineStringEnd(line)) {
                    inMultilineString = false;
                }
                continue;
            }

            relevantLineIndices.add(i + 1);
        }

        return relevantLineIndices;
    }

    private boolean isLineInlineIgnore(String line) {
        return line.isBlank() || Arrays.stream(lineIgnorePrefixes).anyMatch(it -> line.trim().startsWith(it))
                || (line.trim().startsWith(multilineStringMarker) && line.trim().substring(3).contains(multilineStringMarker));
    }

    private boolean isMultilineStringStart(String line) {
        return line.trim().startsWith(multilineStringMarker);
    }

    private boolean isMultilineStringEnd(String line) {
        return line.trim().contains(multilineStringMarker);
    }
}
