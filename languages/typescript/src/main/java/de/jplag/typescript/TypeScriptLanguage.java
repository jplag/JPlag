package de.jplag.typescript;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.antlr.AbstractAntlrLanguage;

import java.io.File;
import java.util.List;
import java.util.Set;

public class TypeScriptLanguage extends AbstractAntlrLanguage {

    private static final String IDENTIFIER = "typescript";
    private final TypeScriptLanguageOptions options = new TypeScriptLanguageOptions();

    public TypeScriptLanguage() {
        super(new TypeScriptParserAdapter());
    }

    @Override
    public String[] suffixes() {
        return new String[]{".ts", ".js"};
    }

    @Override
    public String getName() {
        return "Typescript Parser";
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
    public TypeScriptLanguageOptions getOptions() {
        return options;
    }

}
