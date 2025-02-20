package de.jplag.llvmir;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.antlr.AbstractAntlrLanguage;

/**
 * The entry point for the ANTLR parser based LLVM IR language module.
 */
@MetaInfServices(Language.class)
public class LLVMIRLanguage extends AbstractAntlrLanguage {

    private static final String NAME = "LLVM IR";
    private static final String IDENTIFIER = "llvmir";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 70;
    private static final String[] FILE_EXTENSIONS = {".ll"};

    public LLVMIRLanguage() {
        super(new LLVMIRParserAdapter());
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
}
