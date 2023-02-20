package de.jplag.cpp2;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * The entry point for the ANTLR parser based C++ language module.
 */
@MetaInfServices(Language.class)
public class CPPLanguage implements Language {
    private static final String IDENTIFIER = "cpp2";

    private final CPPParserAdapter parser;

    public CPPLanguage() {
        parser = new CPPParserAdapter();
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
