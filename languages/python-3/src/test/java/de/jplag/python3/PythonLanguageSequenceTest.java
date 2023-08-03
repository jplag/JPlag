package de.jplag.python3;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.ParsingException;
import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenType;

public class PythonLanguageSequenceTest {
    private static final File testFileLocation = Path.of("src", "test", "resources", "de", "jplag", "python3").toFile();
    private final PythonLanguage language = new PythonLanguage();

    @ParameterizedTest
    @MethodSource("provideFileData")
    void testTokenSequences(File file, TokenType[] sequence) throws ParsingException {
        TokenType[] fileTokens = language.parse(Set.of(file)).stream().map(Token::getType).toArray(TokenType[]::new);

        Assertions.assertArrayEquals(sequence, fileTokens);
    }

    public static List<Arguments> provideFileData() {
        TokenType[] logTokens = new TokenType[] {Python3TokenType.IMPORT, Python3TokenType.ASSIGN, Python3TokenType.ARRAY, Python3TokenType.APPLY,
                SharedTokenType.FILE_END};
        TokenType[] unicodeTokens = new TokenType[] {Python3TokenType.ASSIGN, SharedTokenType.FILE_END};

        return List.of(Arguments.of(new File(testFileLocation, "log.py"), logTokens),
                Arguments.of(new File(testFileLocation, "unicode.py"), unicodeTokens));
    }
}
