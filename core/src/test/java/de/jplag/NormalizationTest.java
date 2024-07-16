package de.jplag;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class NormalizationTest extends TestBase {
    private Map<String, List<TokenType>> tokenStringMap;
    private List<TokenType> originalTokenString;
    private SubmissionSet submissionSet;

    @BeforeEach
    void setUp() throws ExitException {
        JPlagOptions options = getDefaultOptions("normalization");
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        submissionSet = builder.buildSubmissionSet();

    }

    private void normalizeSubmissions(boolean sorting) {
        submissionSet.normalizeSubmissions(sorting);
        Function<Submission, List<TokenType>> getTokenString = submission -> submission.getTokenList().stream().map(Token::getType).toList();
        tokenStringMap = submissionSet.getSubmissions().stream().collect(Collectors.toMap(Submission::getName, getTokenString));
        originalTokenString = tokenStringMap.get("Squares.java");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testInsertionNormalization(boolean sorting) {
        normalizeSubmissions(sorting);
        Assertions.assertIterableEquals(originalTokenString, tokenStringMap.get("SquaresInserted.java"));
    }

    @Test
    void testReorderingNormalization() {
        normalizeSubmissions(true);
        Assertions.assertIterableEquals(originalTokenString, tokenStringMap.get("SquaresReordered.java"));
    }

    @Test
    void testInsertionReorderingNormalization() {
        normalizeSubmissions(true);
        Assertions.assertIterableEquals(originalTokenString, tokenStringMap.get("SquaresInsertedReordered.java"));
    }
}
