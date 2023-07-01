package de.jplag.llvmir;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * This represents the LLVMIR language as a language supported by JPlag.
 */
@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {

    private static final String NAME = "LLVMIR Parser";
    private static final String IDENTIFIER = "llvmir";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 10;
    private static final String[] FILE_EXTENSIONS = {".ll"};
    private final LLVMParserAdapter parserAdapter;

    public Language() {
        this.parserAdapter = new LLVMParserAdapter();
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
        return DEFAULT_MIN_TOKEN_MATCH;
    }

    @Override
    public List<Token> parse(Set<File> files) throws ParsingException {
        return parserAdapter.parse(files);
    }
}
