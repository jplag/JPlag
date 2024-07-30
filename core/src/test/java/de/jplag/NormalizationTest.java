package de.jplag;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class NormalizationTest extends TestBase {
    private final Map<String, List<TokenType>> tokenStringMap;
    private final List<TokenType> originalTokenString;

    NormalizationTest() throws ExitException {
        JPlagOptions options = getDefaultOptions("normalization");
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        submissionSet.normalizeSubmissions();
        Function<Submission, List<TokenType>> getTokenString = submission -> submission.getTokenList().stream().map(Token::getType).toList();
        tokenStringMap = submissionSet.getSubmissions().stream().collect(Collectors.toMap(Submission::getName, getTokenString));
        originalTokenString = tokenStringMap.get("Squares.java");
    }

    @Test
    void testInsertionNormalization() {
        Assertions.assertIterableEquals(originalTokenString, tokenStringMap.get("SquaresInserted.java"));
    }

    @Test
    void testReorderingNormalization() {
        Assertions.assertIterableEquals(originalTokenString, tokenStringMap.get("SquaresReordered.java"));
    }

    @Test
    void testInsertionReorderingNormalization() {
        Assertions.assertIterableEquals(originalTokenString, tokenStringMap.get("SquaresInsertedReordered.java"));
    }
}