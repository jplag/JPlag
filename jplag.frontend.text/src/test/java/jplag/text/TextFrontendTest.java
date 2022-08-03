package jplag.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Token;
import de.jplag.TokenList;
import de.jplag.TokenPrinter;
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
        TokenList result = frontend.parse(baseDirectory, input);
        logger.info(TokenPrinter.printTokens(result, baseDirectory, Arrays.asList(input)));

        // Compare parsed tokens:
        Map<Integer, Token> tokenTypes = new HashMap<>();
        StreamSupport.stream(result.allTokens().spliterator(), false).forEach(it -> tokenTypes.put(it.getType(), it));

        assertEquals(293, result.size());
        assertEquals(156, tokenTypes.values().size());
    }

}
