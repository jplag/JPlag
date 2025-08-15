package de.java.multilang;

import static de.jplag.SharedTokenType.FILE_END;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.cpp.CPPTokenType;
import de.jplag.java.JavaTokenType;
import de.jplag.multilang.MultiLanguage;
import de.jplag.multilang.MultiLanguageOptions;

class MultilangTest {
    private static File testDataDirectory;
    private static File javaCode;
    private static File cppCode;

    private static final List<TokenType> expectedTokens = List.of(CPPTokenType.FUNCTION_BEGIN, CPPTokenType.RETURN, CPPTokenType.FUNCTION_END,
            FILE_END, JavaTokenType.J_CLASS_BEGIN, JavaTokenType.J_CLASS_END, FILE_END);

    @BeforeAll
    static void setUp() throws IOException {
        testDataDirectory = Files.createTempDirectory("multiLanguageTestData").toFile();
        cppCode = new File(testDataDirectory, "CppCode.cpp");
        javaCode = new File(testDataDirectory, "JavaCode.java");

        MultilangTest.class.getResourceAsStream("/de/jplag/multilang/testDataSet/CppCode.cpp").transferTo(new FileOutputStream(cppCode));
        MultilangTest.class.getResourceAsStream("/de/jplag/multilang/testDataSet/JavaCode.java").transferTo(new FileOutputStream(javaCode));
    }

    @Test
    void testMultiLanguageParsing() throws ParsingException {
        MultiLanguage languageModule = new MultiLanguage();

        ((MultiLanguageOptions) languageModule.getOptions()).getLanguageNames().setValue("java,cpp");

        Set<File> sources = new TreeSet<>(List.of(javaCode, cppCode)); // Using TreeSet to ensure order of entries
        List<Token> tokens = languageModule.parse(sources, false);

        Assertions.assertEquals(expectedTokens, tokens.stream().map(Token::getType).toList());
    }

    @Test
    void testInvalidLanguage() {
        MultiLanguage languageModule = new MultiLanguage();
        ((MultiLanguageOptions) languageModule.getOptions()).getLanguageNames().setValue("thisIsNotALanguage");

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            languageModule.parse(Set.of(javaCode, cppCode), false);
        });
    }

    @AfterAll
    static void cleanUp() {
        javaCode.delete();
        cppCode.delete();
        testDataDirectory.delete();
    }
}
