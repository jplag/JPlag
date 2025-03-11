package de.jplag.scheme;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.ParsingException;
import de.jplag.Token;

@MetaInfServices(de.jplag.Language.class)
public class SchemeLanguage implements de.jplag.Language {

    private static final String NAME = "Scheme";
    private static final String IDENTIFIER = "scheme";

    @Override
    public String[] suffixes() {
        return new String[] {".scm", ".SCM", ".ss", ".SS"};
    }

    @Override
    public String getName() {
        return NAME;
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
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new Parser().parse(files);
    }
}
