package de.jplag.cpp2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.TestAbortedException;

import de.jplag.ParsingException;
import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenPrinter;
import de.jplag.TokenType;

/**
 * Tests asserting the extraction of nontrivial tokens. As the ANTLR grammar requires some workarounds to have the token
 * extraction similar to the Java language module, these tests covers the extraction of such tokens.
 */
class TokenExtractionTest {

    @ParameterizedTest
    @ValueSource(strings = {"i = 10", "i += 10", "i -= 10", "i += 10", "i /= 10", "i %= 10", "i >>= 10", "i <<= 10", "i &= 10", "i ^= 10", "i |= 10",
            "i++", "i--", "++i", "--i",})
    void testAssign(String expr, @TempDir Path path) {
        String function = """
                void f(int i) {
                    %s;
                }
                """.formatted(expr);
        List<Token> all = extractFromString(path, function).tokens();
        List<Token> assignTokens = all.stream().filter(token -> token.getType() == CPPTokenType.ASSIGN).toList();
        assertEquals(1, assignTokens.size());
    }

    @Test
    void testFunctionCall(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                void f() {
                    b->funCall();
                    C::funCall();
                    funCall();
                    a.funCall();
                }
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.FUNCTION_BEGIN, CPPTokenType.APPLY, CPPTokenType.APPLY, CPPTokenType.APPLY, CPPTokenType.APPLY,
                CPPTokenType.FUNCTION_END);
    }

    @Test
    void testLoop(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                void f() {
                    do {
                        goto a;
                    } while (true);

                    a:
                    while (true) {
                        break;
                    }

                    for (;;) {
                        continue;
                    }
                    return;
                }
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.FUNCTION_BEGIN, CPPTokenType.DO_BEGIN, CPPTokenType.GOTO, CPPTokenType.DO_END,
                CPPTokenType.WHILE_BEGIN, CPPTokenType.BREAK, CPPTokenType.WHILE_END, CPPTokenType.FOR_BEGIN, CPPTokenType.CONTINUE,
                CPPTokenType.FOR_END, CPPTokenType.RETURN, CPPTokenType.FUNCTION_END);

    }

    @ParameterizedTest
    @ValueSource(strings = {"this->myMethod(v)", "MyClass::myMethod(v)", "myMethod(v)", "m.myMethod(v)"})
    void testFunctionCalls(String expression, @TempDir Path path) {
        TokenResult result = extractFromString(path, """
                void a(string v) {
                  %s;
                }
                """.formatted(expression));
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.FUNCTION_BEGIN, CPPTokenType.VARDEF, CPPTokenType.APPLY, CPPTokenType.FUNCTION_END);
    }

    @Test
    void testIfElse(@TempDir Path path) {
        // test extraction of if/else constructs
        TokenResult result = extractFromString(path, """
                void a(int a, int b, int x, int y) {
                    if (a < b) {
                        x = 5;
                    } else if (a > b) {
                        {
                        y = 10;
                        }
                        x = y + b;
                    } else {
                        y = -20;
                    }
                }
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.FUNCTION_BEGIN, CPPTokenType.VARDEF, CPPTokenType.VARDEF, CPPTokenType.VARDEF,
                CPPTokenType.VARDEF, CPPTokenType.IF_BEGIN, CPPTokenType.ASSIGN, CPPTokenType.ELSE, CPPTokenType.IF_BEGIN, CPPTokenType.ASSIGN,
                CPPTokenType.ASSIGN, CPPTokenType.ELSE, CPPTokenType.ASSIGN, CPPTokenType.IF_END, CPPTokenType.IF_END, CPPTokenType.FUNCTION_END);
    }

    @Test
    void testDoubleArrayDeclaration(@TempDir Path path) {
        // ensure NEWARRAY is extracted
        TokenResult result = extractFromString(path, """
                double* b = new double[10];
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.VARDEF, CPPTokenType.ASSIGN, CPPTokenType.NEWARRAY);
    }

    @Test
    void testFunctionCallInAssignmentOutsideFunction(@TempDir Path path) {
        // test function call extraction in an assignment context outside a function body at top-level
        TokenResult result = extractFromString(path, """
                int x = square(2);
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.VARDEF, CPPTokenType.ASSIGN, CPPTokenType.APPLY);
    }

    @Test
    void testFunctionCallInAssignmentInsideClassOutsideFunction(@TempDir Path path) {
        // test function call extraction in an assignment context outside a function body in a class
        TokenResult result = extractFromString(path, """
                class A {
                    int x = square(3);
                };
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.CLASS_BEGIN, CPPTokenType.VARDEF, CPPTokenType.ASSIGN, CPPTokenType.APPLY,
                CPPTokenType.CLASS_END);
    }

    @Test
    void testUnion(@TempDir Path path) {
        // ensure union is extracted
        TokenResult result = extractFromString(path, """
                union S {
                    std::int32_t n;
                    std::uint16_t s[2];
                    std::uint8_t c;
                };
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.UNION_BEGIN, CPPTokenType.VARDEF, CPPTokenType.VARDEF, CPPTokenType.VARDEF,
                CPPTokenType.UNION_END);
    }

    @Test
    void testArrayInit(@TempDir Path path) {
        // ensure { and } are extracted
        TokenResult result = extractFromString(path, """
                int a[] = {1, 2, 3};
                int b[] {1, 2, 3};
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.VARDEF, CPPTokenType.ASSIGN, CPPTokenType.BRACED_INIT_BEGIN, CPPTokenType.BRACED_INIT_END,
                CPPTokenType.VARDEF, CPPTokenType.BRACED_INIT_BEGIN, CPPTokenType.BRACED_INIT_END);
    }

    static void assertTokenTypes(List<Token> tokens, TokenType... types) {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.getType() == SharedTokenType.FILE_END) {
                assertEquals(i, types.length);
                return;
            }
            assertEquals(types[i], token.getType(), "Unexpected token at index " + i);
        }
    }

    TokenResult extractFromString(@TempDir Path path, String content) {
        Path filePath = path.resolve("content.cpp");
        try {
            Files.writeString(filePath, content);
        } catch (IOException e) {
            throw new TestAbortedException("Failed to write temp file", e);
        }
        CPPLanguage language = new CPPLanguage();
        List<Token> tokens;
        try {
            tokens = language.parse(Set.of(filePath.toFile()));
        } catch (ParsingException e) {
            throw new TestAbortedException("Failed to extract tokens", e);
        }
        return new TokenResult(tokens, filePath.toFile());
    }

    record TokenResult(List<Token> tokens, File file) {
    }
}