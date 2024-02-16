package de.jplag.typescript;

import org.kohsuke.MetaInfServices;

import de.jplag.antlr.AbstractAntlrLanguage;

/**
 * This represents the TypeScript language as a language supported by JPlag.
 */
@MetaInfServices(de.jplag.Language.class)
public class TypeScriptLanguage extends AbstractAntlrLanguage {

    private static final String IDENTIFIER = "typescript";
    private final TypeScriptLanguageOptions options = new TypeScriptLanguageOptions();

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
    protected TypeScriptParserAdapter initializeParser(boolean normalize) {
        return new TypeScriptParserAdapter(getOptions().useStrictDefault());
    }
}
