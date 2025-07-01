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

    @Override
    public List<String> fileExtensions() {
        return List.of(".ll");
    }

    @Override
    public String getName() {
        return "LLVM IR";
    }

    @Override
    public String getIdentifier() {
        return "llvmir";
    }

    @Override
    public int minimumTokenMatch() {
        return 70;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new LLVMIRParserAdapter().parse(files);

    }
}
