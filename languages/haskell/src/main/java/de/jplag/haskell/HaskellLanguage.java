package de.jplag.haskell;

import de.jplag.antlr.AbstractAntlrLanguage;
import de.jplag.haskell.grammar.HaskellParser;

public class HaskellLanguage extends AbstractAntlrLanguage {

    private static final String IDENTIFIER = "haskell";
    private static final String NAME = "Haskell";

    public HaskellLanguage() {
        super(new HaskellParserAdapter());
    }

    @Override
    public String[] suffixes() {
        return new String[]{".hs", ".lhs"};
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public int minimumTokenMatch() {
        return 9;
    }
}
