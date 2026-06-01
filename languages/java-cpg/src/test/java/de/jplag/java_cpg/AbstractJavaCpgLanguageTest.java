package de.jplag.java_cpg;

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

/**
 * Basic test class for testing the Java language module.
 */
public abstract class AbstractJavaCpgLanguageTest {

    protected static final Path BASE_PATH = Path.of("src", "test", "resources", "java");
    private static final String LOG_MESSAGE = "Tokens of {}: {}";
    private final Logger logger = LoggerFactory.getLogger(AbstractJavaCpgLanguageTest.class);
    protected File baseDirectory;
    protected JavaCpgLanguage language;

    /**
     * Sets up the base directory and the language module.
     */
    @BeforeEach
    void setUp() {
        language = new JavaCpgLanguage();
        baseDirectory = BASE_PATH.toFile();
        assertTrue(baseDirectory.exists(), "Could not find base directory!");
    }

    /**
     * Parses a java file in the {@code baseDirectory} and returns the list of token types.
     * @param fileName is the name of the file to parse.
     * @param transform whether to apply the transformation to the parsed graph.
     * @param enableSemanticAnalysis whether to enable semantic analysis during parsing.
     * @return the token types.
     * @throws ParsingException if parsing fails.
     */
    protected List<Token> parseJavaFile(String fileName, boolean transform, boolean enableSemanticAnalysis) throws ParsingException {
        language.getOptions().setEnableSemanticAnalysis(enableSemanticAnalysis);
        List<Token> parsedTokens = language.parse(Set.of(new File(baseDirectory.getAbsolutePath(), fileName)), transform);
        logger.info(LOG_MESSAGE, fileName, parsedTokens.stream().map(Token::getType).toList());
        logger.info(TokenPrinter.printTokens(parsedTokens, BASE_PATH.toAbsolutePath().toFile()));
        return parsedTokens;
    }

}