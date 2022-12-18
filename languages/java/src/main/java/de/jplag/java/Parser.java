package de.jplag.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.semantics.SemanticToken;

public class Parser extends AbstractParser {
    private List<SemanticToken> tokens;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public List<SemanticToken> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        new JavacAdapter().parseFiles(files, this);
        // print();
        return tokens;
    }

    public void add(SemanticToken token) {
        tokens.add(token);
    }

    public void print() {
        long currentLine = 0;
        for (SemanticToken t : tokens) {
            if (t.getLine() != currentLine) {
                currentLine = t.getLine();
                System.out.println();
                System.out.println(t.getLine());
            }
            System.out.print(t.getType().getDescription());
            System.out.print(" | ");
            System.out.println(t.semantics());
        }
    }
}
