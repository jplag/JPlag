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
@MetaInfServices(Language.class)
public class TypeScriptLanguage implements Language {

    private final TypeScriptLanguageOptions options = new TypeScriptLanguageOptions();

    @Override
    public List<String> fileExtensions() {
        return List.of(".ts", ".js");
    }

    @Override
    public String getName() {
        return "TypeScript";
    }

    @Override
    public String getIdentifier() {
        return "typescript";
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
