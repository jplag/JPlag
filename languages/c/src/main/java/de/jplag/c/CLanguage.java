package de.jplag.c;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

@MetaInfServices(Language.class)
public class CLanguage implements Language {

    @Override
    public String[] suffixes() {
        return new String[] {".cpp", ".cxx", ".c++", ".c", ".cc", ".h", ".hpp", ".hh", ".hxx"};
    }

    @Override
    public String getName() {
        return "C";
    }

    @Override
    public String getIdentifier() {
        return "c";
    }

    @Override
    public int minimumTokenMatch() {
        return 12;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new Scanner().scan(files);
    }
}
