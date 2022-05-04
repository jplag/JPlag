package de.jplag.csharp;

import static de.jplag.csharp.CSharpTokenConstants.ASSIGNMENT;
import static de.jplag.csharp.CSharpTokenConstants.CLASS;
import static de.jplag.csharp.CSharpTokenConstants.CLASS_BEGIN;
import static de.jplag.csharp.CSharpTokenConstants.CLASS_END;
import static de.jplag.csharp.CSharpTokenConstants.CONSTRUCTOR;
import static de.jplag.csharp.CSharpTokenConstants.DECLARE_VAR;
import static de.jplag.csharp.CSharpTokenConstants.INVOCATION;
import static de.jplag.csharp.CSharpTokenConstants.METHOD;
import static de.jplag.csharp.CSharpTokenConstants.METHOD_BEGIN;
import static de.jplag.csharp.CSharpTokenConstants.METHOD_END;
import static de.jplag.csharp.CSharpTokenConstants.PROPERTY;
import static de.jplag.csharp.CSharpTokenConstants.RETURN;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;

import de.jplag.TokenConstants;
import de.jplag.TokenList;
import de.jplag.TokenPrinter;
import de.jplag.testutils.TestErrorConsumer;

public class MinimalCSharpFrontendTest {
    private static final int EXPEXTED_NUMBER_OF_TOKENS = 15;
    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "csharp");
    private static final String TEST_SUBJECT = "TestClass.cs";

    private de.jplag.Language frontend;
    private File baseDirectory;

    @Before
    public void setUp() throws Exception {
        TestErrorConsumer consumer = new TestErrorConsumer();
        frontend = new Language(consumer);
        baseDirectory = BASE_PATH.toFile();
        assertTrue("Could not find base directory!", baseDirectory.exists());
    }

    @Test
    public void testParsingTestClass() {
        List<Integer> expectedToken = List.of(CLASS, CLASS_BEGIN, DECLARE_VAR, CONSTRUCTOR, METHOD, METHOD_BEGIN, INVOCATION, METHOD_END, PROPERTY,
                DECLARE_VAR, PROPERTY, RETURN, ASSIGNMENT, CLASS_END, TokenConstants.FILE_END);

        // Parse test input
        String[] input = new String[] {TEST_SUBJECT};
        TokenList result = frontend.parse(baseDirectory, input);
        System.out.println(TokenPrinter.printTokens(result, baseDirectory, Arrays.asList(input)));

        // Compare parsed tokens:
        assertEquals(EXPEXTED_NUMBER_OF_TOKENS, result.size());
        List<Integer> actualToken = StreamSupport.stream(result.allTokens().spliterator(), false).map(it -> it.getType()).collect(toList());
        assertEquals(expectedToken, actualToken);
    }

}
