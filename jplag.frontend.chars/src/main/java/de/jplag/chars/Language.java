package de.jplag.chars;

import java.io.File;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

/*
 * read in text files as characters
 */
public class Language implements de.jplag.Language {
    private ErrorConsumer program;

    private de.jplag.chars.Parser parser = new Parser();

    public Language(ErrorConsumer program) {
        this.program = program;
        this.parser.setProgram(this.program);
    }

    @Override
    public String[] suffixes() {
        return new String[] {".TXT", ".txt", ".ASC", ".asc", ".TEX", ".tex"};
    }

    @Override
    public int errorCount() {
        return this.parser.errorsCount();
    }

    @Override
    public String getName() {
        return "Character Parser";
    }

    @Override
    public String getShortName() {
        return "char";
    }

    @Override
    public int minimumTokenMatch() {
        return 10;
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
    public boolean supportsColumns() {
        return false;
    }

    @Override
    public boolean isPreformatted() {
        return false;
    }

    @Override
    public boolean usesIndex() {
        return true;
    }

    @Override
    public int numberOfTokens() {
        return 36;
    }
}
