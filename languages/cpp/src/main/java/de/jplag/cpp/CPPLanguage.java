package de.jplag.cpp;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

import com.google.auto.service.AutoService;

/**
 * The entry point for the ANTLR parser based C++ language module.
 */
@AutoService(Language.class)
public class CPPLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of(".cpp", ".cxx", ".c++", ".c", ".cc", ".h", ".hpp", ".hh", ".hxx");
    }

    @Override
    public String getName() {
        return "C++";
    }

    @Override
    public String getIdentifier() {
        return "cpp";
    }

    @Override
    public int minimumTokenMatch() {
        return 12;
    }

    @Override
    public boolean tokensHaveSemantics() {
        return true;
    }

    @Override
    public boolean supportsNormalization() {
        return true;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new CPPParserAdapter().parse(files);
    }

    @Override
    public boolean hasPriority() {
        return true; // Priority over the C language module.
    }
}
