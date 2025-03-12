package de.jplag.kotlin;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * This represents the Kotlin language as a language supported by JPlag.
 */
@MetaInfServices(Language.class)
public class KotlinLanguage implements Language {

    @Override
    public String[] suffixes() {
        return new String[] {".kt"};
    }

    @Override
    public String getName() {
        return "Kotlin";
    }

    @Override
    public String getIdentifier() {
        return "kotlin";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new KotlinParserAdapter().parse(files);
    }
}
