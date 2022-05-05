package de.jplag.csharp;

import static de.jplag.csharp.CSharpTokenConstants.*;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.jplag.Token;
import de.jplag.TokenConstants;
import de.jplag.TokenList;
import de.jplag.TokenPrinter;
import de.jplag.testutils.TestErrorConsumer;

class MinimalCSharpFrontendTest {
    private static final int EXPEXTED_NUMBER_OF_TOKENS = 15;
    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "csharp");
    private static final String TEST_SUBJECT = "TestClass.cs";

    private de.jplag.Language frontend;
    private File baseDirectory;

    @BeforeEach
    public void setUp() {
        TestErrorConsumer consumer = new TestErrorConsumer();
        frontend = new Language(consumer);
        baseDirectory = BASE_PATH.toFile();
        assertTrue(baseDirectory.exists(), "Could not find base directory!");
    }

    @Test
    void testParsingTestClass() {
        List<Integer> expectedToken = List.of(CLASS, CLASS_BEGIN, DECLARE_VAR, CONSTRUCTOR, METHOD, METHOD_BEGIN, INVOCATION, METHOD_END, PROPERTY,
                DECLARE_VAR, PROPERTY, RETURN, ASSIGNMENT, CLASS_END, TokenConstants.FILE_END);

        // Parse test input
        String[] input = new String[] {TEST_SUBJECT};
        TokenList result = frontend.parse(baseDirectory, input);
        System.out.println(TokenPrinter.printTokens(result, baseDirectory, Arrays.asList(input)));

        // Compare parsed tokens:
        assertEquals(EXPEXTED_NUMBER_OF_TOKENS, result.size());
        List<Integer> actualToken = StreamSupport.stream(result.allTokens().spliterator(), false).map(Token::getType).collect(toList());
        assertEquals(expectedToken, actualToken);
    }

}
