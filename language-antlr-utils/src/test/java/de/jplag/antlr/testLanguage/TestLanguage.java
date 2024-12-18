package de.jplag.antlr.testLanguage;

import de.jplag.antlr.AbstractAntlrLanguage;
import de.jplag.antlr.AbstractAntlrParserAdapter;

public class TestLanguage extends AbstractAntlrLanguage {
    @Override
    protected AbstractAntlrParserAdapter<?> initializeParser(boolean normalize) {
        return new TestParserAdapter(this);
    }

    @Override
    public String[] suffixes() {
        return new String[] {"expression"};
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getIdentifier() {
        return "test";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }
}
