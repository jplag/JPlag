package jplag.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenAttribute;
import de.jplag.TokenPrinter;
import de.jplag.text.NaturalLanguage;

class NaturalLanguageTest {
    private final Logger logger = LoggerFactory.getLogger(NaturalLanguageTest.class);

    private static final Path BASE_PATH = Path.of("src", "test", "resources");
    private static final String TEST_SUBJECT = "FutureJavaDoc.txt";

    private de.jplag.Language language;
    private File baseDirectory;

    @BeforeEach
    public void setUp() {
        language = new NaturalLanguage();
        baseDirectory = BASE_PATH.toFile();
        assertTrue(baseDirectory.exists(), "Could not find base directory!");
    }

    @Test
    void testParsingJavaDoc() throws ParsingException {
        // Parse test input
        List<Token> result = language.parse(Set.of(new File(BASE_PATH.toFile(), TEST_SUBJECT)));
        logger.info(TokenPrinter.printTokens(result, baseDirectory));

        List<TokenAttribute> tokenTypes = result.stream().map(Token::getTypeCompat).toList();
        assertEquals(283, tokenTypes.size());
        assertEquals(158, new HashSet<>(tokenTypes).size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\n", "\r", "\r\n",})
    void testLineBreakInputs(String input) throws IOException, ParsingException {
        File testFile = File.createTempFile("input", "txt");
        Files.writeString(testFile.toPath(), input);
        List<Token> result = language.parse(Set.of(testFile));
        assertEquals(1, result.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\ntoken", "\rtoken", "\r\ntoken",})
    void testTokenAfterLineBreak(String input) throws IOException, ParsingException {
        File testFile = File.createTempFile("input", "txt");
        Files.writeString(testFile.toPath(), input);
        List<Token> result = language.parse(Set.of(testFile));
        assertEquals(2, result.get(0).getLine());
    }

}
