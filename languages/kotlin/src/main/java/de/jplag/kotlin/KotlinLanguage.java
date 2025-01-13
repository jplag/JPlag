package de.jplag.kotlin;

import java.util.List;

import org.kohsuke.MetaInfServices;

import de.jplag.antlr.AbstractAntlrLanguage;
import de.jplag.tokentypes.CodeStructureTokenTypes;
import de.jplag.tokentypes.ExceptionHandlingTokenTypes;
import de.jplag.tokentypes.ImperativeTokenType;
import de.jplag.tokentypes.ObjectOrientationTokens;

/**
 * This represents the Kotlin language as a language supported by JPlag.
 */
@MetaInfServices(de.jplag.Language.class)
public class KotlinLanguage extends AbstractAntlrLanguage {

    private static final String NAME = "Kotlin";
    private static final String IDENTIFIER = "kotlin";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 8;
    private static final String[] FILE_EXTENSIONS = {".kt"};

    public KotlinLanguage() {
        super(new KotlinParserAdapter());
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
    public List<Class<?>> getTokenContexts() {
        return List.of(CodeStructureTokenTypes.class, ExceptionHandlingTokenTypes.class, ImperativeTokenType.class, ObjectOrientationTokens.class);
    }
}
