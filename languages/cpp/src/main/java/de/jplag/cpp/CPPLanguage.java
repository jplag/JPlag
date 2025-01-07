package de.jplag.cpp;

import java.util.List;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.antlr.AbstractAntlrLanguage;
import de.jplag.tokentypes.ArraySyntaxTokenTypes;
import de.jplag.tokentypes.ExceptionHandlingTokenTypes;
import de.jplag.tokentypes.ImperativeTokenAttribute;
import de.jplag.tokentypes.InlineIfTokenTypes;
import de.jplag.tokentypes.ObjectOrientationTokens;

/**
 * The entry point for the ANTLR parser based C++ language module.
 */
@MetaInfServices(Language.class)
public class CPPLanguage extends AbstractAntlrLanguage {
    private static final String NAME = "C++";
    private static final String IDENTIFIER = "cpp";

    public CPPLanguage() {
        super(new CPPParserAdapter());
    }

    @Override
    public String[] suffixes() {
        return new String[] {".cpp", ".CPP", ".cxx", ".CXX", ".c++", ".C++", ".c", ".C", ".cc", ".CC", ".h", ".H", ".hpp", ".HPP", ".hh", ".HH"};
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
    public boolean tokensHaveSemantics() {
        return true;
    }

    @Override
    public boolean supportsNormalization() {
        return true;
    }

    @Override
    public List<Class<?>> getTokenContexts() {
        return List.of(ArraySyntaxTokenTypes.class, ExceptionHandlingTokenTypes.class, ImperativeTokenAttribute.class, InlineIfTokenTypes.class,
                ObjectOrientationTokens.class);
    }
}
