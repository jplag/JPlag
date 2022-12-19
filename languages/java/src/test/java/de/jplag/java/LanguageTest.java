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
                      System.out.println("Hello World!");
                      myMethod("Oh no");
                    }
                };
                """);
        Language language = new Language();
        List<Token> tokens = language.parse(Set.of(resolve.toFile()));
        System.out.println(TokenPrinter.printTokens(tokens, resolve.toFile()));
    }
  
}