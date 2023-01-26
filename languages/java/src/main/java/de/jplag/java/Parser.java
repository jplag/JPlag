package de.jplag.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;

public class Parser extends AbstractParser {
    private List<Token> tokens;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        new JavacAdapter().parseFiles(files, this);
        // printSemantics();
        return tokens;
    }

    public void add(Token token) {
        tokens.add(token);
    }

    public void printSemantics() {
        long currentLine = 0;
        for (Token t : tokens) {
            if (t.getLine() != currentLine) {
                currentLine = t.getLine();
                System.out.println();
                System.out.println(t.getLine());
            }
            System.out.print(t.getType().getDescription());
            System.out.print(" | ");
            System.out.println(t.getSemantics());
        }
        System.out.println();
        System.out.println("=".repeat(100));
    }
}
