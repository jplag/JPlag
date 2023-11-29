package de.jplag.csharp;

import org.kohsuke.MetaInfServices;

import de.jplag.antlr.AbstractAntlrLanguage;

/**
 * C# language with full support of C# 6 features and below.
 */
@MetaInfServices(de.jplag.Language.class)
public class CSharpLanguage extends AbstractAntlrLanguage {
    private static final String NAME = "C# 6 Parser";
    private static final String IDENTIFIER = "csharp";
    private static final String[] FILE_ENDINGS = new String[] {".cs", ".CS"};
    private static final int DEFAULT_MIN_TOKEN_MATCH = 8;

    public CSharpLanguage() {
        super(new CSharpParserAdapter());
    }

    @Override
    public String[] suffixes() {
        return FILE_ENDINGS;
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
        return DEFAULT_MIN_TOKEN_MATCH;
    }
}
