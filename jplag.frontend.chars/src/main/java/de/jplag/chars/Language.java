package de.jplag.chars;

import java.io.File;

import org.kohsuke.MetaInfServices;

import de.jplag.TokenList;

/*
 * read in text files as characters
 */
@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {

    public static final String IDENTIFIER = "char";
    private final Parser parser;

    public Language() {
        parser = new Parser();
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
    public String getIdentifier() {
        return IDENTIFIER;
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
}
