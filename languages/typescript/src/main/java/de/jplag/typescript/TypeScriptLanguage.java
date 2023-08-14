package de.jplag.typescript;

import de.jplag.antlr.AbstractAntlrLanguage;

public class TypeScriptLanguage extends AbstractAntlrLanguage {

    private static final String IDENTIFIER = "typescript";
    private final TypeScriptLanguageOptions options = new TypeScriptLanguageOptions();

    public TypeScriptLanguage() {
        super();
    }

    @Override
    public String[] suffixes() {
        return new String[] {".ts", ".js"};
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

    @Override
    protected TypeScriptParserAdapter initializeParser() {
        return new TypeScriptParserAdapter(getOptions().useStrictDefault.getValue());
    }
}
