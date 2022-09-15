package jplag.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Token;
import de.jplag.TokenPrinter;
import de.jplag.TokenType;
import de.jplag.text.Language;

class TextFrontendTest {
    private final Logger logger = LoggerFactory.getLogger("JPlag-Test");

    private static final Path BASE_PATH = Path.of("src", "test", "resources");
    private static final String TEST_SUBJECT = "FutureJavaDoc.txt";

    private de.jplag.Language frontend;
    private File baseDirectory;

    @BeforeEach
    public void setUp() {
        frontend = new Language();
        baseDirectory = BASE_PATH.toFile();
        assertTrue(baseDirectory.exists(), "Could not find base directory!");
    }

    @Test
    void testParsingJavaDoc() {
        // Parse test input
        String[] input = new String[] {TEST_SUBJECT};
        List<Token> result = frontend.parse(baseDirectory, input);
        logger.info(TokenPrinter.printTokens(result, baseDirectory));

        List<TokenType> tokenTypes = result.stream().map(Token::getType).toList();
        assertEquals(283, tokenTypes.size());
        assertEquals(158, new HashSet<>(tokenTypes).size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\n", "\r", "\r\n",})
    void testLineBreakInputs(String input, @TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, input);
        List<Token> result = frontend.parse(tempDir.toFile(), new String[] {"input.txt"});
        assertEquals(1, result.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\ntoken", "\rtoken", "\r\ntoken",})
    void testTokenAfterLineBreak(String input, @TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, input);
        List<Token> result = frontend.parse(tempDir.toFile(), new String[] {"input.txt"});
        assertEquals(2, result.get(0).getLine());
    }

}
