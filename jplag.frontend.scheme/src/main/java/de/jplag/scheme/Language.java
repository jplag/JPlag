package de.jplag.scheme;

import java.io.File;

import de.jplag.TokenList;

public class Language implements de.jplag.Language {
    private final de.jplag.scheme.Parser parser;

    public Language() {
        parser = new Parser();
    }

    @Override
    public String[] suffixes() {
        return new String[] {".scm", ".SCM", ".ss", ".SS"};
    }

    @Override
    public String getName() {
        return "SchemeR4RS Parser [basic markup]";
    }

    @Override
    public String getShortName() {
        return "scheme";
    }

    @Override
    public int minimumTokenMatch() {
        return 13;
    }

    @Override
    public TokenList parse(File dir, String[] files) {
        return this.parser.parse(dir, files);
    }

    @Override
    public boolean hasErrors() {
        return this.parser.hasErrors();
    }
}
