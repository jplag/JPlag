package de.jplag.antlr.testLanguage;

import de.jplag.antlr.AbstractAntlrLanguage;

public class TestLangauge extends AbstractAntlrLanguage {
    /**
     * New instance
     */
    public TestLangauge() {
        super(new TestParserAdapter());
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
