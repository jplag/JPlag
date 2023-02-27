package de.jplag.java;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenPrinter;
import de.jplag.TokenType;

/**
 * Basic test class for testing the Java language module.
 */
public abstract class AbstractJavaLanguageTest {

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "java");
    private static final String LOG_MESSAGE = "Tokens of {}: {}";
    private final Logger logger = LoggerFactory.getLogger(JavaBlockTest.class);
    private de.jplag.Language language;
    protected File baseDirectory;

    /**
     * Sets up the base directory and the language module.
     */
    @BeforeEach
    void setUp() {
        language = new Language();
        baseDirectory = BASE_PATH.toFile();
        assertTrue(baseDirectory.exists(), "Could not find base directory!");
    }

    /**
     * Parses a java file in the {@link AbstractJavaLanguageTest.baseDirectory} and returns the list of token types.
     * @param fileName is the name of the file to parse.
     * @return the token types.
     * @throws ParsingException if parsing fails.
     */
    protected List<TokenType> parseJavaFile(String fileName) throws ParsingException {
        List<Token> parsedTokens = language.parse(Set.of(new File(baseDirectory, fileName)));
        List<TokenType> tokenTypes = parsedTokens.stream().map(Token::getType).toList();
        logger.info(LOG_MESSAGE, fileName, tokenTypes);
        logger.info(TokenPrinter.printTokens(parsedTokens, BASE_PATH.toAbsolutePath().toFile()));
        return tokenTypes;
    }

}