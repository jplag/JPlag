package de.jplag.rust;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * This represents the Rust language as a language supported by JPlag.
 */
@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {

    protected static final String[] FILE_EXTENSIONS = {".rs"};
    private static final String NAME = "Rust Language Module";
    private static final String IDENTIFIER = "rust";
    private static final int MINIMUM_TOKEN_MATCH = 8;

    private final RustParserAdapter parserAdapter;

    public Language() {
        this.parserAdapter = new RustParserAdapter();
    }

    @Override
    public String[] suffixes() {
        return FILE_EXTENSIONS;
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
        return MINIMUM_TOKEN_MATCH;
    }

    @Override
    public List<Token> parse(Set<File> files) throws ParsingException {
        return parserAdapter.parse(files);
    }
}
