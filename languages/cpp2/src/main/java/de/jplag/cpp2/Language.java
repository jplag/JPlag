package de.jplag.cpp2;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Experimental;
import org.kohsuke.MetaInfServices;

import de.jplag.ParsingException;
import de.jplag.Token;

@Experimental
@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {
    private static final String IDENTIFIER = "cpp2";

    private final Parser parser; // cpp code is scanned not parsed

    public Language() {
        parser = new Parser();
    }

    @Override
    public String[] suffixes() {
        return new String[] {".cpp", ".CPP", ".cxx", ".CXX", ".c++", ".C++", ".c", ".C", ".cc", ".CC", ".h", ".H", ".hpp", ".HPP", ".hh", ".HH"};
    }

    @Override
    public String getName() {
        return "C/C++ Parser";
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
        return this.parser.scan(files);
    }
}
