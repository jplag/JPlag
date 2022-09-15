package de.jplag.java;

import java.io.File;
import java.util.List;

import org.kohsuke.MetaInfServices;

import de.jplag.Token;

/**
 * Language for Java 9 and newer.
 */
@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {
    public static final String IDENTIFIER = "java";

    private final Parser parser;

    public Language() {
        parser = new Parser();
    }

    @Override
    public String[] suffixes() {
        return new String[] {".java", ".JAVA"};
    }

    @Override
    public String getName() {
        return "Javac based AST plugin";
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public int minimumTokenMatch() {
        return 9;
    }

    @Override
    public List<Token> parse(File directory, String[] files) {
        return this.parser.parse(directory, files);
    }

    @Override
    public boolean hasErrors() {
        return this.parser.hasErrors();
    }

}
