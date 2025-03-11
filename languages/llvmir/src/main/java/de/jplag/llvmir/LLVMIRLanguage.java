package de.jplag.llvmir;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * The entry point for the ANTLR parser based LLVM IR language module.
 */
@MetaInfServices(Language.class)
public class LLVMIRLanguage implements Language {

    private static final String NAME = "LLVM IR";
    private static final String IDENTIFIER = "llvmir";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 70;
    private static final String[] FILE_EXTENSIONS = {".ll"};

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
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new LLVMIRParserAdapter().parse(files);

    }
}
