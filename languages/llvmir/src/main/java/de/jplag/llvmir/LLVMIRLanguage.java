package de.jplag.llvmir;

import org.kohsuke.MetaInfServices;

import de.jplag.antlr.AbstractAntlrLanguage;

/**
 * This represents the LLVMIR language as a language supported by JPlag.
 */
@MetaInfServices(de.jplag.Language.class)
public class LLVMIRLanguage extends AbstractAntlrLanguage {

    private static final String NAME = "LLVMIR Parser";
    private static final String IDENTIFIER = "llvmir";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 20;
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
