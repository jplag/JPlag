package de.jplag.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;

class JavaBlockTest {
    private static final Path BASE_PATH = Path.of("src", "test", "resources", "java");
    private static final String LOG_MESSAGE = "Tokens of {}: {}";

    private final Logger logger = LoggerFactory.getLogger(JavaBlockTest.class);

    private de.jplag.Language language;
    private File baseDirectory;

    @BeforeEach
    public void setUp() {
        language = new Language();
        baseDirectory = BASE_PATH.toFile();
        assertTrue(baseDirectory.exists(), "Could not find base directory!");
    }

    @ParameterizedTest
    @MethodSource("provideClassPairs")
    @DisplayName("Test pairs of classes with explicit vs. implicit blocks.")
    void testJavaClassPair(String fileName1, String fileName2) throws ParsingException {
        List<TokenType> tokenTypes1 = parseFile(fileName1);
        List<TokenType> tokenTypes2 = parseFile(fileName2);
        assertEquals(tokenTypes1.size(), tokenTypes2.size());
        assertIterableEquals(tokenTypes1, tokenTypes2);
    }

    private List<TokenType> parseFile(String fileName) throws ParsingException {
        List<Token> parsedTokens = language.parse(Set.of(new File(baseDirectory, fileName)));
        List<TokenType> tokenTypes = parsedTokens.stream().map(Token::getType).toList();
        logger.info(LOG_MESSAGE, fileName, tokenTypes);
        return tokenTypes;
    }

    private static Stream<Arguments> provideClassPairs() {
        return Stream.of(Arguments.of("IfWithBraces.java", "IfWithoutBraces.java"), // just if conditions
                Arguments.of("Verbose.java", "Compact.java")); // complex case with different blocks
    }

}
