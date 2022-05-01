package de.jplag.csharp;

import java.io.File;
import java.util.Arrays;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

/**
 * C# language with full support of C# 6 features and below.
 * @author Timur Saglam
 */
public class Language implements de.jplag.Language {
    private final CSharpParserAdapter parser;

    public Language(ErrorConsumer program) {
        parser = new CSharpParserAdapter(program);
    }

    @Override
    public String[] suffixes() {
        return new String[] {".cs", ".CS"};
    }

    @Override
    public String getName() {
        return "C# 6 Parser";
    }

    @Override
    public String getShortName() {
        return "C# 6";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public TokenList parse(File dir, String[] files) {
        return this.parser.parse(dir, Arrays.asList(files));
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
        return false;
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
