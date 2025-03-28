package de.jplag.golang;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

@MetaInfServices(Language.class)
public class GoLanguage implements Language {

    @Override
    public String[] suffixes() {
        return new String[] {".go"};
    }

    @Override
    public String getName() {
        return "Go";
    }

    @Override
    public String getIdentifier() {
        return "go";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new GoParserAdapter().parse(files);
    }
}
