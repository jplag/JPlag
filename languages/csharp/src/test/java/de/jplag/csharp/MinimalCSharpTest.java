package de.jplag.csharp;

import static de.jplag.csharp.CSharpTokenType.ACCESSORS_BEGIN;
import static de.jplag.csharp.CSharpTokenType.ACCESSORS_END;
import static de.jplag.csharp.CSharpTokenType.ACCESSOR_BEGIN;
import static de.jplag.csharp.CSharpTokenType.ACCESSOR_END;
import static de.jplag.csharp.CSharpTokenType.ASSIGNMENT;
import static de.jplag.csharp.CSharpTokenType.CLASS;
import static de.jplag.csharp.CSharpTokenType.CLASS_BEGIN;
import static de.jplag.csharp.CSharpTokenType.CLASS_END;
import static de.jplag.csharp.CSharpTokenType.CONSTRUCTOR;
import static de.jplag.csharp.CSharpTokenType.FIELD;
import static de.jplag.csharp.CSharpTokenType.IF;
import static de.jplag.csharp.CSharpTokenType.IF_BEGIN;
import static de.jplag.csharp.CSharpTokenType.IF_END;
import static de.jplag.csharp.CSharpTokenType.INVOCATION;
import static de.jplag.csharp.CSharpTokenType.LOCAL_VARIABLE;
import static de.jplag.csharp.CSharpTokenType.METHOD;
import static de.jplag.csharp.CSharpTokenType.METHOD_BEGIN;
import static de.jplag.csharp.CSharpTokenType.METHOD_END;
import static de.jplag.csharp.CSharpTokenType.PROPERTY;
import static de.jplag.csharp.CSharpTokenType.RETURN;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenPrinter;
import de.jplag.TokenType;

class MinimalCSharpTest {
    private final Logger logger = LoggerFactory.getLogger(MinimalCSharpTest.class);

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "csharp");
    private static final String TEST_SUBJECT = "TestClass.cs";

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
        List<TokenType> expectedToken = List.of(CLASS, CLASS_BEGIN, FIELD, CONSTRUCTOR, LOCAL_VARIABLE, METHOD, METHOD_BEGIN, IF, IF_BEGIN,
                INVOCATION, IF_END, IF_BEGIN, INVOCATION, IF_END, METHOD_END, PROPERTY, ACCESSORS_BEGIN, ACCESSOR_BEGIN, ACCESSOR_END, ACCESSOR_BEGIN,
                ACCESSOR_END, ACCESSORS_END, FIELD, PROPERTY, ACCESSORS_BEGIN, ACCESSOR_BEGIN, RETURN, ACCESSOR_END, ACCESSOR_BEGIN, ASSIGNMENT,
                ACCESSOR_END, ACCESSORS_END, CLASS_END, SharedTokenType.FILE_END);

        // Parse test input
        List<Token> result = language.parse(Set.of(new File(baseDirectory, TEST_SUBJECT)));
        logger.info(TokenPrinter.printTokens(result, baseDirectory));

        // Compare parsed tokens:
        assertEquals(expectedToken.size(), result.size());
        List<TokenType> actualToken = result.stream().map(Token::getType).toList();
        assertEquals(expectedToken, actualToken);
    }

}
