package de.jplag.chars;

import java.io.File;

import org.kohsuke.MetaInfServices;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

/*
 * read in text files as characters
 */
@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {

    private final Parser parser;

    /**
     * Prototype Constructor for {@link MetaInfServices}.
     */
    public Language() {
        this.parser = null;
    }

    private Language(ErrorConsumer program) {
        parser = new Parser(program);
    }

    @Override
    public de.jplag.Language createInitializedLanguage(ErrorConsumer errorConsumer) {
        return new Language(errorConsumer);
    }

    @Override
    public String[] suffixes() {
        return new String[] {".TXT", ".txt", ".ASC", ".asc", ".TEX", ".tex"};
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
