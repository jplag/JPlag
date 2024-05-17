package de.jplag.cpp;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.antlr.AbstractAntlrLanguage;

/**
 * The entry point for the ANTLR parser based C++ language module.
 */
@MetaInfServices(Language.class)
public class CPPLanguage extends AbstractAntlrLanguage {
    private static final String IDENTIFIER = "cpp";

    public CPPLanguage() {
        super(new CPPParserAdapter());
    }

    @Override
    public String[] suffixes() {
        return new String[] {".cpp", ".CPP", ".cxx", ".CXX", ".c++", ".C++", ".c", ".C", ".cc", ".CC", ".h", ".H", ".hpp", ".HPP", ".hh", ".HH"};
    }

    @Override
    public String getName() {
        return "C++";
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
    public boolean tokensHaveSemantics() {
        return true;
    }

    @Override
    public boolean supportsNormalization() {
        return true;
    }
}
