package de.jplag.csharp;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * C# language with full support of C# 6 features and below.
 */
@MetaInfServices(de.jplag.Language.class)
public class CSharpLanguage implements Language {
    private static final String NAME = "C#";
    private static final String IDENTIFIER = "csharp";
    private static final String[] FILE_ENDINGS = new String[] {".cs", ".CS"};
    private static final int DEFAULT_MIN_TOKEN_MATCH = 8;

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

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new CSharpParserAdapter().parse(files);
    }
}
