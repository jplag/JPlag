package de.jplag.scheme;

import java.io.File;
import java.util.List;

import org.kohsuke.MetaInfServices;

import de.jplag.Token;

@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {

    public static final String IDENTIFIER = "scheme";
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
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public int minimumTokenMatch() {
        return 13;
    }

    @Override
    public List<Token> parse(File dir, String[] files) {
        return this.parser.parse(dir, files);
    }

    @Override
    public boolean hasErrors() {
        return this.parser.hasErrors();
    }
}
