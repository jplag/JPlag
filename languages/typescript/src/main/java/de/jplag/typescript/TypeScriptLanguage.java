package de.jplag.typescript;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * This represents the TypeScript language as a language supported by JPlag.
 */
@MetaInfServices(de.jplag.Language.class)
public class TypeScriptLanguage implements Language {

    private static final String IDENTIFIER = "typescript";
    private static final String NAME = "TypeScript";
    private final TypeScriptLanguageOptions options = new TypeScriptLanguageOptions();

    @Override
    public String[] suffixes() {
        return new String[] {".ts", ".js"};
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
        return 12;
    }

    @Override
    public TypeScriptLanguageOptions getOptions() {
        return options;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new TypeScriptParserAdapter(options.useStrictDefault()).parse(files);
    }
}
