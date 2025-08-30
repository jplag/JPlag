package de.jplag.scheme;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * Scheme language module facade.
 */
@MetaInfServices(Language.class)
public class SchemeLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of(".scm", ".ss");
    }

    @Override
    public String getName() {
        return "Scheme";
    }

    @Override
    public String getIdentifier() {
        return "scheme";
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
