package de.jplag.python3;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Token;

@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {

    public static final String IDENTIFIER = "python3";

    private final Parser parser;

    public Language() {
        parser = new Parser();
    }

    @Override
    public String[] suffixes() {
        return new String[] {".py"};
    }

    @Override
    public String getName() {
        return "Python3 Parser";
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public int minimumTokenMatch() {
        return 12;
    }

    @Override
    public List<Token> parse(Set<File> files) {
        return this.parser.parse(files);
    }

    @Override
    public boolean hasErrors() {
        return this.parser.hasErrors();
    }
}
