package de.jplag.java;

import java.io.File;
import java.util.List;

import de.jplag.ErrorConsumer;
import de.jplag.Token;

/**
 * Language for Java 9 and newer.
 */
public class Language implements de.jplag.Language {
    private final Parser parser;

    public Language(ErrorConsumer errorConsumer) {
        parser = new Parser(errorConsumer);
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
    public String getShortName() {
        return "java";
    }

    @Override
    public int minimumTokenMatch() {
        return 9;
    }

    @Override
    public boolean supportsColumns() {
        return true;
    }

    @Override
    public boolean isPreformatted() {
        return true;
    }

    @Override
    public boolean usesIndex() {
        return false;
    }

    @Override
    public int numberOfTokens() {
        return JavaTokenConstants.NUM_DIFF_TOKENS;
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
