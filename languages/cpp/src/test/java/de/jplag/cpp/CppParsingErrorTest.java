package de.jplag.cpp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.jplag.ParsingException;
import de.jplag.cpp.grammar.CPP14Lexer;
import de.jplag.cpp.grammar.CPP14Parser;

class CppParsingErrorTest {
    /** Directory for the C++ source files parsed by each test. */
    @TempDir
    Path temporaryDirectory;

    @Test
    void reportsMissingDeclaratorInsteadOfNullPointerException() throws IOException {
        Path source = Files.writeString(temporaryDirectory.resolve("malformed.cpp"), "void f() { x x * 1; }\n");

        ParsingException exception = assertThrows(ParsingException.class, () -> new CPPLanguage().parse(Set.of(source.toFile()), false));

        assertTrue(exception.getMessage().contains(source.toString()));
        assertTrue(exception.getMessage().contains("cannot extract declarator at line 1:15"));
        assertFalse(exception.getCause() instanceof NullPointerException);
    }

    @Test
    void continuesParsingOtherRecoverableSyntaxErrors() throws Exception {
        String code = "void f() { int x = 1 }\n";
        Path source = Files.writeString(temporaryDirectory.resolve("recoverable.cpp"), code);
        var parser = new CPP14Parser(new CommonTokenStream(new CPP14Lexer(CharStreams.fromString(code))));
        parser.removeErrorListeners();
        parser.translationUnit();
        assertTrue(parser.getNumberOfSyntaxErrors() > 0);

        var tokens = new CPPLanguage().parse(Set.of(source.toFile()), false);
        assertTrue(tokens.stream().anyMatch(token -> token.getType() == CPPTokenType.FUNCTION_BEGIN));
        assertTrue(tokens.stream().anyMatch(token -> token.getType() == CPPTokenType.ASSIGN));
    }

    @Test
    void continuesParsingValidCpp() throws Exception {
        Path source = Files.writeString(temporaryDirectory.resolve("valid.cpp"), "void f() { int x = 0; x = x * 1; }\n");
        var tokens = new CPPLanguage().parse(Set.of(source.toFile()), false);

        assertTrue(tokens.stream().anyMatch(token -> token.getType() == CPPTokenType.FUNCTION_BEGIN));
        assertTrue(tokens.stream().anyMatch(token -> token.getType() == CPPTokenType.ASSIGN));
    }
}
