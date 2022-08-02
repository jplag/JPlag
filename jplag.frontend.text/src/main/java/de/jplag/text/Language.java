package de.jplag.text;

import java.io.File;

import de.jplag.TokenList;

public class Language implements de.jplag.Language {

    private final ParserAdapter parserAdapter;

    public Language() {
        parserAdapter = new ParserAdapter();
    }

    @Override
    public String[] suffixes() {
        return new String[] {".TXT", ".txt", ".ASC", ".asc", ".TEX", ".tex"};
    }

    @Override
    public String getName() {
        return "Text ParserAdapter";
    }

    @Override
    public String getShortName() {
        return "text";
    }

    @Override
    public int minimumTokenMatch() {
        return 5;
    }

    @Override
    public TokenList parse(File dir, String[] files) {
        return parserAdapter.parse(dir, files);
    }

    @Override
    public boolean hasErrors() {
        return parserAdapter.hasErrors();
    }

    @Override
    public boolean isPreformatted() {
        return false;
    }
}
