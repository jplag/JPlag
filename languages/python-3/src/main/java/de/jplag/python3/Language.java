package de.jplag.python3;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.ParsingException;
import de.jplag.Token;

@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {

    private static final String IDENTIFIER = "python3";

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
    public List<Token> parse(Set<File> files) throws ParsingException {
        return this.parser.parse(files);
    }
}
