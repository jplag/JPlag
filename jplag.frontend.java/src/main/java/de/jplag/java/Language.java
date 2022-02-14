package de.jplag.java;

import java.io.File;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

/**
 * Language for Java 9 and newer.
 */
public class Language implements de.jplag.Language {
    private Parser parser;

    public Language(ErrorConsumer program) {
        this.parser = new Parser();
        this.parser.setProgram(program);
    }

    @Override
    public String[] suffixes() {
        return new String[] {".java", ".JAVA"};
    }

    @Override
    public String getName() {
        return "Javac 1.9+ based AST plugin";
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
    public TokenList parse(File dir, String[] files) {
        return this.parser.parse(dir, files);
    }

    @Override
    public boolean hasErrors() {
        return this.parser.hasErrors();
    }

    @Override
    public int errorCount() {
        return this.parser.errorsCount();
    }

}
