package de.jplag.python3;

import org.kohsuke.MetaInfServices;

import de.jplag.antlr.AbstractAntlrLanguage;

@MetaInfServices(de.jplag.Language.class)
public class PythonLanguage extends AbstractAntlrLanguage {

    private static final String IDENTIFIER = "python3";

    public PythonLanguage() {
        super(new PythonParserAdapter());
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
}
