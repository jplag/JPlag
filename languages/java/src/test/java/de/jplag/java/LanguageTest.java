package de.jplag.java;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenPrinter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
class LanguageTest {

    @Test
    void parse(@TempDir Path tmp) throws IOException, ParsingException {
        Path resolve = tmp.resolve("Content.java");
        Files.writeString(resolve, """
                class MyClass {
                  public int myNum;
                  private String myString;
                    void myMethod(String value) {
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
                        System.out.println("Hello World!");
                      } else {
                        myMethod("Oh no");
                      }
                      try {
                        Integer.parseInt("1");
                      } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                      }
                    }
                    private final long myNum2 = 10;
                }
                enum MyEnum {
                  a,
                  b,
                  c
                }
                """);
        Language language = new Language();
        List<Token> tokens = language.parse(Set.of(resolve.toFile()));
        System.out.println(TokenPrinter.printTokens(tokens, resolve.toFile()));
    }
  
}