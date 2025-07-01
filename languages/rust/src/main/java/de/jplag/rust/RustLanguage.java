package de.jplag.rust;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * This represents the Rust language as a language supported by JPlag.
 */
@MetaInfServices(Language.class)
public class RustLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of(".rs");
    }

    @Override
    public String getName() {
        return "Rust";
    }

    @Override
    public String getIdentifier() {
        return "rust";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new RustParserAdapter().parse(files);
    }
}
