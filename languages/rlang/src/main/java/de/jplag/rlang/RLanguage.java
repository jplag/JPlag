package de.jplag.rlang;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * This represents the R language as a language supported by JPlag.
 */
@MetaInfServices(Language.class)
public class RLanguage implements Language {

    @Override
    public String[] suffixes() {
        return new String[] {".R", ".r"};
    }

    @Override
    public String getName() {
        return "R";
    }

    @Override
    public String getIdentifier() {
        return "rlang";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new RParserAdapter().parse(files);
    }
}
