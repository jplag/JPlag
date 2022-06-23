package de.jplag.csharp;

import java.io.File;
import java.util.Arrays;

import org.kohsuke.MetaInfServices;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

/**
 * C# language with full support of C# 6 features and below.
 * @author Timur Saglam
 */
@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {
    private static final String NAME = "C# 6 Parser";
    private static final String SHORT_NAME = "C# 6";
    private static final String[] FILE_ENDINGS = new String[] {".cs", ".CS"};
    private static final int DEFAULT_MIN_TOKEN_MATCH = 8;

    private final CSharpParserAdapter parser;

    /**
     * Prototype Constructor for {@link MetaInfServices}.
     */
    public Language() {
        this.parser = null;
    }

    Language(ErrorConsumer program) {
        parser = new CSharpParserAdapter(program);
    }

    @Override
    public de.jplag.Language initializeLanguage(ErrorConsumer errorConsumer) {
        return new Language(errorConsumer);
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
    public String getShortName() {
        return SHORT_NAME;
    }

    @Override
    public int minimumTokenMatch() {
        return DEFAULT_MIN_TOKEN_MATCH;
    }

    @Override
    public TokenList parse(File dir, String[] files) {
        return parser.parse(dir, Arrays.asList(files));
    }

    @Override
    public boolean hasErrors() {
        return parser.hasErrors();
    }

    @Override
    public boolean supportsColumns() {
        return true;
    }

    @Override
    public boolean isPreformatted() {
        return true;
    }

    @Override
    public boolean usesIndex() {
        return false;
    }

    @Override
    public int numberOfTokens() {
        return CSharpTokenConstants.NUM_DIFF_TOKENS;
    }
}
