package de.jplag.cpp2;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenPrinter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.TestAbortedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LanguageTest {

    @Test
    void parse(@TempDir Path tmp) throws IOException, ParsingException {
        Path resolve = tmp.resolve("content.cpp");
        Files.writeString(resolve, """
                class MyClass {
                  public:
                    int myNum;
                    string myString;
                    void newExpressions() {
                      double a[5];
                      double* b = new double[10];
                      MyClass* c = new MyClass();
                      MyClass d;
                    }
                    void myMethod(string value) {
                      int i = 5;
                      i += 10;
                      switch(i) {
                        case 10:
                          i--;
                        case 9:
                          i--;
                          break;
                        default:
                          i = 8;
                      }
                      if (i * 10 > 80) {
                        cout << "Hello World!" << endl;
                      } else {
                        this->myMethod("Oh no");
                      }
                    }
                    
                    void a(string v) {
                      this->myMethod(v);
                    }
                    
                    void b(string v) {
                      MyClass::myMethod(v);
                    }
                    
                    void c(string v) {
                      myMethod(v);
                    }
                    
                    void d(MyClass m, string v) {
                      m.myMethod(v);
                    }
                    
                    void exceptional() {
                      try {
                        int age = 15;
                        if (age >= 18) {
                          cout << "Access granted - you are old enough.";
                        } else {
                          throw (age);
                        }
                      }
                      catch (int myNum) {
                        cout << "Access denied - You must be at least 18 years old.\\n";
                        cout << "Age is: " << myNum;
                      }
                    }
                  private:
                    const long myNum2 = 10;
                };
                enum MyEnum {
                  a,
                  b = 3,
                  c
                };
                """);
        Language language = new Language();
        List<Token> tokens = language.parse(Set.of(resolve.toFile()));
        System.out.println(TokenPrinter.printTokens(tokens, resolve.toFile()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "i = 10",
            "i += 10",
            "i -= 10",
            "i += 10",
            "i /= 10",
            "i %= 10",
            "i >>= 10",
            "i <<= 10",
            "i &= 10",
            "i ^= 10",
            "i |= 10",
            "i++",
            "i--",
            "++i",
            "--i",
    })
    void testAssign(String expr, @TempDir Path path) {
        String function = """
                void f(int i) {
                    %s;
                }
                """.formatted(expr);
        List<Token> all = extractFromString(path, function).tokens();
        List<Token> assignTokens = all.stream()
                .filter(token -> token.getType() == CPPTokenType.C_ASSIGN).toList();
        assertEquals(1, assignTokens.size());
    }

    @Test
    void testFunctionCall(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                void f() {
                    funCall();
                    a.funCall();
                    b->funCall();
                    C::funCall();
                }
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
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
    }
    @Test
    void testIfElse(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                void f(int a, int b) {
                    int x = 0;
                    int y = 1;
                    if (a < b) {
                        x = 5;
                    } else if (a > b) {
                        y = 10;
                        x = y + b;
                    } else {
                        y = -20;
                    }
                }
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
    }

    TokenResult extractFromString(@TempDir Path path, String content) {
        Path filePath = path.resolve("content.cpp");
        try {
            Files.writeString(filePath, content);
        } catch (IOException e) {
            throw new TestAbortedException("Failed to write temp file", e);
        }
        Language language = new Language();
        List<Token> tokens;
        try {
            tokens = language.parse(Set.of(filePath.toFile()));
        } catch (ParsingException e) {
            throw new TestAbortedException("Failed to extract tokens", e);
        }
        return new TokenResult(tokens, filePath.toFile());
    }
    record TokenResult(List<Token> tokens, File file) {}
}