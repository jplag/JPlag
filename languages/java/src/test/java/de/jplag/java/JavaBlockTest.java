package de.jplag.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;

class JavaBlockTest {
    private static final String LOG_MESSAGE = "Tokens of {}: {}";
    private static final Path BASE_PATH = Path.of("src", "test", "resources", "java");

    private final Logger logger = LoggerFactory.getLogger(JavaBlockTest.class);

    private de.jplag.Language language;
    private File baseDirectory;

    @BeforeEach
    public void setUp() {
        language = new Language();
        baseDirectory = BASE_PATH.toFile();
        assertTrue(baseDirectory.exists(), "Could not find base directory!");
    }

    @Test
    void testParsingTestClass() throws ParsingException {
        List<TokenType> tokenTypes1 = parseFile("IfWithBraces.java");
        List<TokenType> tokenTypes2 = parseFile("IfWithoutBraces.java");
        assertEquals(tokenTypes1.size(), tokenTypes2.size());
        assertIterableEquals(tokenTypes1, tokenTypes2);
    }

    private List<TokenType> parseFile(String fileName) throws ParsingException {
        List<Token> parsedTokens = language.parse(Set.of(new File(baseDirectory, fileName)));
        List<TokenType> tokenTypes = parsedTokens.stream().map(Token::getType).toList();
        logger.info(LOG_MESSAGE, fileName, tokenTypes);
        return tokenTypes;
    }

}
